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

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

import java.util.Optional;

import org.iobserve.analysis.model.ResourceEnvironmentModelBuilder;
import org.iobserve.analysis.model.ResourceEnvironmentModelProvider;
import org.iobserve.analysis.utils.ExecutionTimeLogger;
import org.iobserve.common.record.EJBDeployedEvent;
import org.iobserve.common.record.IDeploymentRecord;
import org.iobserve.common.record.ServletDeployedEvent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;

/**
 * TAllocation checks whether a referenced resource container exists in the pcm model.
 * If no resource container exists a new container is created.
 *
 * @author Robert Heinrich
 * @author Nicolas Boltz
 */
public final class TAllocation extends AbstractConsumerStage<IDeploymentRecord> {

    /** reference to {@link ResourceEnvironment} provider. */
    private final ResourceEnvironmentModelProvider resourceEnvModelProvider;
    /** output port. */
    private final OutputPort<IDeploymentRecord> deploymentOutputPort = this.createOutputPort();

    /**
     * Creates new TAllocation filter.
     *
     * @param resourceEvnironmentModelProvider
     *            the resource environment model provider
     */
    public TAllocation(final ResourceEnvironmentModelProvider resourceEvnironmentModelProvider) {
        this.resourceEnvModelProvider = resourceEvnironmentModelProvider;
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
            final String serivce = ((ServletDeployedEvent) event).getSerivce();
            this.updateModel(serivce);
        } else if (event instanceof EJBDeployedEvent) {
            final String service = ((EJBDeployedEvent) event).getSerivce();
            this.updateModel(service);
        }
        
        ExecutionTimeLogger.getInstance().stopLogging(event);

        // forward the event
        this.deploymentOutputPort.send(event);
    }

    /**
     * Update the allocation model with the given server-name if necessary.
     *
     * @param serverName
     *            server name
     */
    private void updateModel(final String serverName) {
    	Optional<ResourceContainer> optContainer = ResourceEnvironmentModelBuilder.getResourceContainerByName(this.resourceEnvModelProvider.getModel(), serverName);
    	if(!optContainer.isPresent()) {
    		this.resourceEnvModelProvider.loadModel();
    		final ResourceEnvironment model = this.resourceEnvModelProvider.getModel();
    		ResourceEnvironmentModelBuilder.createResourceContainer(model, serverName);
    		this.resourceEnvModelProvider.save();
    	}
    }
    
    public OutputPort<IDeploymentRecord> getDeploymentOutputPort() {
        return this.deploymentOutputPort;
    }
}