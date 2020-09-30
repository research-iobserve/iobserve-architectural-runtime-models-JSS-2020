package org.iobserve.experiments.cli;

import org.iobserve.analysis.userbehavior.test.ClusteringEvaluation;
import org.iobserve.analysis.userbehavior.test.TEntryEventSequenceTest;

/**
 * Main class for starting the accuracy experiments of iobserve-analysis.
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
		
		// 
    	TEntryEventSequenceTest usagebehaviorTest = new TEntryEventSequenceTest();
    	usagebehaviorTest.startTests();
    	usagebehaviorTest.startRMETest();
    	
    	// Clustering experiment
    	ClusteringEvaluation clusteringEval = new ClusteringEvaluation(4000, 2000, 2000, 0, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new ClusteringEvaluation(2000, 4000, 2000, 0, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new ClusteringEvaluation(2000, 2000, 4000, 0, 100);
    	clusteringEval.evaluateTheClustering();
    	
    	clusteringEval = new ClusteringEvaluation(4000, 2000, 2000, 10, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new ClusteringEvaluation(2000, 4000, 2000, 10, 100);
    	clusteringEval.evaluateTheClustering();
    	clusteringEval = new ClusteringEvaluation(2000, 2000, 4000, 10, 100);
    	clusteringEval.evaluateTheClustering();
	}

}
