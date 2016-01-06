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

import com.namleesin.smartalert.R;

import java.util.ArrayList;

/**
 * Created by chitacan on 2015. 12. 30..
 */
public class TimelineListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<TimelineData> mDataArray;
    private PackageManager mPkgMgr;

    TimelineListAdapter(Context context)
    {
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
        if(convertView == null)
        {
            convertView = mInflater.inflate(R.layout.layout_timeline_list, parent, false);
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
        }
        return convertView;
    }
}
