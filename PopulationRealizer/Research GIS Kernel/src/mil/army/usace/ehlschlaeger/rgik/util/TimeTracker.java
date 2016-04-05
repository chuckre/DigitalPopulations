/**
 * 
 */
package mil.army.usace.ehlschlaeger.rgik.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;


/**
 * Quicky util to find what's taking so long.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class TimeTracker {
    protected static Logger log = Logger.getLogger(TimeTracker.class.getPackage().getName());
    
    /** The time at which start() was called. */
    protected static Date start;
    /** The last time at which finished() was called. */
    protected static Date lastTime;

    /** Construction is prohibited; all methods are static. */
    private TimeTracker() {
    }
    
    /**
     * Call this to activate TimeTracker.  Simply importing it doesn't do it.
     */
    public static void start(String title) {
        start = new Date();
        lastTime = start;
        runSystemGC();
        long mem = usedMemory();
        float fmem = (float) (mem/1024.0/1024.0);
        LogUtil.progress(log, "TimeTracker: %s started at %s (%.1f MiB in use)",
            title, start.toString(), fmem);
    }

    /**
     * Report the cumulative time taken since start() was first called.
     * Best used when app shuts down.
     * Does nothing if start() was never called.  Does not report memory.
     */
    public static void total() {
        if(start != null) {
            LogUtil.progress(log, "TimeTracker: Application has taken %s",
                format(start, new Date()));
        }
    }
    
    /**
     * Log the completion of a stage of execution, and the amount of time it took.
     * Does nothing if start() was never called.
     * 
     * @param task description of work just completed
     */
    public static void finished(String task) {
        if(lastTime != null) {
            Date now = new Date();
            runSystemGC();
            long mem = usedMemory();
            float fmem = (float) (mem/1024.0/1024.0);
            LogUtil.progress(log, "TimeTracker: %s took %s (%.1f MiB in use)",
                      task, format(lastTime, now),
                      //(long)Math.round((now.getTime()-lastTime.getTime())/1000.0),
                      fmem);
            lastTime = now;
        }
    }

    /**
     * Format the difference between two Date objects as a human-readable string.
     * 
     * @param start first time marker
     * @param end   second time marker
     * @return human-readable string
     */
    public static String format(Date start, Date end) {
        if(start.after(end)) {
            Date t = start;
            start = end;
            end = t;
        }
        
        long delta = end.getTime()-start.getTime();
        return format(delta);
    }

    /**
     * Format an amount of time as a human-readable string.
     * 
     * @param millis number of milliseconds
     * @return human-readable string
     */
    public static String format(long millis) {
        // Round up for reporting purposes.
        long secs = (long) Math.ceil(millis/1000.0);
        
        long mins = secs/60;   //div
        secs %= 60;            //remainder
        
        long hrs = mins/60;
        mins %= 60;
        
        long days = hrs/24;
        hrs %= 24;

        ArrayList<String> items = new ArrayList<String>();
        if(days > 0)
            items.add(String.format("%d day", days));
        if(hrs > 0)
            items.add(String.format("%d hr", hrs));
        if(mins > 0)
            items.add(String.format("%d min", mins));
        if(secs > 0)
            items.add(String.format("%d sec", secs));
        
        return ObjectUtil.join(items, " ");
    }
    
    /**
     * Strongly encourage garbage collector to clean up as much as possible.
     * May stall program for a bit, but necessary to get an accurate read
     * from usedMemory().
     * 
     * @see <a href="http://www.roseindia.net/javatutorials/determining_memory_usage_in_java.shtml"
     *   >Determining Memory Usage in Java</a>
     */
    public static void runSystemGC() {
        long usedMem1 = usedMemory (), usedMem2 = Long.MAX_VALUE;
        for (int i = 0; (usedMem1 < usedMem2) && (i < 500); ++ i) {
            System.runFinalization();
            System.gc();
            Thread.yield();
            
            usedMem2 = usedMem1;
            usedMem1 = usedMemory ();
        }
    }
    
    /**
     * Report amount of memory currently used by the program.  May include
     * dropped but not yet collected.  It is recommended to runGC() first.
     * 
     * @return amount of memory in use by program, in bytes
     */
    public static long usedMemory() {
        long mem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        return mem;
    }
        
}
