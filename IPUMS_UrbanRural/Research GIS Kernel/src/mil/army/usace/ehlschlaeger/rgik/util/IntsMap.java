package mil.army.usace.ehlschlaeger.rgik.util;

/**
 * Map from a list of ints to object. More efficient than
 * HashMap<Integer,Object>, but requires storage for all keys from 0 to the
 * largest (i.e. it's really just a multi-dimensional automatic array.)
 * <P>
 * Note that if put(a,b,c) is used, then get(a,b) cannot be used. In fact,
 * get(a,b) will throw a ClassCastException since that location will not contain
 * a T, but instead an internal object that manages (a,b,*). Also, put(null,
 * a,b) will cause all (a,b,*) to become unmapped.
 * 
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class IntsMap<T> {
    // cannot have a type; element may be a T or another ExtList.
    ExtendableList map = new ExtendableList();

    /** Construct empty instance. */
    public IntsMap() {
    }

    /**
     * Retrieve a value.
     * 
     * @param keys
     *            list of ints that locate desired object
     * 
     * @return requested value or null
     * 
     * @throws ClassCastException
     *             if any prefix list of keys was used to store data. i.e.
     *             get(a,b,c) will crash if put(a,b) was previously called.
     */
    public T get(int... keys) {
        assert keys.length >= 1;
        ExtendableList m = map;
        for(int i=0; i<keys.length-1; i++) {
            ExtendableList m2 = (ExtendableList) m.get(keys[i]);
            if(m2 == null)
                // no mapping
                return null;
            else
                m = m2;
        }
        return (T) m.get(keys[keys.length-1]);
    }

    /**
     * Store a value. The value can be null, and the internal arrays will be
     * stretched to include the given index, but get() will return null just as
     * if the location was unmapped.
     * 
     * @param value
     *            value to store
     * 
     * @param keys
     *            list of ints that specify location to store value
     * 
     * @throws ClassCastException
     *             if any prefix list of keys was used to store data. i.e.
     *             get(a,b,c) will crash if put(a,b) was previously called.
     */
    public void put(T value, int... keys) {
        // 'value' must be first due to java varargs
        assert keys.length >= 1;
        ExtendableList m = map;
        for(int i=0; i<keys.length-1; i++) {
            ExtendableList m2 = (ExtendableList) m.get(keys[i]);
            if(m2 == null) {
                m2 = new ExtendableList();
                m.set(keys[i], m2);
            }
            m = m2;
        }
        
        m.set(keys[keys.length-1], value);
    }
    
    public void remove(int... keys) {
        assert keys.length >= 1;
        ExtendableList m = map;
        for(int i=0; i<keys.length-1; i++) {
            ExtendableList m2 = (ExtendableList) m.get(keys[i]);
            if(m2 == null)
                // no mapping
                return;
            else
                m = m2;
        }
        m.set(keys[keys.length-1], null);
    }
}
