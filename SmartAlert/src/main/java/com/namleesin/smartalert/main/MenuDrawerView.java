package com.namleesin.smartalert.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.namleesin.smartalert.R;

import java.util.ArrayList;

/**
 * Created by nanjui on 2016. 2. 20..
 */
public class MenuDrawerView extends LinearLayout
{
    public class MenuDrawerItem
    {
        int icon_res;
        String item_txt;
    }

    private LayoutInflater mInflater;
    private ArrayList<MenuDrawerItem> mItemArray = new ArrayList<>();
    private MenuDrawerAdapter mAdapter;
    private ListView mMenuListView;

    public MenuDrawerView(Context context) {
        super(context);
        initView(context);
    }

    public MenuDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }


    private void initView(Context context)
    {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.layout_menu_drawer, this);
        mMenuListView = (ListView) findViewById(R.id.list_menu_drawer);
        TypedArray icon_array = getResources().obtainTypedArray(R.array.menu_icon);
        String text_array[] = getResources().getStringArray(R.array.menu_str);

        ArrayList<MenuDrawerItem> item_array = new ArrayList<>();
        mAdapter = new MenuDrawerAdapter(getContext());
        for(int i=0; i<icon_array.length(); i++)
        {
            MenuDrawerItem item = new MenuDrawerItem();
            item.icon_res = icon_array.getResourceId(i, 0);
            item.item_txt = text_array[i];
            item_array.add(item);
        }

        mAdapter.setItem(item_array);
        mMenuListView.setAdapter(mAdapter);
    }

    public void setOnMenuItemClickListener(AdapterView.OnItemClickListener l)
    {
        mMenuListView.setOnItemClickListener(l);
    }

    public void setCloseClickListener(OnClickListener l)
    {
        ImageButton btn_close = (ImageButton) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(l);
    }

    public class MenuDrawerAdapter extends BaseAdapter
    {
        MenuDrawerAdapter(Context context)
        {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItem(ArrayList<MenuDrawerItem> items) {
            mItemArray.clear();
            mItemArray.addAll(items);
        }

        @Override
        public int getCount() {
            return mItemArray.size();
        }

        @Override
        public Object getItem(int i) {
            return mItemArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.layout_menu_drawer_item, viewGroup, false);
            }

            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
            icon.setImageResource(mItemArray.get(i).icon_res);
            TextView item = (TextView) convertView.findViewById(R.id.text);
            item.setText(mItemArray.get(i).item_txt);

            return convertView;
        }
    }
}
