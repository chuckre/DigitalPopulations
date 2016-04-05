package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

import com.vividsolutions.jts.geom.Envelope;



/**
 * Simple representation of a quad tree node.
 * 
 * @param <T>
 *            type of point contained within. This should normally implement
 *            Boundable, though this particular class doesn't care.
 * 
 * @author William R. Zwicky
 */
public class Tile<T> implements Iterable<Tile<T>> {
    /**
     * Bounding box that was used to generate tile. May be larger than actual
     * bounds of contents.
     */
    public Envelope bounds;
    /**
     * Points residing within the bounds of this tile.
     */
    public List<T> contents;
    /**
     * Child tiles.
     */
    public Tile<T> nw, ne, sw, se;

    
    /**
     * Pre-order iterate this tree of tiles (returns this, then visits nw,ne,sw,se).
     * @return new pre-order iterator, starting from this
     */
    public Iterator<Tile<T>> iterator() {
        return new Iterator<Tile<T>>() {
            Stack<Tile<T>> nextItems;
            
            // Instance initializer, basically an anonymous constructor.
            {
                nextItems = new Stack<Tile<T>>();
                nextItems.push(Tile.this);
            }
            
            public boolean hasNext() {
                return (! nextItems.isEmpty());
            }

            public Tile<T> next() {
                try {
                    Tile<T> item = nextItems.pop();
                    
                    // push children in reverse order so they come out in forward order
                    if(item.se != null)
                        nextItems.push(item.se);
                    if(item.sw != null)
                        nextItems.push(item.sw);
                    if(item.ne != null)
                        nextItems.push(item.ne);
                    if(item.nw != null)
                        nextItems.push(item.nw);
                    
                    return item;
                }
                catch(EmptyStackException e) {
                    throw new NoSuchElementException("No more tiles in tree.");
                }
            }

            public void remove() {
                throw new UnsupportedOperationException(getClass().getSimpleName()+".remove() is not supported.");
            }
        };
    }

    /**
     * Iterate through the items contained in this tree. Tiles are visited in
     * the order of Tile.iterator(), and points are produced in the order of
     * contents.iterator().
     * 
     * @return iterator over contents of all tiles
     */
    public Iterator<T> contentIterator() {
        return new Iterator<T>() {
            Iterator<Tile<T>> tileIter;
            Iterator<T> itemIter;

            // Instance initializer.
            {
                tileIter = Tile.this.iterator();
                itemIter = null;
                findNext();
            }
            
            public boolean hasNext() {
                // pointIter is only null only once we've completely run out of tiles
                return itemIter == null ? false : itemIter.hasNext();
            }

            public T next() {
                T item = itemIter.next();
                findNext();
                return item;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }

            /**
             * Make sure itemIter is ready to produce the next point.
             * Returns nothing; use hasNext() to see if we're valid.
             */
            protected void findNext() {
                for(;;) {
                    // current iterator still valid?
                    if(itemIter != null && itemIter.hasNext())
                        // yes! keep it.
                        break;
                    // else we need to find a new one
                    if(! tileIter.hasNext()) {
                        // no more tiles; signal end
                        tileIter = null;
                        itemIter = null;
                        break;
                    }
                    // get next tile
                    List<T> items = tileIter.next().contents;
                    // has points?
                    if(items != null && items.size() > 0) {
                        // yes! save iterator.
                        itemIter = items.iterator();
                        break;
                    }
                    // else keep looping
                }
            }
        };
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(getClass().getSimpleName()).append("[");
        if(contents != null && contents.size() > 0)
            buf.append(contents.size()).append(" elements, ");
        
        int kids = 0;
        if(nw != null)
            kids ++;
        if(ne != null)
            kids ++;
        if(sw != null)
            kids ++;
        if(se != null)
            kids ++;
        if(kids > 0)
            buf.append(kids).append(" children, ");
        
        buf.append(bounds).append("]");
        return buf.toString();
    }
}
