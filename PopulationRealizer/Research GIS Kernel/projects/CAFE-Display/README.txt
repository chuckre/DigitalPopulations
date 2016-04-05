"CAFE Display" is a stand-alone version of Csv2Kml for the generation of Google Earth presentations of CAFE survey answers.  The survey is an Excel file shipped with this project.

NOTE that this app is written in Java, so you must have a recent version of Java installed to use it.  Visit the vendor for directions and the latest version:
  http://www.java.com/


To use this system:
  1. Open the spreadsheet.
  2. Answer the questions to the best of your ability.
  3. Save the spreadsheet.
  4. Switch to the "toKMZ" tab in the spreadsheet, and export it as a comma-seperated value (CSV) file.
     In OpenOffice 3.2:
       Use default name (same name as xlsx file)
       Change path so file is saved to "outputs" directory
       Save as type: "Text CSV"
       Field delimiter: ,
       Text delimiter: "
       X Save cell content as shown
  5. Drag the CSV file onto GenerateKML.bat.
  6. When the process completes, press a key to close the window, then open the new KMZ file to view the results.
  7. Optionally drag the CSV file onto TestTemplate.bat to preview the description balloons in your browser.  This is generally only useful for testing modifications to the templates in the "software" directory.


Notes:
 * "KML" and "KMZ" are functionally identical; they are simply different ways of wrapping up a Google Earth data set.
 * A "KML" file is a single text file containing a set of placemarks and associated styling info.  A "KMZ" is actually a zip file that can contain multiple KML files, icons, extra styling files, and other data as necessary to describe the placemarks.
