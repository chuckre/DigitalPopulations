package mil.army.usace.ehlschlaeger.rgik.unsorted;
import java.awt.Frame;
import java.io.IOException;

import mil.army.usace.ehlschlaeger.rgik.core.GISData;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.WoodTopographicVariables;
import mil.army.usace.ehlschlaeger.rgik.gui.AnimationFrame;
import mil.army.usace.ehlschlaeger.rgik.gui.RGISAnimatedView;
/**
 *  Copyright Charles R. Ehlschlaeger,
 *  work: 309-298-1841, fax: 309-298-3003,
 *	<http://faculty.wiu.edu/CR-Ehlschlaeger2/>
 *  This software is freely usable for research and educational purposes. Contact C. R. Ehlschlaeger
 *  for permission for other purposes.
 *  Use of this software requires appropriate citation in all published and unpublished documentation.
 */
public class WoodPlanCurvatureAnimation {
	public WoodPlanCurvatureAnimation() throws IOException {
		int doRealizations = 4;
		/*
		GISLattice t2 = new GISLattice( t.getWestEdge() + t.getEWResolution(), 
			t.getNorthEdge() - t.getNSResolution(), t.getEWResolution(),
			t.getNSResolution(), t.getNumberRows() - 2, t.getNumberColumns() - 2);
		for( int r = 0; r < t2.getNumberRows() - 1; r++) {
			for( int c = 0; c < t2.getNumberColumns() - 1; c++) {
				t2.setCellValue( r, c, t.getCellValue( r + 1, c + 1));
			}
		}
		t = t2;
		*/

		GISLattice dem = GISLattice.loadEsriAscii("TestDEM");
		WoodTopographicVariables wtv = new WoodTopographicVariables( dem, 100.0, 1.0);
		GISLattice s = wtv.getSlopeMap();
		s.writeAsciiEsri( "TestDEMslope100");
		GISLattice a = wtv.getAspectMap();
		a.writeAsciiEsri( "TestDEMaspect100");
		GISLattice pr = wtv.getProfileCurvatureMap();
		pr.writeAsciiEsri( "TestDEMproCurve100");
		GISLattice pl = wtv.getPlanCurvatureMap();
		pl.writeAsciiEsri( "TestDEMplanCurve100");

		GISLattice[] realizations = new GISLattice[ doRealizations];
		for( int i = 0; i < doRealizations; i++) {
			/*
			GISLattice a = new GISLattice( "TestAllFour" + i);
			GISLattice b = new GISLattice( a.getWestEdge() + a.getEWResolution(), 
				a.getNorthEdge() - a.getNSResolution(), a.getEWResolution(),
				a.getNSResolution(), a.getNumberRows() - 2, a.getNumberColumns() - 2);
			*/
			wtv = new WoodTopographicVariables( dem, 45.1 + i * 15.0, 1.0);
			realizations[ i] = wtv.getPlanCurvatureMap();
			realizations[ i].writeAsciiEsri( "TestDEMplan" + (45.0 + i * 15.0));
		}
		pl.setRealizations( realizations);
		pl.setContourInterval( .01);
		pl.setContourIndexRate( 5);
		pl.setName( "Plan Curvature");
		GISData gisObject[] = new GISData[ 1];
		gisObject[ 0] = (GISData) pl;
		RGISAnimatedView view = new RGISAnimatedView( 400, 560, 4, doRealizations, 3.0);
		view.setData( gisObject);
		//view.setSize( 1000, 1000);
		Frame f = new AnimationFrame( view, 400, 560, false);
		f.setVisible( true);
	}

	public static void main( String[] args) throws IOException {
		new WoodPlanCurvatureAnimation();
	}
}