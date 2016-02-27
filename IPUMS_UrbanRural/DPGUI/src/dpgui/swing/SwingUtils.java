package dpgui.swing;

import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

/**
 * Misc functions supporting Swing classes.
 *
 * @author William R. Zwicky
 */
public class SwingUtils {
    /**
     * Replace the options in a combo box with a new set.
     * nulls are skipped.  Selected value is preserved.
     *
     * @param combob GUI component to update
     * @param values list of values to put into component, or null to erase
     */
    public static void replaceContents(JComboBox combob, Object[] values) {
        Object cur = combob.getSelectedItem();
        DefaultComboBoxModel m = (DefaultComboBoxModel)combob.getModel();
        m.removeAllElements();
        if(values != null)
            for(Object v : values)
                if(v != null)
                    m.addElement(v);
        combob.setSelectedItem(cur);
        combob.setEnabled(true);
    }

    /**
     * Replace the options in a combo box with a new set.
     * nulls are skipped.  Selected value is preserved.
     *
     * @param combob GUI component to update
     * @param values list of values to put into component, or null to erase
     */
    public static void replaceContents(JComboBox combob, Vector<?> values) {
        Object cur = combob.getSelectedItem();
        DefaultComboBoxModel m = (DefaultComboBoxModel)combob.getModel();
        m.removeAllElements();
        if(values != null)
            for(Object v : values)
                if(v != null)
                    m.addElement(v);
        combob.setSelectedItem(cur);
        combob.setEnabled(true);
    }

    /**
     * Replace the options in a combo box with a new set.
     * nulls are skipped.  Selected value is preserved.
     *
     * @param combob GUI component to update
     * @param values list of values to put into component, or null to erase
     */
    public static void replaceContents(JComboBox combob, List<?> values) {
        Object cur = combob.getSelectedItem();
        DefaultComboBoxModel m = (DefaultComboBoxModel)combob.getModel();
        m.removeAllElements();
        if(values != null)
            for(Object v : values)
                if(v != null)
                    m.addElement(v);
        combob.setSelectedItem(cur);
        combob.setEnabled(true);
    }

    /**
     * Set starting dir and file for a chooser.
     * chooser.setSelectedFile doesn't work correctly when given a directory.
     *
     * @param chooser
     * @param file
     */
    public static void setSelectedFile(JFileChooser chooser, File file) {
        if(file.isDirectory())
            chooser.setCurrentDirectory(file);
        else
            chooser.setSelectedFile(file);
    }
}
