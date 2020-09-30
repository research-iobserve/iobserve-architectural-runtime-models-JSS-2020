/***************************************************************************
 * Copyright (C) 2017 iObserve Project (https://www.iobserve-devops.net)
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
package org.iobserve.analysis.filter.reader;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import kieker.common.record.IMonitoringRecord;
import kieker.common.util.filesystem.BinaryCompressionMethod;
import kieker.common.util.filesystem.FSUtil;
import teetime.framework.AbstractStage;
import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.stage.FileExtensionSwitch;
import teetime.stage.basic.merger.Merger;
import teetime.stage.className.ClassNameRegistryCreationFilter;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.Directory2FilesFilter;
import teetime.stage.io.filesystem.format.binary.file.BinaryFile2RecordFilter;
import teetime.stage.io.filesystem.format.text.file.DatFile2RecordFilter;

/** Note: This is a temporary measure, the real filter is available in teetime/kieker. */
/**
 * @author Christian Wulf
 *
 * @since 1.0
 */
public final class Dir2RecordsFilter extends CompositeStage {

    private final ClassNameRegistryCreationFilter classNameRegistryCreationFilter;
    private final Merger<IMonitoringRecord> recordMerger;

    private ClassNameRegistryRepository classNameRegistryRepository;

    /**
     * Default constructor using a new instance of {@link ClassNameRegistryRepository}.
     */
    public Dir2RecordsFilter() {
        this(new ClassNameRegistryRepository());
    }

    /**
     * Constructor for an external class name registry.
     *
     * @param classNameRegistryRepository
     *            a class name registry
     */
    public Dir2RecordsFilter(final ClassNameRegistryRepository classNameRegistryRepository) {
        this.classNameRegistryRepository = classNameRegistryRepository;

        // TODO does not yet work with more than one thread due to classNameRegistryRepository:
        // classNameRegistryRepository is set after the ctor
        // create stages
        final ClassNameRegistryCreationFilter localClassNameRegistryCreationFilter = new ClassNameRegistryCreationFilter(
                this.classNameRegistryRepository);
        final Directory2FilesFilter directory2FilesFilter = new Directory2FilesFilter(new Comparator<File>() {

            @Override
            public int compare(final File o1, final File o2) {
                try {
                    return o1.getCanonicalFile().compareTo(o2.getCanonicalFile());
                } catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }

        });

        final FileExtensionSwitch fileExtensionSwitch = new FileExtensionSwitch();

        final DatFile2RecordFilter datFile2RecordFilter = new DatFile2RecordFilter(this.classNameRegistryRepository);
        final BinaryFile2RecordFilter binaryFile2RecordFilter = new BinaryFile2RecordFilter(
                this.classNameRegistryRepository);

        this.recordMerger = new Merger<>();

        // store ports due to readability reasons
        final OutputPort<File> normalFileOutputPort = fileExtensionSwitch
                .addFileExtension(FSUtil.NORMAL_FILE_EXTENSION);
        final OutputPort<File> binFileOutputPort = fileExtensionSwitch
                .addFileExtension(BinaryCompressionMethod.NONE.getFileExtension());

        // connect ports by pipes
        this.connectPorts(localClassNameRegistryCreationFilter.getOutputPort(), directory2FilesFilter.getInputPort());
        this.connectPorts(directory2FilesFilter.getOutputPort(), fileExtensionSwitch.getInputPort());

        this.connectPorts(normalFileOutputPort, datFile2RecordFilter.getInputPort());
        this.connectPorts(binFileOutputPort, binaryFile2RecordFilter.getInputPort());

        this.connectPorts(datFile2RecordFilter.getOutputPort(), this.recordMerger.getNewInputPort());
        this.connectPorts(binaryFile2RecordFilter.getOutputPort(), this.recordMerger.getNewInputPort());

        // prepare pipeline
        this.classNameRegistryCreationFilter = localClassNameRegistryCreationFilter;
    }

    public AbstractStage getFirstStage() {
        return this.classNameRegistryCreationFilter;
    }

    public InputPort<File> getInputPort() {
        return this.classNameRegistryCreationFilter.getInputPort();
    }

    public OutputPort<IMonitoringRecord> getOutputPort() {
        return this.recordMerger.getOutputPort();
    }

    public ClassNameRegistryRepository getClassNameRegistryRepository() {
        return this.classNameRegistryRepository;
    }

    public void setClassNameRegistryRepository(final ClassNameRegistryRepository classNameRegistryRepository) {
        this.classNameRegistryRepository = classNameRegistryRepository;
    }

}
