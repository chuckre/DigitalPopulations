package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Spec for a region map and its associated table. Part of FileRelationship.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class Regions {
    /** Unique ID for this map/table pair. */
    @XmlAttribute
    public String id;
    
    /** Map file which defines regions for aggregate statistics. */
    @XmlAttribute(required=true)
    public String map;

    /**
     * Table (CSV) file which specifies aggregate statistics for regions in map.
     */
    @XmlAttribute(required=true)
    public String table;

    /** Column in regionTable which matches the region codes in the map. */
    @XmlAttribute
    public String key;

    /**
     * Column in regionTable which specifies the number of households in each
     * region.
     */
    @XmlAttribute
    public String households;

    /** Column in regionTable which specifies the number people in each region. */
    @XmlAttribute
    public String population;

    
    
    public Regions() {
    }
    
    public Regions(String id, String mapFile, String tableFile, String keyColumn) {
        this.id = id;
        this.map = mapFile;
        this.table = tableFile;
        this.key = keyColumn;
    }
    
    /**
     * Column in regionTable which specifies the number of empty households in
     * each region.
     */
    @XmlAttribute
    public String vacancies;
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<regions");
        if(!ObjectUtil.isBlank(id))
            buf.append(" id=\"").append(id).append('"');
        buf.append(" map=\"").append(map).append('"');
        buf.append(" table=\"").append(table).append('"');
        buf.append("/>");
        return buf.toString();
    }
}
