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
    private List<Integer> lineHeights;

    /**
     * 每一行的子view
     */
    List<View> lineViews;

    public FlowLayout(Context context) {
        this(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHorizontalSpacing = DisplayUtil.dip2px(context,15);
        mVerticalSpacing = DisplayUtil.dip2px(context,8);

        init();
    }

    private void init() {
        lineViews = new ArrayList<>();
        lineHeights = new ArrayList<>();
        allLines = new ArrayList<>();
    }

    private void clean(){
        lineViews.clear();
        lineHeights.clear();
        allLines.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        clean();
        // 测量子view
        // 获取子view的个数
        int childCount = this.getChildCount();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        // 整个流式布局的宽度和高度
        // 宽度是行中宽度的最大值
        int parentNeededWidth = 0;
        // 高度是行高的累加
        int parentNeededHeight = 0;

        //获取限制值
        int selfWidth = MeasureSpec.getSize(widthMeasureSpec);
        int selfHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 记录当前行的宽高
        // 宽度是当前行的所有子view之和
        int lineWidthUsed = 0;
        // 高度是当前行所有子view中高度的最大值
        int lineHeight = 0;

        // 先测量子view，再根据子view，测量自己的
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView,widthMeasureSpec,heightMeasureSpec);

            // 获取child的测量宽高，这里请不要使用getWidth，因为这个方法是在onLayout之后才会有值
            int childMeasureWidth = childView.getMeasuredWidth();
            int childMeasureHeight = childView.getMeasuredHeight();

            // 已经放入的宽度如果大于父容器宽度，就换行
            if (childMeasureWidth + lineWidthUsed > selfWidth){
                allLines.add(lineViews);
                // 创建新的一行
                lineViews = new ArrayList<>();
                //换行之后我们需要加上之前的宽度和行距和之前已经使用的宽度，高度同理
                parentNeededHeight += lineHeight;
                parentNeededWidth = Math.max(parentNeededWidth,lineWidthUsed);

                lineHeights.add(lineHeight);
                lineWidthUsed = 0;
                lineHeight = 0;
            }

            //记录每一个child
            lineViews.add(childView);
            //记录每一行使用的size
            lineWidthUsed += childMeasureWidth;
            //记录每一行的行高
            lineHeight = Math.max(lineHeight,childMeasureHeight);

            // 处理最后一行的显示
            if (i == childCount - 1){
                parentNeededHeight += lineHeight;
                parentNeededWidth = Math.max(parentNeededWidth,lineWidthUsed);
                lineHeights.add(lineHeight);
                allLines.add(lineViews);
            }
        }

        int realWidth = (widthMode == MeasureSpec.EXACTLY) ? selfWidth : parentNeededWidth;
        int realHeight = (heightMode == MeasureSpec.EXACTLY) ? selfHeight : parentNeededHeight;

        //测量本view
        setMeasuredDimension(realWidth,realHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineCount = allLines.size();

        int curLeft = 0;
        int curTop = 0;

        // 处理每一行中的view
        for (int i = 0; i < lineCount; i++) {
            List<View> lineView = allLines.get(i);
            int lineHeight = lineHeights.get(i);
            // 一行的处理
            for (int i1 = 0; i1 < lineView.size(); i1++) {
                View view = lineView.get(i1);

                int right = curLeft + view.getMeasuredWidth();
                int bottom = curTop + view.getMeasuredHeight();

                view.layout(curLeft, curTop,right,bottom);

                curLeft += view.getMeasuredWidth();
            }
            curLeft = 0;
            curTop += lineHeight;
        }

    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
