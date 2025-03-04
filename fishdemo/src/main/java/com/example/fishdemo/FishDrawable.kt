package com.example.fishdemo;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FishDrawable extends Drawable {

    private Paint mPaint;
    private Path mPath;
    // 除了身体之外的部分的透明度
    private final static int OTHER_ALPHA = 110;
    // 身体的透明度
    private final static int BODY_ALPHA = 160;
    // 鱼身的中心点(重心)
    private PointF mMiddlePoint;

    private final static float HEAD_RADIUS = 50f;

    private float mFishMainAngle = 90f;

    // 鱼身的长度
    private final static float BODY_LENGTH = 3.2f * HEAD_RADIUS;

    private final static float FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS;

    private final static float FINS_LENGTH = 1.3f * HEAD_RADIUS;

    // <---鱼尾--->
    // 尾部大圆的半径(圆心就是身体底部的中点)
    private final static float BIG_CIRCLE_RADIUS = 0.7f * HEAD_RADIUS;
    // 尾部中圆的半径
    private final static float MIDDLE_CIRCLE_RADIUS = 0.6f * BIG_CIRCLE_RADIUS;
    // 尾部小圆的半径
    private final static float SMALL_CIRCLE_RADIUS = 0.4f * MIDDLE_CIRCLE_RADIUS;
    // 寻找尾部中圆圆心的线长
    private final static float FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS;
    // 寻找尾部小圆圆心的线长
    private final static float FIND_SMALL_CIRCLE_LENGTH = MIDDLE_CIRCLE_RADIUS * (0.4f + 2.7f);
    // 寻找大三角形底边中心点的线长
    private final static float FIND_TRIANGLE_LENGTH = MIDDLE_CIRCLE_RADIUS * 2.7f;

    public FishDrawable() {
        init();
    }

    private void init() {
        // 画笔初始化
        mPaint = new Paint();
        // 画笔类型--填充
        mPaint.setStyle(Paint.Style.FILL);
        // 设置颜色
        mPaint.setARGB(OTHER_ALPHA, 244, 92, 71);
        // 抗锯齿
        mPaint.setAntiAlias(true);
        // 防抖
        mPaint.setDither(true);

        // PointF为float型的坐标
        mMiddlePoint = new PointF(4.19f * HEAD_RADIUS, 4.19f * HEAD_RADIUS);

        // 路径
        mPath = new Path();

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(-1,1);
        //周期
        valueAnimator.setDuration(1000);
        //设置循环模式，从-1到1，再从1到1
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        
    }

    /**
     * 绘制，类似于自定义view中的onDraw()方法
     *
     * @param canvas
     */
    @Override
    public void draw(@NonNull Canvas canvas) {
        float fishAngle = mFishMainAngle;

        PointF headPoint = calculatePoint(mMiddlePoint, BODY_LENGTH / 2, fishAngle);
        canvas.drawCircle(headPoint.x, headPoint.y, HEAD_RADIUS, mPaint);

        // 鱼右鳍
        PointF rightFishFins = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle - 110);
        makeFins(canvas, rightFishFins, fishAngle, true);

        // 鱼左鳍
        PointF leftFishFins = calculatePoint(headPoint, FIND_FINS_LENGTH, fishAngle + 110);
        makeFins(canvas, leftFishFins, fishAngle, false);

        // 身体底部的中心点坐标
        // 绘制节肢1
        PointF bodyBottomCenterPoint = calculatePoint(headPoint, BODY_LENGTH, fishAngle - 180);
        makeSegment(canvas, bodyBottomCenterPoint, BIG_CIRCLE_RADIUS, MIDDLE_CIRCLE_RADIUS, FIND_MIDDLE_CIRCLE_LENGTH, fishAngle, true);

        // 绘制节肢2
        PointF middleCircleCenterPoint = calculatePoint(bodyBottomCenterPoint, FIND_MIDDLE_CIRCLE_LENGTH, fishAngle - 180);
        makeSegment(canvas, middleCircleCenterPoint, MIDDLE_CIRCLE_RADIUS, SMALL_CIRCLE_RADIUS, FIND_SMALL_CIRCLE_LENGTH, fishAngle, false);

        // 绘制大三角形
        makeTriangle(canvas, middleCircleCenterPoint, FIND_TRIANGLE_LENGTH, BIG_CIRCLE_RADIUS, fishAngle);
        // 绘制小三角形
        makeTriangle(canvas, middleCircleCenterPoint, FIND_TRIANGLE_LENGTH-10, BIG_CIRCLE_RADIUS-20, fishAngle);

        makeBody(canvas,headPoint,bodyBottomCenterPoint,fishAngle);
    }

    /**
     * 绘制身体
     * @param canvas
     * @param headPoint 头部的点
     * @param bodyCenterPoint 鱼身体的中心点
     * @param fishAngle
     */
    private void makeBody(Canvas canvas,PointF headPoint,PointF bodyCenterPoint,float fishAngle) {
        // 身体的四个点
        PointF topLeftPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle + 90);
        PointF topRightPoint = calculatePoint(headPoint, HEAD_RADIUS, fishAngle - 90);
        PointF bottomLeftPoint = calculatePoint(bodyCenterPoint, BIG_CIRCLE_RADIUS, fishAngle + 90);
        PointF bottomRightPoint = calculatePoint(bodyCenterPoint, BIG_CIRCLE_RADIUS, fishAngle - 90);

        PointF controlLeft = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle + 130);
        PointF controlRight = calculatePoint(headPoint, BODY_LENGTH * 0.56f, fishAngle - 130);

        // 画鱼身
        mPath.reset();
        mPath.moveTo(topLeftPoint.x,topLeftPoint.y);
        mPath.lineTo(topRightPoint.x,topRightPoint.y);
        mPath.quadTo(controlRight.x,controlRight.y, bottomRightPoint.x, bottomRightPoint.y);
        mPath.lineTo(bottomLeftPoint.x,bottomLeftPoint.y);
        mPath.quadTo(controlLeft.x,controlLeft.y, topLeftPoint.x, topLeftPoint.y);
        mPaint.setAlpha(BODY_ALPHA);
        canvas.drawPath(mPath,mPaint);
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
    private void makeTriangle(Canvas canvas, PointF startPoint, float findCenterLength, float findEdgeLength, float fishAngle) {
        // 底部中心点的坐标
        PointF centerPoint = calculatePoint(startPoint, findCenterLength, fishAngle - 180);

        // 底部的两个点
        PointF leftPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle + 90);
        PointF rightPoint = calculatePoint(centerPoint, findEdgeLength, fishAngle - 90);

        // 绘制三角形(鱼尾)
        mPath.reset();
        mPath.moveTo(leftPoint.x, leftPoint.y);
        mPath.lineTo(rightPoint.x, rightPoint.y);
        mPath.lineTo(startPoint.x, startPoint.y);
        canvas.drawPath(mPath, mPaint);
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
    private void makeSegment(Canvas canvas, PointF bottomCenterPoint, float bigRadius, float smallRadius, float findSmallCircleLength, float fishAngle, boolean hasBigCircle) {
        // 梯形上底的中心点
        PointF upperCenterPoint = calculatePoint(bottomCenterPoint, findSmallCircleLength, fishAngle - 180);

        // 梯形的四个顶点
        PointF bottomLeftPoint = calculatePoint(bottomCenterPoint, bigRadius, fishAngle + 90);
        PointF bottomRightPoint = calculatePoint(bottomCenterPoint, bigRadius, fishAngle - 90);
        PointF upperLeftPoint = calculatePoint(upperCenterPoint, smallRadius, fishAngle + 90);
        PointF upperRightPoint = calculatePoint(upperCenterPoint, smallRadius, fishAngle - 90);

        if (hasBigCircle) {
            //绘制大圆
            canvas.drawCircle(bottomCenterPoint.x, bottomCenterPoint.y, bigRadius, mPaint);
        }
        //绘制小圆
        canvas.drawCircle(upperCenterPoint.x, upperCenterPoint.y, smallRadius, mPaint);
        //绘制梯形
        mPath.reset();
        mPath.moveTo(bottomLeftPoint.x, bottomLeftPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        mPath.lineTo(upperRightPoint.x, upperRightPoint.y);
        mPath.lineTo(upperLeftPoint.x, upperLeftPoint.y);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 绘制鱼鳍
     *
     * @param canvas
     * @param startPoint    起始点的坐标
     * @param fishHeadAngle 鱼头起始点的角度
     * @param isRightFins
     */
    public void makeFins(Canvas canvas, PointF startPoint, float fishHeadAngle, boolean isRightFins) {
        float controlAngle = 115;

        // 结束点
        PointF endPoint = calculatePoint(startPoint, FINS_LENGTH, fishHeadAngle - 180);
        // 控制点
        PointF controlPoint = calculatePoint(startPoint, 1.8f * FINS_LENGTH, isRightFins ? fishHeadAngle - controlAngle : fishHeadAngle + controlAngle);

        // 这个path容器不会主动删除里面的元素，所以每次调用前应该给它重置
        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        // 二阶贝塞尔曲线
        mPath.quadTo(controlPoint.x, controlPoint.y, endPoint.x, endPoint.y);

        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 求对应点的坐标
     *
     * @param startPoint 起始点的坐标
     * @param length     两点间的长度
     * @param angle      鱼头相对于x坐标的角度
     * @return
     */
    public static PointF calculatePoint(PointF startPoint, float length, float angle) {
        //Math.toRadians将角度换成弧度，例Π，2Π
        float deltaX = (float) (Math.cos(Math.toRadians(angle))) * length;
        float deltaY = (float) (-Math.sin(Math.toRadians(angle))) * length;
        return new PointF(startPoint.x + deltaX, startPoint.y + deltaY);
    }

    /**
     * 设置透明度的方法
     *
     * @param alpha 透明度
     */
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    /**
     * 设置一个颜色过滤器，在绘制出来之前，被绘制内容的每一个像素都会被颜色过滤器改变
     *
     * @param colorFilter
     */
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
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
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    /**
     * 如果imageview的宽高是wrap_content,则获取这个值
     *
     * @return
     */
    @Override
    public int getIntrinsicHeight() {
        return (int) (8.38f * HEAD_RADIUS);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) (8.38f * HEAD_RADIUS);
    }
}
