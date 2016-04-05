package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.File;
import java.util.List;

import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

/**
 * Signals something is wrong with the data available
 * (too much, too little, inconsistent values, etc.)
 * Also contains static methods to validate user data.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class DataException extends RuntimeException {
    public DataException() {
    }

    public DataException(String message) {
        super(message);
    }

    public DataException(Throwable cause) {
        super(cause);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Throws if given value is null or "".  Use to validate incoming user data.
     * 
     * @param value object to test
     * @param message message to throw on failure
     */
    public static void verifyNotBlank(String value, String message) {
        if(ObjectUtil.isBlank(value))
            throw new DataException(message);
    }

    /**
     * Throws if given File object doesn't actually contain a path.
     * 
     * @param file object to test
     * @param message message to throw on failure
     */
    public static void verifyNotBlank(File file, String message) {
        if(file == null || ObjectUtil.isBlank(file.getPath()))
            throw new DataException(message);
    }

    /**
     * Throws if given list is null or empty.  Use to validate incoming user data.
     * 
     * @param list object to test
     * @param message message to throw on failure
     */
    public static void verifyNotEmpty(List<?> list, String message) {
        if(list == null || list.size() == 0)
            throw new DataException(message);
    }
    
    /**
     * Throws if given object is null.
     * 
     * @param object reference to test
     * @param message message to throw on failure
     */
    public static void verifyNotNull(Object object, String message) {
        if(object == null)
            throw new DataException(message);
    }
}
