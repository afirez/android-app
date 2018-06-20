package com.mcp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;


/**
 *
 */
public class MySurfaceView extends SurfaceView implements Callback, Runnable {

    private static final String TAG = "MySurfaceView";

    private SurfaceHolder sfh;
    private Paint paint;
    private Thread th;
    private boolean flag;
    private Canvas canvas;
    private int screenW, screenH;
    //声明一张icon位图
    private Bitmap bmpIcon;
    //记录两个触屏点的坐标
    private int x1, x2, y1, y2;
    //倍率
    private float scale = 1;
    //记录上次的倍率
    private float oldRate = 1;
    //记录第一次触屏时线段的长度
    private float oldLineDistance;
    //判定是否头次多指触点屏幕
    private boolean isFirst = true;
    private int dx;
    private int dy;
    private boolean mHasTwoPointer;
    private boolean mHasMorePointer;
    private MotionEvent mOldEvent;
    private volatile int mDegrees;

    public void setDegrees(int degrees) {
        mDegrees = degrees;
    }

    public int getDegrees() {
        return mDegrees;
    }

    /**
     * SurfaceView初始化函数
     */
    public MySurfaceView(Context context) {
        super(context);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setAntiAlias(true);
        setFocusable(true);
        bmpIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.chenyixun);
    }

    /**
     * SurfaceView视图创建，响应此函数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW = this.getWidth();
        screenH = this.getHeight();
        flag = true;
        //实例线程
        th = new Thread(this);
        //启动线程
        th.start();
    }

    /**
     * 游戏绘图
     */
    public void myDraw() {
        try {
            canvas = sfh.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE);
                canvas.save();
                //缩放画布(以图片中心点进行缩放，XY轴缩放比例相同)
                canvas.scale(scale, scale, screenW / 2, screenH / 2);
                canvas.translate(dx / scale, dy / scale);
                //绘制位图icon
                canvas.rotate(mDegrees, screenW / 2, screenH / 2);
                canvas.drawBitmap(bmpIcon, screenW / 2 - bmpIcon.getWidth() / 2, screenH / 2 - bmpIcon.getHeight() / 2, paint);
                canvas.restore();
                //便于观察，这里绘制两个触点时形成的线段
//                    canvas.drawLine(x1, y1, x2, y2, paint);
                if (mOldEvent != null && mOldEvent.getPointerCount() == 2) {
                    x1 = (int) mOldEvent.getX(0);
                    y1 = (int) mOldEvent.getY(0);
                    x2 = (int) mOldEvent.getX(1);
                    y2 = (int) mOldEvent.getY(1);
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null)
                sfh.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * 触屏事件监听
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //用户手指抬起默认还原为第一次触屏标识位，并且保存本次的缩放比例
        int action = event.getAction();
        int pointerCount = event.getPointerCount();
        Log.d(TAG, "onTouchEvent: pointerCount = " + pointerCount);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: ACTION_DOWN");
                mHasTwoPointer = false;
                mHasMorePointer = false;
                if (mOldEvent != null) {
                    mOldEvent.recycle();
                }
                mOldEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: ACTION_MOVE");
                if (pointerCount == 2) {
                    if (!mHasTwoPointer) {
                        mHasTwoPointer = true;
                        oldLineDistance = (float) Math.sqrt(
                                Math.pow(event.getX(1) - event.getX(0), 2)
                                        + Math.pow(event.getY(1) - event.getY(0), 2));
                    } else {
                        float newLineDistance = (float) Math.sqrt(
                                Math.pow(event.getX(1) - event.getX(0), 2)
                                        + Math.pow(event.getY(1) - event.getY(0), 2));
                        scale = oldRate * newLineDistance / oldLineDistance;
                    }
                } else if (pointerCount == 1) {
                    if (!mHasTwoPointer && mOldEvent != null) {
                        dx = dx + (int) (event.getX(0) - mOldEvent.getX(0));
                        dy = dy + (int) (event.getY(0) - mOldEvent.getY(0));
                    }
                }
                if (mOldEvent != null) {
                    mOldEvent.recycle();
                }
                mOldEvent = MotionEvent.obtain(event);
                break;
            case MotionEvent.ACTION_UP:
                oldRate = scale;
                if (mOldEvent != null) {
                    mOldEvent.recycle();
                    mOldEvent = null;
                }
                Log.d(TAG, "onTouchEvent: ACTION_UP");
                break;
        }

        Log.d(TAG, "onTouchEvent: scale = " + scale + " dx = " + dx + " dy = " + dy);

        return true;
    }

    /**
     * 按键事件监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 游戏逻辑
     */
    private void logic() {
    }

    @Override
    public void run() {
        while (flag) {
            long start = System.currentTimeMillis();
            myDraw();
            logic();
            long end = System.currentTimeMillis();
            try {
                if (end - start < 50) {
                    Thread.sleep(50 - (end - start));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * SurfaceView视图状态发生改变，响应此函数
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * SurfaceView视图消亡时，响应此函数
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }
}
