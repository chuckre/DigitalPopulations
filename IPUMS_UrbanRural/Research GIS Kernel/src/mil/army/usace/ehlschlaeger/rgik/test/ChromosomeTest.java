package mil.army.usace.ehlschlaeger.rgik.test;

import mil.army.usace.ehlschlaeger.rgik.gene.Chromosome;

/**
 * Not a unit test; we don't have a good way to test all the random methods.
 */
public class ChromosomeTest {
    /** in alpha testing */
    public static void main( String argv[]) {
        Chromosome[] testCs = new Chromosome[ 4];
        testCs[ 0] = new Chromosome();
        testCs[ 0].setNumberParameters( 1);
        testCs[ 0].setParameter( 10, 0.0, 1.0);
        //testCs[ 0].setParameter( 10, 0.0, 1.0);
        //testCs[ 0].setParameter( 10, 0.0, 1.0);
        testCs[ 0].print();
        testCs[ 0].printAllele();
        for( int i = 1; i < testCs.length; i++) {
            testCs[ i] = new Chromosome( testCs[ 0]);
            testCs[ i].print();
            testCs[ i].printAllele();
        }
        System.out.println("");
        testCs[ 0].printAllele();
        testCs[ 1].printAllele();
        testCs[ 0].print();
        testCs[ 1].print();
        testCs[ 0].cross( testCs[ 1]);
        testCs[ 0].printAllele();
        testCs[ 1].printAllele();
        testCs[ 0].print();
        testCs[ 1].print();
        testCs[ 0].cross( testCs[ 1]);
        testCs[ 0].printAllele();
        testCs[ 1].printAllele();
        testCs[ 0].print();
        testCs[ 1].print();
        testCs[ 0].cross( testCs[ 1]);
        testCs[ 0].printAllele();
        testCs[ 1].printAllele();
        testCs[ 0].print();
        testCs[ 1].print();
        testCs[ 0].cross( testCs[ 1]);
        testCs[ 0].printAllele();
        testCs[ 1].printAllele();
        testCs[ 0].print();
        testCs[ 1].print();
    }
}
