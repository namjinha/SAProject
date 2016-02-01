package com.namleesin.smartalert.commonView;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.namleesin.smartalert.R;

/**
 * Created by nanjui on 2016. 1. 30..
 */
public class PullDownInputView extends LinearLayout implements View.OnTouchListener {
    private final int EXPAND_HEIGHT = 100;
    private float mY;
    private float mDistance;
    private float mMaxHeight = 0;
    private boolean isInputboxShown = false;

    public PullDownInputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_pulldown, this);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMaxHeight = getHeight() + EXPAND_HEIGHT;
                PullDownInputView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void initialize(Activity activity){
        activity.getWindow().getDecorView().setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        if(isInputboxShown == true)
            return false;

        View temp = findViewById(R.id.view_temp);
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, getResources().getDisplayMetrics());
                if(mDistance < h)
                {
                    h = 0;
                }
                else
                {
                    isInputboxShown = true;
                    EditText edit = (EditText) findViewById(R.id.keyword);
                    edit.setEnabled(true);
                }
                temp.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)h));
                break;
            case MotionEvent.ACTION_MOVE:
                mDistance = Math.abs(mY - event.getY());
                if(mDistance > mMaxHeight)
                {
                    mDistance = mMaxHeight;
                }
                mDistance *= 0.9;
                temp.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)mDistance));
                break;
        }
        return false;
    }
}
