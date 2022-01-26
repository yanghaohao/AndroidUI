package com.example.yanghao.pusherdemo;

import android.content.Context;
import android.text.TextPaint;
import android.util.DisplayMetrics;

/**
 * dp、sp转换为px的工具类
 * @author younghow
 * @date 2020/11/11
 */
public class DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * @param context 上下文
     * @param pxValue px的值
     * @return dp的值
     */
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    /**
     * 将dip或dp值转换为px值，保证尺寸不变
     * @param context 上下文
     * @param dipValue dp
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * dpi 是会根据手机的不同而变化的  所以 要动态的计算的
     * @param context
     * @param dp
     * @return
     */
    public static int convertDpToPixel(Context context,int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) (dp * displayMetrics.density);
    }

    public float getTextWidth(Context Context, String text, int textSize){
        TextPaint paint = new TextPaint();
        float scaledDensity = Context.getResources().getDisplayMetrics().scaledDensity;
        paint.setTextSize(scaledDensity * textSize);
        return paint.measureText(text);
    }
}
