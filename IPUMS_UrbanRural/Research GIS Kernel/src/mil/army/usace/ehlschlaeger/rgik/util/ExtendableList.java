package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Like ArrayList, but set() can take any index, and array will be extended to support it.
 * 
 * @param <E>
 *
 * @author William R. Zwicky
 */
public class ExtendableList<E> extends ArrayList<E> {
    public ExtendableList() {
    }
    
    public ExtendableList(Collection<E> collection) {
        super(collection);
    }
    
    public ExtendableList(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index
     *            index of the element to return
     * @return the element at the specified position in this list, or null if
     *         index is past end
     */
    @Override
    public E get(int index) {
        if(index >= size())
            return null;
        else
            return super.get(index);
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element. Array will be expanded with nulls if necessary.
     * 
     * @param index
     *            index of the element to replace
     * @param element
     *            element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public E set(int index, E element) {
        ensureCapacity(index+1);
        while(index >= size())
            add(null);
        return super.set(index, element);
    }
}
