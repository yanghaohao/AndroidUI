package com.example.fishdemo

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PointF
import android.graphics.drawable.Drawable
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class FishDrawable : Drawable() {
    private var mPaint: Paint? = null
    private var mPath: Path? = null

    // 鱼身的中心点(重心)
    private var mMiddlePoint: PointF? = null
    private val mFishMainAngle = 90f

    init {
        init()
    }

    private fun init() {
        // 画笔初始化
        mPaint = Paint()
        // 画笔类型--填充
        mPaint!!.style = Paint.Style.FILL
        // 设置颜色
        mPaint!!.setARGB(OTHER_ALPHA, 244, 92, 71)
        // 抗锯齿
        mPaint!!.isAntiAlias = true
        // 防抖
        mPaint!!.isDither = true

        // PointF为float型的坐标
        mMiddlePoint = PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS)

        // 路径
        mPath = Path()
        val valueAnimator = ValueAnimator.ofFloat(-1f, 1f)
        //周期
        valueAnimator.setDuration(1000)
        //设置循环模式，从-1到1，再从1到1
        valueAnimator.repeatMode = ValueAnimator.REVERSE
    }

    /**
     * 绘制，类似于自定义view中的onDraw()方法
     *
     * @param canvas
     */
    override fun draw(@NonNull canvas: Canvas) {
        val fishAngle = mFishMainAngle
        val headPoint = calculatePoint(mMiddlePoint, BODY_LENGTH / 2, fishAngle)
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint!!)

        // 鱼右鳍
        val rightFishFins = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle - 110)
        makeFins(canvas, rightFishFins, fishAngle, true)

        // 鱼左鳍
        val leftFishFins = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle + 110)
        makeFins(canvas, leftFishFins, fishAngle, false)

        // 身体底部的中心点坐标
        // 绘制节肢1
        val bodyBottomCenterPoint = calculatePoint(headPoint, BODY_LENGTH, fishAngle - 180)
        makeSegment(
            canvas,
            bodyBottomCenterPoint,
            BIG_CIRCLE_RADIUS,
            MIDDLE_CIRCLE_RADIUS,
            FIND_MIDDLE_CIRCLE_LENGTH,
            fishAngle,
            true
        )

        // 绘制节肢2
        val middleCircleCenterPoint =
            calculatePoint(bodyBottomCenterPoint, FIND_MIDDLE_CIRCLE_LENGTH, fishAngle - 180)
        makeSegment(
            canvas,
            middleCircleCenterPoint,
            MIDDLE_CIRCLE_RADIUS,
            SMALL_CIRCLE_RADIUS,
            FIND_SMALL_CIRCLE_LENGTH,
            fishAngle,
            false
        )

        // 绘制大三角形
        makeTriangle(
            canvas,
            middleCircleCenterPoint,
            FIND_TRIANGLE_LENGTH,
            BIG_CIRCLE_RADIUS,
            fishAngle
        )
        // 绘制小三角形
        makeTriangle(
            canvas,
            middleCircleCenterPoint,
            FIND_TRIANGLE_LENGTH - 10,
            BIG_CIRCLE_RADIUS - 20,
            fishAngle
        )
        makeBody(canvas, headPoint, bodyBottomCenterPoint, fishAngle)
    }

    /**
     * 绘制身体
     * @param canvas
     * @param headPoint 头部的点
     * @param bodyCenterPoint 鱼身体的中心点
     * @param fishAngle
     */
    private fun makeBody(
        canvas: Canvas,
        headPoint: PointF,
        bodyCenterPoint: PointF,
        fishAngle: Float
    ) {
        // 身体的四个点
        val topLeftPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle + 90)
        val topRightPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle - 90)
        val bottomLeftPoint = calculatePoint(bodyCenterPoint, BIG_CIRCLE_RADIUS, fishAngle + 90)
        val bottomRightPoint = calculatePoint(bodyCenterPoint, BIG_CIRCLE_RADIUS, fishAngle - 90)
        val controlLeft = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle + 130)
        val controlRight = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle - 130)

        // 画鱼身
        mPath!!.reset()
        mPath!!.moveTo(topLeftPoint.x, topLeftPoint.y)
        mPath!!.lineTo(topRightPoint.x, topRightPoint.y)
        mPath!!.quadTo(controlRight.x, controlRight.y, bottomRightPoint.x, bottomRightPoint.y)
        mPath!!.lineTo(bottomLeftPoint.x, bottomLeftPoint.y)
        mPath!!.quadTo(controlLeft.x, controlLeft.y, topLeftPoint.x, topLeftPoint.y)
        mPaint!!.alpha = BODY_ALPHA
        canvas.drawPath(mPath!!, mPaint!!)
    }

    /**
     * 绘制鱼尾(三角形)
     *
     * @param canvas
     * @param startPoint
     * @param findCenterLength 鱼尾的高度
     * @param findEdgeLength   鱼尾底边的一半
     * @param fishAngle
     */
    private fun makeTriangle(
        canvas: Canvas,
        startPoint: PointF,
        findCenterLength: Float,
        findEdgeLength: Float,
        fishAngle: Float
    ) {
        // 底部中心点的坐标
        val centerPoint = calculatePoint(startPoint, findCenterLength, fishAngle - 180)

        // 底部的两个点
        val leftPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle + 90)
        val rightPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle - 90)

        // 绘制三角形(鱼尾)
        mPath!!.reset()
        mPath!!.moveTo(leftPoint.x, leftPoint.y)
        mPath!!.lineTo(rightPoint.x, rightPoint.y)
        mPath!!.lineTo(startPoint.x, startPoint.y)
        canvas.drawPath(mPath!!, mPaint!!)
    }

    /**
     * 画节肢
     *
     * @param canvas
     * @param bottomCenterPoint     梯形中心点的坐标
     * @param bigRadius             大圆的半径
     * @param smallRadius           小圆的半径
     * @param findSmallCircleLength 寻找梯形小圆的线长
     * @param fishAngle
     * @param hasBigCircle          是否有大圆
     */
    private fun makeSegment(
        canvas: Canvas,
        bottomCenterPoint: PointF,
        bigRadius: Float,
        smallRadius: Float,
        findSmallCircleLength: Float,
        fishAngle: Float,
        hasBigCircle: Boolean
    ) {
        // 梯形上底的中心点
        val upperCenterPoint =
            calculatePoint(bottomCenterPoint, findSmallCircleLength, fishAngle - 180)

        // 梯形的四个顶点
        val bottomLeftPoint = calculatePoint(bottomCenterPoint, bigRadius, fishAngle + 90)
        val bottomRightPoint = calculatePoint(bottomCenterPoint, bigRadius, fishAngle - 90)
        val upperLeftPoint = calculatePoint(upperCenterPoint, smallRadius, fishAngle + 90)
        val upperRightPoint = calculatePoint(upperCenterPoint, smallRadius, fishAngle - 90)
        if (hasBigCircle) {
            //绘制大圆
            canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigRadius, mPaint!!)
        }
        //绘制小圆
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint!!)
        //绘制梯形
        mPath!!.reset()
        mPath!!.moveTo(bottomLeftPoint.x, bottomLeftPoint.y)
        mPath!!.lineTo(bottomRightPoint.x, bottomRightPoint.y)
        mPath!!.lineTo(upperRightPoint.x, upperRightPoint.y)
        mPath!!.lineTo(upperLeftPoint.x, upperLeftPoint.y)
        canvas.drawPath(mPath!!, mPaint!!)
    }

    /**
     * 绘制鱼鳍
     *
     * @param canvas
     * @param startPoint    起始点的坐标
     * @param fishHeadAngle 鱼头起始点的角度
     * @param isRightFins
     */
    fun makeFins(canvas: Canvas, startPoint: PointF, fishHeadAngle: Float, isRightFins: Boolean) {
        val controlAngle = 115f

        // 结束点
        val endPoint = calculatePoint(startPoint, FINS_LENGTH, fishHeadAngle - 180)
        // 控制点
        val controlPoint = calculatePoint(
            startPoint,
            1.8f * FINS_LENGTH,
            if (isRightFins) fishHeadAngle - controlAngle else fishHeadAngle + controlAngle
        )

        // 这个path容器不会主动删除里面的元素，所以每次调用前应该给它重置
        mPath!!.reset()
        mPath!!.moveTo(startPoint.x, startPoint.y)
        // 二阶贝塞尔曲线
        mPath!!.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y)
        canvas.drawPath(mPath!!, mPaint!!)
    }

    /**
     * 设置透明度的方法
     *
     * @param alpha 透明度
     */
    override fun setAlpha(alpha: Int) {
        mPaint!!.alpha = alpha
    }

    /**
     * 设置一个颜色过滤器，在绘制出来之前，被绘制内容的每一个像素都会被颜色过滤器改变
     *
     * @param colorFilter
     */
    override fun setColorFilter(@Nullable colorFilter: ColorFilter?) {
        mPaint!!.setColorFilter(colorFilter)
    }

    /**
     * 这个值可以根据setAlpha中设置的值进行调整，比如。alpha==0时设置为pixelFormat.TRANSPARENT,
     * 在alpha==255时，设置pixelFormat.OPAQUE,在其他时候设置pixelFormat.TRANSLUCENT
     * pixel.OPAQUE:完全不透明覆盖下面的所有内容
     * pixel.TRANSPARENT:透明完全不显示任何东西
     * pixelFormat.TRANSLUCENT:只有绘制的地方才覆盖下面的内容
     *
     * @return
     */
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    /**
     * 如果imageview的宽高是wrap_content,则获取这个值
     *
     * @return
     */
    override fun getIntrinsicHeight(): Int {
        return (8.38f * HEAD_RADIUS).toInt()
    }

    override fun getIntrinsicWidth(): Int {
        return (8.38f * HEAD_RADIUS).toInt()
    }

    companion object {
        // 除了身体之外的部分的透明度
        private const val OTHER_ALPHA = 110

        // 身体的透明度
        private const val BODY_ALPHA = 160
        private const val HEAD_RADIUS = 50f

        // 鱼身的长度
        private const val BODY_LENGTH = 3.2f * HEAD_RADIUS
        private const val FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS
        private const val FINS_LENGTH = 1.3f * HEAD_RADIUS

        // <---鱼尾--->
        // 尾部大圆的半径(圆心就是身体底部的中点)
        private const val BIG_CIRCLE_RADIUS = 0.7f * HEAD_RADIUS

        // 尾部中圆的半径
        private const val MIDDLE_CIRCLE_RADIUS = 0.6f * BIG_CIRCLE_RADIUS

        // 尾部小圆的半径
        private const val SMALL_CIRCLE_RADIUS = 0.4f * MIDDLE_CIRCLE_RADIUS

        // 寻找尾部中圆圆心的线长
        private const val FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS

        // 寻找尾部小圆圆心的线长
        private const val FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f)

        // 寻找大三角形底边中心点的线长
        private const val FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f

        /**
         * 求对应点的坐标
         *
         * @param startPoint 起始点的坐标
         * @param length     两点间的长度
         * @param angle      鱼头相对于x坐标的角度
         * @return
         */
        fun calculatePoint(startPoint: PointF?, length: Float, angle: Float): PointF {
            //Math.toRadians将角度换成弧度，例Π，2Π
            val deltaX = Math.cos(Math.toRadians(angle.toDouble())).toFloat() * length
            val deltaY = (-Math.sin(Math.toRadians(angle.toDouble()))).toFloat() * length
            return PointF(startPoint!!.x + deltaX, startPoint.y + deltaY)
        }
    }
}
