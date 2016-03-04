package com.namleesin.smartalert.settingmgr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.namleesin.smartalert.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter
{
    private class ViewHolder
    {
        public ImageView imageview;
        public TextView textview;
        public CheckBox checkBox;
    }

    private Context mContext = null;
    private ArrayList<ListViewItem> mListData = new ArrayList<ListViewItem>();

    public ListViewAdapter(Context mContext)
    {
        super();
        this.mContext = mContext;
    }

    public void setData(ArrayList<ListViewItem> aListData)
    {
        this.mListData = aListData;
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
            convertView = inflater.inflate(R.layout.noti_listitem, null);

            holder.imageview = (ImageView) convertView.findViewById(R.id.appicon);
            holder.textview = (TextView) convertView.findViewById(R.id.appname);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkstate);

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
            holder.imageview.setVisibility(View.GONE);
        }

        holder.textview.setText(data.mAppName);

        if(1 == data.mFilterState)
        {
            holder.checkBox.setChecked(true);
        }
        else
        {
            holder.checkBox.setChecked(false);
        }

        return convertView;
    }
}


