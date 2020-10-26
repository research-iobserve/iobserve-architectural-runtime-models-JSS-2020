package org.iobserve.experiments;

import org.iobserve.experiments.accuracy.userbehavior.UserBehaviorAccuracyExperiment;
import org.iobserve.experiments.accuracy.usergroups.UserGroupDetectionAccuracyExperiment;

/**
 * Main class for starting the user behavior and user group accuracy experiments of iobserve-analysis.
 * 
 * @author Nicolas Boltz
 * @author Robert Heinrich
 */
public class ExperimentsMain {

	/**
	 * Main function
	 * 
	 * @param args
	 * 			command line arguments.
	 */
	public static void main(String[] args) {
		// Code for running accuracy tests for TEntryEventSequence
    	UserBehaviorAccuracyExperiment usagebehaviorTest = new UserBehaviorAccuracyExperiment();
    	usagebehaviorTest.startExperiment();
    	usagebehaviorTest.startRMEExperiment();
    	
    	// Clustering experiment
    	UserGroupDetectionAccuracyExperiment clusteringEval = new UserGroupDetectionAccuracyExperiment(4000, 2000, 2000, 0, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new UserGroupDetectionAccuracyExperiment(2000, 4000, 2000, 0, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new UserGroupDetectionAccuracyExperiment(2000, 2000, 4000, 0, 100);
    	clusteringEval.evaluateTheClustering();
    	
    	clusteringEval = new UserGroupDetectionAccuracyExperiment(4000, 2000, 2000, 10, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new UserGroupDetectionAccuracyExperiment(2000, 4000, 2000, 10, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new UserGroupDetectionAccuracyExperiment(2000, 2000, 4000, 10, 100);
    	clusteringEval.evaluateTheClustering();
	}

}
