package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;


/**
 * Describes a map and a table, and how they relate.
 *
 * @author William R. Zwicky
 */
public class AttributeMap {
    /** Unique ID for this map/table pair. */
    @XmlAttribute(required=true)
    public String id;
    
    /** Map file which defines regions for aggregate statistics. */
    @XmlAttribute(required=true)
    public String map;

    /**
     * Table (CSV) file which specifies aggregate statistics for regions in map.
     */
    @XmlAttribute(required=false)
    public String table;

    /** Column in regionTable which matches the region codes in the map. */
    @XmlAttribute(required=false)
    public String key;

    
    
    public AttributeMap() {
    }
    
    public AttributeMap(String id, String mapFile) {
        this.id = id;
        this.map = mapFile;
    }
    
    public AttributeMap(String id, String mapFile, String tableFile, String keyColumn) {
        this.id = id;
        this.map = mapFile;
        this.table = tableFile;
        this.key = keyColumn;
    }
    
    

    /**
     * Perform basic consistency checks.
     */
    public void validate() {
        ArrayList<String> msgs = new ArrayList<String>();

        if(ObjectUtil.isBlank(id))
            msgs.add(String.format("id value is mandatory: %s", this));
        if(ObjectUtil.isBlank(map))
            msgs.add(String.format("map value is mandatory: %s", this));
        if(ObjectUtil.isBlank(table) != ObjectUtil.isBlank(key))
            msgs.add(String.format("if table is given, key must also be given: %s", this));

        if(! msgs.isEmpty())
            throw new DataException(ObjectUtil.join(msgs, "\n"));
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("<attribute");
        buf.append(" id=\"").append(id).append('"');
        buf.append(" map=\"").append(map).append('"');
        if(!ObjectUtil.isBlank(table))
            buf.append(" table=\"").append(table).append('"');
        if(!ObjectUtil.isBlank(key))
            buf.append(" key=\"").append(key).append('"');
        buf.append("/>");
        return buf.toString();
    }
}
