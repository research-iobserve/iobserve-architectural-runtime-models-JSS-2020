# Detecting User Groups Experiment

**setup**
As this experiment does not need external data or models (excluding the iobserve-analysis branch 
'scalability-usability-1'). 
To run this experiment, run the code below. If error occur try substituting the analysis call in the main-method of 
'./iobserve-analysis/analysis-cli/.../AnalysisMain' with the aforementioned calls. The source code utilized by the 
calls below is located in the folder 
'./iobserve-analysis/analysis/src/main/java/org/iobserve/analysis/userbehavior/test'.

```java
	// variance in user groups = 0
	ClusteringEvaluation clusteringEval = new ClusteringEvaluation(4000, 2000, 2000, 0, 100);
    clusteringEval.evaluateTheClustering();
	clusteringEval = new ClusteringEvaluation(2000, 4000, 2000, 0, 100);
    clusteringEval.evaluateTheClustering();
    clusteringEval = new ClusteringEvaluation(2000, 2000, 4000, 0, 100);
    clusteringEval.evaluateTheClustering();
    
	// variance in user groups = 10
    clusteringEval = new ClusteringEvaluation(4000, 2000, 2000, 10, 100);
    clusteringEval.evaluateTheClustering();
    clusteringEval = new ClusteringEvaluation(2000, 4000, 2000, 10, 100);
    clusteringEval.evaluateTheClustering();
    clusteringEval = new ClusteringEvaluation(2000, 2000, 4000, 10, 100);
    clusteringEval.evaluateTheClustering();
```

**'results' folder**
Contains all data that resulted in running the experiment.
The results folder contains two sub-folders, one for each value of variance in user groups the experiment is run with.
The result files are made up of a 'ClusteringMetrics' and a 'ClusteringResults' file for each distribution of the 
user groups. The 'ClusteringMetrics' files contain the calculated metrics to evaluate the quality of detecting the user
groups. Each line contains the metrics of one run of the experiment with the given distribution. The last line is
the calculated means value of all runs.

The 'ClusteringResults' files show the actual results of the distribution assignment. Each column represents a user
group detected by iobserve-analysis. The number in the column is the amount of user sessions which have been assigned 
to the group. Each line represents one of the three user groups and their individual percentage, provided by the 
constructor call of 'ClusteringEvaluation'. Further information about the setup is provided by the documentation for 
this experiment.