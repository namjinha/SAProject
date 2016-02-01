package com.namleesin.smartalert.timeline;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.namleesin.smartalert.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by comus1200 on 2015. 12. 30..
 */
public class TimelineListAdapter extends BaseAdapter {

    private Context mCtx;
    private LayoutInflater mInflater;
    private ArrayList<TimelineData> mDataArray;
    private PackageManager mPkgMgr;
    private int mAdCnt = 0;

    TimelineListAdapter(Context context)
    {
        mCtx = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataArray = new ArrayList<>();
        mPkgMgr = context.getPackageManager();
    }

    public void setData(ArrayList<TimelineData> data)
    {
        mDataArray.clear();
        mDataArray.addAll(data);
    }

    @Override
    public int getCount() {
        return mDataArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null && position %5 == 0)
        {
            AdView adView = new AdView(mCtx);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(mCtx.getString(R.string.banner_ad_unit_id));
            AdRequest adRequest = new AdRequest.Builder()
                                     .build();
            adView.loadAd(adRequest);
            mAdCnt++;
            return adView;
        }

        if(convertView == null)
        {
            position -= mAdCnt;
            convertView = mInflater.inflate(R.layout.layout_timeline_list, parent, false);

            TextView tv_date = (TextView) convertView.findViewById(R.id.date);
            long dateLong = Long.valueOf(mDataArray.get(position).getDate());
            String date = new SimpleDateFormat("yyyy. MM. dd").format(new Date(dateLong));
            tv_date.setText(date);

            TextView tv_content = (TextView) convertView.findViewById(R.id.content);
            tv_content.setText(mDataArray.get(position).getContent());

            TextView tv_appname = (TextView) convertView.findViewById(R.id.app_name);
            tv_appname.setText(mDataArray.get(position).getAppName());

            ImageView iv_icon = (ImageView) convertView.findViewById(R.id.icon);
            try {
                Drawable icon = mPkgMgr.getApplicationInfo(mDataArray.get(position).getPkgName(), PackageManager.GET_UNINSTALLED_PACKAGES).loadIcon(mPkgMgr);
                iv_icon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            convertView.setTag(position);
        }
        return convertView;
    }
}
