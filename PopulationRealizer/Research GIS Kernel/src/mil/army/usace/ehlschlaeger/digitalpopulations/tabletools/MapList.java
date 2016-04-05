package mil.army.usace.ehlschlaeger.digitalpopulations.tabletools;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Wrap a list so it can also act like a map. Doesn't actually implement
 * java.util.Map due to collisions between List and Map. A "schema" must be
 * supplied which assigns a name or key value to each index in the data list.
 * The value list can be replaced wholesale so a single instance can be used to
 * examine i.e. all the rows in a table.
 * <P>
 * The constructor creates a new schema object for the instance, but
 * shallowCopy() can be used to share schemas between instances and conserve
 * memory.
 * 
 * @param <K>
 *            data type for map keys
 * @param <V>
 *            data type for list values
 * 
 * @author William R. Zwicky
 */
public class MapList<K,V> extends AbstractList<V> {
    private Map<K,Integer> map;
    private List<V> values;

    /**
     * Create object with no field names, only numeric indices.
     */
    public MapList() {
        map = new HashMap<K, Integer>();
    }
    
    /**
     * @param schema key name for each 'column' in list
     */
    public MapList(List<K> schema) {
        map = new HashMap<K, Integer>();
        for(int i=0; i<schema.size(); i++)
            map.put(schema.get(i), i);
    }

    /**
     * @param schema key name for each 'column' in list
     * @param values value for each 'column'
     */
    public MapList(List<K> schema, List<V> values) {
        this(schema);
        this.values = values;
    }
    
    // === List interface === //

    @Override
    public V get(int index) {
        return values.get(index);
    }

    @Override
    public int size() {
        return values.size();
    }
    
    @Override
    public V set(int index, V value) {
        return values.set(index,value);
    }
    
    // === Extra Magic === //

    /**
     * Add alternate names for every column. this.get(schema2.get(i)) will
     * access values[i], just like this.get(original_schema.get(i)). Null values
     * will be skipped, but all other entries will be added at their
     * corresponding index.
     */
    public void addAliases(List<K> schema2) {
        for(int i=0; i<schema2.size(); i++)
            if(schema2.get(i) != null)
                map.put(schema2.get(i), i);
    }
    
    /**
     * Replace value list with an entirely new list. List is not cloned; all
     * methods will manipulate the given object.
     */
    public void setValues(List<V> values) {
        this.values = values;
    }

    /**
     * @return value container object
     */
    public List<V> getValues() {
        return values;
    }

    /**
     * Get a field by name.  Name must appear in the schema.
     * 
     * @param key name of field to access
     * @return value at the corresponding index
     */
    public V get(K key) {
        Integer idx = map.get(key);
        if(idx == null)
            throw new IllegalArgumentException("No key \""+key+"\"");
        return values.get(idx.intValue());
    }

    /**
     * Replace a field by name.  Name must appear in the schema.
     * 
     * @param key name of field to access
     * @param value new value for field
     * @return the element previously at the specified position
     */
    public V set(K key, V value) {
        Integer idx = map.get(key);
        if(idx == null)
            throw new IllegalArgumentException("No key \""+key+"\"");
        return values.set(idx.intValue(), value);
    }

    /**
     * Create a shallow copy of this instance. Copy can be used to index into a
     * different array without wasting memory on a new schema. Note that both
     * the schema and value list is shared with this instance.
     */
    public MapList<K,V> shallowCopy() {
        MapList<K, V> neu = new MapList<K, V>();
        neu.map = this.map;
        neu.values = this.values;
        return neu;
    }

    /**
     * Create a similar object that uses the same schema, but indexes a
     * different list.
     */
    public MapList<K,V> shallowCopy(List<V> values) {
        MapList<K, V> neu = new MapList<K, V>();
        neu.map = this.map;
        neu.values = values;
        return neu;
    }
}
