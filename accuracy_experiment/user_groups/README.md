# Detecting User Groups Experiment

##### **Setup**
This experiment has no input folder, as it does not need external data due to data being randomly generated during the experiment 
(except for iobserve-analysis). 
To run this experiment, run the main function of the *'./code/iobserve-analysis/experiments/.../ExperimentsMain'* class.
The source code utilized by the calls below is located in the folder 
*'./code/iobserve-analysis/experiments/src/main/java/org/iobserve/experiments/accuracy/usergroups'*.

##### **'results' folder**
Contains all data that resulted in running the experiment.
The results folder contains two sub-folders, one for each value of variance in user groups the experiment is run with.
The result files are made up of a *'ClusteringMetrics'* and a *'ClusteringResults'* file for each distribution of the 
user groups. The *'ClusteringMetrics'* files contain the calculated metrics to evaluate the quality of detecting the user
groups. Each line contains the metrics of one run of the experiment with the given distribution. The last line is
the calculated means value of all runs.

The *'ClusteringResults'* files show the actual results of the distribution assignment. Each column represents a user
group detected by iobserve-analysis. The number in the column is the amount of user sessions which have been assigned 
to the group. Each line represents one of the three user groups and their individual percentage, provided by the 
constructor call of *'ClusteringEvaluation'*. Further information about the setup is provided by the documentation for 
this experiment.
