# PACOM
PACOM tool set provides software to generate metric maps and indicator maps for the PACOM framework using ratio maps (i.e. the output of digital population kernel analysis).

#Software instruction
http://digitalpopulations.pbworks.com/w/page/110164099/PACOM

#What does it do?
  * Generate PACOM metric maps for each realization by combining ratio maps of the same attributes
  * Generate PACOM indicator maps for each realization by combining metric maps; spatial associated random error is added in this process
  * Generate summary statistics for metric maps and indicator maps of all realizations

#Prerequisites
  * GDAL (for more information: http://www.gdal.org/)
  * Spatial Random field generator (included in src): http://digitalpopulations.pbworks.com/w/page/110082820/SpatialRandomFieldGenerator
  * Density Map Raster Calculation tool as part of the kernel analysis utility tools (included in src): http://digitalpopulations.pbworks.com/w/page/96200604/KernelAnalysisUtilityTools

#Compile and run
metric.sh and indicator.sh are bash scripts that can be run directly without compiling. <br>
However, two supplementary tools: Spatial Random Field Generator and Density Map Raster Calculation tool needs to be compiled. To compile these two tools, go to the src directory and run make. 
To run the metric tool
metric.sh OutputFileName MinRzn MaxRzn InputRatioMap1 MinWeight1 MaxWeight1 InputRatioMap2 MinWeight2 MaxWeight2 .... 
Input arguments
 1. OutputFieldName: the output GeoTiff files for realizations
  i. the files will be named like:  OutputFileName_rzn###.tif, OutputFileName_mean.tif, OutputFileName_sd.tif and etc.
 2. MinRzn: the index of the first realization to be analyze
 3. MaxRzn: the index of the last realization to be analyzed
 4. InputRatioMap*N* MinWeight*N* MaxWeight*N*: the file name and the weight range of each input ratio map
  i. There can be as many as 26 input ratio maps. Each input map must be followed by the minimum and maximum weight of that map
  ii. The input ratio maps are named such as InputRatio1_rzn###.tif
  iii. The MinWeight and MaxWeight must be between 0.0 to 1.0; the actual weight will be uniformly sampled between this range

To run the indicator tool
indicator.sh OutputFileName MinRzn MaxRzn D_RF E_RF F_RF MinWeight_RF MaxWeight_RF InputMetric1 MinWeight1 MaxWeight1 InputMetric2 MinWeight2 MaxWeight2 ....
Input arguments
 1. OutputFieldName: the output GeoTiff files for realizations
  i. the files will be named like:  OutputFileName_rzn###.tif, OutputFileName_mean.tif, OutputFileName_sd.tif and etc.
 2. MinRzn: the index of the first realization to be analyzed
 3. MaxRzn: the index of the last realization to be analyzed
 4. D_RF E_RF F_RF: the D, E and F of the filtering function to generate the spatial random field. More details of them are in http://digitalpopulations.pbworks.com/w/page/110082820/SpatialRandomFieldGenerator
 5. InputMetric1 MinWeight1 MaxWeight1: the file name and the weight range of each ratio map
  i. There can be as many as 26 input metric maps. Each input map must be followed by the minimum and maximum weight of that map
  ii. The input ratio maps are named such as InputMetric1_rzn###.tif
  iii. The MinWeight and MaxWeight must be between 0.0 to 1.0; the actual weight will be uniformly sampled between this range
