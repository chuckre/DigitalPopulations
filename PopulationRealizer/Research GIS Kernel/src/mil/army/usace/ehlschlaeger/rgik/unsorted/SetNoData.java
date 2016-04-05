package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;

/**
 * Simple command-line tool to run {@link GISClass.setCellValueToNoData(int)}
 * on a map file.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class SetNoData  {
	public SetNoData() {
	}

	public static void main( String argv[]) throws IOException {
		if( argv.length < 3) {
            System.out.println( "SetNoData: unset all cells with given value.");
            System.out.println( "Usage:");
			System.out.println( "  java SetNoData inputMap outputMap cellValue");
			System.exit( -1);
		}
		String iMap = argv[ 0];
		String oMap = argv[ 1];
		int c = (new Integer( argv[2].trim())).intValue();
		GISClass in_map = GISClass.loadEsriAscii( iMap);
		in_map.setCellValueToNoData( c);
		in_map.writeAsciiEsri( oMap);
	}
}
