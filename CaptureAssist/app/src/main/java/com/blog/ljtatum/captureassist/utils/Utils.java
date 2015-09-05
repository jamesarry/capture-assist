package com.blog.ljtatum.captureassist.utils;

/**
 * Created by Tatum on 8/15/2015.
 */
public class Utils {

    private static final String EMPTY = "";
    private static final String NULL = "null";

    /**
     * Method checks if String value is empty
     *
     * @param str
     * @return
     */
    public static boolean isStringEmpty(String str) {
        return str == null || str.length() == 0 || EMPTY.equals(str.trim()) || NULL.equals(str);
    }

    /**
     * Method is used to check if objects are null
     *
     * @param objectToCheck
     * @param <T>
     * @return
     */
    public static <T> boolean checkIfNull(T objectToCheck) {
        return objectToCheck == null;
    }

}
