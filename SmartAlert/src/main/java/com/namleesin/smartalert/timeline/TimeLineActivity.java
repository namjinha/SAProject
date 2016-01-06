package com.namleesin.smartalert.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.widget.ListView;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.dbmgr.DBValue;

import java.util.ArrayList;

/**
 * Created by chitacan on 2015. 12. 24..
 */
public class TimeLineActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<TimelineData>> {
    public static String TIMELINE_TYPE = "type";
    public static String TIMELINE_PKG = "pkg";
    public static int TYPE_PACKAGE = 0;

    public static int TYPE_FAVORITE = 1;
    public static int TYPE_HATE = 2;
    public static int TYPE_TIME = 3;


    private int type;
    private String param;
    private int queryType;
    private TimelineListAdapter mAdapter;
    private ListView mTimelineListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        type = intent.getIntExtra(TIMELINE_TYPE, 0);

        if(type == TYPE_PACKAGE)
        {
            param = intent.getStringExtra(TIMELINE_PKG);
            Log.d("NJ LEE", "param : " + param);
            queryType = DBValue.TYPE_SELECT_PACKAGE_INFO;
        }

        setContentView(R.layout.activity_timeline);
        mTimelineListView = (ListView) findViewById(R.id.timeline_list);
        mTimelineListView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(1001, null, this).forceLoad();
    }

    @Override
    public Loader<ArrayList<TimelineData>> onCreateLoader(int id, Bundle args) {

        return new TimelineDataLoader(this, queryType, param);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<TimelineData>> loader, ArrayList<TimelineData> data) {
        Log.d("NJ LEE", "data : "+data);
        if(data != null && data.size() > 0)
        {
            mAdapter.setData(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<TimelineData>> loader) {

    }
}
