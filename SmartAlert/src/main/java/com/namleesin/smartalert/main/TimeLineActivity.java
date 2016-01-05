package com.namleesin.smartalert.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by chitacan on 2015. 12. 24..
 */
public class TimeLineActivity extends Activity {
    public static String TIMELINE_TYPE = "type";
    public static String TIMELINE_PKG = "pkg";
    public static int TYPE_PACKAGE = 0;

    public static int TYPE_FAVORITE = 1;
    public static int TYPE_HATE = 2;
    public static int TYPE_TIME = 3;


    private int type;
    private String pkg_name;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        type = intent.getIntExtra(TIMELINE_TYPE, 0);

        if(type == TYPE_PACKAGE)
        {
            pkg_name = intent.getStringExtra(TIMELINE_PKG);
        }

    }
}
