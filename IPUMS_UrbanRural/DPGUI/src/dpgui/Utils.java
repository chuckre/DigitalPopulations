package dpgui;

import java.util.Arrays;
import java.util.Set;
import javax.swing.TransferHandler.TransferSupport;

/**
 * Misc functions supporting Java and its included classes.
 *
 * @author William R. Zwicky
 */
public class Utils {
    public static String asString(Object obj) {
        if(obj == null)
            return "";
        else
            return obj.toString();
    }

    public static String toString(Set<Integer> classes, String seperator) {
        StringBuilder sb = new StringBuilder();
        for (Integer cls : classes) {
            if(sb.length() > 0)
                sb.append(seperator);
            sb.append(cls);
        }
        return sb.toString();
    }

    /** Sun is too lazy to code this. */
    public static String toString(TransferSupport support) {
        return String.format("%d %s %s %d %d",
            support.getDropAction(),
            Arrays.toString(support.getDataFlavors()),
            support.getDropLocation(),
            support.getSourceDropActions(),
            support.getUserDropAction()
            );
    }
}
