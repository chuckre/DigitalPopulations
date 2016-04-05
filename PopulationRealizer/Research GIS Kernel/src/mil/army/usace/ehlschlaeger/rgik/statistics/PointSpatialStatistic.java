package mil.army.usace.ehlschlaeger.rgik.statistics;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHouseholdRealization;

/**
 * Evaluate the quality of a map based on the location of its points. A value of
 * zero indicates optimal placement.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 */
public interface PointSpatialStatistic extends SpatialStatistic {
    /**
     * Modifies spatial statistic based on creating a point at a new location.
     * 
     * @param newPoint
     *            household that was added to map
     * @param mapNumber
     *            NOT USED, must be zero
     */
    public abstract void modifySS4NewPt(PumsHouseholdRealization newPoint, int mapNumber);

    /**
     * Modifies spatial statistic based on removing a point.
     * 
     * @param removedPoint
     *            household that was removed from map
     * @param mapNumber
     *            NOT USED, must be zero
     */
    public abstract void modifySS4RemovedPt(PumsHouseholdRealization removedPoint, int mapNumber);
}
