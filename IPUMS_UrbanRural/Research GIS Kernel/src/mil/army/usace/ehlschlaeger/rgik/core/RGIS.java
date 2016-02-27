package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.rgik.util.MyReader;



/**
 * Root class for all RGIK classes.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public abstract class RGIS implements Serializable {
    /** Target folder for all outputs. */
    protected static File outputFolder = new File(".").getAbsoluteFile();
    /** System logger. */
    protected static Logger log = Logger.getLogger(RGIS.class.getName());
    
	protected RGIS() {
    }

    /**
     * The target directory for all generated files. Defaults to current
     * directory at the time that the RGIS class was loaded (generally the dir
     * from which the system was started.)
     */
	public static File getOutputFolder() {
	    return outputFolder;
	}

	    /**
     * Change the target directory for all generated files. This is NOT created,
     * so ensure it exists before anyone tries to write a file.
     * 
     * @param folder
     *            new output directory
     * @throws IOException
     *             if output folder cannot be created
     */
	public static void setOutputFolder(File folder) throws IOException {
	    outputFolder = folder.getAbsoluteFile();
	}

    /** setVerbose( true) will instruct the RGIS function to print out many 
     *  details of variable values. setVerbose() should be used for debugging
     *  purposes.
     */
    public static void setVerbose( boolean value) {
        if(value)
            log.setLevel(Level.FINE);
        else
            log.setLevel(Level.WARNING);
    }

    public static boolean getVerbose() {
        return(log.isLoggable(Level.FINE));
    }
	
    /**
     * Quicky helper to open files the way readLineNoNull likes.
     * 
     * @throws IOException
     *             if file doesn't exist.
     * @deprecated use {@link MyReader} instead.
     */
    public FileReader openFile(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName);
        return fr;
    }

    /**
     * Reads past end of file are converted into exceptions. Exceptions are
     * wrapped with another exception that includes the file name in the
     * message.
     * 
     * @throws IOException
     *             on any error.
     * @deprecated use {@link MyReader#readLine()} instead.
     */
    public String readLineNoNull(BufferedReader br, String fileName)
            throws IOException {
        String s = "";
        try {
            s = br.readLine();
            if (s == null)
                throw new IOException("Read past end of file " + fileName);
        } catch (IOException ioe) {
            throw new IOException("Error reading file: " + fileName, ioe);
        }
        return (s);
    }

// DEBUG: Un-comment this to help debug serialization.
//        Output is vast, be sure to log to a file.
//  private void writeObject(java.io.ObjectOutputStream stream)
//          throws IOException {
//      System.out.println(this);
//  }
}
