package mil.army.usace.ehlschlaeger.rgik.test;

import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;

public class TestGISGridmakeSaTScanGridFile { 
	public TestGISGridmakeSaTScanGridFile() throws IOException {
		GISGrid g = new GISGrid( 100., 200., 10., 10., 10, 10);
		g.writeSaTScanGridFile( "testGISGridmakeSaTScanGridFileOutput");
	}

	public static void main( String argv[]) throws IOException {
		new TestGISGridmakeSaTScanGridFile();
		System.exit( 0);
	}
}