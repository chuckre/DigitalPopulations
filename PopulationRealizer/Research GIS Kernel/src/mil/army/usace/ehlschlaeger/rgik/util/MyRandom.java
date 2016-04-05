package mil.army.usace.ehlschlaeger.rgik.util;

import java.io.Serializable;
import java.util.List;
import java.util.Random;



/**
 * Extra tools for generating random numbers.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
public class MyRandom extends Random implements Serializable {
    /**
     * Produce a random integer from the uniform distribution between two
     * values.
     * 
     * @param source
     *            a random-number generator
     * @param min
     *            smallest value acceptable
     * @param max
     *            largest value acceptable
     * @return a number between <code>min</code> and <code>max</code>
     *         (inclusive)
     */
    public static int nextInt(Random source, int min, int max) {
        return min + source.nextInt(max-min+1);
    }

    /**
     * Produce a random integer from the uniform distribution between two
     * values.
     * 
     * @param source
     *            a random-number generator
     * @param min
     *            smallest value acceptable
     * @param max
     *            largest value acceptable
     * @return a number between <code>min</code> and <code>max</code>
     *         (inclusive)
     */
    public static long nextLong(Random source, long min, long max) {
        // nextDouble() produces from range [0,1).
        double r = source.nextDouble();
        // Since 1.00 is impossible, max+1 will never be generated, but every
        // other integer is equally likely.
        return (long) Math.floor(min + r * (max - min + 1));
    }

    /**
     * Produce a random double "minus a little" (max will never be produced.)
     * 
     * @param source
     *            a random-number generator
     * @param min
     *            smallest value acceptable
     * @param max
     *            smallest value&gt;min that is NOT acceptable
      * @return a random number in [min,max)
     */
    public static double nextDoubleM(Random source, double min, double max) {
        double r = source.nextDouble();
        return min + (max-min)*r;
    }
    
    /**
     * Produce a random number from a specific set of doubles. Value will be at
     * least <code>min</code>, never more than <code>max</code>, and a multiple
     * of <code>increment</code> away from <code>min</code>. <code>max</code>
     * will never be generated if it is not an increment from <code>min</code>.
     * 
     * @param source
     *            a random-number generator
     * @param min
     *            smallest value acceptable
     * @param max
     *            largest value acceptable
     * @param increment
     *            spacing of allowable values
     * @return a number between <code>min</code> and <code>max</code>
     *         (inclusive) that is a multiple of multiple of
     *         <code>increment</code> away from <code>min</code>
     */
    public static double nextDouble(Random source, double min, double max, double increment) {
        long maxindex = (long) Math.floor((max - min) / increment);
        long index = nextLong(source, 0, maxindex);
        double value = min + (index * increment);
        return value;
    }

    /**
     * Produce a random number, normally distributed, with a give mean and
     * standard deviation.
     * 
     * @param source
     *            a random-number generator
     * @param mean
     *            mean value of range
     * @param std_dev
     *            standard deviation of range. If zero, result will always be
     *            mean.
     * 
     * @return a random number
     */
    public static double nextGaussian(Random source, double mean, double std_dev) {
        // Yup, it really is this simple.
        return source.nextGaussian() * std_dev + mean;
    }

    /**
     * Return a random element from a list.
     * 
     * @param <T> datatype of list elements
     * @param source random number generator
     * @param list list of elements
     * 
     * @return one of the elements from the list\
     */
    public static <T> T pick(Random source, List<T> list) {
        int i = source.nextInt(list.size());
        return list.get(i);
    }
}
