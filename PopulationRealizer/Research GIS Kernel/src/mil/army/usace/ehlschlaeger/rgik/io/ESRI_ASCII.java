package mil.army.usace.ehlschlaeger.rgik.io;

import java.io.File;
import java.io.FileNotFoundException;



/**
 * Common utils for working with ESRI ASCII files. It would be nice to put the
 * load and write methods here, but that didn't work out. The file format is
 * simple and well known, but the way each class interprets the file varies.
 * Thus the best place for those methods are in with their classes.
 * <p>
 * <UL>
 *   <LI> {@link GISGrid} handles the file header.
 *   <LI> {@link GISClass} can load and save integer data.
 *   <LI> {@link GISLattice} can load and save floating-point data.
 *   <LI> {@link GIS
 * </UL>
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class ESRI_ASCII {
    /**
     * Standard file extension for ESRI ASCII files.
     */
    public static final String FILE_EXTENSION = ".asc";

    /**
     * Determine most plausible file for given partial path and name. Adds
     * ".asc" extension if necessary, and returns absolute path. Use this for
     * files that must already exist.
     * 
     * @param path
     *            abbreviated file spec (path may be absolute or relative;
     *            extension is optional)
     * @return absolute path to exactly the file requested
     * @throws FileNotFoundException
     *             if no appropriate file can be found.
     */
    public static File findFile(String path) throws FileNotFoundException {
        File file = new File(path);
        if (!file.exists()) {
            file = new File(path + FILE_EXTENSION);
            if (!file.exists())
                throw new FileNotFoundException(path);
        }
        return file.getAbsoluteFile();
    }

    /**
     * Add our extension if missing. Use this for files that are about to be
     * created.
     * 
     * @param path
     *            string path to fixup
     * @return updated string
     */
    public static String fixName(String path) {
        if(!path.toLowerCase().endsWith(FILE_EXTENSION))
            path += FILE_EXTENSION;
        return path;
    }
}
