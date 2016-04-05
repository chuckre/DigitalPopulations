package mil.army.usace.ehlschlaeger.rgik.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Misc utilities for Java objects.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public abstract class ObjectUtil {
    /**
     * Smarter equals than .equals(). Handles nulls well: if both refs are null,
     * returns true. If ref1 is null, uses ref2's equals(). Otherwise, uses
     * ref1's .equals().
     * 
     * @param ref1 one object to test
     * @param ref2 other object to test
     * @return true if objects are "equal"
     */
    public static boolean objEquals(Object ref1, Object ref2) {
        if(ref1 == null) {
            if(ref2 == null)
                return true;
            else
                return ref2.equals(ref1);
        }
        else {
            return ref1.equals(ref2);
        }
    }
    
    protected static final Pattern ISBLANK_REGEX = Pattern.compile("\\s*");

    /**
     * Determine whether string contains anything useful.
     * @param string string to test
     * @return true if null, empty, or contains only whitespace
     */
    public static boolean isBlank(String string) {
        return string == null || ISBLANK_REGEX.matcher(string).matches();
    }

    /**
     * @return "" if string is null, else string.  Use with + operator.
     */
    public static String nz(String str) {
        return str==null ? "" : str;
    }
    
    /**
     * Convert a BitSet into an array of boolean.
     * 
     * @param bitset set of bits to convert
     * @param size number of bits to convert.  length() it not useful here, and size() may
     *     not give the number you want.
     * @return result of conversion
     */
    public static boolean[] toArray(BitSet bitset, int size) {
        boolean[] b = new boolean[size];
        for(int i=0; i<size; i++)
            b[i] = bitset.get(i);
        return b;
    }

    /**
     * Assemble a list of items into a single string.
     *
     * @param list objects to assemble into string.  toString() is called on each.
     * @param sep string to insert between each pair of items
     *
     * @return string
     */
    public static String join(List<?> list, String sep) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if(i > 0)
                buf.append(sep);
            buf.append(list.get(i).toString());
        }
        return buf.toString();
    }

    /**
     * Returns an array list containing the elements returned by the specified
     * iterator in the order they are returned by the iterator. Iterator is
     * consumed by the process.
     * 
     * @param iterator
     *            iterator providing elements for the returned array list
     * 
     * @return an array list containing the elements returned by the specified
     *         enumeration.
     */
    public static <T> ArrayList<T> list(Iterator<T> iterator) {
        ArrayList<T> list = new ArrayList<T>();
        while(iterator.hasNext())
            list.add(iterator.next());
        return list;
    }

    /**
     * Type-safe clone method. Slightly slower than clone(), since we need to
     * use introspection to gain access to clone().
     * <P>
     * Reminder: In Java, clone() is ill-defined. You are generally better off
     * defining your own copy() methods that you can document and customize.
     * 
     * @param <T>
     *            type of instance to clone
     * @param obj
     *            instance to clone
     * 
     * @return result of instance's clone() method
     * @throws RuntimeException
     *             if clone() fails or cannot be called
     */
    @SuppressWarnings("unchecked")
    public static <T extends Cloneable> T clone(T obj) {
        try {
            Method m = obj.getClass().getMethod("clone");
            return (T) m.invoke(obj);
        } catch (SecurityException e) {
            throw new RuntimeException("Not cloneable: "+obj, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Not cloneable: "+obj, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Not cloneable: "+obj, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Not cloneable: "+obj, e);
        }
    }
    
    /**
     * Deep-clone an array of arbitrary dimension and type.
     * Java arrays don't automatically deep-clone, so this method will take 
     * care of the given array and its elements, their elements, and so on to any depth.
     * This method will NOT traverse through objects, only directly contained arrays.
     * If any element is an object, its clone() method will be called, and the result
     * will depend on what that method copies.
     * 
     * @param array the object or array to copy
     * @param cloneLeaves whether to call clone() on Object elements, or to
     *     simply copy the reference.  If true, clone() will be called on any
     *     object encountered.  If false, the object reference will be copied
     *     instead so that the same instance appears in both arrays.  If false
     *     and 'array' is not an array, then it will be returned unmodified.
     * @return deep copy of given array
     * 
     * @throws SecurityException on any error finding or calling clone()
     * @throws NoSuchMethodException on any error finding or calling clone()
     * @throws IllegalArgumentException on any error finding or calling clone()
     * @throws IllegalAccessException on any error finding or calling clone()
     * @throws InvocationTargetException on any error finding or calling clone()
     */
    public static Object cloneArray(Object array, boolean cloneLeaves) {
        if(array == null)
            return null;
        else if(array.getClass().isArray()) {
            // Is an array of some sort: what kind of element?
            Object neu = Array.newInstance(array.getClass().getComponentType(), Array.getLength(array));

            if(array.getClass().getComponentType().isArray()) {
                // Array of arrays: recurse.
                Object[] aold = (Object[])array;
                Object[] aneu = (Object[])neu;
                for(int i=0; i<Array.getLength(array); i++)
                    aneu[i] = cloneArray(aold[i], cloneLeaves);
            }
            else {
                // Array of values: what type?
                if(!cloneLeaves || array.getClass().getComponentType().isPrimitive())
                    // Array of primitives: System.arraycopy.
                    System.arraycopy(array, 0, neu, 0, Array.getLength(array));
                else {
                    // Array of objects: recurse.
                    //  - Can't use getComponent.getMethod("clone") here; it will fail on
                    //    Object[] even if all the elements are cloneable.
                    Object[] aold = (Object[])array;
                    Object[] aneu = (Object[])neu;
                    for(int i=0; i<Array.getLength(array); i++)
                        aneu[i] = cloneArray(aold[i], cloneLeaves);
                }
            }
            return neu;
        }
        else {
            // Not an array: clone.
            if(cloneLeaves) {
                try {
                    Method cloner = array.getClass().getMethod("clone");
                    return cloner.invoke(array);
                } catch (SecurityException e) {
                    throw new RuntimeException("Not cloneable: "+array, e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Not cloneable: "+array, e);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Not cloneable: "+array, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Not cloneable: "+array, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Not cloneable: "+array, e);
                }
            }
            else
                return array;
        }
    }

    /**
     * Crash-free parser.  Returns default if string cannot be parsed.
     * 
     * @param strval text to scan
     * @param defalt value to return if strval is invalid
     * 
     * @return number form of strval, or defalt if invalid
     */
    public static int parseInt(String strval, int defalt) {
        try {
            return Integer.parseInt(strval);
        } catch (NumberFormatException e) {
            return defalt;
        }
    }

    /**
     * Crash-free parser.  Returns default if string cannot be parsed.
     * 
     * @param strval text to scan
     * @param defalt value to return if strval is invalid
     * 
     * @return number form of strval, or defalt if invalid
     */
    public static double parseDouble(String strval, double defalt) {
        try {
            return Double.parseDouble(strval);
        } catch (NumberFormatException e) {
            return defalt;
        }
    }

    /**
     * Parse a variety of strings as boolean (true/false, 1/0, etc.).
     * 
     * @param strval
     *            string to parse
     * @return boolean
     * @throws IllegalArgumentException
     *             if string is not recognized
     */
    public static boolean parseBoolean(String strval) {
        final String[]  STRS = {"true", "false", "t",  "f",   "1",  "0",   "y",  "n"};
        final boolean[] VALS = {true,   false,   true, false, true, false, true, false};
        strval = strval.trim().toLowerCase();
        for(int i=0; i<STRS.length; i++)
            if(STRS[i].equals(strval))
                return VALS[i];
        throw new IllegalArgumentException("Not identifiable as a boolean: "+strval);
    }
    
    /**
     * More useful version that never returns null.
     * @param e exception or throwable
     * @return simple string message
     */
    public static String getMessage(Throwable e) {
        String msg;
        if(isBlank(e.getMessage())) {
            if(e.getCause() == null)
                msg = e.getClass().getSimpleName();
            else
                msg = getMessage(e.getCause());
        }
        else {
            msg = e.getMessage();
            if(e.getCause() != null)
                msg += "\n  due to "+getMessage(e.getCause());
        }
        return msg;
    }
    
    /**
     * Helper to make java.util.Pattern easy to use:  extracts a string
     * from within another string.
     * 
     * @param string text to scan
     * @param regex pattern to execute
     * @param group index of group to extract
     * 
     * @return value of requested group
     */
    public static String extract(String string, String regex, int group) {
        // Can't be done in one line because idiots at Sun require find() before
        // group() will work. Why? WTF else could group() possibly mean? Why
        // doesn't matcher() start scanning?
        Matcher p = Pattern.compile(regex).matcher(string);
        p.find();
        return p.group(group);
    }
}
