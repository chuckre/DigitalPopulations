package mil.army.usace.ehlschlaeger.rgik.core;

import java.io.Serializable;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsHousehold;



/**
 * Compute a number that represents the result of some analysis of a point.
 * <P>
 * Copyright Charles R. Ehlschlaeger, work: 309-298-1841, fax: 309-298-3003,
 * <http://faculty.wiu.edu/CR-Ehlschlaeger2/> This software is freely usable for
 * research and educational purposes. Contact C. R. Ehlschlaeger for permission
 * for other purposes. Use of this software requires appropriate citation in all
 * published and unpublished documentation.
 */
public interface TransformAttributes2double extends Serializable {
    /**
     * Compute a number that represents the result of some analysis of a point.
     * 
     * @param house
     *            object to evaluate
     * @return one floating point number. The meaning of the number depends on
     *         the implementation, but in general larger numbers should mean
     *         "better".
     */
    double getDouble(PumsHousehold house);
}
