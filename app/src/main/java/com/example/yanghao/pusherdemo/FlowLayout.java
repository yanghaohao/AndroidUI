package com.example.yanghao.pusherdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : younghow
 * @date : 2022/1/25 19:40
 * description:
 */
public class FlowLayout extends ViewGroup {

    /**
     * 每个item的横向间距
     */
    private int mHorizontalSpacing = 0;
    /**
     * 每个item的纵向间距
     */
    private int mVerticalSpacing = 0;
    /**
     * 记录所有行，一行一行地存储，用于layout
     */
    private List<List<View>> allLines;
    /**
     * 记录每一行地行高,用于layout
     */
    private List<Integer> lineHeights = new ArrayList<>();

    public FlowLayout(Context context) {
        super(context);

        mHorizontalSpacing = DisplayUtil.dip2px(context,15);
        mVerticalSpacing = DisplayUtil.dip2px(context,8);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHorizontalSpacing = DisplayUtil.dip2px(context,15);
        mVerticalSpacing = DisplayUtil.dip2px(context,8);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHorizontalSpacing = DisplayUtil.dip2px(context,15);
        mVerticalSpacing = DisplayUtil.dip2px(context,8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        initMeasureParams();
        //测量子view
        int childCount = getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int parentNeededWidth = 0;
        int parentNeededHeight = 0;

        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);

        List<View> lineViews = new ArrayList<>();

        int lineWidthUsed = 0;
        int lineHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);

            LayoutParams childLayoutParams = childView.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,paddingLeft + paddingRight,childLayoutParams.width);
            int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec,paddingLeft + paddingRight,childLayoutParams.height);
            childView.measure(childWidthMeasureSpec,childHeightMeasureSpec);

            //获取child的测量宽高，这里请不要使用getWidth，因为这个方法是在onLayout之后才会有值
            int childMeasureWidth = childView.getMeasuredWidth();
            int childMeasureHeight = childView.getMeasuredHeight();

            if (childMeasureWidth + lineWidthUsed + mHorizontalSpacing > selfWidth){
                allLines.add(lineViews);
                lineHeights.add(lineHeight);
                //换行之后我们需要加上之前的宽度和行距和之前已经使用的宽度，高度同理
                parentNeededHeight = parentNeededHeight + lineHeight + mVerticalSpacing;
                parentNeededWidth = Math.max(parentNeededWidth,lineWidthUsed + mHorizontalSpacing);

                lineViews = new ArrayList<>();
                lineWidthUsed = 0;
                lineHeight = 0;
            }

            //记录每一个child
            lineViews.add(childView);

            //记录每一行使用的size
            lineWidthUsed = lineWidthUsed + childMeasureWidth + mHorizontalSpacing;

            //记录每一行的行高
            lineHeight = Math.max(lineHeight,childMeasureHeight);
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int realWidth = (widthMode == MeasureSpec.EXACTLY) ? selfWidth : parentNeededWidth;
        int realHeight = (heightMode == MeasureSpec.EXACTLY) ? selfHeight : parentNeededHeight;

        //测量本view
        setMeasuredDimension(realWidth,realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineCount = allLines.size();

        int curLeft = getPaddingLeft();
        int curTop = getPaddingTop();

        for (int i = 0; i < lineCount; i++) {
            List<View> lineView = allLines.get(i);
            int lineHeight = lineHeights.get(i);
            for (int i1 = 0; i1 < lineView.size(); i1++) {
                View view = lineView.get(i1);

                int right = curLeft + view.getMeasuredWidth();
                int bottom = curTop + view.getMeasuredHeight();

                view.layout(curLeft, curTop,right,bottom);

                curLeft = right + mHorizontalSpacing;
            }
            curLeft = getPaddingLeft();
            curTop = curTop + lineHeight + mVerticalSpacing;
        }

    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    private void initMeasureParams(){
        if(allLines != null){
            allLines.clear();
        }else {
            allLines = new ArrayList<>();
        }

        if (lineHeights != null){
            lineHeights.clear();
        }else {
            lineHeights = new ArrayList<>();
        }
    }
}
