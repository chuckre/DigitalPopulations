package mil.army.usace.ehlschlaeger.rgik.core;


/**
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author Chuck Ehlschlaeger
 */
public abstract class ErrorDistributionModel extends RGIS implements RGISFunction  {
	private GISData appRegion;

	public ErrorDistributionModel() {
		super();
	}

	public GISData getApplicationArea() {
		return appRegion;
	}

	public void setApplicationArea( GISData applicationArea) {
		appRegion = applicationArea;
	}

	public abstract GISData makeInitialMap();
}
