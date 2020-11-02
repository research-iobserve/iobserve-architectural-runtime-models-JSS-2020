# Deployment Accuracy Experiment

The experiment checks if deployment and undeployment events in iobserve-analysis work according to their specification.
The experiment is made up of different sub-experiments, which each executes a certain part of the deployment or undeployment process.
These sub-experiments can be combined to simulate different deployment and undeployment use cases.

##### **Setup**
To run these experiments first follow the instructions on 'Setting up iObserve to replicate the experiments' in 
*'../../README.md'*.
Run iobserve-analysis like described, using the contents of the input folder.
The source code of iobserve-analysis is located in the *'./code/iobserve-analysis/'* folder.

##### **'input' folder**
The **'/input/pcm'** folder contains the palladio models used to run iobserve-analysis for this experiment.
The **'/input/experiments'** folder contains a kieker log folder for each sub-experiment which contains the kieker files,
that should be used for input when running a sub-experiment in iobserve-analysis.

##### **'result' folder**
This folder contains a simplified overview of whether each sub-experiment was run successfully and showed 
the expected results which are described below.

## Sub-Experiment setup
**Events:** 
                                            d1...d5 = Deployment events for node 1 to 5
                                            ud1...ud5 = Undeployment events for node 1 to 5
		
**Recommended order of execution/Use cases:**
                                           1. allocation -> replication -> dereplication -> deallocation
										   2. allocation -> migration-A -> dereplication -> deallocation
										   3. allocation -> migration-B -> dereplication -> deallocation
_______________________________________________________________________________________________________________________
- ##### **allocation: d1,d2,d3,d4,d5**
  Deploys the org.cocome.tradingsystem.inventory.application.Store to each of the resource containers "node-X" (X=1..5).

  *Assumes a model without resource containers.*
  
  **Expected result:** Newly created resource containers with names "node-X" (X=1..5),
  each with org.cocome.tradingsystem.inventory.application.Store deployed.
  New corresponding assembly contexts in system and allocation model.
_______________________________________________________________________________________________________________________
- ##### **replication: d1,d2,d3,d4,d5**
  Deploys the org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice to the existing resource containers "node-X" (X=1..5).
  
  *Assumes model with resource containers "node-X" (X=1..5).*
  
  **Expected result:** Each resource container "node-X" (X=1..5) has org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice
  deployed on it. New corresponding assembly contexts in system and allocation model.
_______________________________________________________________________________________________________________________
- ##### **dereplication: ud5,ud4,ud3,ud2,ud1**
  Undeploys org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice from the existing resource containers.
  
  *Assumes a model which already contains components from allocation and replication experiment.*
  
  **Expected result:** Each resource container "node-X" (X=1..5) does not have 
  org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice deployed on it anymore. Corresponding assembly contexts
  in system and allocation model removed.
_______________________________________________________________________________________________________________________
- ##### **deallocation: ud5,ud4,ud3,ud2,ud1**
  Undeploys the org.cocome.tradingsystem.inventory.application.Store from each of the resource containers "node-X" (X=1..5).
  
  *Assumes a model with resource containers named "node-X", X=1..5, with only org.cocome.tradingsystem.inventory.application.Store deployed.*
  
  **Expected result:** No resource container in resource environment. Corresponding assembly contexts in system and allocation model removed.
_______________________________________________________________________________________________________________________
- ##### **migration-A: d1,ud1,d2,ud2,d3,ud3,d4,ud4,d5**
  Migration of org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice with short time of undeployment. 
  New resource containers are not created or removed from resource environment.
  
  *Assumes a model which already contains components from allocation experiment.*
  
  **Expected result:** org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice is deployed on resource container "node-5".
  New corresponding assembly context in system and allocation model.
_______________________________________________________________________________________________________________________
- ##### **migration-B: d1,d2,ud1,d3,ud2,d4,ud3,d5,ud4**
  Overlapping migration of org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice. 
  New resource containers are not created or removed from resource environment.
  
  *Assumes a model which already contains components from allocation experiment.*
  
  **Expected result:** org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice is deployed on resource container "node-5".
  New corresponding assembly context in system and allocation model.
_______________________________________________________________________________________________________________________
 
##### **Additional information** to kieker and pcm data mapping.
  rac/kieker: org.cocome.tradingsystem.inventory.application.store.StoreServer --> pcm: org.cocome.tradingsystem.inventory.application.Store
  rac/kieker: org.cocome.cloud.logic.webservice.cashdeskline.cashdesk.CashDesk --> pcm: org.cocome.cloud.logic.webservice.cashdeskline.cashdeskservice  