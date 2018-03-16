package com.jingyue.lygame.widget;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.laoyuegou.android.lib.utils.LogUtils;

/**
 * Created by zhanglei on 2017/6/15.
 */
public class RcmdVDHLayout extends LinearLayout {
    private ViewDragHelper mDragger;

    private View mAutoBackView;
    private boolean flag = false;
    private int lasttop = 0;
    private int cha = 300;
    private Point mAutoBackOriginPos = new Point();
    private ViewDragHelper.Callback callback;
    private RcmdMoveListener skipListener;

    public RcmdVDHLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setFadingEdgeLength(cha);
        callback = new ViewDragHelper.Callback()
        {
            @Override
            public boolean tryCaptureView(View child, int pointerId)
            {
                //mEdgeTrackerView禁止直接移动
                return mAutoBackView == child;
            }

//            @Override
//            public int clampViewPositionHorizontal(View child, int left, int dx)
//            {
//                return left;
//            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy)
            {
                int newtop =top;
                if(top>0){
                    newtop = 0;
                }else if(top<-cha-10){
                    newtop = -cha-10;
                }
                lasttop = newtop;
                if(top<=-cha){
                    if(skipListener!=null){
                        flag = false;
                        skipListener.Drag2Top();
                    }
                }else if(top<0&&(!flag)){
                    if(skipListener!=null){
                        flag = true;
                        skipListener.Drag2Bottom();
                    }
                }
                return newtop;
            }


            //手指释放的时候回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel)
            {
                flag = true;
                //mAutoBackView手指释放时可以自动回去
                if (releasedChild == mAutoBackView)
                {
                    if(Math.abs(lasttop)>=cha/2){
//                        try {
//                            Thread.currentThread().sleep(1000);//阻断2秒
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        if(skipListener!=null){
                            skipListener.onSkip();
                        }
                    }
                    mDragger.settleCapturedViewAt(0, mAutoBackOriginPos.y);
                    invalidate();
                }
            }

            //在边界拖动时回调
//            @Override
//            public void onEdgeDragStarted(int edgeFlags, int pointerId)
//            {
//                mDragger.captureChildView(mEdgeTrackerView, pointerId);
//            }

            @Override
            public int getViewHorizontalDragRange(View child)
            {
                return 0;
            }

            @Override
            public int getViewVerticalDragRange(View child)
            {
                return cha;
            }
        };
        mDragger = ViewDragHelper.create(this, 1.0f, callback);
//        mDragger.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    public void setListener(RcmdMoveListener listener){
        this.skipListener = listener;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mDragger.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll()
    {
        if(mDragger.continueSettling(true))
        {
            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if(lasttop<=-cha){
            return;
        }
        mAutoBackView.layout(l, t, r, b+cha);
        mAutoBackOriginPos.y = mAutoBackView.getTop();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        mAutoBackView = getChildAt(0);
    }
}
