package com.example.colortrackview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ColorTrackView extends AppCompatTextView {

    Paint originPaint,changePaint;
    private float mColorProgress = 0.5f;
    private Orientation orientation;

    public enum Orientation{
        LEFT2RIGHT,RIGHT2LEFT
    }

    public ColorTrackView(Context context) {
        this(context,null);
    }

    public ColorTrackView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorTrackView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initPaint(context,attrs);
    }

    private void initPaint(Context context,AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,R.styleable.ColorTrackView);

        int originColor = typedArray.getColor(R.styleable.ColorTrackView_originColor,getTextColors().getDefaultColor());
        int changeColor = typedArray.getColor(R.styleable.ColorTrackView_changeColor,getTextColors().getDefaultColor());

        // 回收
        typedArray.recycle();

        originPaint = getPaintByColor(originColor);
        changePaint = getPaintByColor(changeColor);
    }

    public Paint getPaintByColor(int color){
        Paint paint = new Paint();
        // 设置颜色
        paint.setColor(color);
        // 抗锯齿
        paint.setAntiAlias(true);
        // 防抖动
        paint.setDither(true);
        // 设置字体大小 就是textView的大小
        paint.setTextSize(getTextSize());
        return paint;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int currentPoint = (int) (mColorProgress * getWidth());

        if (orientation == Orientation.LEFT2RIGHT){
            drawText(canvas,originPaint,0,currentPoint);
            drawText(canvas,changePaint,currentPoint,getWidth());
        }else {
            drawText(canvas,changePaint,getWidth() - currentPoint,getWidth());
            drawText(canvas,originPaint,0,getWidth() - currentPoint);
        }
    }

    private void drawText(Canvas canvas,Paint paint,int start,int end){

        canvas.save();
        Rect rect1 = new Rect(start,0,end,getHeight());
        canvas.clipRect(rect1);

        String text = getText().toString();
        if (TextUtils.isEmpty(text)){ return; }

        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(),rect);
        // 获取x坐标
        int dx = getWidth() / 2 - rect.width() / 2;
        //获取基线
        Paint.FontMetricsInt fontMetricsInt = changePaint.getFontMetricsInt();
        int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        int baseLine = getHeight() / 2 + dy;
        // 绘制文字
        canvas.drawText(text,dx,baseLine,paint);

        canvas.restore();
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public void setColorProgress(float mColorProgress) {
        this.mColorProgress = mColorProgress;
        invalidate();
    }
}
