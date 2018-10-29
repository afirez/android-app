package com.afirez.app.gesture;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollerViewPager extends ViewGroup {
    public ScrollerViewPager(Context context) {
        this(context, null);
    }

    public ScrollerViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollerViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScrollerViewPager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        scroller = new Scroller(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                child.layout(
                        i * child.getMeasuredWidth(),
                        0,
                        (i + 1) * child.getMeasuredWidth(),
                        child.getMeasuredHeight()
                );
            }
        }
    }

    private float startX;
    private int startScrollX;
    private int currItem;
    private Scroller scroller;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("touch", "onTouchEvent: " + event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startScrollX = getScrollX();
                startX = event.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                currItem = Math.round((float) getScrollX() / getWidth());
                if(currItem < 0) {
                    currItem = 0;
                } else if(currItem > getChildCount() -1) {
                    currItem = getChildCount() - 1;
                }
                int dx = currItem * getWidth() - getScrollX();
                Log.i("touch", getScrollX() + " " + dx);
                scroller.startScroll(getScrollX(), 0, dx, 0);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                int scrollX = startScrollX + (int) (startX - event.getX());
                scrollTo(scrollX, 0);
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        boolean computeScrollOffset = scroller.computeScrollOffset();
        Log.i("touch", "" + computeScrollOffset);
        if (computeScrollOffset) {
            scrollTo(scroller.getCurrX(), 0);
            invalidate();
        }
    }
}
