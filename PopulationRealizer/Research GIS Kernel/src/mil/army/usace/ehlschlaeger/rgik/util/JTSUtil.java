package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.ItemVisitor;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.AbstractNode;
import com.vividsolutions.jts.index.strtree.Boundable;
import com.vividsolutions.jts.index.strtree.ItemBoundable;
import com.vividsolutions.jts.index.strtree.STRtree;

/**
 * Helpers for working with the Java Topology Suite.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 *
 * @author William R. Zwicky
 */
public class JTSUtil {
    /** An envelope that covers everything (+/- infinity). */
    public static final Envelope ALL_ENVELOPE = new Envelope(
            Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);

    /**
     * Enlarge an envelope in one dimension so that it becomes square. Other
     * dimension will remain the same size.
     * 
     * @param env
     *            box to make square
     */
    public static void square(Envelope env) {
        if(env.getWidth() > env.getHeight()) {
            double extra = (env.getWidth() - env.getHeight()) / 2;
            env.expandBy(0, extra);
        }
        else if(env.getHeight() > env.getWidth()) {
            double extra = (env.getHeight() - env.getWidth()) / 2;
            env.expandBy(extra, 0);
        }
    }

    /**
     * Find points that are with an envelope. JTS' query methods sometimes
     * return points outside the envelope. This method is need to polish the
     * result.
     * 
     * @param index
     *            container of points to scan. All objects must implement
     *            Boundable.
     * @param bounds
     *            box outlining points desired
     * 
     * @return List of objects inside bounds
     */
    public static List<Boundable> query(SpatialIndex index, final Envelope bounds) {
        final ArrayList<Boundable> answer = new ArrayList<Boundable>();
        
        index.query(bounds, new ItemVisitor() {
            public void visitItem(Object item) {
                Boundable node = (Boundable) item;
                if(bounds.contains((Envelope) node.getBounds()))
                    answer.add(node);
            }
        });
        return answer;
        
        
//        // index.query() returns unqualified List, but Java doesn't mind this:
//        @SuppressWarnings("unchecked")
//        List<Boundable> nodes = index.query(bounds);
//        
//        for (Iterator<Boundable> iterator = nodes.iterator(); iterator.hasNext();) {
//            Boundable node = (Boundable) iterator.next();
//            if(! bounds.contains((Envelope) node.getBounds()))
//                iterator.remove();
//        }
//        return nodes;
    }
    
    /**
     * Helper to remove multiple objects from a SpatialIndex.
     * 
     * @param tree a JTS spatial index
     * @param points any collection of csv2kml.Point objects
     */
    public static void removeAll(SpatialIndex tree, Collection<Boundable> points) {
        for (Boundable node : points) {
            tree.remove((Envelope) node.getBounds(), node);
        }
    }

    /**
     * Build pre-order iterator for JTS STRtree instance.
     * Yields values at each node before visiting children.
     * 
     * @param index object to iterate
     * @return iterator for contents of object
     */
    public static Iterator<Object> iterate(final STRtree index) {
        return new Iterator<Object>() {
            Queue<Object> todo;
            Object next;
            {
                todo = new LinkedList<Object>();
                todo.add(index.getRoot());
                next = getNext();
            }
            
            public boolean hasNext() {
                return next != null;
            }
            public Object next() {
                if(next == null)
                    throw null;
                else {
                    Object prev = next;
                    next = getNext();
                    return prev;
                }
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }

            /** Find the next valid data object. */
            private Object getNext() {
                while(!todo.isEmpty()) {
                    Object item = todo.remove();
                    if(item instanceof ItemBoundable) {
                        Object next = ((ItemBoundable)item).getItem();
                        if(next != null)
                            return next;
                    }
                    else {
                        List<?> kids = ((AbstractNode)item).getChildBoundables();
                        todo.addAll(kids);
                    }
                }
                return null;
            }
        };
    }
    
//    /**
//     * Compute bounding box of 
//     * @param index
//     *            collection of objects to analyze. All objects must implement
//     *            Boundable.
//     * @return bounding box of all objects in index
//     */
//    public static Envelope getBounds(final SpatialIndex index) {
//        
//        return new STRtree() {
//            public Envelope getBounds() {
//                return (Envelope) ((STRtree)index).getRoot().getBounds();
//            }
//        }.getBounds();
//
//        
//        
//        
//        // SpatialIndex has no queryAll, so we need this:
//        final Envelope bounds = new Envelope();
//        index.query(ALL_ENVELOPE, new ItemVisitor() {
//            public void visitItem(Object item) {
//                Boundable point = (Boundable) item;
//                Envelope env = (Envelope) point.getBounds();
//                bounds.expandToInclude(env);
//            }
//        });
//        return bounds;
//    }
}
