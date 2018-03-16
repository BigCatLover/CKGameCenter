package com.jingyue.lygame.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by zhanglei on 2017/9/30.
 */
public class CustomClickView extends LinearLayout {
    private long startTime = 0;
    private float mDownX;       //触摸的X坐标
    private float mDownY;       //触摸的Y坐标

    public CustomClickView(Context context) {
        super(context);
    }

    public CustomClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private OnClickListener mOnClickListener;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        float x = ev.getX();
        float y = ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                long currentTime = System.currentTimeMillis();
                long cha = currentTime - startTime;
                float deltaX = x - mDownX;      //右移为正，左移为负
                float deltaY = y - mDownY;      //下移为正，上移为负
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if(absDeltaX == 0 &&absDeltaY == 0){
                    if(cha<100){
                        if(mOnClickListener!=null){
                            mOnClickListener.onSkip();
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onSkip();
    }
}
