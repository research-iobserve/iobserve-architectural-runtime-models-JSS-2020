/***************************************************************************
 * Copyright (C) 2014 iObserve Project (https://www.iobserve-devops.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package org.iobserve.analysis.filter;

import java.util.Optional;

import org.iobserve.analysis.model.AllocationModelBuilder;
import org.iobserve.analysis.model.AllocationModelProvider;
import org.iobserve.analysis.model.ResourceEnvironmentModelBuilder;
import org.iobserve.analysis.model.ResourceEnvironmentModelProvider;
import org.iobserve.analysis.model.SystemModelBuilder;
import org.iobserve.analysis.model.SystemModelProvider;
import org.iobserve.analysis.model.correspondence.Correspondent;
import org.iobserve.analysis.model.correspondence.ICorrespondence;
import org.iobserve.analysis.utils.ExecutionTimeLogger;
import org.iobserve.analysis.utils.Opt;
import org.iobserve.common.record.EJBDeployedEvent;
import org.iobserve.common.record.IDeploymentRecord;
import org.iobserve.common.record.ServletDeployedEvent;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * This class contains the transformation for updating the PCM allocation model with respect to
 * deployment. It processes deployment events and uses the correspondence information in the RAC to
 * update the PCM allocation model.
 *
 * @author Robert Heinrich
 * @author Alessandro Giusa
 */
public final class TDeployment extends AbstractConsumerStage<IDeploymentRecord> {

    /** reference to correspondent model. */
    private final ICorrespondence correspondence;
    /** reference to allocation model provider. */
    private final AllocationModelProvider allocationModelProvider;
    /** reference to system model provider. */
    private final SystemModelProvider systemModelProvider;
    /** reference to resource environment model provider. */
    private final ResourceEnvironmentModelProvider resourceEnvModelProvider;
    /** output port. */
    private final OutputPort<ResourceContainer> outputPort = this.createOutputPort();

    /**
     * Creates new TDeallocation filter.
     *
     * @param correspondence
     *            the correspondence model access
     * @param allocationModelProvider
     *            allocation model provider
     * @param systemModelProvider
     *            system model provider
     * @param resourceEnvironmentModelProvider
     *            resource environment model provider
     */
    public TDeployment(final ICorrespondence correspondence, final AllocationModelProvider allocationModelProvider,
            final SystemModelProvider systemModelProvider,
            final ResourceEnvironmentModelProvider resourceEnvironmentModelProvider) {
        // get all model references
        this.correspondence = correspondence;
        this.allocationModelProvider = allocationModelProvider;
        this.systemModelProvider = systemModelProvider;
        this.resourceEnvModelProvider = resourceEnvironmentModelProvider;
    }

    /**
     * This method is triggered for every deployment event.
     *
     * @param event
     *            one deployment event to be processed
     */
    @Override
    protected void execute(final IDeploymentRecord event) {
    	ExecutionTimeLogger.getInstance().startLogging(event);
    	
        if (event instanceof ServletDeployedEvent) {
            this.process((ServletDeployedEvent) event);

        } else if (event instanceof EJBDeployedEvent) {
            this.process((EJBDeployedEvent) event);
        }
        
        ExecutionTimeLogger.getInstance().stopLogging(event);
    }

    /**
     * Process the given {@link ServletDeployedEvent} event and update the model.
     *
     * @param event
     *            event to process
     */
    private void process(final ServletDeployedEvent event) {
        final String service = event.getSerivce();
        final String context = event.getContext();
        Opt.of(this.correspondence.getCorrespondent(context)).ifPresent()
                .apply(correspondence -> this.updateModel(service, correspondence))
                .elseApply(() -> System.out.printf("No correspondent found for %s \n", service));
    }

    /**
     * Process the given {@link EJBDeployedEvent} event and update the model.
     *
     * @param event
     *            event to process
     */
    private void process(final EJBDeployedEvent event) {
        final String service = event.getSerivce();
        final String context = event.getContext();

        Opt.of(this.correspondence.getCorrespondent(context)).ifPresent()
                .apply(correspondent -> this.updateModel(service, correspondent))
                .elseApply(() -> System.out.printf("No correspondent found for %s \n", service));
    }

    /**
     * Update the system model and allocation model by the given correspondent.
     *
     * @param serverName
     *            name of the server
     * @param correspondent
     *            correspondent
     */
    private void updateModel(final String serverName, final Correspondent correspondent) {
        final String entityName = correspondent.getPcmEntityName();

        // build the assembly context name
        final String asmContextName = entityName + "_" + serverName;

        // get the model parts by name
        final Optional<ResourceContainer> optResourceContainer = ResourceEnvironmentModelBuilder
                .getResourceContainerByName(this.resourceEnvModelProvider.getModel(), serverName);
        
        if(optResourceContainer.isPresent()) {
        	// get assembly context by name or create it if necessary.
            final AssemblyContext assemblyContext;
            final Optional<AssemblyContext> optAssCtx = SystemModelBuilder
                    .getAssemblyContextByName(this.systemModelProvider.getModel(), asmContextName);
            if (optAssCtx.isPresent()) {
                assemblyContext = optAssCtx.get();
            } else {
            	this.systemModelProvider.loadModel();
            	assemblyContext = SystemModelBuilder.createAssemblyContextsIfAbsent(this.systemModelProvider.getModel(), asmContextName);
                this.systemModelProvider.save();
            }

            // add context as allocation context to the allocation model
            this.allocationModelProvider.loadModel();
            AllocationModelBuilder.addAllocationContextIfAbsent(this.allocationModelProvider.getModel(), optResourceContainer.get(),
                    assemblyContext);
            this.allocationModelProvider.save();
        } else {
        	System.out.printf("AssemblyContext %s was not available?!\n", asmContextName);
        }
    }
    
    public OutputPort<ResourceContainer> getOutputPort() {
        return this.outputPort;
    }
}