package com.yiyangzhu.yyweather;

import android.app.Activity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * This class programs the point from which classes can get a RequestQueue
 */
public class ProgramRequestQueue {

    private static RequestQueue queue;

    public static void initialize(Activity activity) {
        queue = Volley.newRequestQueue(activity);
    }

    public static RequestQueue getRequestQueue() {
        return queue;
    }
}
