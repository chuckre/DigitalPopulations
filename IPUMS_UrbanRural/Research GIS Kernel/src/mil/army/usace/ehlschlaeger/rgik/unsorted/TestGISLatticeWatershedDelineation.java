package mil.army.usace.ehlschlaeger.rgik.unsorted;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.WoodTopographicVariables;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;

// java -mx1024m TestGISLatticeWatershedDelineation > TestGISLatticeWatershedDelineation.txt

public class TestGISLatticeWatershedDelineation extends RGIS { 
	public TestGISLatticeWatershedDelineation() throws IOException {
		super();
		Date d = new Date();
		System.out.println( "GISLattice.main() pre new GISLattice() " + d.toString());
		//GISLattice t = new GISLattice( "nw_dem");
		//GISLattice t = new GISLattice( "countydem");
		GISLattice t = GISLattice.loadEsriAscii("testing_dem");
		Random rng = new Random( (long) 1);
		for( int r = 0; r < t.getNumberRows(); r++) {
			for( int c = 0; c < t.getNumberColumns(); c++) {
				if( t.isNoData( r, c) == false) {
					t.setCellValue( r, c, t.getCellValue( r, c) + rng.nextDouble() - 0.5);
				}
			}
		}
		d = new Date();
		System.out.println( "GISLattice.main() pre flowMap() " + d.toString());
		BooleanGrid truePits = null;

		//*
		WatershedDelineation f = new WatershedDelineation(t);
		GISClass flow = f.flowMap( truePits);
		d = new Date();
		System.out.println( "GISLattice.main() pre writeAsciiEsri() " + d.toString());
		//flow.writeAsciiEsri( "flow");
		flow.writeAsciiEsri( "testing_flow");
		//*/

		// OR

		/*
		GISClass flow = new GISClass( "flow");
		*/

		d = new Date();
		System.out.println( "GISLattice.main() pre slope calculations " + d.toString());
		double maxTres = Math.max( t.getEWResolution(), t.getNSResolution());
		double weightExp = 0.001;
		WoodTopographicVariables wtv = new WoodTopographicVariables( t, maxTres * 1.5, weightExp);
		GISLattice slopeT = wtv.getSlopeMap();
		d = new Date();
		System.out.println( "GISLattice.main() pre writeAsciiEsri() " + d.toString());
		//slopeT.writeAsciiEsri( "slope");
		slopeT.writeAsciiEsri( "testing_slope");

		t = null;
		wtv = null;

		//*
		d = new Date();
		System.out.println( "GISLattice.main() pre accumulateDownhill() " + d.toString());
		Rivers riverTool = new Rivers(flow);
		GISLattice accumMap = riverTool.accumulateDownhill();
		d = new Date();
		System.out.println( "GISLattice.main() pre writeAsciiEsri() " + d.toString());
		//accumMap.writeAsciiEsri( "accumMap");
		accumMap.writeAsciiEsri( "testing_accumMap");
		//*/

		//*
		d = new Date();
		System.out.println( "GISLattice.main() pre rivers() " + d.toString());
		GISClass riverMap = riverTool.rivers( accumMap, 500000.0);
		d = new Date();
		System.out.println( "GISLattice.main() pre writeAsciiEsri() " + d.toString());
		riverMap.writeAsciiEsri( "testing_riverMap");
		//*/

		d = new Date();
		System.out.println( "GISLattice.main() pre accumulateUphill() " + d.toString());
		GISLattice accumUpMap = riverTool.accumulateUphill( slopeT, riverMap);
		d = new Date();
		System.out.println( "GISLattice.main() pre writeAsciiEsri() " + d.toString());
		//accumUpMap.writeAsciiEsri( "accumUpMap");
		accumUpMap.writeAsciiEsri( "testing_accumUpMap");


		d = new Date();
		System.out.println( "GISLattice.main() pre basins() " + d.toString());
		GISClass terminalBasinMap = new GISClass( flow);
		GISClass basinMap = riverTool.basins( riverMap, terminalBasinMap);
		d = new Date();
		System.out.println( "GISLattice.main() pre writeAsciiEsri() " + d.toString());
		//basinMap.writeAsciiEsri( "basinMap");
		basinMap.writeAsciiEsri( "testing_basinMap");
		terminalBasinMap.writeAsciiEsri( "testing_terminalBasinMap");

		d = new Date();
		System.out.println( "GISLattice.main() done " + d.toString());
	}

	public static void main( String argv[]) throws IOException {
		new TestGISLatticeWatershedDelineation();
		System.exit( 0);
	}
}