package mil.army.usace.ehlschlaeger.rgik.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;




/**
 * Breadth-first iterator for GISPointQuadTree. Visits given node's points, then
 * all of the points from its immediate children, then points from
 * grandchildren, etc. Changes must not be made to any of these nodes, as they
 * could cause the iterator to crash, skip points, or visit points repeatedly.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public class GISPointQuadTreeBreadthFirstIterator<T extends GISPoint> {
    private int                          nextPointToDo,
                                         pointsInCurrentPointList;
    private T[]                          currentPointList;
    private LinkedList<GISPointQuadTree<T>> toDo;

    /**
     * Create a new iterator for a quad tree.
     * @param qt the GISPointQuadTree to iterate.
     */
    public GISPointQuadTreeBreadthFirstIterator(GISPointQuadTree<T> qt) {
        toDo = new LinkedList<GISPointQuadTree<T>>();
        currentPointList = qt.getPointArray();
        pointsInCurrentPointList = qt.getNumberPointsInNode();
        GISPointQuadTree<T>[] array = qt.getSubQTNodes();
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                toDo.add(array[i]);
            }
        }
        nextPointToDo = 0;
    }

    /**
     * Returns next point in order, or null if done.
     * @return next point or null if done
     */
    public T next() {
        if (currentPointList != null) {
            T next = currentPointList[nextPointToDo];
            nextPointToDo++;
            if (nextPointToDo >= pointsInCurrentPointList) {
                currentPointList = null;
            }
            return next;
        } else {
            if (toDo.isEmpty())
                return null;
            else {
                GISPointQuadTree<T> nextQT = toDo.remove();
                pointsInCurrentPointList = nextQT.getNumberPointsInNode();
                GISPointQuadTree<T>[] array = nextQT.getSubQTNodes();
                nextPointToDo = 0;
                currentPointList = nextQT.getPointArray();
                if (array != null) {
                    for (int i = 0; i < array.length; i++) {
                        toDo.add(array[i]);
                    }
                }
                return (next());
            }
        }
    }

    /**
     * Iterate tree as documented, and return all visited points in a single
     * list. The list can then be safely iterated while making changes to
     * the tree.  Changes to the list itself will have no effect on the tree,
     * though changes to the points will be preserved.
     * 
     * @param tree the GISPointQuadTree to iterate
     * @return List<GISPoint> of the contents in this tree, in the order
     *         dictated by GISPointQuadTreeBreadthFirstIterator.
     */
    public static <T extends GISPoint> List<T> toList(GISPointQuadTree<T> tree) {
        ArrayList<T> contents = new ArrayList<T>();
        GISPointQuadTreeBreadthFirstIterator<T> iter = new GISPointQuadTreeBreadthFirstIterator<T>(tree);
        T point;
        while((point = iter.next()) != null) {
            contents.add(point);
        }
        return contents;
    }
}
