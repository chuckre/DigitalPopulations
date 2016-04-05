package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.util.logging.Logger;

import mil.army.usace.ehlschlaeger.rgik.util.LogUtil;


/**
 * Simple helper to log progress periodically, only when progress is actually made.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 *
 * @author William R. Zwicky
 */
public class ProgressToy {
    private Logger log;
    private float period = 0;
    private String note = "Built";
    private long outputStarted = -1;
    private long nextReport;
    private long lastPercent;
    private long total;

    /**
     * Create reporter that reports periodically.
     * 
     * @param log
     *            receiver of reports
     * @param periodSecs
     *            how often to report progress
     * @param totalUnits
     *            if >0, progress will be reported as a percentage of this
     *            amount. If <=0, number will be reported as given.
     */
    public ProgressToy(Logger log, float periodSecs, long totalUnits) {
        this.log = log;
        this.period = periodSecs;
        this.total = totalUnits;
        
        outputStarted = System.currentTimeMillis();
        nextReport = outputStarted + (long)(period*1000);
    }

    /**
     * Create reporter that reports periodically.
     * 
     * @param log
     *            receiver of reports
     * @param periodSecs
     *            how often to report progress
     * @param totalUnits
     *            if >0, progress will be reported as a percentage of this
     *            amount. If <=0, number will be reported as given.
     * @param note
     *            text to print before number or percentage
     */
    public ProgressToy(Logger log, float periodSecs, long totalUnits, String note) {
        this(log, periodSecs, totalUnits);
        this.note = note;
    }

    /**
     * @return amount of time that has passed since this instance was created, in seconds
     */
    public double elapsed() {
        long now = System.currentTimeMillis();
        return (now-outputStarted) / 1000.0;
    }
    
    /**
     * Report progress. Only logs a report if time as elapsed AND progress has
     * actually changed.
     * 
     * @param unitsComplete number of items that are now complete
     */
    public synchronized void printProgress(long unitsComplete) {
        long now = System.currentTimeMillis();
        // Report only once every N seconds.
        if(now > nextReport) {
            while(now > nextReport)
                nextReport += (long)(period*1000);
            
            // Don't report if no progress made.
            long pct;
            if(total <= 0)
                pct = unitsComplete;
            else
                pct = (int) Math.round(100.0*unitsComplete/total);
            
            if(pct > lastPercent) {
                if(total <= 0)
                    LogUtil.progress(log, "  %s %d", note, pct);
                else {
                    if(pct < 100)
                        LogUtil.progress(log, "  %s %d%%", note, pct);
                    else
                        LogUtil.progress(log, "  Finishing tasks ...");
                }
                lastPercent = pct;
            }
        }
    }

    /**
     * Update internal state for given amount of work, but don't actually print
     * a report.
     * 
     * @param unitsComplete number of items that are now complete
     * 
     * @return true if a progress report should be logged now, false if not yet
     */
    public synchronized boolean updateProgress(long unitsComplete) {
        long now = System.currentTimeMillis();
        boolean rtn = false;
        
        // Report only once every N seconds.
        if(now > nextReport) {
            while(now > nextReport)
                nextReport += (long)(period*1000);
            
            // Don't report if no progress made.
            long pct;
            if(total <= 0)
                pct = unitsComplete;
            else
                pct = (int) Math.round(100.0*unitsComplete/total);
            
            if(pct > lastPercent) {
                rtn  = true;
                lastPercent = pct;
            }
        }
        return rtn;
    }
}
