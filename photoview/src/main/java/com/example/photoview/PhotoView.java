package com.example.photoview;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.Nullable;

public class PhotoView extends View {

    private Bitmap bitmap;
    private Paint paint;
    // 偏移量
    private float originalOffsetX;
    private float originalOffsetY;

    // 一边全屏，一边留白
    private float smallScale;
    // 一边全屏，一边超出屏幕
    private float bigScale;
    // 当前的缩放比例
    private float currentScale;
    private final static float OVER_SCALE_FACTOR = 1.5f;
    private boolean isEnlarge;
    private GestureDetector gestureDetector;
    /**
     * 属性动画，放大缩小的效果
     */
    private ObjectAnimator scaleAnimator;
    private float offsetX;
    private float offsetY;
    private OverScroller overScroller;
    private FlingRunnable flingRunnable;
    private ScaleGestureDetector scaleGestureDetector;
    private boolean isScale;

    public void setCurrentScale(float currentScale) {
        this.currentScale = currentScale;
        invalidate();
    }

    public ObjectAnimator getScaleAnimator() {
        if (scaleAnimator == null) {
            scaleAnimator = ObjectAnimator.ofFloat(this, "currentScale", 0);
        }

        if (isScale){
            isScale = false;
            scaleAnimator.setFloatValues(smallScale, currentScale);
        }else {
            // 放大缩小(属性值)的范围
            scaleAnimator.setFloatValues(smallScale, bigScale);
        }
        return scaleAnimator;
    }

    public PhotoView(Context context) {
        this(context, null);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo);
        paint = new Paint();

        gestureDetector = new GestureDetector(context, new PhotoGestureListener());
        overScroller = new OverScroller(context);
        flingRunnable = new FlingRunnable();

        scaleGestureDetector = new ScaleGestureDetector(context,new PhotoScaleGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 双指操作优先
        boolean result = scaleGestureDetector.onTouchEvent(event);
        if (!scaleGestureDetector.isInProgress()){
            return gestureDetector.onTouchEvent(event);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float scaleFraction = (currentScale - smallScale) / (bigScale - smallScale);
        canvas.translate(offsetX * scaleFraction, offsetY * scaleFraction);
        canvas.scale(currentScale, currentScale, getWidth() >> 1, getHeight() >> 1);
        //绘制图片
        canvas.drawBitmap(bitmap, originalOffsetX, originalOffsetY, paint);
    }

    // onMeasure -> onSizeChanged
    // 每次尺寸改变时也会调用
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        originalOffsetX = (getWidth() - bitmap.getWidth()) / 2f;
        originalOffsetY = (getHeight() - bitmap.getHeight()) / 2f;

        // 横向的图片
        if ((bitmap.getWidth() / bitmap.getHeight()) > (getWidth() / getHeight())) {
            smallScale = (float) getWidth() / bitmap.getWidth();
            bigScale = (float) getHeight() / bitmap.getHeight() * OVER_SCALE_FACTOR;
        }
        // 纵向的图片
        else {
            smallScale = (float) getHeight() / bitmap.getHeight();
            bigScale = (float) getWidth() / bitmap.getWidth() * OVER_SCALE_FACTOR;
        }

        currentScale = smallScale;
    }

    class PhotoGestureListener extends GestureDetector.SimpleOnGestureListener {

        /**
         * 事件UP时触发 双击的时候在第二次抬起的时候触发
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        /**
         * 长按时触发  默认触发时间是300ms
         *
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
        }

        /**
         * 类似move事件
         *
         * @param e1
         * @param e2
         * @param distanceX 在 X 轴(单位时间)滑过的距离 旧位置减去新位置
         * @param distanceY 在 Y 轴(单位时间)滑过的距离 旧位置减去新位置
         * @return
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            //只有在放大的情况下才能进行移动
            if (isEnlarge) {
                offsetX -= distanceX;
                offsetY -= distanceY;
                fixOffsets();
                invalidate();
            }
            return false;
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
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (isEnlarge) {
                overScroller.fling((int) offsetX, (int) offsetY, (int) velocityX
                        , (int) velocityY, -(int) (bitmap.getWidth() * bigScale - getWidth()) / 2
                        , (int) (bitmap.getWidth() * bigScale - getWidth()) / 2
                        , -(int) (bitmap.getHeight() * bigScale - getHeight()) / 2
                        , (int) (bitmap.getHeight() * bigScale - getHeight()) / 2, 300, 300);
                postOnAnimation(flingRunnable);
            }
            return false;
        }

        /**
         * 延时触发 100ms --> 为了处理点击效果，例:水波纹
         *
         * @param e
         */
        @Override
        public void onShowPress(MotionEvent e) {
        }

        /**
         * 按下时触发 注意直接返回true
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        /**
         * 双击 --> 第二次点击的时候 触发 --> 事件 40ms(小于表示抖动,防抖动) -(双击) 300ms
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isEnlarge = !isEnlarge;

            if (isEnlarge) {
                offsetX = (e.getX() - getWidth() / 2f) - (e.getX() - (getWidth() >> 1)) * bigScale / smallScale;
                offsetY = (e.getY() - getHeight() / 2f) - (e.getY() - (getHeight() >> 1)) * bigScale / smallScale;
                fixOffsets();
                // 启动属性动画
                getScaleAnimator().start();
            } else {
                getScaleAnimator().reverse();
            }
            return false;
        }

        /**
         * 双击 --> 第二次的触摸事件(down,up,move)
         *
         * @param e
         * @return
         */
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        /**
         * 单击按下时触发,双击时不触发，延时300ms的tap事件
         *
         * @param e
         * @return
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

    }

    private void fixOffsets() {
        offsetX = Math.min(offsetX, (bitmap.getWidth() * bigScale - getWidth()) / 2);
        offsetX = Math.max(offsetX, -(bitmap.getWidth() * bigScale - getWidth()) / 2);
        offsetY = Math.min(offsetY, (bitmap.getHeight() * bigScale - getHeight()) / 2);
        offsetY = Math.max(offsetY, -(bitmap.getHeight() * bigScale - getHeight()) / 2);
    }

    class FlingRunnable implements Runnable {

        @Override
        public void run() {
            // 动画还在执行则返回true
            if (overScroller.computeScrollOffset()) {
                offsetX = overScroller.getCurrX();
                offsetY = overScroller.getCurrY();
                invalidate();
                // 每帧动画执行一次，性能更好
                postOnAnimation(this);
            }
        }
    }

    class PhotoScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener{

        private float initScale;
        /**
         * 缩放
         * @param detector
         * @return
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if ((currentScale > smallScale && !isEnlarge) || currentScale == smallScale && isEnlarge){
                isEnlarge = !isEnlarge;
            }

            isScale = true;
            currentScale = initScale * detector.getScaleFactor();
            invalidate();
            return false;
        }

        /**
         * 缩放前 注意返回true 消费事件
         * @param detector
         * @return
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            initScale = currentScale;
            return true;
        }

        /**
         * 缩放后
         * @param detector
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }
}
