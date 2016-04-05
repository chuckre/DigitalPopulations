package mil.army.usace.ehlschlaeger.rgik.gui;

import java.io.IOException;

import javax.swing.JFileChooser;

import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;



/**
 * this class generates SaTScan "grid" files. Given a GISGrid or ESRI ASCII
 * Grid, MakeSaTScanGridFile will make the grid file.
 */
public class MakeSaTScanGridFile { 
	public MakeSaTScanGridFile() throws IOException {
		int returnVal = JFileChooser.APPROVE_OPTION + 1;
		JFileChooser chooser = null;
		while( returnVal != JFileChooser.APPROVE_OPTION) { 
			chooser = new JFileChooser();
			chooser.setFileSelectionMode( JFileChooser.FILES_ONLY);
			chooser.setDialogTitle( "GISGrid File Chooser");
			chooser.setMultiSelectionEnabled( false);
			FileExtensionChooser filter = new FileExtensionChooser();
			filter.addExtension( "asc");
			filter.setDescription( "ESRI ASCII Grid File");
			chooser.setFileFilter( filter);
			returnVal = chooser.showOpenDialog( null);
		}
		GISLattice g = GISLattice.loadEsriAscii( chooser.getSelectedFile());
		GISGrid gg = (GISGrid) g;
		gg.writeSaTScanGridFile( chooser.getSelectedFile().getName());
	}

	public static void main( String argv[]) throws IOException {
		new MakeSaTScanGridFile();
		System.exit( 0);
	}
}