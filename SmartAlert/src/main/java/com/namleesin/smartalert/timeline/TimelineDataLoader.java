package com.namleesin.smartalert.timeline;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class TimelineDataLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<TimelineData>>
{
	private PackageManager mPkgMgr;
	private DbHandler mDbHandler;
	private int mQueryType = 0;
	private String mParam;

	private ArrayList<TimelineData> noti_data_list = null;

	public TimelineDataLoader(Context context, int queryType, String param)
	{
		super(context);
		mPkgMgr = context.getPackageManager();
		mDbHandler = new DbHandler(context);
		mQueryType = queryType;
		mParam = param;
	}

	@Override
	public ArrayList<TimelineData> loadInBackground()
	{
		noti_data_list = new ArrayList<TimelineData>();
		Cursor cursor = mDbHandler.selectDBData(mQueryType, mParam);
		if(cursor == null || cursor.getCount() == 0)
			return noti_data_list;

		cursor.moveToFirst();
		do
		{
			String pkgName = cursor.getString(DBValue.CULNUM_PACKAGE);
			TimelineData data = null;
			try {
				data = new TimelineData()
                        .setPkgName(pkgName)
                        .setContent(cursor.getString(DBValue.CULNUM_SUBTXT))
                        .setAppName((String) mPkgMgr.getApplicationInfo(pkgName, PackageManager.GET_UNINSTALLED_PACKAGES).loadLabel(mPkgMgr))
                        .setLikeStatus(cursor.getInt(DBValue.CULNUM_STATUS))
						.setDate(cursor.getString(DBValue.CULNUM_DATE));

				noti_data_list.add(data);
			}
			catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}while(cursor.moveToNext());

		return noti_data_list;
	}

	@Override protected void onStartLoading() {
	}

	@Override public void deliverResult(ArrayList<TimelineData> datas) {

		super.deliverResult(datas);
	}
}
