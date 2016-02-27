package mil.army.usace.ehlschlaeger.rgik.statistics;

import java.io.IOException;
import java.io.PrintStream;

import mil.army.usace.ehlschlaeger.rgik.core.RGISFunction;



/**
 * Evaluates the "quality" of some subset of GIS data relative to the rest of
 * the data. Type of objects analyzed is defined by derived classes, and meaning
 * of "quality" is defined by actual implementations. Smaller values are
 * generally better.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public interface SpatialStatistic extends RGISFunction {
    /**
     * Create a "functional" copy of this object.  By "functional", we mean data that is 
     * commonly modified (i.e. statistical counts) is cloned, while other data (i.e. goals)
     * is shared.
     */
    public SpatialStatistic createCopy();

    /**
     * Returns spread between two spatial statistics. The parameter goalSpatialStatistic
     * will be the spatial statistic the spatial data uncertainty model "this" spatial statistic
     * will want the realizations to mimic.
     */
    public double spread(SpatialStatistic goal);

    /**
     * Generate human-readable detailed listing of object contents.
     * 
     * @param out place to write data
     * @throws IOException 
     */
    public void print(PrintStream out) throws IOException;

    /**
     * Generate human-readable detailed listing of object's current contents, as
     * compared to goal's contents. 'goal' generally needs to be the same
     * concrete type as 'this'.
     * 
     * @param out place to write data
     * @param goal
     */
    public void print(PrintStream out, SpatialStatistic goal) throws IOException;

    /**
     * If isMultiMap(), then this can be used to print information from the
     * individual maps.
     * 
     * @param mapNumber index of map data to print
     * @param out place to write data
     */
    public void printOneMapMeasure(int mapNumber, PrintStream out);

    /**
     * Report if this object is configured with more than one map.
     * @return false if we have a single map; true if more
     */
    public boolean isMultiMap();
}
