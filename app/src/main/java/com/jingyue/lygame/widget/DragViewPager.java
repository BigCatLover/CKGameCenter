package com.jingyue.lygame.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-12 18:07
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class DragViewPager extends ViewPager {

    private ViewDragHelper mViewDragHelper;
    private DragCallBack mDragCallBack;
    private final int[] mOrignPosition = new int[2];
    private float maxSnapRange = 0f;

    public DragViewPager(Context context) {
        this(context, null);
    }

    public DragViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragCallBack = new DragCallBack();
        mViewDragHelper = ViewDragHelper.create(this, mDragCallBack);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxSnapRange = getHeight() / 3;
    }

    private class DragCallBack extends ViewDragHelper.Callback {

        private float mOffsetPercent;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (child instanceof ImageView) {
                mOrignPosition[0] = child.getLeft();
                mOrignPosition[1] = child.getTop();
                return true;
            }
            return false;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mOffsetPercent = Math.max(0, Math.min(dy / maxSnapRange, 1f));
            setAlpha(mOffsetPercent);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int min = getPaddingLeft();
            final int max = getRight() - getPaddingRight();
            return Math.max(min, Math.min(max, left));
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            mViewDragHelper.settleCapturedViewAt(mOrignPosition[0], mOrignPosition[1]);
        }
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mViewDragHelper.processTouchEvent(ev);
        return true;
    }

    interface DragLinstener {
    }
}
