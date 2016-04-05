package mil.army.usace.ehlschlaeger.rgik.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.rgik.io.StringOutputStream;

/**
 * Helper to clean and organize our use of java.util.logging.
 * <P>
 * Recommended startup:<PRE>
 * LogUtil.getRootLogger().setLevel(Level.INFO);
 * LogUtil.cleanFormat();
 * </PRE>
 * <P>
 * Use this sequence to log to a file, while sending a reduced copy to the console:<PRE>
 * LogUtil.getRootLogger().setLevel(Level.INFO);
 * LogUtil.quietConsole();
 * LogUtil.cleanFormat();
 * LogUtil.setOutput(new File("dir/output.log").getCanonicalPath());
 * </PRE>
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class LogUtil {
    // OFF      Integer.MAX_VALUE
    // SEVERE   1000
    // WARNING   900
    
    /** Level for announcing progress (percent complete, phase finished, etc.) */
    public static final Level PROGRESS = new LogLevel("PROGRESS", 890);
    /** Level for announcing results (numbers of things). */
    public static final Level RESULT = new LogLevel("RESULT", 880);
    /** Level for announcing details leading to results (items skipped, intermediate values). */
    public static final Level DETAIL = new LogLevel("DETAIL", 870);
    
    // INFO      800
    // CONFIG    700
    // FINE      500
    // FINER     400
    // FINEST    300
    
    /** Log a blank line at all levels. */
    protected static final LogRecord NEWLINE = new LogRecord(Level.OFF, "");

    /** Current output file. */
    protected static FileHandler fh = null;

    
    /** Instantiation is forbidden. */
    private LogUtil() {
    }
    
    /** Helper to fetch root logger. */
    public static Logger getRootLogger() {
        return Logger.getLogger("");
    }

    /**
     * Reset all handlers to our one-line CleanForammter.
     */
    public static void cleanFormat() {
        Logger root = getRootLogger();
        Formatter fmt = new CleanFormatter();
        for(Handler h : root.getHandlers()) {
            if (h instanceof ConsoleHandler) {
                h.setFormatter(fmt);
            }
        }
    }

    /**
     * Reset all handlers to our one-line CleanForammter.
     */
    public static void cleanFormat(Logger log) {
        Formatter fmt = new CleanFormatter();
        for(Handler h : log.getHandlers()) {
            h.setFormatter(fmt);
        }
    }    
    
    /**
     * Copy logging output to a file.  Closes previous file.
     * Uses our "clean" format.
     * 
     * @param pattern
     * @throws SecurityException
     * @throws IOException
     */
    public static void setOutput(String pattern) throws SecurityException, IOException {
        FileHandler new_fh = new FileHandler(pattern);
        new_fh.setFormatter(new CleanFormatter());
        
        Logger root = getRootLogger();
        synchronized (root) {
            root.addHandler(new_fh);
            if(fh != null)
                root.removeHandler(fh);
        }
        if(fh != null)
            fh.close();
        fh = new_fh;
    }

    /**
     * Set console to only print progress reports and errors.
     */
    public static void quietConsole() {
        Logger root = getRootLogger();
        for(Handler h : root.getHandlers()) {
            if (h instanceof ConsoleHandler) {
                h.setLevel(PROGRESS);
            }
        }
    }

    /**
     * Stops all logging to the console.
     */
    public static void disableConsole() {
        Logger root = getRootLogger();
        for(Handler h : root.getHandlers()) {
            if (h instanceof ConsoleHandler) {
                root.removeHandler(h);
            }
        }
    }

    /**
     * Helper to find currently configured log level.
     * 
     * @param log
     * 
     * @return configured or inherited log level of given logger, or null if no
     *         logger is configured
     */
    public static Level getEffectiveLevel(Logger log) {
        Level level;
        
        do {
            level = log.getLevel();
            if(level == null)
                log = log.getParent();
            else
                break;
        } while(log != null);
        
        return level;
    }
    
    /**
     * Helper to determine if a logger will write output from a given level.
     * 
     * @param log logger that will be used to write messages
     * @param desiredLevel level at which messages will be written
     * 
     * @return 'true' if messages will be written; 'false' if not
     */
    public static boolean allowed(Logger log, Level desiredLevel) {
        return getEffectiveLevel(log).intValue() >= desiredLevel.intValue();
    }
    
    /**
     * Log a blank line at all levels.
     * @param log
     */
    public static void cr(Logger log) {
        log.log(NEWLINE);
    }

    /**
     * Log a progress message.
     * @param log
     * @param message
     */
    public static void progress(Logger log, Object message) {
        log.log(PROGRESS, message.toString());
    }

    /**
     * Log a progress message.
     * @param log
     * @param format
     * @param args
     */
    public static void progress(Logger log, String format, Object... args) {
        log.log(PROGRESS, String.format(format, args));
    }
    
    /**
     * Log the result of some computation.
     * 
     * @param log
     * @param message
     */
    public static void result(Logger log, Object message) {
        log.log(RESULT, message.toString());
    }

    /**
     * Log the result of some computation.
     * 
     * @param log
     * @param format
     * @param args
     */
    public static void result(Logger log, String format, Object... args) {
        log.log(RESULT, String.format(format, args));
    }
    
    /**
     * Log detail explaining a computation or result.
     * @param log
     * @param message
     */
    public static void detail(Logger log, Object message) {
        log.log(DETAIL, message.toString());
    }

    /**
     * Log detail explaining a computation or result.
     * 
     * @param log
     * @param format
     * @param args
     */
    public static void detail(Logger log, String format, Object... args) {
        log.log(DETAIL, String.format(format, args));
    }
}



/**
 * Makes logging.Level instantiable.
 */
class LogLevel extends Level {
    public LogLevel(String name, int value) {
        super(name, value);
    }
}



/**
 * Print only messages to output, not names, dates, or levels.
 */
class CleanFormatter extends Formatter {
    private boolean did_cr = false;
    
    @Override
    public String format(LogRecord record) {
        // Our caller filters levels.
        
        // Print only one blank line.
        if("".equals(record.getMessage())) {
            if(did_cr)
                return "";
            else {
                did_cr = true;
                return "\n";
            }
        }
        else {
            did_cr = false;
        
            StringBuffer buf = new StringBuffer();
            
            // Header only in emergencies.
            if(record.getLevel().intValue() >= Level.WARNING.intValue())
                buf.append(record.getLevel()).append(": ");
//          buf.append(record.getLevel()).append(": ").append(record.getLoggerName()).append(": ");
            
            buf.append(formatMessage(record));

            // Ensure a newline at the end.
            char tail = (buf.length() == 0 ? '\0' : buf.charAt(buf.length() - 1));
            switch (tail) {
                case '\n':
                case '\r':
                    break;
                default:
                    buf.append("\n");
            }
            
            if(record.getThrown() != null) {
                StringOutputStream sw = new StringOutputStream();
                record.getThrown().printStackTrace(sw);
                buf.append("  ").append(sw.toString());
            }
            
            return buf.toString();
        }
    }
}
