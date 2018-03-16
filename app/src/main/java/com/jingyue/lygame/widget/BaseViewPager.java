package com.jingyue.lygame.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.ScrollView;

import com.laoyuegou.android.lib.utils.LogUtils;

/**
 * Created by zhanglei on 2017/6/15.
 */

public class BaseViewPager extends ViewPager {
    private boolean scroll = true;
    private float preX = 0;
    private float preY = 0;
    private int touchSlop = 0;

    public BaseViewPager(Context context) {
        this(context,null);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = (int) (ViewConfiguration.get(context).getScaledTouchSlop() * 0.3f);
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        /*return false;//super.onTouchEvent(arg0);*/
        if (scroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    private ScrollView scrollParent;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(scrollParent == null){
            ViewParent parent ;
            View view = this;
            while((parent = view.getParent()) != null){
                if(parent instanceof ScrollView){
                    scrollParent = (ScrollView)parent;
                    break;
                }
                view = (View)parent;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (scroll)
            return false;
        else {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                preX = event.getX();
                preY = event.getY();
            } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                float x = event.getX() - preX;
                float y = event.getY() - preY;
               if(Math.abs(x)>touchSlop){
                   scrollParent.requestDisallowInterceptTouchEvent(true);
               }
//                if (Math.abs(x) > 10) {
//                    return true;
//                } else {
//                    preX = event.getX();
//                }
            }else if(event.getAction() == MotionEvent.ACTION_CANCEL ||
                    event.getAction() == MotionEvent.ACTION_UP){
                scrollParent.requestDisallowInterceptTouchEvent(false);
            }
            return super.onInterceptTouchEvent(event);
        }
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }


}
