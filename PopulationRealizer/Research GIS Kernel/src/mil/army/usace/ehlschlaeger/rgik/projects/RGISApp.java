/**
 * 
 */
package mil.army.usace.ehlschlaeger.rgik.projects;

import mil.army.usace.ehlschlaeger.rgik.core.RGIS;

/**
 * Parent class for all RGIK sample projects.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public abstract class RGISApp extends RGIS {
    private static boolean printed = false;

    /**
     * Default constructor.
     * Ensures our copyright is printed once when program starts.
     */
    public RGISApp() {
        if( printed == false) {
            printed = true;
            System.out.println( "This software is copyrighted by Charles Ehlschlaeger.");
            System.out.println( "This software is written in Java, and works for Java");
            System.out.println( "versions 1.6.0 and later. The software is part of the");
            System.out.println( "Research Geographic Information System (RGIS). RGIS is");
            System.out.println( "a public domain GISystem designed by Dr. Charles R.");
            System.out.println( "Ehlschlaeger as a research and educational tool.");
            System.out.println( "This software is freely usable for research and educational");
            System.out.println( "purposes. Contact C. R. Ehlschlaeger for permission for");
            System.out.println( "other uses. Use of this software requires appropriate");
            System.out.println( "citation in all published and unpublished documentation.");
            System.out.println( "Some of this software requires extensive testing before");
            System.out.println( "it can be considered bug free (this is version 0.4).");
            System.out.println( "email: cre111@wiu.edu");
            System.out.println();
        }
    }

}
