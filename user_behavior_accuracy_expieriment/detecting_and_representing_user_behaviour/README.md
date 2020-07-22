# Detecting and Representing User Behaviour Experiment

The folder **'input'** contains the files for the **pcm** folder of iobserve-analysis. 
To run this experiment, run the code below. If error occur try substituting the analysis call in the 
AnalysisMain.main(...) method with the aforementioned calls.

```java
	TEntryEventSequenceTest usagebehaviorTest = new TEntryEventSequenceTest();
	usagebehaviorTest.startTests();
	usagebehaviorTest.startRMETest();
```

**Paths in TEntryEventSequenceTest have be altered according to the location of the input folder and desired result output folder.**


The folder **'results'** contains the raw result files of one run of the experiment, that have been used to create the documentation.