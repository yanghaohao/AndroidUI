package com.example.photoview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.OverScroller

class PhotoView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : View(context, attrs, defStyleAttr) {
        private var bitmap: Bitmap? = null
        private var paint: Paint? = null

        // 偏移量
        private var originalOffsetX = 0f
        private var originalOffsetY = 0f

        // 一边全屏，一边留白
        private var smallScale = 0f

        // 一边全屏，一边超出屏幕
        private var bigScale = 0f

        // 当前的缩放比例
        private var currentScale = 0f
        private var isEnlarge = false
        private var gestureDetector: GestureDetector? = null

        /**
         * 属性动画，放大缩小的效果
         */
        private var scaleAnimator: ObjectAnimator? = null
        private var offsetX = 0f
        private var offsetY = 0f
        private var overScroller: OverScroller? = null
        private var flingRunnable: FlingRunnable? = null
        private var scaleGestureDetector: ScaleGestureDetector? = null
        private var isScale = false

        fun setCurrentScale(currentScale: Float) {
            this.currentScale = currentScale
            invalidate()
        }

        fun getScaleAnimator(): ObjectAnimator? {
            if (scaleAnimator == null) {
                scaleAnimator = ObjectAnimator.ofFloat(this, "currentScale", 0f)
            }
            if (isScale) {
                isScale = false
                scaleAnimator!!.setFloatValues(smallScale, currentScale)
            } else {
                // 放大缩小(属性值)的范围
                scaleAnimator!!.setFloatValues(smallScale, bigScale)
            }
            return scaleAnimator
        }

        init {
            init(context)
        }

        private fun init(context: Context) {
            bitmap = BitmapFactory.decodeResource(resources, R.drawable.photo)
            paint = Paint()
            gestureDetector = GestureDetector(context, PhotoGestureListener())
            overScroller = OverScroller(context)
            flingRunnable = FlingRunnable()
            scaleGestureDetector = ScaleGestureDetector(context, PhotoScaleGestureListener())
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            // 双指操作优先
            val result = scaleGestureDetector!!.onTouchEvent(event)
            return if (!scaleGestureDetector!!.isInProgress) {
                gestureDetector!!.onTouchEvent(event)
            } else {
                result
            }
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val scaleFraction = (currentScale - smallScale) / (bigScale - smallScale)
            canvas.translate(offsetX * scaleFraction, offsetY * scaleFraction)
            canvas.scale(currentScale, currentScale, (width shr 1).toFloat(), (height shr 1).toFloat())
            // 绘制图片
            canvas.drawBitmap(bitmap!!, originalOffsetX, originalOffsetY, paint)
        }

        // onMeasure -> onSizeChanged
        // 每次尺寸改变时也会调用
        override fun onSizeChanged(
            w: Int,
            h: Int,
            oldw: Int,
            oldh: Int,
        ) {
            super.onSizeChanged(w, h, oldw, oldh)
            originalOffsetX = (width - bitmap!!.width) / 2f
            originalOffsetY = (height - bitmap!!.height) / 2f

            // 横向的图片
            if (bitmap!!.width / bitmap!!.height > width / height) {
                smallScale = width.toFloat() / bitmap!!.width
                bigScale = height.toFloat() / bitmap!!.height * OVER_SCALE_FACTOR
            } else {
                smallScale = height.toFloat() / bitmap!!.height
                bigScale = width.toFloat() / bitmap!!.width * OVER_SCALE_FACTOR
            }
            currentScale = smallScale
        }

        internal inner class PhotoGestureListener : GestureDetector.SimpleOnGestureListener() {
            /**
             * 事件UP时触发 双击的时候在第二次抬起的时候触发
             *
             * @param e
             * @return
             */
            override fun onSingleTapUp(e: MotionEvent): Boolean = false

            /**
             * 长按时触发  默认触发时间是300ms
             *
             * @param e
             */
            override fun onLongPress(e: MotionEvent) {}

            /**
             * 类似move事件
             *
             * @param e1
             * @param e2
             * @param distanceX 在 X 轴(单位时间)滑过的距离 旧位置减去新位置
             * @param distanceY 在 Y 轴(单位时间)滑过的距离 旧位置减去新位置
             * @return
             */
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float,
            ): Boolean {
                // 只有在放大的情况下才能进行移动
                if (isEnlarge) {
                    offsetX -= distanceX
                    offsetY -= distanceY
                    fixOffsets()
                    invalidate()
                }
                return false
            }

            /**
             * 抛掷(惯性)
             *
             * @param e1
             * @param e2
             * @param velocityX 在 X 轴(单位时间)滑过的距离 旧位置减去新位置
             * @param velocityY 在 Y 轴(单位时间)滑过的距离 旧位置减去新位置
             * @return
             */
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float,
            ): Boolean {
                if (isEnlarge) {
                    overScroller!!.fling(
                        offsetX.toInt(),
                        offsetY.toInt(),
                        velocityX.toInt(),
                        velocityY.toInt(),
                        -(bitmap!!.width * bigScale - width).toInt() / 2,
                        (bitmap!!.width * bigScale - width).toInt() / 2,
                        -(bitmap!!.height * bigScale - height).toInt() / 2,
                        (bitmap!!.height * bigScale - height).toInt() / 2,
                        300,
                        300,
                    )
                    postOnAnimation(flingRunnable)
                }
                return false
            }

            /**
             * 延时触发 100ms --> 为了处理点击效果，例:水波纹
             *
             * @param e
             */
            override fun onShowPress(e: MotionEvent) {}

            /**
             * 按下时触发 注意直接返回true
             *
             * @param e
             * @return
             */
            override fun onDown(e: MotionEvent): Boolean = true

            /**
             * 双击 --> 第二次点击的时候 触发 --> 事件 40ms(小于表示抖动,防抖动) -(双击) 300ms
             *
             * @param e
             * @return
             */
            override fun onDoubleTap(e: MotionEvent): Boolean {
                isEnlarge = !isEnlarge
                if (isEnlarge) {
                    offsetX = e.x - width / 2f - (e.x - (width shr 1)) * bigScale / smallScale
                    offsetY = e.y - height / 2f - (e.y - (height shr 1)) * bigScale / smallScale
                    fixOffsets()
                    // 启动属性动画
                    getScaleAnimator()!!.start()
                } else {
                    getScaleAnimator()!!.reverse()
                }
                return false
            }

            /**
             * 双击 --> 第二次的触摸事件(down,up,move)
             *
             * @param e
             * @return
             */
            override fun onDoubleTapEvent(e: MotionEvent): Boolean = false

            /**
             * 单击按下时触发,双击时不触发，延时300ms的tap事件
             *
             * @param e
             * @return
             */
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean = false
        }

        private fun fixOffsets() {
            offsetX = Math.min(offsetX, (bitmap!!.width * bigScale - width) / 2)
            offsetX = Math.max(offsetX, -(bitmap!!.width * bigScale - width) / 2)
            offsetY = Math.min(offsetY, (bitmap!!.height * bigScale - height) / 2)
            offsetY = Math.max(offsetY, -(bitmap!!.height * bigScale - height) / 2)
        }

        internal inner class FlingRunnable : Runnable {
            override fun run() {
                // 动画还在执行则返回true
                if (overScroller!!.computeScrollOffset()) {
                    offsetX = overScroller!!.currX.toFloat()
                    offsetY = overScroller!!.currY.toFloat()
                    invalidate()
                    // 每帧动画执行一次，性能更好
                    postOnAnimation(this)
                }
            }
        }

        internal inner class PhotoScaleGestureListener : ScaleGestureDetector.OnScaleGestureListener {
            private var initScale = 0f

            /**
             * 缩放
             *
             * @param detector
             * @return
             */
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (currentScale > smallScale && !isEnlarge || currentScale == smallScale && isEnlarge) {
                    isEnlarge = !isEnlarge
                }
                isScale = true
                currentScale = initScale * detector.scaleFactor
                invalidate()
                return false
            }

            /**
             * 缩放前 注意返回true 消费事件
             *
             * @param detector
             * @return
             */
            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                initScale = currentScale
                return true
            }

            /**
             * 缩放后
             *
             * @param detector
             */
            override fun onScaleEnd(detector: ScaleGestureDetector) {}
        }

        companion object {
            private const val OVER_SCALE_FACTOR = 1.5f
        }
    }
