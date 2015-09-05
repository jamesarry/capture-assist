package com.blog.ljtatum.captureassist.logger;

import android.util.Log;

import com.blog.ljtatum.captureassist.constants.Constants;

/**
 * Created by Tatum on 8/8/2015.
 */
public class Logger {

    /**
     * Helper method for logging e-verbose
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (msg != null) {
            if (Constants.DEBUG) {
                Log.e(tag, msg);
            }
        }
    }

    /**
     * Helper method for logging d-verbose
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (msg != null) {
            if (Constants.DEBUG) {
                Log.d(tag, msg);
            }
        }
    }

    /**
     * Helper method for logging i-verbose
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (msg != null) {
            if (Constants.DEBUG) {
                Log.i(tag, msg);
            }
        }
    }

    /**
     * Helper method for logging v-verbose
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (msg != null) {
            if (Constants.DEBUG) {
                Log.v(tag, msg);
            }
        }
    }

    /**
     * Helper method for logging w-verbose
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (msg != null) {
            if (Constants.DEBUG) {
                Log.w(tag, msg);
            }
        }
    }

    /**
     * Helper method to display data on Console
     *
     * @param msg message to be displayed
     * @return null
     */
    public static void printOnConsole(String msg) {
        if (Constants.DEBUG) {
            System.out.println(msg);
        }
    }
}