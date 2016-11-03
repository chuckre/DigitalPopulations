
#Digital Populations

<!--[Lateset stable version](https://github.com/chuckre/DigitalPopulations/tree/603ae017b4f25774415b38e3fbf8427fb090ece8)-->

The compiled jar is at [PopulationRealizer/out/artifacts/Research_GIS_Kernel_jar](https://github.com/chuckre/DigitalPopulations/tree/master/PopulationRealizer/out/artifacts/Research_GIS_Kernel_jar)

##Download and run

1. Install Java SDK >=1.7
2. Download the pre-compiled jar at [PopulationRealizer/out/artifacts/Research_GIS_Kernel_jar](https://github.com/chuckre/DigitalPopulations/tree/master/PopulationRealizer/out/artifacts/Research_GIS_Kernel_jar)
3. Open command line terminal 
4. The jar is a standalone environment which can be renamed and moved around, to run Digigtal Populations with the jar:
```
    java -jar "path_to_the_jar" -c "path_to_the_last_run_properties"
```    
##Build software development environment

1. Register a Github account
1. Contact [Dr. Charles Ehlschlaeger](https://github.com/chuckre) to get permission
1. Install Java SDK >=1.7
2. Install [Ant](http://ant.apache.org/)
3. Install Git
4. Open command line terminal
5. cd to a directory you want to store the DigitalPopulations repo
6. run commands sequence
    ```
    git clone https://github.com/chuckre/DigitalPopulations
    cd DigitalPopulations
    ant artifact.research_gis_kernel:jar -buildfile PopulationRealizer/populationrealizer.xml
    ```

7. The compiled jar will be generated at ```./PopulationRealizer/__artifacts_temp/Research GIS Kernel.jar```
8. The jar is a standalone environment which can be renamed and moved around, to run Digigtal Populations with the jar:
    ```
    java -jar "path_to_the_jar" -c "path_to_the_last_run_properties"
    ```    
    
##What is Digital Populations?
 
Digital Populations is a software suite for synthesizing plausible geo-referenced households and people from census or polling data.
 
Digital Populations starts with statistics and partial data from an actual census, and generates a complete census that resembles the input data.  The generated census contains locations and statistics for every household and individual even though the input data contains no location data and only partial statistics.
 
The original purpose of Digital Populations was to facilitate the detection of statistical clusters.  Cluster detection requires a complete population data set, however most governments don't release that data for privacy reasons.  Digital Populations can generate configurations of households that can be used for cluster detection. More recently, Digital Populations is being used to to provide rich contextual information of households and people for agent-based social-cultural behavior models. The latest research makes it possible to map household and person surveys into neighborhood-scale maps that include the representation of errors and uncertainties as part of the map sets.
 
Digital population toolkit contains a list of components:
- Census Generation Tool generates simulated survey data
- Relationship File GUI provides user interface to generate the formatted input parameter files for Census Generation Tool
- Dp2Kml and Csv2Kml tools converts digital population results and CSV files to KML format which can be viewed in Google Earth
- Cluster Detection scan for clusters of related people around certain events
- KernelAnalysis develop maps of variable proportions using kernel functions
- DigPop Realization Subset Selection tool and Density Map Raster Calculation tool are the utility tools for Digital Population Kernel Analysis
 
##Documentation:

[Introduction and Tutorial](http://digitalpopulations.pbworks.com/Digital-Populations-Tutorial)

[The Relationship File GUI](http://digitalpopulations.pbworks.com/The-Relationship-File-GUI)

[Running Digital Populations](http://digitalpopulations.pbworks.com/Running-Digital-Populations)

[Data File Reference](http://digitalpopulations.pbworks.com/Data-File-Reference)

[Glossary](http://digitalpopulations.pbworks.com/Glossary)

[FAQ](http://digitalpopulations.pbworks.com/FAQ)
 
[Phases of the CensusGen Algorithm]()

[Dp2Kml](http://digitalpopulations.pbworks.com/w/page/Dp2Kml-Tool): Viewing the results from Digital Populations in Google Earth

[Csv2Kml](http://digitalpopulations.pbworks.com/w/page/Csv2Kml-Tool): Viewing general CSV files in Google Earth

[CsvPlusMap](http://digitalpopulations.pbworks.com/w/page/CsvPlusMap-Tool):  Append values from a map to a table as a new column

[Cluster Detection](http://digitalpopulations.pbworks.com/ClusterDetection): Scan for clusters of related people around certain events.

[KernelAnalysis](http://digitalpopulations.pbworks.com/KernelAnalysis): Develop maps of variable proportions using kernel functions.
 
Part of this research was directly or indirectly supported by the following organizations:
City University of New York Graduate College
Hunter College
Western Illinois University
Engineer Research Development Center 
