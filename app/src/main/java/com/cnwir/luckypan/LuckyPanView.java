package com.cnwir.luckypan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cnwir.luckypan.R;

/**
 * Created by Windows on 2015/8/19.
 */
public class LuckyPanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {


    private SurfaceHolder mHolder;


    private Canvas mCanvas;

    /**
     * surface中的线程
     */
    private Thread t;

    /**
     * 线程中的控制开关
     */
    private boolean isRunning;

    /**
     * 盘快的文字
     */

    private String[] mStrs = new String[]{"警察", "平民", "杀手", "平民",
            "妹子", "平民"};

    /**
     * 盘快的图片
     */

    private int[] mImgs = new int[]{R.mipmap.police, R.mipmap.commoner,
            R.mipmap.killer, R.mipmap.commoner, R.mipmap.meizi,
            R.mipmap.commoner};

    /**
     * 每个盘块的颜色
     */
    private int[] mColors = new int[]{0xFFFFC300, 0xFFF17E01, 0xFFFFC300,
            0xFFF17E01, 0xFFFFC300, 0xFFF17E01};


    /**
     * 图片对应的bitmap
     */

    private Bitmap[] mImgsBitmap;

    /**
     * 盘快的个数
     */

    private int mItemCount = 6;

    /**
     * 盘快的范围
     */
    private RectF mRange = new RectF();

    /**
     * 盘快的半径
     */

    private float mRadius;

    /**
     * 绘制盘快的画笔
     */

    private Paint mArcPaint;

    /**
     * 绘制文字的画笔
     */
    private Paint mTextPaint;

    /**
     * 盘快的速度
     */

    private double mSpeed = 0;

    /**
     * 开始的角度
     */
    private volatile float mStartAngle;

    /**
     * 是否点击了停止
     */

    private boolean isShouldEnd;

    /**
     * 盘快的中心点
     */


    private float mCenter;

    /**
     * 控件的padding，这里我们认为4个padding的值一致，以paddingleft为标准
     */

    private int mPadding;

    /**
     * 设置背景图
     */

    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.bg2);

    /**
     * 字体的大小
     */

    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    public LuckyPanView(Context context) {
        this(context, null);
    }

    public LuckyPanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();

        mHolder.addCallback(this);

        /**
         * 获得焦点
         */
        setFocusable(true);
        setFocusableInTouchMode(true);

        /**
         * 设置常量
         */
        setKeepScreenOn(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredHeight(), getMeasuredWidth());
        mPadding = getPaddingLeft();

        mRadius = (width - mPadding * 2) / 2;

        mCenter = width / 2;

        setMeasuredDimension(width, width);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //初始化盘快的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        //初始化文字的画笔

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        //初始化盘快的范围

        mRange = new RectF(mPadding, mPadding, mRadius * 2 + mPadding, mRadius * 2 + mPadding);

        //初始化图片

        mImgsBitmap = new Bitmap[mItemCount];

        for (int i = 0; i < mImgsBitmap.length; i++) {

            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);


        }


        isRunning = true;
        t = new Thread(this);
        t.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        isRunning = false;

    }

    @Override
    public void run() {

        while (isRunning) {

            long start = System.currentTimeMillis();

            draw();

            long end = System.currentTimeMillis();

            if (end - start < 50) {

                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void draw() {

        try {
            mCanvas = mHolder.lockCanvas();

            if (mCanvas != null) {
                //绘制背景
                drawBg();

                //绘制盘快

                float tempAngle = mStartAngle;

                float sweepAngle = 360 / mItemCount;

                for (int i = 0; i < mItemCount; i++) {

                    mArcPaint.setColor(mColors[i]);
                    //绘制盘快
                    mCanvas.drawArc(mRange, tempAngle, sweepAngle, true, mArcPaint);
                    //绘制文本
                    drawText(tempAngle, sweepAngle, mStrs[i]);
                    //绘制图片
                    drawIcon(tempAngle, mImgsBitmap[i]);

                    tempAngle += sweepAngle;
                }

                mStartAngle += mSpeed;

                if (isShouldEnd) {

                    mSpeed -= 1;
                }
                if (mSpeed <= 0) {

                    mSpeed = 0;
                    isShouldEnd = false;
                }


            }
        } catch (Exception e) {


        } finally {
            if (mCanvas != null) {

                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }


    }

    /**
     * 绘制图片
     *
     * @param tempAngle
     * @param bitmap
     */
    private void drawIcon(float tempAngle, Bitmap bitmap) {

        int imgsWidth = (int) (mRadius / 4);

        float angle = (float) ((tempAngle + 360 / mItemCount / 2) * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2 * Math.cos(angle));

        int y = (int) (mCenter + mRadius / 2 * Math.sin(angle));

        //确定图片的位置
        Rect rect = new Rect(x - imgsWidth / 2, y - imgsWidth / 2, x + imgsWidth / 2, y + imgsWidth / 2);

        mCanvas.drawBitmap(bitmap, null, rect, null);

    }

    /**
     * 绘制文本
     *
     * @param tempAngle
     * @param sweepAngle
     * @param str
     */
    private void drawText(float tempAngle, float sweepAngle, String str) {

        Path path = new Path();
        path.addArc(mRange, tempAngle, sweepAngle);

        float textWidth = mTextPaint.measureText(str);

        int hOffset = (int) (mRadius * 2 * Math.PI / mItemCount / 2 - textWidth / 2);

        int vOffset = (int) (mRadius / 6);

        mCanvas.drawTextOnPath(str, path, hOffset, vOffset, mTextPaint);


    }

    /**
     * 绘制背景
     */
    private void drawBg() {


        mCanvas.drawColor(0xfffffff);

        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2, mPadding / 2, getMeasuredWidth() - mPadding / 2, getMeasuredWidth() - mPadding / 2), null);


    }

    /**
     * 点击转盘启动
     */
    public void luckyStart(int index) {


        //每一项的角度
        float angle = 360 / mItemCount;

        //计算每一项的重讲范围

        float from = 270 - (index + 1) * angle;

        float end = from + angle;
        

        //设置停下来的角度

        float targetFrom = 4 * 360 + from;

        float targetEnd = 4 * 360 + end;

        /**
         *
         *v1 -> 0
         * v1 -1
         * (v1 + 0) (v1 + 1) / 2 = targetFrom
         *v^2 = 2as
         *
         *
         */

//        float v1 = (float) Math.sqrt(2 * targetFrom);
//        float v2 = (float) Math.sqrt(2 * targetEnd);

        float v1 = (float) ((-1 + (Math.sqrt(1 + 8 * targetFrom))) / 2);
        float v2 = (float) ((-1 + (Math.sqrt(1 + 8 * targetEnd))) / 2);
        mSpeed = v1 + Math.random() * (v2 - v1);
//        mSpeed = v2;

        isShouldEnd = false;

    }

    /**
     * 点击转盘停止
     */

    public void luckyStop() {

        mStartAngle = 0;
        isShouldEnd = true;

    }

    /**
     * 判断是否开始转动
     *
     * @return
     */
    public boolean isStart() {


        return mSpeed != 0;
    }

    /**
     * 判断停止按钮是否按下
     *
     * @return
     */
    public boolean isShouldEnd() {

        return isShouldEnd;
    }
}
