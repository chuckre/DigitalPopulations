package dpgui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Framework to convert a business model into a <i>listenable</i>.
 * Listeners can register to be informed when specific members change.
 *
 * @author William R. Zwicky
 */
public class ModelMonitor {
    /** Top object of business model. */
    protected Object root;
    /** Listeners and the nodes they're interested in. */
    protected Map<String, List<ModelListener>> listeners = new HashMap<String, List<ModelListener>>();

    public ModelMonitor() {
    }

    public ModelMonitor(Object root) {
        this.root = root;
    }

    /**
     * Change entire object tree.  Fires suitable event.
     * @param root new top-level object
     */
    public void setRoot(Object root) {
        Object old = this.root;
        this.root = root;
        fireNodeChanged("root", old, root);
    }

    /**
     * @return current top-level object
     */
    public Object getRoot() {
        return this.root;
    }

    /**
     * Register a listener for changes to the model.  Listener will be called
     * if path, any parent, or any child is changed.  i.e. For "root.x",
     * listener will be called if fireNodeChanged is called with "root",
     * "root.x", or "root.x.y".  Listener will not be called for "root.z".
     * <P>
     * Plan your path strings carefully to avoid cycles.  Ensure "root.foo"
     * doesn't trigger any "root.foo.*" events, as "root.foo" will be called
     * again.
     * <P>
     * Strictly speaking, "path" and "node" are just strings and never used to
     * access data.  Thus any syntax can be used, as long as all speakers and
     * listeners agree.  Suggestion:  use dotted strings to refer to a chain of
     * members; use "[]" to indicate members of an array or container.
     *
     * @param path full dotted name of member to monitor.  Must equal "root" or
     *     start with "root."; "root" refers to top level object.
     * @param listener
     */
    public void addListener(String path, ModelListener listener) {
        assert(path.equals("root") || path.startsWith("root."));

        // "x.y.z" means listen to z and all children of z:
        //   if x.y.z changes, fire
        //   if x.y.z.w changes, fire
        //   if x.y changes, fire; z and children have probably changed
        //   if x.y.a changes, ignore
        List<ModelListener> cur = listeners.get(path);
        if(cur == null) {
            cur = new ArrayList<ModelListener>();
            listeners.put(path, cur);
        }
        cur.add(listener);
    }

    /**
     * Remove a listener from every path it's listening to.
     * 
     * @param listener object to remove
     */
    public void removeListener(ModelListener listener) {
        for (List<ModelListener> list : listeners.values()) {
            if(list.contains(listener))
                list.remove(listener);
        }
    }

    /**
     * Remove all listeners for a certain path.
     * @param path string path to remove
     */
    public void removeListeners(String path) {
        listeners.remove(path);
    }

    /**
     * Clear all listeners from all paths.
     */
    public void removeAllListeners() {
        listeners.clear();
    }

    /**
     * Inform listeners that a value in the model has been modified.
     * <P>
     * How to use:
     * <UL>
     *   <LI> If the variable myModel.first.foo has changed, fire "root.first.foo"
     *   <LI> If two variables myModel.first.foo and myModel.first.bar have
     *        changed, then either fire each change individually as above, or
     *        fire "root.first".  Which style you use depends on how much work
     *        the app must do, how much the screen flashes, etc.
     * </UL>
     *
     * @param node string path to member that changed
     * @param oldValue prior value of this member.  May be null if old value is
     *     not known, or 'node' contains [].
     * @param newValue new value of this member
     */
    public void fireNodeChanged(String node, Object oldValue, Object newValue) {
        for (String key : listeners.keySet()) {
            if(key.startsWith(node) || node.startsWith(key)) {
                // key.startsWith(node) means a child of key has changed
                // node.startsWith(node) means a parent of key has changed, so key may have changed as well
                List<ModelListener> list = listeners.get(key);
                for(ModelListener listener : list)
                    listener.nodeChanged(node, oldValue, newValue);
            }
        }
    }
}
