@echo off
set CLASSPATH=%CD%\bin;%CD%\lib\SuperCSV-1.52.jar

REM ------------
REM Sample projects, integrated into RGIK.

rem OK java mil.army.usace.ehlschlaeger.rgik.projects.BenningPoint
rem OK java mil.army.usace.ehlschlaeger.rgik.projects.CarsonNoise


REM ------------
REM Runnable classes, not integrated yet.

cd tests

rem OK java mil.army.usace.ehlschlaeger.rgik.AnnalsRandomSamples
rem OK java mil.army.usace.ehlschlaeger.rgik.test.AnnalsResults
rem OK java mil.army.usace.ehlschlaeger.rgik.AnnalsResults200205
rem OK java mil.army.usace.ehlschlaeger.rgik.AnnalsResults200206
rem OK java mil.army.usace.ehlschlaeger.rgik.AnnalsResults200207
rem FAIL java mil.army.usace.ehlschlaeger.rgik.AnnalsResults200207d
rem OK java mil.army.usace.ehlschlaeger.rgik.ChanceRealized
rem OK java mil.army.usace.ehlschlaeger.rgik.test.ChromosomeTest
rem OK java mil.army.usace.ehlschlaeger.rgik.ClassMorph
rem OK java mil.army.usace.ehlschlaeger.rgik.Cluster
rem OK java mil.army.usace.ehlschlaeger.rgik.CreBigDecimal
rem OK java mil.army.usace.ehlschlaeger.rgik.test.DEMData >DEMData.log
rem OK java mil.army.usace.ehlschlaeger.rgik.test.DEMData2
rem OK java mil.army.usace.ehlschlaeger.rgik.Distance
rem OK java mil.army.usace.ehlschlaeger.rgik.DistanceDecay
rem OK java mil.army.usace.ehlschlaeger.rgik.test.ElevationNoise
rem OK java mil.army.usace.ehlschlaeger.rgik.gene.GAParameters
rem OK java mil.army.usace.ehlschlaeger.rgik.gene.GeneticAlgorithm
rem OK java mil.army.usace.ehlschlaeger.rgik.core.GISClassReadRowByRow
rem OK java mil.army.usace.ehlschlaeger.rgik.core.GISClassRLE
rem OK java mil.army.usace.ehlschlaeger.rgik.GISFacet
rem OK java mil.army.usace.ehlschlaeger.rgik.core.GISio
rem OK java mil.army.usace.ehlschlaeger.rgik.core.GISLattice
rem OK java mil.army.usace.ehlschlaeger.rgik.HorizontalErrorTest
rem OK java mil.army.usace.ehlschlaeger.rgik.Hyperbola
rem OK java mil.army.usace.ehlschlaeger.rgik.IndependentRandomSamples
rem OK java mil.army.usace.ehlschlaeger.rgik.LatticeRandomField
rem OK java mil.army.usace.ehlschlaeger.rgik.LinearDecay
rem OK java mil.army.usace.ehlschlaeger.rgik.LinkList
rem OK java mil.army.usace.ehlschlaeger.rgik.List
rem OK java mil.army.usace.ehlschlaeger.rgik.Matrix
rem OK java mil.army.usace.ehlschlaeger.rgik.MWD
rem OK java mil.army.usace.ehlschlaeger.rgik.NoiseEvent
rem OK java mil.army.usace.ehlschlaeger.rgik.NoiseSet
rem OK java mil.army.usace.ehlschlaeger.rgik.NoiseSetLayered
rem OK java mil.army.usace.ehlschlaeger.rgik.NoiseSetPreLayered
rem OK java mil.army.usace.ehlschlaeger.rgik.test.RandomStartTest
rem OK java mil.army.usace.ehlschlaeger.rgik.RFtest
rem OK java mil.army.usace.ehlschlaeger.rgik.gene.RubberSheet
rem OK java mil.army.usace.ehlschlaeger.rgik.Sensor
rem OK java mil.army.usace.ehlschlaeger.rgik.SolveViaLeastSquares
rem FAIL java mil.army.usace.ehlschlaeger.rgik.SpatialDataUncertaintyModel
rem OK java mil.army.usace.ehlschlaeger.rgik.Stack
rem OK java mil.army.usace.ehlschlaeger.rgik.TestGISLatticeWatershedDelineation
rem OK java mil.army.usace.ehlschlaeger.rgik.test.TestProbMap
rem OK java -Xmx256m mil.army.usace.ehlschlaeger.rgik.TreeDouble
rem OK java -Xmx256m mil.army.usace.ehlschlaeger.rgik.test.TreeObjectTest

goto DONE


REM ------------
REM These NEED ARGS to run.

java mil.army.usace.ehlschlaeger.rgik.ClassCrop
java mil.army.usace.ehlschlaeger.rgik.ClassWobbly
java mil.army.usace.ehlschlaeger.rgik.ConfusionMatrix
java mil.army.usace.ehlschlaeger.rgik.DensoStat
java mil.army.usace.ehlschlaeger.rgik.GISClassSubset
java mil.army.usace.ehlschlaeger.rgik.GISLatticeSubset
java mil.army.usace.ehlschlaeger.rgik.ProbMap
java mil.army.usace.ehlschlaeger.rgik.ProbMapContinue
java mil.army.usace.ehlschlaeger.rgik.RandomMaps
java mil.army.usace.ehlschlaeger.rgik.SetNoData
java mil.army.usace.ehlschlaeger.rgik.ThreeByThreeFilter
java mil.army.usace.ehlschlaeger.rgik.WoodTopographicVariables

:DONE
goto DONE


REM ------------
REM GUI apps.

java mil.army.usace.ehlschlaeger.rgik.CSVTable ss01pri.csv
java mil.army.usace.ehlschlaeger.rgik.GISClass
java mil.army.usace.ehlschlaeger.rgik.NoiseGraph
java mil.army.usace.ehlschlaeger.rgik.RGISAnimatedView
java mil.army.usace.ehlschlaeger.rgik.RGISView
java mil.army.usace.ehlschlaeger.rgik.SemiVariogram
java mil.army.usace.ehlschlaeger.rgik.TableData
java mil.army.usace.ehlschlaeger.rgik.VaryingStandardDeviationErrorModel
java mil.army.usace.ehlschlaeger.rgik.WoodPlanCurvatureAnimation


REM ------------
REM Digital Populations

rem OK java -Xmx256m mil.army.usace.ehlschlaeger.digitalpopulations.ConflatePumsTracts
rem OK java -Xmx256m mil.army.usace.ehlschlaeger.digitalpopulations.ConflatePumsTractsPQTest
rem java mil.army.usace.ehlschlaeger.digitalpopulations.MakeSaTScanGridFile
rem OK java -Xmx 256m mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery
rem OK java mil.army.usace.ehlschlaeger.digitalpopulations.TestGISGridmakeSaTScanGridFile

REM Need Args

rem java mil.army.usace.ehlschlaeger.digitalpopulations.ConditionalPointClusterDetector
rem java mil.army.usace.ehlschlaeger.digitalpopulations.LandcoverPopulationDensity
rem java mil.army.usace.ehlschlaeger.digitalpopulations.PointClusterDetector

REM GUI Apps

rem java mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdsWindow
rem java mil.army.usace.ehlschlaeger.digitalpopulations.PumsPopulations


REM ------------
REM DP Projects

REM projects\RhodeIslandDigitalPopulations

rem OK java -Xmx300m ConflatePumsQueryWithTracts


:DONE
cd ..
