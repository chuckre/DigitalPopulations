package mil.army.usace.ehlschlaeger.rgik.util;

import java.lang.reflect.Method;
import java.util.Arrays;



/**
 * Runs a class's <code>main</code> method after having user press enter. Useful
 * for profiling, as some profilers won't decorate code that was loaded before
 * the profiler.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class DelayedRunMain {
    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            System.out.println("Usage: java "+DelayedRunMain.class.getName()+" <class name> <class's args>");
            System.exit(-1);
        }
            
        System.out.println("Press the ENTER key to start");
        System.in.read();

        // Find class and its main()
        Class<?> cls = Class.forName(args[0]);
        Method meth = cls.getMethod("main", String[].class);
        
        // Cut out class name
        String[] methargs = Arrays.copyOfRange(args, 1, args.length);
        // We must wrap args in an array as Java misinterprets String[] as a
        // list of params to main(), rather than an object to be passed in as
        // the first and only param to main().
        meth.invoke(null, new Object[] { methargs });
    }
}
