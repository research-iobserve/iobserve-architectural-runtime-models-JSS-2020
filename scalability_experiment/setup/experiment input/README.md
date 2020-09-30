# Experiment input

A explanation of the experiment setups and model configurations is provided in the experiment documentation 
(../../documentation/usage_modelling_scalability.pdf).

The files in the log directories consist of exactly one .map and at least one .dat files.

The .map file maps iObserve event types to tokens, that are used in the .dat files.

The .dat files contain a chronological collection of events, the monitoring data. Every line represents an 
single event, starting with a token that specifies the event type and containing different data depending on 
the event type. Most events reference a function of an object or an object itself, but since the object is only
specified by name/path the .rac file in the pcm folder is needed for mapping.