package com.jingyue.lygame.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.AppConstants;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-31 20:44
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class DrawerNestScrollView extends NestedScrollView {

    public static final int DEFUALT_ANIMATION_TIME = AppConstants.DEFAULT_LONG_ANIMATION_TIME;

    private View mHandlerView;

    private DrawerListener mDl;

    private boolean isOpen = true;

    public boolean isOpen() {
        return isOpen;
    }

    public void addDrawerListener(DrawerListener drawerListener) {
        this.mDl = drawerListener;
    }

    public DrawerNestScrollView(Context context) {
        this(context, null);
    }

    public DrawerNestScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onOpen() {
        if(isOpen){
            return;
        }
        isOpen = true;
        if (mDl != null) {
            mDl.onOpen();
        }
    }

    public void onClose() {
        if(!isOpen){
            return;
        }
        isOpen = false;
        if (mDl != null) {
            mDl.onClose();
        }
    }

    public void onStart(boolean open) {
        if (mDl != null) {
            mDl.onstart(open);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHandlerView = findViewById(R.id.close_drawer_handler);
    }

    public View getHandlerView() {
        return mHandlerView;
    }

    public void openDrawer() {
        animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(DEFUALT_ANIMATION_TIME)
                .setListener(animationOepnListener);
    }

    public void closeDrawer() {
        animate().translationY(getMeasuredHeight())
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(DEFUALT_ANIMATION_TIME)
                .setListener(animationCloseListener)
                .start();
    }

    private Animator.AnimatorListener animationOepnListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            if(mDl != null){
                mDl.onstart(true);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if(isOpen){
                return;
            }
            if (mDl != null) {
                isOpen = true;
                mDl.onOpen();
            }
        }
    };
    private Animator.AnimatorListener animationCloseListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            if(mDl != null){
                mDl.onstart(false);
            }
        }
        @Override
        public void onAnimationEnd(Animator animation) {
            if(!isOpen){
                return;
            }
            if (mDl != null) {
                isOpen = false;
                mDl.onClose();
            }
        }
    };

    public interface DrawerListener {
        void onstart(boolean open);

        void onOpen();

        void onClose();

        void offset(float scrollY);
    }

    public static class DrawerAdapterListner implements DrawerListener {

        @Override
        public void onstart(boolean open) {

        }

        @Override
        public void onOpen() {

        }

        @Override
        public void onClose() {

        }

        @Override
        public void offset(float scrollY) {
        }
    }
}
