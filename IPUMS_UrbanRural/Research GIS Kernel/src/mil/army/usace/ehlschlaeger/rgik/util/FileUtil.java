package mil.army.usace.ehlschlaeger.rgik.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;



/**
 * Collection of miscellaneous file and path utilities.
 *
 * @author William R. Zwicky
 */
public class FileUtil {
    /**
     * Find a file relative to another file.  "new File(parent,path)" is
     * broken (specifically, new File("C:\\","C:\\") improperly appends the two
     * strings, then fails to canonicalize.)
     *
     * @param parent object to resolve against, or null for current directory
     * @param path file path to locate
     * @return absolute, canonical path to given file, or null if 'path' is
     *     null or blank
     */
    public static File resolve(File parent, String path) {
        if(ObjectUtil.isBlank(path))
            return null;

        File f = new File(path);
        if(!f.isAbsolute() && parent != null)
            f = new File(parent, path);

        try {
            f = f.getCanonicalFile();
        } catch (IOException e) {
            f = f.getAbsoluteFile();
        }
        return f;
    }

    /**
     * Break a path down into individual elements and add to a list.
     * example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
     * Author: David M. Howard.
     *
     * @param f input file
     * @return a List collection with the individual elements of the path in reverse order
     */
    protected static List<String> getPathList(File f) throws IOException {
        List<String> l = new ArrayList<String>();
        File r = f.getAbsoluteFile();
        while (r != null) {
            l.add(r.getName());
            r = r.getParentFile();
        }
        return l;
    }

    /**
     * figure out a string representing the relative path of
     * 'f' with respect to 'r'
     * Author: David M. Howard.
     *
     * @param r home path
     * @param f path of file
     */
    protected static String matchPathLists(List<String> r, List<String> f) {
        int i;
        int j;
        String s;
        // start at the beginning of the lists
        // iterate while both lists are equal
        s = "";
        i = r.size() - 1;
        j = f.size() - 1;

        // first eliminate common root
        while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
            i--;
            j--;
        }

        // for each remaining level in the home path, add a ..
        for (; i >= 0; i--) {
            s += ".." + File.separator;
        }

        // for each level in the file path, add the path
        for (; j >= 1; j--) {
            s += f.get(j) + File.separator;
        }

        // file name
        s += f.get(j);
        return s;
    }

    /**
     * get relative path of File 'f' with respect to 'home' directory
     * <PRE>
     * example : home = /a/b/c
     *           f    = /a/d/e/x.txt
     *           s = getRelativePath(home,f) = ../../d/e/x.txt
     * </PRE>
     * Author: David M. Howard.
     *
     * @param home base path, should be a directory, not a file, or it doesn't make sense
     * @param f file to generate path for
     * @return path from home to f as a string
     */
    public static String relativize(File home, File f) throws IOException {
        List<String> homelist = getPathList(home);
        List<String> filelist = getPathList(f);

        String s = matchPathLists(homelist, filelist);

        return s;
    }

    /**
     * Rename a file, replacing file with same name if it exists.
     * 
     * @param srcFile file to rename
     * @param dstFile new name for file.  Will be deleted if it currently exists.
     * 
     * @throws IOException if unable to delete newFile or rename tempFile
     */
    public static void replaceFile(File srcFile, File dstFile) throws IOException {
        if(dstFile.exists()) {
            if(!dstFile.delete())
                throw new IOException(String.format("Unable to delete \"%s\"; results are in \"%s\".", dstFile, srcFile));
        }
        if(!srcFile.renameTo(dstFile))
            throw new IOException(String.format("Unable to rename \"%s\" to \"%s\".", srcFile, dstFile));
    }

    /**
     * Call replaceFile, writing errors to log file and returning quietly.
     * 
     * @param log receiver for errors
     * @param srcFile file to rename
     * @param dstFile new name for file
     */
    public static void replaceFile(Logger log, File srcFile, File dstFile) {
        try {
            FileUtil.replaceFile(srcFile, dstFile);
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }
}

