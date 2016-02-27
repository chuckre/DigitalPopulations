package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;



/**
 * Wrapper for the &lt;popdensity&gt; element. Allows user to specify population
 * density directly, rather than letting system calculate it automatically.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class PopDensity {
    protected String map;
    protected String landuse;

    public PopDensity() {
    }

    @XmlAttribute(name = "map")
    public String getMap() {
        return map;
    }

    /**
     * Specify density using a map file. Must be ESRI ASCII formatted, and cells
     * must line up with land-use and region maps. Each cell specifies a
     * relative density, so a cell containing "6.4" will receive double the
     * households placed in a cell with "3.2".
     * <P>
     * If censusgen builds a density map automatically, it will write the map to
     * "LandcoverPopulationDensity-map.asc", and this file can be re-submitted
     * here.
     * <P>
     * Only one of setMap and setLanduse can be called, since the other would be
     * redundant.
     * 
     * @param mapFile
     *            path and name of density map to load and use
     */
    public void setMap(String mapFile) {
        if(this.landuse != null)
            throw new DataException("Only one of map and landuse can be set.");
        this.map = mapFile;
    }
    
    @XmlAttribute(name = "landuse")
    public String getLanduse() {
        return landuse;
    }

    /**
     * Specify a per-land-use-class density table. System will generate a
     * density table by applying these numbers to the land-use map.
     * <P>
     * File must be formatted as a comma-seperated-value table. One column must
     * be named "class" and contain class codes; a column named "desc" must
     * contain user-readable descriptions of the class codes, and a column named
     * "density" must provide the relative density values.
     * <P>
     * If censusgen builds a density map automatically, it will write the table
     * to "LandcoverPopulationDensity-landuse.csv", and this file can be
     * re-submitted here.
     * <P>
     * Only one of setMap and setLanduse can be called, since the other would be
     * redundant.
     * 
     * @param classDensityTable
     *            path and name of table to load and use
     */
    public void setLanduse(String classDensityTable) {
        if(this.map != null)
            throw new DataException("Only one of map and landuse can be set.");
        this.landuse = classDensityTable;
    }
}
