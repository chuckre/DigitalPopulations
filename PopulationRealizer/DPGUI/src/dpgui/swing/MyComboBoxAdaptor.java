package dpgui.swing;

import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * Adds fireContentsChanged() to DefaultComboBoxModel.
 * Is an "adapter" as get/setSelectedItem must be overridden to access model
 * values.
 *
 * @author William R. Zwicky
 */
public abstract class MyComboBoxAdaptor extends DefaultComboBoxModel {
    public MyComboBoxAdaptor() {
    }

    /** Initialize with available options. */
    public MyComboBoxAdaptor(Vector<String> options) {
        super(options);
    }

    /** Notify listeners that available options have changed. */
    public void fireOptionsChanged() {
        super.fireContentsChanged(this, 0, getSize()-1);
    }

    /** Helper to call fireContentsChanged() given the containing combo box. */
    public static void fireOptionsChanged(JComboBox combo) {
        ((MyComboBoxAdaptor)combo.getModel()).fireOptionsChanged();
    }
}
