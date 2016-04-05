package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;



/**
 * Slightly modified extension of Properties that saves its keys in sorted
 * order. As a side effect, keys() returns keys in sorted order. Note that all
 * keys must implement Comparable.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class SortedProperties extends java.util.Properties {
    @Override
    public synchronized Enumeration<Object> keys() {
        Vector<Object> v = new Vector<Object>(keySet());
        
        // Dirty cast to assume keys are Comparable.
        // Extra (Object) cast is required to make Oracle Java shut up.
        @SuppressWarnings("unchecked")
        Vector<? extends Comparable<Object>> vc = (Vector<? extends Comparable<Object>>)(Object)v;
        
        Collections.sort(vc);
        return v.elements();
    }
}
