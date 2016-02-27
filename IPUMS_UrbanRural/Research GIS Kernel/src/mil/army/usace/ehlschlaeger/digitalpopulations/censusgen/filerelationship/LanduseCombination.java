package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;



/**
 * Provides a rule for combining multiple disparate land-use classes to a single
 * class. Part of FileRelationship.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class LanduseCombination {
    /**
     * Human-readable description of this grouping.
     */
    @XmlAttribute public String desc;

    /**
     * All cells with a class number given by 'classes' will be reassigned this
     * class number. In any list of LanduseCombination, the 'target' attribute
     * can be the same value listed in the 'classes' attribute of another. All
     * mappings are applied in parallel so cycles don't occur.
     */
    @XmlAttribute public int target;
    
    /**
     * Which classes to replace with new number.
     * Specified as a list of integer ranges, i.e. "1-4,17-49,51".
     * All integers must be non-negative.
     */
    @XmlAttribute public String classes;
 
    
    /** Construct a new blank instance. */
    public LanduseCombination() {
    }

    /** Construct a new complete instance. */
    public LanduseCombination(String desc, int target, String classes) {
        this.desc = desc;
        this.target = target;
        this.classes = classes;
    }

    /**
     * Determine if one number is within a our list of classes.
     * 
     * @param testClass
     *            integer to test
     * @return true if 'classes' includes the given number
     */
    public boolean contains(int testClass) {
        return contains(this.classes, testClass);
    }

    @Override
    public String toString() {
        return String.format("<combination classes=\"%s\" target=\"%d\" desc=\"%s\"/>",
            classes, target, desc);
    }
    
    /**
     * Determine if one number is within a described set. Description must be
     * formatted as a comma- or space-separated list of ranges, where each range
     * is a single integer or two integers formatted "first-last". "First" may
     * be larger or smaller than "last".
     * <p>
     * Note this method is not very fast, as it re-parses the string every time.
     * If you need to call this in a time-sensitive loop, you'll want to build
     * another data structure from 'classes' first.
     * 
     * @param classes
     *            description of a set of integers
     * @param testClass
     *            integer to test
     * @return true if testClass is any of the listed numbers, or within any of
     *         the listed ranges
     */
    public static boolean contains(String classes, int testClass) {
        String[] ranges = classes.split("\\s|,");
        for (String range : ranges) {
            int p = range.indexOf('-');
            if(p < 0) {
                // lone int
                if( testClass == Integer.parseInt(range))
                    return true;
            } 
            else {
                // range "7-9"
                int first = Integer.parseInt(range.substring(0, p));
                int last = Integer.parseInt(range.substring(p + 1));
                if(first <= last) {
                    if(first <= testClass && testClass <= last)
                        return true;
                }
                else {
                    if(last <= testClass && testClass <= first)
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Create a Set that contains only the Integers listed in the given spec.
     * 
     * @param classes
     *            description of a set of integers, as for contains()
     * @return Set<Integer> of the numbers in the description
     */
    public static Set<Integer> makeIntSet(String classes) {
        HashSet<Integer> intset = new HashSet<Integer>();

        // space-separated set of ranges
        if(!ObjectUtil.isBlank(classes)) {
            String[] ranges = classes.split("\\s|,");
            for (String range : ranges) {
                if(range.length() > 0) {
                    int p = range.indexOf('-');
                    try {
                        if(p < 0) {
                            // lone int
                            intset.add(new Integer(range));
                        }
                        else {
                            // range "7-9"
                            int first = Integer.parseInt(range.substring(0, p));
                            int last = Integer.parseInt(range.substring(p + 1));
                            for (int i = first; i <= last; i++)
                                intset.add(new Integer(i));
                        }
                    }
                    catch(NumberFormatException e) {
                        throw new IllegalArgumentException("Illegal range spec \""+range+"\"");
                    }
                    catch(IndexOutOfBoundsException e) {
                        throw new IllegalArgumentException("Illegal range spec \""+range+"\"");
                    }
                }
            }
        }
        
        return intset;
    }

    /**
     * Create BitSet with a '1' for every number in the given spec. Generally
     * smaller and faster than Set<Integer>.
     * 
     * @param classes
     *            description of a set of integers, as for contains()
     * @return BitSet of the numbers in the description
     */
    public static BitSet makeBitSet(String classes) {
        BitSet set = new BitSet();

        if(!ObjectUtil.isBlank(classes)) {
            // space-separated set of ranges
            String[] ranges = classes.split("\\s|,");
            for (String range : ranges) {
                range = range.trim();
                if(range.length() > 0) {
                    int p = range.indexOf('-');
                    try {
                        if(p < 0) {
                            // lone int
                            set.set(Integer.parseInt(range));
                        }
                        else {
                            // range like "7-9"
                            int first = Integer.parseInt(range.substring(0, p));
                            int last = Integer.parseInt(range.substring(p + 1));
                            for (int i = first; i <= last; i++)
                                set.set(i);
                        }
                    }
                    catch(NumberFormatException e) {
                        throw new IllegalArgumentException("Illegal range spec \""+range+"\"");
                    }
                    catch(IndexOutOfBoundsException e) {
                        throw new IllegalArgumentException("Illegal range spec \""+range+"\"");
                    }
                }
            }
        }

        return set;
    }
}
