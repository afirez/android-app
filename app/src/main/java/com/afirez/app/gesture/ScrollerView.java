package com.afirez.app.gesture;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class ScrollerView extends View {

    private Paint paint;

    public ScrollerView(Context context) {
        this(context, null);
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        paint = new Paint();
        scroller = new Scroller(getContext());
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.scrollBy(20, 20);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(500, 500);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.RED);

        canvas.drawCircle(250, 250, 50, paint);
    }

    private float startX;
    private float startY;
    private int startScrollX;
    private int startScrollY;
    private Scroller scroller;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("touch", "onTouchEvent: " + event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                startScrollX = getScrollX();
                startScrollY = getScrollY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
//                scrollTo(startScrollX, startScrollY);
                scroller.startScroll(getScrollX(), getScrollY(), startScrollX - getScrollX(), startScrollY - getScrollY());
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                scrollTo(startScrollX + (int)(startX - event.getX()), startScrollY + (int)(startY - event.getY()));
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
        }
    }
}
