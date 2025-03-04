package com.example.base

import android.content.Context
import android.text.TextPaint

/**
 * dp、sp转换为px的工具类
 * @author younghow
 * @date 2020/11/11
 */
class DisplayUtil {
    fun getTextWidth(Context: Context, text: String?, textSize: Int): Float {
        val paint = TextPaint()
        val scaledDensity = Context.resources.displayMetrics.scaledDensity
        paint.textSize = scaledDensity * textSize
        return paint.measureText(text)
    }

    companion object {
        /**
         * 将sp值转换为px值，保证文字大小不变
         * @param context
         * @param spValue
         * @return
         */
        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * dpi 是会根据手机的不同而变化的  所以 要动态的计算的
         * @param context
         * @param dp
         * @return
         */
        fun convertDpToPixel(context: Context, dp: Int): Int {
            val displayMetrics = context.resources.displayMetrics
            return (dp * displayMetrics.density).toInt()
        }
    }
}

/**
 * 将px值转换为dip或dp值，保证尺寸大小不变
 * @param context 上下文
 * @return dp的值
 */
fun Int.dp(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this / scale + 0.5f).toInt()
}

/**
 * 将dip或dp值转换为px值，保证尺寸不变
 * @param context 上下文
 * @param dipValue dp
 * @return px
 */
fun Int.px(context: Context): Int {
    val scale = context.resources.displayMetrics.density
    return (this * scale + 0.5f).toInt()
}

/**
 * 将px值转换为sp值，保证文字大小不变
 * @param context
 * @param pxValue
 * @return
 */
fun Int.sp(context: Context): Int {
    val fontScale = context.resources.displayMetrics.scaledDensity
    return (this / fontScale + 0.5f).toInt()
}
