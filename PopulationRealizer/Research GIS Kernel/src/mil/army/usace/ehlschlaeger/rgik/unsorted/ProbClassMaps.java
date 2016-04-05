package mil.army.usace.ehlschlaeger.rgik.unsorted;

import java.io.IOException;
import java.util.Random;

import mil.army.usace.ehlschlaeger.rgik.core.GISClass;
import mil.army.usace.ehlschlaeger.rgik.core.GISLattice;
import mil.army.usace.ehlschlaeger.rgik.core.GISio;
import mil.army.usace.ehlschlaeger.rgik.core.RGIS;
import mil.army.usace.ehlschlaeger.rgik.core.RGISFunction;
import mil.army.usace.ehlschlaeger.rgik.util.BooleanGrid;




/**
 * ProbClassMaps class creates probability maps for qualitative thematic
 * conflation.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public class ProbClassMaps extends RGIS implements RGISFunction  {
	private int minCat, maxCat, numLags;
	private boolean isCat[];
	private GISLattice prob[];
	private double halfRes, res, maxDist, lagPercent[][];
	private GISio gisIO;
	private Runtime rt;

	public int getMinimumClass() {
		return minCat;
	}

	public int getMaximumClass() {
		return maxCat;
	}

	/** in alpha testing */
	public ProbClassMaps( GISClass goal, GISClass existing) {
		super();
		rt = Runtime.getRuntime();
		gisIO = new GISio( 12);
		res = Math.min( goal.getNSResolution(), goal.getEWResolution());
		halfRes = res / (double) 2.0;
		GISClass existResampled = new GISClass( goal, existing);
		minCat = existResampled.getMinimumValue();
		maxCat = existResampled.getMaximumValue();
		maxDist = existing.distance( 0, 0, existing.getNumberRows() - 1, existing.getNumberColumns() - 1);
		isCat = new boolean[ maxCat - minCat + 1];
		lagPercent = new double[ maxCat - minCat + 1][];
		for( int cat = minCat; cat <= maxCat; cat++) {
			int iCat = cat - minCat;
			isCat[ iCat] = false;
		}
		for( int r = existResampled.getNumberRows() - 1; r >= 0; r--) {
			for( int c = existResampled.getNumberColumns() - 1; c >= 0; c--) {
				if( ! existResampled.isNoData( r, c)) {
					int cat = existResampled.getCellValue( r, c);
					if( isCat[ cat - minCat] == false) {
						int iCat = cat - minCat;
						isCat[ iCat] = true;
					}
				}
			}
		}
		GISLattice distance[] = this.makeDistanceMaps( existResampled, minCat, maxCat, isCat);
 		int lagCount[][] = new int[ maxCat - minCat + 1][];
		numLags = (int) Math.ceil(maxDist * 2 / res);
		for( int cat = minCat; cat <= maxCat; cat++) {
			int iCat = cat - minCat;
if( isCat[ iCat]) {
			lagPercent[ iCat] = new double[ numLags];
			lagCount[ iCat] = new int[ numLags];
			for( int n = 0; n < numLags; n++) {
				lagPercent[ iCat][ n] = (double) 0.0;
				lagCount[ iCat][ n] = 0;
			}
			for( int rr = existResampled.getNumberRows() - 1; rr >= 0; rr--) {
				for( int cc = existResampled.getNumberColumns() - 1; cc >= 0; cc--) {
					if( ! goal.isNoData( rr, cc)) {
						double dist = distance[ iCat].getCellValue( rr, cc);
						int lag = getLag( dist);
						lagCount[ iCat][ lag]++;
						if( goal.getCellValue( rr, cc) == cat)
							lagPercent[ iCat][ lag] += 1.0f;
					}
				}
			}
			double lastPercent = (double) 0.0;
			for( int lag = numLags - 1; lag >= 0; lag--) {
				if( lagCount[ iCat][ lag] > 0) {
					lagPercent[ iCat][ lag] /= (double) lagCount[ iCat][ lag];
					lastPercent = lagPercent[ iCat][ lag];
				} else {
					lagPercent[ iCat][ lag] = lastPercent;
				}
			}
}
		}
		calcProb( distance);
	}

	/** in alpha testing */
	public ProbClassMaps( ProbClassMaps original, GISClass study) {
/*
		gisIO = new GISio( 12);
		res = Math.min( goal.getNSResolution(), goal.getEWResolution());
		halfRes = res / (double) 2.0;
		GISClass existResampled = new GISClass( goal, existing);
		minCat = existResampled.getMinimumValue();
		maxCat = existResampled.getMaximumValue();
		maxDist = existing.distance( 0, 0, existing.getNumberRows() - 1, existing.getNumberColumns() - 1;
		isCat = new boolean[ maxCat - minCat + 1];
		lagPercent = new double[ maxCat - minCat + 1][];
		numLags = new int[ maxCat - minCat + 1];
		for( int cat = minCat; cat <= maxCat; cat++) {
			int iCat = cat - minCat;
			isCat[ iCat] = false;
		}
		for( int r = existResampled.getNumberRows() - 1; r >= 0; r--) {
			for( int c = existResampled.getNumberColumns() - 1; c >= 0; c--) {
				if( ! existResampled.isNoData( r, c)) {
					int cat = existResampled.getCellValue( r, c);
					if( isCat[ cat - minCat] == false) {
						int iCat = cat - minCat;
						isCat[ iCat] = true;
					}
				}
			}
		}
		GISLattice distance[] = this.makeDistanceMaps( existResampled, minCat, maxCat, isCat);
		int lagCount = new int[ maxCat - minCat + 1][];
		numLags = (int) Math.ceil(maxDist * 2 / res);
		for( int cat = minCat; cat <= maxCat; cat++) {
			int iCat = cat - minCat;
			lagPercent[ iCat] = new double[ numLags];
			lagCount[ iCat] = new int[ numLags];
			for( int n = 0; n < numLags; n++) {
				lagPercent[ iCat][ n] = (double) 0.0;
				lagCount[ iCat][ n] = 0;
			}
			for( int rr = existResampled.getNumberRows() - 1; rr >= 0; rr--) {
				for( int cc = existResampled.getNumberColumns() - 1; cc >= 0; cc--) {
					double dist = distance[ iCat].getCellValue( rr, cc);
					int lag = getLag( dist);
					lagCount[ iCat][ lag]++;
					double same = (double) 0.0;
					if( goal.getCellValue( rr, cc) == cat)
						same = (double) 1.0;
					lagPercent[ iCat][ lag] += same;
				}
			}
			double lastPercent = (double) 0.0;
			for( int lag = numLags - 1; lag >= 0; lag--) {
				if( lagCount[ iCat][ lag] > 0) {
					lagPercent[ iCat][ lag] /= (double) lagCount[ iCat][ lag];
					lastPercent = lagPercent[ iCat][ lag];
				} else {
					lagPercent[ iCat][ lag] = lastPercent;
				}
			}
		}
		calcProb( distance);
*/
	}

	public int getLag( double distance) {
		return( (int) ((distance + maxDist) / res));
	}

	public GISLattice[] makeDistanceMaps( GISClass map, int minC, int maxC, boolean isCat[]) {
		GISLattice distance[] = new GISLattice[ maxC - minC + 1];
		for( int cat = minC; cat <= maxC; cat++) {
			int iCat = cat - minCat;
			if( isCat[ iCat]) {
				System.out.println( "cat: " + cat);
				distance[ iCat] = new GISLattice( map);
				BooleanGrid catMap = map.catBooleanMap( cat);
				distance[ iCat].distanceMap( catMap);
				catMap.reverse();
				GISLattice inCat = new GISLattice( map);
				inCat.distanceMap( catMap);
				for( int rr = map.getNumberRows() - 1; rr >= 0; rr--) {
					for( int cc = map.getNumberColumns() - 1; cc >= 0; cc--) {
						double dist = distance[ iCat].getCellValue( rr, cc);
						if( dist > halfRes)
							distance[ iCat].setCellValue( rr, cc, dist - halfRes);
						else
							distance[ iCat].setCellValue( rr, cc, halfRes - 
								inCat.getCellValue( rr, cc));
					}
				}
			}
		}
		return( distance);
	}

	public void freeLags() {
		if( lagPercent != null) {
			for( int i = 0; i < lagPercent.length; i++) {
				lagPercent[ i] = null;
			}
		}
		rt.gc();
	}

	public GISClass realize( long seed) {
		Random ran = new Random( seed);
		GISClass makeClass = new GISClass( prob[ 0]);
		for( int r = makeClass.getNumberRows() - 1; r >= 0; r--) {
			for( int c = makeClass.getNumberColumns() - 1; c >= 0; c--) {
				double rand = ran.nextDouble();
				int cat = minCat - 1;
				do {
					rand -= prob[ ++cat - minCat].getCellValue( r, c);
				} while( rand > (double) 0.0);
				makeClass.setCellValue( r, c, cat);
			}
		}
		return( makeClass);
	}

	public GISClass realize( GISLattice[] randMaps) {
		GISClass makeClass = new GISClass( prob[ 0]);
		for( int r = makeClass.getNumberRows() - 1; r >= 0; r--) {
			for( int c = makeClass.getNumberColumns() - 1; c >= 0; c--) {
				int count = 0;
				double sumPreviousProb = 0.0f;
				int cat = 0;
				for( cat = minCat; cat < maxCat; cat++) {
					double probCat = prob[ count].getCellValue( r, c);
					double doProb = probCat / (1.0f - sumPreviousProb);
					sumPreviousProb += probCat;
					double rand = randMaps[ count].getCellValue( r, c);
					if( rand <= doProb) {
						makeClass.setCellValue( r, c, cat);
						cat = maxCat + 1;
					}
					count++;
				}
				if( cat == maxCat) {
						makeClass.setCellValue( r, c, cat);
				}
			}
		}
		return( makeClass);
	}

	public void print() {
		System.out.println( "minCat: " + minCat + ", maxCat: " + maxCat);
		gisIO.printBuffered( " ");
		System.out.print( " ");
		for( int cat = minCat; cat <= maxCat; cat++) {
			int iCat = cat - minCat;
			if( isCat[ iCat]) {
				gisIO.printBuffered( " " + cat);
			}
			System.out.println( "");
		}
		for( int lag = 0; lag < numLags - 1; lag++) {
			for( int cat = minCat; cat <= maxCat; cat++) {
				if( isCat[ cat - minCat]) {
					boolean doStuff = false;
					for( int cc = minCat; cc <= maxCat; cc++) {
						if( lagPercent[ cc][ lag] > (double) 0.000001) {
							doStuff = true;
							cc = maxCat + 1;
						}
					}
					if( doStuff) {
						gisIO.printBuffered( " " + (res * lag - maxDist)); 
						// same variable name as parent loop on purpose
						for( cat = minCat; cat <= maxCat; cat++) {
							gisIO.printBuffered( " " + (lagPercent[ cat][ lag]));
						}
						System.out.println( "");
					}
				}
			}
		}
	}

	public void calcProb( GISLattice distance[]) {
		prob = new GISLattice[ maxCat - minCat + 1];
		for( int cat = minCat; cat <= maxCat; cat++) {
			if( isCat[ cat - minCat]) {
				int iCat = cat - minCat;
				prob[ iCat] = new GISLattice( distance[0]);
			}
		}
		for( int r = distance[0].getNumberRows() - 1; r >= 0; r--) {
			for( int c = distance[0].getNumberColumns() - 1; c >= 0; c--) {
				double total = (double) 0.0;
				for( int cat = minCat; cat <= maxCat; cat++) {
					int iCat = cat - minCat;
					if( isCat[ iCat]) {
						total += lagPercent[ iCat][ getLag( distance[ iCat].getCellValue( r, c))];
					}
				}
				for( int cat = minCat; cat <= maxCat; cat++) {
					int iCat = cat - minCat;
					if( isCat[ iCat]) {
						prob[ iCat].setCellValue( r, c,
							lagPercent[ iCat][ getLag( distance[ iCat].getCellValue( r, c))] / total);
					}
				}
			}
		}			
	}

	public void save( String fileHeader) throws IOException {
		for( int cat = minCat; cat <= maxCat; cat++) {
			if( isCat[ cat - minCat]) {
				this.prob[ cat - minCat].writeAsciiEsri( fileHeader + cat);
			}
		}
	}
}
