package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.util.HashSet;
import java.util.Set;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;


/**
 * Helpers for working with GeoTools coordinate referencing.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @see <A HREF="http://docs.codehaus.org/display/GEOTDOC/04+What+Axis+is+X">What Axis is X</A>
 * 
 * @author William R. Zwicky
 */
public class GTRefUtil {
    private static HashSet<AxisDirection> TOWARD_RIGHT;
    private static HashSet<AxisDirection> TOWARD_UP;

    static {
        TOWARD_RIGHT = new HashSet<AxisDirection>();
        TOWARD_RIGHT.add( AxisDirection.DISPLAY_LEFT );
        TOWARD_RIGHT.add( AxisDirection.EAST );
        TOWARD_RIGHT.add( AxisDirection.GEOCENTRIC_X );
        TOWARD_RIGHT.add( AxisDirection.COLUMN_POSITIVE );
        
        TOWARD_UP = new HashSet<AxisDirection>();
        TOWARD_UP.add( AxisDirection.DISPLAY_UP );
        TOWARD_UP.add( AxisDirection.NORTH );
        TOWARD_UP.add( AxisDirection.GEOCENTRIC_Y );
        TOWARD_UP.add( AxisDirection.ROW_POSITIVE );
    }

    /**
     * Try to find a positive horizontal or east axis in a CRS.
     * 
     * @param crs
     * @return
     */
    public static int indexOfPosX( CoordinateReferenceSystem crs ){
        return indexOf( crs, TOWARD_RIGHT );
    }
    
    /**
     * Try to find a positive vertical or north axis in a CRS.
     * 
     * @param crs
     * @return
     */
    public static int indexOfPosY( CoordinateReferenceSystem crs ){
        return indexOf( crs, TOWARD_UP );
    }

    private static int indexOf( CoordinateReferenceSystem crs, Set<AxisDirection> direction ){
        CoordinateSystem cs = crs.getCoordinateSystem();
        for( int index=0; index<cs.getDimension(); index++){
            CoordinateSystemAxis axis = cs.getAxis(index);
            if( direction.contains( axis.getDirection() ) )
                    return index;
        }   
        return -1;
     }    
}
