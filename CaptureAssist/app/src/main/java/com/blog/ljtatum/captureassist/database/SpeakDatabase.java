package com.blog.ljtatum.captureassist.database;

import com.blog.ljtatum.captureassist.logger.Logger;

/**
 * Created by Emmanuel on 8/15/2015.
 */
public class SpeakDatabase {

    private final static String TAG = SpeakDatabase.class.getSimpleName();

    public static class Welcome {

        public static String message(int id) {
            String msg = "";

            if (id == 0) {
                msg = "Hello Detective, my name is Android 6";
            } else if (id == 1) {

            } else if (id == 2) {


            } else {
                Logger.e(TAG, "Error: message id not in scope");
            }
            return msg;
        }
    }
}
