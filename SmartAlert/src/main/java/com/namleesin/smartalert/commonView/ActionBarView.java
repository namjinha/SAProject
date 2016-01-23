package com.namleesin.smartalert.commonView;

import android.app.Notification;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.namleesin.smartalert.R;

import org.w3c.dom.Attr;

/**
 * Created by chitacan on 2016. 1. 14..
 */
public class ActionBarView extends LinearLayout {
    public static final int ACTIONBAR_TYPE_MAIN     = 0;
    public static final int ACTIONBAR_TYPE_ACTIVITY = 1;
    public static final int ACTIONBAR_TYPE_VIEW     = 2;

    private View.OnClickListener mGraghBtnListener;
    private OnClickListener mFinishBtnListener;
    private OnClickListener mMenuBtnListener;

    public ActionBarView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs);
    }

    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View main = inflater.inflate(R.layout.layout_actionbar_view, this, false);
        addView(main);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ActionBarView,
                0, 0);

        int type = a.getInteger(R.styleable.ActionBarView_actionbarType, -1);
        String title = a.getString(R.styleable.ActionBarView_actionbarTitle);
        initView(type, title);
    }

    public void initView(int type, String title)
    {
        LinearLayout main_layout = (LinearLayout) findViewById(R.id.title_main);
        LinearLayout activity_layout = (LinearLayout) findViewById(R.id.title_activity);

        if(type == ACTIONBAR_TYPE_MAIN)
        {
            main_layout.setVisibility(View.VISIBLE);
            activity_layout.setVisibility(View.GONE);
        }
        else if(type > 0)
        {
            main_layout.setVisibility(View.GONE);
            activity_layout.setVisibility(View.VISIBLE);
            if (type == ACTIONBAR_TYPE_VIEW)
            {
                activity_layout.findViewById(R.id.back_arrow).setVisibility(View.GONE);
            }

            if(title != null) {
                TextView view = (TextView) findViewById(R.id.title_txt);
                view.setText(title);
            }
        }
    }

    public void setTitleText(String text)
    {
        TextView view = (TextView) findViewById(R.id.title_txt);
        view.setText(text);
    }

    public void setTitleType(int type, String titleStr)
    {
        initView(type, titleStr);
        invalidate();
    }

    public void setOnGraphButtonListener(OnClickListener l)
    {
        mGraghBtnListener = l;
    }

    public void setOnFinishButtonListener(OnClickListener l)
    {
        mFinishBtnListener = l;
    }

    public void OnFinishButtonListener(View v)
    {
        if(mFinishBtnListener != null)
        {
            mFinishBtnListener.onClick(v);
        }
    }

    public void OnGraphButtonListener(View v)
    {

    }

    public void setOnMenuButtonListener(OnClickListener l)
    {
        mMenuBtnListener = l;
    }

    public void OnMenuButtonListener(View v)
    {
        mMenuBtnListener.onClick(v);
    }
}
