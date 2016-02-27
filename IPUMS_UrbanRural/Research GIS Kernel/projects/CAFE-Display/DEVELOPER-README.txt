"CAFE Display" is a stand-alone version of Csv2Kml for the generation of Google Earth presentations of CAFE survey answers.  The survey is an Excel file shipped with this project.  The user is expected to answer the questions, then export a specially designed sheet within the file into a CSV file.  This software then generates a KMZ file from the CSV data.

The description balloons are auto-generated from metadata enclosed in the CSV file.  The CSV "standard" does not support metadata; we have devised our own syntax to distinguish it from regular data.  Full documentation is in AutoBubble.html.

Note that the JAR file generated for this distributable is in fact the full Digital Populations system.  The "java -jar csv2kml.jar" syntax runs Csv2Kml, but "java -cp csv2kml.jar" can be used to run any of the programs in the system.


To build a distributable:
 * Run ant task "cafe" to build dist/CAFE-Display.
 * Copy a suitable xlsx file into CAFE-Display.
 * Zip and distribute.


To build without Ant:
 * Export standalone jar:
   * Right-click Research GIS Kernal, select Export.
   * Select Java/Runnable JAR file, click Next.
   * Under Launch configuration, select any Csv2Kml entry.
   * Under "Export destination", use name "csv2kml.jar" in a suitable dir, i.e.
     RGIK/dist/CAFE-Display/software/csv2kml.jar
   * Select option "Package required libraries into generated JAR".
   * Click Finish.
 * Copy support files from this directory to form this tree:
   README.txt
   GenerateKML.bat
   TestTemplate.bat
   software/AutoBubble.html
   software/CAFEStyle.html
   software/CollapsingSections.vm
   software/csv2kml.jar
 * Copy a suitable xlsx file into CAFE-Display.
 * Zip and distribute.
