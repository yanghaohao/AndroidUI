package com.example.eventprimer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class MyViewGroup3 extends RelativeLayout {

    MyView my2;

    public MyViewGroup3(Context context) {
        super(context);
    }

    public MyViewGroup3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewGroup3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        my2 = findViewById(R.id.mv_2);
        Log.e(StringConstants.TAG, "MyViewGroup3: dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        my2.onTouchEvent(event);
        Log.e(StringConstants.TAG, "MyViewGroup3: onTouchEvent");
        return true;
    }
}
