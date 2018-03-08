package com.vandenbreemen.mobilesecurestorage.util;

/**
 * <h2>Intro</h2>
 * <p>
 * <h2>Other Details</h2>
 *
 * @author kevin
 */
public class NumberUtils {

    private NumberUtils() {
    }

    public static boolean empty(Number num) {
        return num == null || empty(num.doubleValue());
    }

    public static boolean empty(Double d) {
        return d == null || d == 0.;
    }

    /**
     * Unbox an integer
     *
     * @param i
     * @return
     */
    public static int integer(Integer i) {
        return i == null ? 0 : i;
    }

    /**
     * Format the given float value to a number with the given decimal places
     *
     * @param f
     * @param decimalPlaces
     * @return
     */
    public static String toString(float f, int decimalPlaces) {
        return String.format("%." + decimalPlaces + "f", f);
    }

    /**
     * Determines the absolute difference between the two values
     *
     * @param f1
     * @param f2
     * @return
     */
    public static float diff(float f1, float f2) {
        return Math.abs(f1 - f2);
    }

    /**
     * Returns either the number or the maximum
     *
     * @param num
     * @param max
     * @return
     */
    public static int thisOrMax(int num, int max) {
        if (num < max)
            return num;
        return max;
    }
}
