package dpgui.model;

/**
 * Listents for changes fired by ModelMonitor.
 *
 * @author William R. Zwicky
 */
public interface ModelListener {
    /**
     * This is called when a registered path has changed values.
     * 'node' may be the registered path, a parent, or a child.
     *
     * @param node string path to member that changed
     * @param oldValue prior value of this member
     * @param newValue new value of this member
     */
    public void nodeChanged(String node, Object oldValue, Object newValue);
}
