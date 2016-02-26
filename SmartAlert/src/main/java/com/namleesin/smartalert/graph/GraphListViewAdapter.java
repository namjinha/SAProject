package com.namleesin.smartalert.graph;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.settingmgr.ListViewItem;

import java.util.ArrayList;

/**
 * Created by namjinha on 2016-02-23.
 */
public class GraphListViewAdapter extends BaseAdapter
{
    private int mMaxCount = 0;
    private class ViewHolder
    {
        public ImageView imageview;
        public TextView textview;
        public ProgressBar progressBar;
        public TextView conttextview;
    }

    private Context mContext = null;
    private ArrayList<ListViewItem> mListData = new ArrayList<ListViewItem>();

    public GraphListViewAdapter(Context mContext)
    {
        super();
        this.mContext = mContext;
    }

    public void setData(ArrayList<ListViewItem> aListData)
    {
        this.mListData = aListData;
    }

    public void setMacCount(int aMaxCount)
    {
        mMaxCount = aMaxCount;
    }

    @Override
    public int getCount()
    {
        return mListData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.graph_listitem, null);

            holder.imageview = (ImageView) convertView.findViewById(R.id.appicon);
            holder.textview = (TextView) convertView.findViewById(R.id.appname);
            holder.conttextview = (TextView) convertView.findViewById(R.id.appcounttxt);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
            holder.progressBar.setMax(mMaxCount);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        ListViewItem data = mListData.get(position);

        if (data.mAppIcon != null)
        {
            holder.imageview.setImageDrawable(data.mAppIcon);
        }
        else
        {
            holder.imageview.setImageResource(R.drawable.ic_launcher);
        }

        Log.d("namjinha", "data.mNotiCount = " + data.mNotiCount);
        holder.textview.setText(data.mAppName);
        holder.conttextview.setText("" + data.mNotiCount);

        holder.progressBar.setProgress(data.mNotiCount);

        return convertView;
    }
}


