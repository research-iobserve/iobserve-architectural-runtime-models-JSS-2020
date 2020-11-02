# Setup
##### **'pcm'**
Contains all PCM models used to represent the system for the experiment.

##### **'experiment input'**
Contains the input files for each experiment run of iobserve-analysis. The files have been created randomly for each 
different input size.

##### **'warum up'**
Contains some input files that are used to warmup the JVM.

## start_10_times_100000.sh:
The script can be used to execute the full experiment automatically on a Linux based machine.

##### **How to use:**
Paths should be altered according to the location of the script, iobserve distribution and used operating system.
	
A distribution of iobserve has to be build, extracted and the call in the script (under comment '# call iobserve') 
altered accordingly.
This can be done by calling the gradle wrapper './gradlew build install' located in the base folder of 
*'./code/iobserve-analysis/'*.
The distribution is created in *'./code/iobserve-analysis/analysis-cli/build/distributions/'*.
If errors exist while building, calling './code/iobserve-analysis/gradlew clean' can help. 
Possible build errors in 'rac_creator' can be ignored, as long as 'analysis' and 'analysis-cli' build without errors.

The PCM models and monitoring data used, are located in folder 'pcm' or 'testdata'.
There is monitoring data for experiments *'equal_events_X_users'* *'X_different_events_one_user'*, with X increasing each 
by the factor of 10, starting at 1, ending at 100000.
Folder 'warmup' contains three small runs of iobserve, which are executed before each run of the actual analysis, to 
warm up the jvm. Warmup can be commented in in the *'AnalysisMain.java'* class in folder *'./iobserve-analysis/analysis-cli/...'* and re-running the gradle wrapper.

*'logging_path'* of the main function of the script holds the path where the experiment results are saved.
A new folder is created for each run of the expermient, stating the timestamp of the start of the script.
Inside a new folder for each experiment (*'equal_events_X_users', 'X_different_events_one_user'*) is created.
Results of each experiment run are saved in an own folder, named with increasing numbers.
The execution time of each iteration of an iobserve filter is measured and saved in a .csv file named according to the 
filter.

## generate_experiment_input.sh:
A script that can be used to generate new input files to run the '**start_10_times_100000.sh**' script with.
The '**kieker.map**' file is needed for each setup and is copied to each of the newly generated setup folders.

Paths should be altered according to the location of the script, *'kieker.map'* file and used operating system.

This script takes a *lot of time* to generate new randomized input files and should only be run if necessary.