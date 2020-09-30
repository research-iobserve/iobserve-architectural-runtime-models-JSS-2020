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
package org.iobserve.service.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.iobserve.analysis.FileObservationConfiguration;
import org.iobserve.analysis.InitializeModelProviders;
import org.iobserve.analysis.model.AllocationModelProvider;
import org.iobserve.analysis.model.RepositoryModelProvider;
import org.iobserve.analysis.model.ResourceEnvironmentModelProvider;
import org.iobserve.analysis.model.SystemModelProvider;
import org.iobserve.analysis.model.UsageModelProvider;
import org.iobserve.analysis.model.correspondence.ICorrespondence;
import org.iobserve.analysis.userbehavior.test.ClusteringEvaluation;
import org.iobserve.analysis.userbehavior.test.TEntryEventSequenceTest;
import org.iobserve.analysis.utils.ExecutionTimeLogger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.IntegerConverter;

import teetime.framework.Configuration;
import teetime.framework.Execution;

/**
 * Main class for starting the iObserve application.
 *
 * @author Reiner Jung
 * @author Robert Heinrich
 * @author Alessandro Giusa
 */
public final class AnalysisMain {

    @Parameter(names = "--help", help = true)
    private boolean help;

    @Parameter(names = { "-v",
            "--variance-of-user-groups" }, required = true, description = "Variance of user groups.", converter = IntegerConverter.class)
    private int varianceOfUserGroups;

    @Parameter(names = { "-t",
            "--think-time" }, required = true, description = "Think time.", converter = IntegerConverter.class)
    private int thinkTime;

    @Parameter(names = { "-c",
            "--closed-workload" }, required = false, description = "Closed workload.", converter = IntegerConverter.class)
    private boolean closedWorkload = false;

    @Parameter(names = { "-i",
            "--input" }, required = true, description = "Kieker monitoring data directory.", converter = FileConverter.class)
    private File monitoringDataDirectory;

    @Parameter(names = { "-p",
            "--pcm" }, required = true, description = "Directory containing PCM model data.", converter = FileConverter.class)
    private File pcmModelsDirectory;
    
    @Parameter(names = { "-g",
    		"--generation-frequency" }, required = true, description = "Frequency of generating output models.", converter = IntegerConverter.class)
    private int generationFrequency;
    
    /**
     * Default constructor.
     */
    private AnalysisMain() {
        // do nothing here
    }

    /**
     * Main function.
     *
     * @param args
     *            command line arguments.
     */
    public static void main(final String[] args) {
        final AnalysisMain main = new AnalysisMain();
        final JCommander commander = new JCommander(main);
        try {
            commander.parse(args);
            main.execute(commander);
        } catch (final ParameterException e) {
            System.err.println(e.getLocalizedMessage());
            commander.usage();
        } catch (final IOException e) {
            System.err.println(e.getLocalizedMessage());
            commander.usage();
        }
    }

    private void execute(final JCommander commander) throws IOException {
        if (this.help) {
            commander.usage();
            System.exit(1);
        } else {
//            File warmupMonitoringDirectory = new File("D:\\Dokumente\\Uni\\HiWi\\iobserve\\iobserve\\iobserve-analysis\\analysis\\res\\warmup_dir\\kieker");
//            File warmupPcmModelDirectory = new File("D:\\Dokumente\\Uni\\HiWi\\iobserve\\iobserve\\iobserve-analysis\\analysis\\res\\warmup_dir\\pcm");
//            this.executeWithConfig("Warmup", warmupMonitoringDirectory, warmupPcmModelDirectory, 0, 0, false);
//            ExecutionTimeLogger.resetTimes();
            
            
            this.checkDirectory(this.monitoringDataDirectory, "Kieker log", commander);
            this.checkDirectory(this.pcmModelsDirectory, "Palladio Model", commander);
            this.executeWithConfig("Analysis", this.monitoringDataDirectory, this.pcmModelsDirectory, this.varianceOfUserGroups, this.thinkTime, 
                    this.closedWorkload, this.generationFrequency);
            ExecutionTimeLogger.getInstance().exportAsCsv();
        }
    }
    
    private void executeWithConfig(final String executionTag, final File monitoringDataDirectory, final File pcmModelDirectory, final int varianceOfUserGroups, 
            final int thinkTime, final boolean closedWorkload, final int generationFrequency) {
        /** create and run application */
        final Collection<File> monitoringDataDirectories = new ArrayList<>();
        AnalysisMain.findDirectories(monitoringDataDirectory.listFiles(), monitoringDataDirectories);

        final InitializeModelProviders modelProviderPlatform = new InitializeModelProviders(
                pcmModelDirectory);

        final ICorrespondence correspondenceModel = modelProviderPlatform.getCorrespondenceModel();
        final RepositoryModelProvider repositoryModelProvider = modelProviderPlatform.getRepositoryModelProvider();
        final UsageModelProvider usageModelProvider = modelProviderPlatform.getUsageModelProvider();
        final ResourceEnvironmentModelProvider resourceEvnironmentModelProvider = modelProviderPlatform
                .getResourceEnvironmentModelProvider();
        final AllocationModelProvider allocationModelProvider = modelProviderPlatform.getAllocationModelProvider();
        final SystemModelProvider systemModelProvider = modelProviderPlatform.getSystemModelProvider();

        final Configuration warmupConfiguration = new FileObservationConfiguration(monitoringDataDirectories,
                correspondenceModel, usageModelProvider, repositoryModelProvider, resourceEvnironmentModelProvider,
                allocationModelProvider, systemModelProvider, varianceOfUserGroups, thinkTime, closedWorkload, generationFrequency);

        System.out.println(executionTag + " configuration");
        final Execution<Configuration> warmup = new Execution<>(warmupConfiguration);
        System.out.println(executionTag + " start");
        warmup.executeBlocking();
        System.out.println(executionTag + " complete");
    }

    private void checkDirectory(final File location, final String locationLabel, final JCommander commander)
            throws IOException {
        if (!location.exists()) {
            System.err.println(locationLabel + " path " + location.getCanonicalPath() + " does not exist.");
            commander.usage();
            System.exit(1);
        }
        if (!location.isDirectory()) {
            System.err.println(locationLabel + " path " + location.getCanonicalPath() + " is not a directory.");
            commander.usage();
            System.exit(1);
        }

    }

    private static void findDirectories(final File[] listFiles, final Collection<File> monitoringDataDirectories) {
        for (final File file : listFiles) {
            if (file.isDirectory()) {
                monitoringDataDirectories.add(file);
                AnalysisMain.findDirectories(file.listFiles(), monitoringDataDirectories);
            }
        }
    }

}
