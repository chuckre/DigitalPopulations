package mil.army.usace.ehlschlaeger.rgik.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import mil.army.usace.ehlschlaeger.rgik.core.CSVTable;




/**
 * GUI view for a CSVTable.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class CSVTableView extends JFrame {
    protected int        startX = -1;
    protected int        startY = 0;
    
    protected CSVTable table;
    protected JTable view;

    /**
     * Load a table from a file and set window size.
     * 
     * @param csvFile
     * @param xSize
     * @param ySize
     * @throws IOException
     */
    public CSVTableView(CSVTable table, int xSize, int ySize) throws IOException {
        this.table = table;
        startTable(xSize, ySize, false, false);
    }
    
    /**
     * Load a table from a file and set window parameters.
     * 
     * @param csvFile
     * @param xSize
     * @param ySize
     * @param horizontalScrollBar
     * @param verticalScrollBar
     * @throws IOException
     */
    public CSVTableView(CSVTable table, int xSize, int ySize, boolean horizontalScrollBar,
            boolean verticalScrollBar) throws IOException {
        this.table = table;
        startTable(xSize, ySize, horizontalScrollBar, verticalScrollBar);
    }

    /**
     * Create a view for the current table contents. Note that Java will be shut
     * down when all windows are closed.
     * 
     * @param xSize
     * @param ySize
     * @param horizontalScrollBar
     * @param verticalScrollBar
     */
    public void startTable(
            int xSize, int ySize,
            boolean horizontalScrollBar, boolean verticalScrollBar) {
        JFrame frame = new JFrame(table.getTitle());
        
        frame.setSize(xSize, ySize);
        frame.addWindowListener(new CSVWindowMonitor());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Dimension frameSize = getSize();
        if (startX < 0)
            startX = screenSize.width - xSize;
        frame.setLocation(startX, startY);
        startX -= 100;
        if (startX < 0)
            startX = screenSize.width - xSize;
        startY += 24;
        if (startY + ySize >= screenSize.height)
            startY = 0;

        view = new JTable(table);
        view.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel tcm = view.getColumnModel();
        for (int columnNumber = tcm.getColumnCount() - 1; columnNumber >= 0; columnNumber--) {
            // System.out.print( columnNumber + " " );
            TableColumn tc = tcm.getColumn(columnNumber);
            tc.setMinWidth(30);
            tc.sizeWidthToFit();
        }

        JScrollPane jsp = new JScrollPane(view);
        if (horizontalScrollBar == true) {
            jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        }
        if (verticalScrollBar == true) {
            jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }
        frame.getContentPane().add(jsp, BorderLayout.CENTER);
    }
    
    /**
     * Remove a column from the visible display.  Has no effect on the underlying data.
     * @see JTable#removeColumn(TableColumn)
     * @throws NullPointerException if the JTable GUI has not been created
     */
    public void removeColumn(String columnName) {
        TableColumn tc = null;
        try {
            tc = view.getColumn(columnName);
        } catch (IllegalArgumentException iae) {
            return;
        }
        view.removeColumn(tc);
    }

    /**
     * identical to JTable isShowing() method
     * @see JTable#isShowing
     */
    public boolean isTableShowing() {
        return (view.isShowing());
    }

    /**
     * identical to DefaultTableModel clearSelection() method
     * @see JTable#clearSelection()
     */
    public void clearSelectedRows() {
        view.clearSelection();
    }

    /**
     * returns an array of selected rows
     */
    public int[] getSelectedRows() {
        int count = view.getSelectedRowCount();
        if (count == 0) {
            return null;
        }
        int[] rows = new int[count];
        count = 0;
        for (int r = 0; r < table.getRowCount(); r++) {
            if (view.isRowSelected(r) == true) {
                rows[count++] = r;
            }
        }
        return (rows);
    }
    
    /**
     * java CSVTable filename1 filename2 to filenameN will open all the files in
     * their own JTables.
     * 
     * @throws IOException
     */
    public static void main(String argv[]) throws IOException {
        if (argv.length < 1) {
            System.out.println("Usage: java CSVTable <file.csv> ...");
            System.out.println("to open a window for each file listed.");
            System.exit(-1);
        }
        CSVTable[] csvTables = new CSVTable[argv.length];
        for (int i = 0; i < argv.length; i++) {
            csvTables[i] = new CSVTable(argv[i]);
            CSVTableView frame = new CSVTableView(csvTables[i], 600, 150, false, false);
            frame.setVisible(true);
        }
    }
}



/** Shut down Java when all views have closed. */
class CSVWindowMonitor extends WindowAdapter {
    private static int numberWindows = 0;

    public CSVWindowMonitor() {
        super();
        numberWindows++;
    }

    // can't sync a static, so we sync this method
    public synchronized void windowClosing(WindowEvent e) {
        Window w = e.getWindow();
        w.setVisible(false);
        w.dispose();
        numberWindows--;
        if (numberWindows == 0) {
            System.exit(0);
        }
    }
}
