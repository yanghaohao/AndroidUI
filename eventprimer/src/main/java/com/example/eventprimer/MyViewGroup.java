package com.example.eventprimer;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.view.MotionEventCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyViewGroup extends LinearLayout {

    private MyViewGroup2 rl2;
    private MyViewGroup3 rl3;
    private View currentView;
    private boolean isIntercept;

    public MyViewGroup(Context context) {
        this(context,null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        rl2 = findViewById(R.id.mvg_2);
        rl3 = findViewById(R.id.mvg_3);
        Log.e(StringConstants.TAG, "MyViewGroup: dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int a = ev.getPointerCount();
        if (a == 1){
            if (ev.getAction() == MotionEvent.ACTION_DOWN){
                if (rl2.getTop() < ev.getY() && rl2.getBottom() > ev.getY()){
                    Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent rl2 instance");
                    currentView = rl2;
                }else if (rl3.getTop() < ev.getY() && rl3.getBottom() > ev.getY()){
                    Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent rl3 instance");
                    currentView = rl3;
                }
                Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent getActionIndex " + ev.getActionIndex());

            }
        }
//        Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent " + ev.getActionMasked() + ev.findPointerIndex(1));
        if (a >= 2 && ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
            Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent getActionIndex " + ev.getActionIndex());
            if ((currentView instanceof MyViewGroup2) && rl2.getTop() < ev.getY(ev.findPointerIndex(1)) && rl2.getBottom() > ev.getY(ev.findPointerIndex(1))){
                Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent rl2");
            }else if ((currentView instanceof MyViewGroup3) && rl3.getTop() < ev.getY(ev.findPointerIndex(1)) && rl3.getBottom() > ev.getY(ev.findPointerIndex(1))){
                Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent rl3");
            }else {
                isIntercept = true;
                Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent true");
                return true;
            }
        }
        Log.e(StringConstants.TAG, "MyViewGroup: onInterceptTouchEvent false");
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int a = event.getPointerCount();
        if (a >= 2){
            for (int i = 0; i < a; i++) {
                Log.e(StringConstants.TAG, "MyViewGroup: onTouchEvent getActionIndex " + event.getActionIndex());
            }
            Log.e(StringConstants.TAG, "MyViewGroup: onTouchEvent getActionIndex " + event.findPointerIndex(0) + "\t" + event.findPointerIndex(1));
            Log.e(StringConstants.TAG, "MyViewGroup: onTouchEvent 调用\t" + event.getPointerId(0) + "\t" + event.getPointerId(1) + "\t" + event.getActionIndex());

            split(event,event.findPointerIndex(0));
            return true;
        }
        return super.onTouchEvent(event);
    }

    public final MotionEvent split(MotionEvent oldEvent, int idBits) {
        MotionEvent ev = MotionEvent.obtainNoHistory(oldEvent);
        Log.e(StringConstants.TAG, "MyViewGroup: onTouchEvent getActionIndex aaaaa" + ev.getPointerCount());

//        synchronized (gSharedTempLock) {
//            final int oldPointerCount = nativeGetPointerCount(mNativePtr);
//            ensureSharedTempPointerCapacity(oldPointerCount);
//            final MotionEvent.PointerProperties[] pp = gSharedTempPointerProperties;
//            final MotionEvent.PointerCoords[] pc = gSharedTempPointerCoords;
//            final int[] map = gSharedTempPointerIndexMap;
//
//            final int oldAction = nativeGetAction(mNativePtr);
//            final int oldActionMasked = oldAction & ACTION_MASK;
//            final int oldActionPointerIndex = (oldAction & ACTION_POINTER_INDEX_MASK)
//                    >> ACTION_POINTER_INDEX_SHIFT;
//            int newActionPointerIndex = -1;
//            int newPointerCount = 0;
//            for (int i = 0; i < oldPointerCount; i++) {
//                nativeGetPointerProperties(mNativePtr, i, pp[newPointerCount]);
//                final int idBit = 1 << pp[newPointerCount].id;
//                if ((idBit & idBits) != 0) {
//                    if (i == oldActionPointerIndex) {
//                        newActionPointerIndex = newPointerCount;
//                    }
//                    map[newPointerCount] = i;
//                    newPointerCount += 1;
//                }
//            }
//
//            if (newPointerCount == 0) {
//                throw new IllegalArgumentException("idBits did not match any ids in the event");
//            }
//
//            final int newAction;
//            if (oldActionMasked == ACTION_POINTER_DOWN || oldActionMasked == ACTION_POINTER_UP) {
//                if (newActionPointerIndex < 0) {
//                    // An unrelated pointer changed.
//                    newAction = ACTION_MOVE;
//                } else if (newPointerCount == 1) {
//                    // The first/last pointer went down/up.
//                    newAction = oldActionMasked == ACTION_POINTER_DOWN
//                            ? ACTION_DOWN
//                            : (getFlags() & FLAG_CANCELED) == 0 ? ACTION_UP : ACTION_CANCEL;
//                } else {
//                    // A secondary pointer went down/up.
//                    newAction = oldActionMasked
//                            | (newActionPointerIndex << ACTION_POINTER_INDEX_SHIFT);
//                }
//            } else {
//                // Simple up/down/cancel/move or other motion action.
//                newAction = oldAction;
//            }
//
//            final int historySize = nativeGetHistorySize(mNativePtr);
//            for (int h = 0; h <= historySize; h++) {
//                final int historyPos = h == historySize ? HISTORY_CURRENT : h;
//
//                for (int i = 0; i < newPointerCount; i++) {
//                    nativeGetPointerCoords(mNativePtr, map[i], historyPos, pc[i]);
//                }
//
//                final long eventTimeNanos = nativeGetEventTimeNanos(mNativePtr, historyPos);
//                if (h == 0) {
//                    ev.initialize(nativeGetDeviceId(mNativePtr), nativeGetSource(mNativePtr),
//                            nativeGetDisplayId(mNativePtr),
//                            newAction, nativeGetFlags(mNativePtr),
//                            nativeGetEdgeFlags(mNativePtr), nativeGetMetaState(mNativePtr),
//                            nativeGetButtonState(mNativePtr), nativeGetClassification(mNativePtr),
//                            nativeGetXOffset(mNativePtr), nativeGetYOffset(mNativePtr),
//                            nativeGetXPrecision(mNativePtr), nativeGetYPrecision(mNativePtr),
//                            nativeGetDownTimeNanos(mNativePtr), eventTimeNanos,
//                            newPointerCount, pp, pc);
//                } else {
//                    nativeAddBatch(ev.mNativePtr, eventTimeNanos, pc, 0);
//                }
//            }
            return ev;
//        }
    }
}
