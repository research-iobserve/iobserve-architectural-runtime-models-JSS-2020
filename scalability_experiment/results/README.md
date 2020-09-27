# Results

The .zip file **'raw results'** contains all resulting files of the experiment. The files of raw results have been 
gathered by copying the logging files of each iobserve-analysis pipeline filter. For this experiment only the logging 
files of Filters TEntryCall, TEntryCallSequence and TEntryEventSequence contain relevant information, other files can 
be disregarded.

** 'summary' folder **

**'equal_events_evaluation_r_skript'** and **'different_events_evaluation_r_skript'** each contain a R script to create
graphs showcasing the results of each respective experiment setup and filter.
The scripts read the raw results of the experiment, calculate the median and each create two PDF files (one for 
TPreprocess and one for TRuntimeUpdate).
To use the scripts, first unpack 'raw results.zip' and change the filepaths in the scripts according to the position of
the 'raw results' folder and where the PDF graph files should be created.
The modified script can than be loaded in R, or copied in a R console.

The four pdf files are the summary graphics, already created using the two R scripts.
