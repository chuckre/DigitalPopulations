package mil.army.usace.ehlschlaeger.rgik.statistics;

import mil.army.usace.ehlschlaeger.rgik.core.GISGrid;


/**
 * Statistic evaluator for data stored in a GISGrid.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public interface GridSpatialStatistic extends SpatialStatistic  {
    /**
     * Modifies spatial statistic based on swapping cell values in maps mapA and
     * mapB at location (row, col).
     */
	public void modify( GISGrid maps[], int mapA, int mapB, int row, int col);
}
