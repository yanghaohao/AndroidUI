package com.example.flowerlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.example.base.dp

/**
 * @author : younghow
 * @date : 2022/1/25 19:40
 * description:
 */
class FlowLayout
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ViewGroup(context, attrs, defStyleAttr) {
        /**
         * 每个item的横向间距
         */
        private var mHorizontalSpacing = 0

        /**
         * 每个item的纵向间距
         */
        private var mVerticalSpacing = 0

        /**
         * 记录所有行，一行一行地存储，用于layout
         */
        private var allLines: MutableList<List<View>?>? = null

        /**
         * 记录每一行地行高,用于layout
         */
        private var lineHeights: MutableList<Int>? = null

        /**
         * 每一行的子view
         */
        var lineViews: MutableList<View>? = null

        init {
            mHorizontalSpacing = 15.dp(context)
            mVerticalSpacing = 8.dp(context)
            init()
        }

        private fun init() {
            lineViews = ArrayList()
            lineHeights = ArrayList()
            allLines = ArrayList()
        }

        private fun clean() {
            lineViews!!.clear()
            lineHeights!!.clear()
            allLines!!.clear()
        }

        override fun onMeasure(
            widthMeasureSpec: Int,
            heightMeasureSpec: Int,
        ) {
            clean()
            // 测量子view
            // 获取子view的个数
            val childCount = this.childCount
            val paddingLeft = paddingLeft
            val paddingRight = paddingRight
            val paddingTop = paddingTop
            val paddingBottom = paddingBottom

            // 整个流式布局的宽度和高度
            // 宽度是行中宽度的最大值
            var parentNeededWidth = 0
            // 高度是行高的累加
            var parentNeededHeight = 0

            // 获取限制值
            val selfWidth = MeasureSpec.getSize(widthMeasureSpec)
            val selfHeight = MeasureSpec.getSize(heightMeasureSpec)
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)

            // 记录当前行的宽高
            // 宽度是当前行的所有子view之和
            var lineWidthUsed = 0
            // 高度是当前行所有子view中高度的最大值
            var lineHeight = 0

            // 先测量子view，再根据子view，测量自己的
            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)

                // 获取child的测量宽高，这里请不要使用getWidth，因为这个方法是在onLayout之后才会有值
                val childMeasureWidth = childView.measuredWidth
                val childMeasureHeight = childView.measuredHeight

                // 已经放入的宽度如果大于父容器宽度，就换行
                if (childMeasureWidth + lineWidthUsed > selfWidth) {
                    allLines!!.add(lineViews)
                    // 创建新的一行
                    lineViews = ArrayList()
                    // 换行之后我们需要加上之前的宽度和行距和之前已经使用的宽度，高度同理
                    parentNeededHeight += lineHeight
                    parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed)
                    lineHeights!!.add(lineHeight)
                    lineWidthUsed = 0
                    lineHeight = 0
                }

                // 记录每一个child
                lineViews!!.add(childView)
                // 记录每一行使用的size
                lineWidthUsed += childMeasureWidth
                // 记录每一行的行高
                lineHeight = Math.max(lineHeight, childMeasureHeight)

                // 处理最后一行的显示
                if (i == childCount - 1) {
                    parentNeededHeight += lineHeight
                    parentNeededWidth = Math.max(parentNeededWidth, lineWidthUsed)
                    lineHeights!!.add(lineHeight)
                    allLines!!.add(lineViews)
                }
            }
            val realWidth = if (widthMode == MeasureSpec.EXACTLY) selfWidth else parentNeededWidth
            val realHeight = if (heightMode == MeasureSpec.EXACTLY) selfHeight else parentNeededHeight

            // 测量本view
            setMeasuredDimension(realWidth, realHeight)
        }

        override fun onLayout(
            changed: Boolean,
            l: Int,
            t: Int,
            r: Int,
            b: Int,
        ) {
            val lineCount = allLines!!.size
            var curLeft = 0
            var curTop = 0

            // 处理每一行中的view
            for (i in 0 until lineCount) {
                val lineView = allLines!![i]
                val lineHeight = lineHeights!![i]
                // 一行的处理
                for (i1 in lineView!!.indices) {
                    val view = lineView[i1]
                    val right = curLeft + view.measuredWidth
                    val bottom = curTop + view.measuredHeight
                    view.layout(curLeft, curTop, right, bottom)
                    curLeft += view.measuredWidth
                }
                curLeft = 0
                curTop += lineHeight
            }
        }

        override fun equals(obj: Any?): Boolean = super.equals(obj)
    }
