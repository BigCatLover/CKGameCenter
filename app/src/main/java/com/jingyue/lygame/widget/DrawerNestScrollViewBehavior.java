package com.jingyue.lygame.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;

import com.jingyue.lygame.R;
import com.laoyuegou.android.lib.utils.LogUtils;

import java.lang.ref.WeakReference;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-31 17:36
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class DrawerNestScrollViewBehavior extends CoordinatorLayout.Behavior<ViewGroup> {

    public DrawerNestScrollViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        actionBarSize = context.getResources().getDimensionPixelSize(R.dimen.actionbar_size);
    }

    /**
     * actionbar 是否根据上下移动进行淡入和淡出
     * 功能暂时未实现
     */
    private boolean enterAlways = true;
    private boolean enableOverScrollUp = false;

    /**
     * actionbar透明变化速度
     */
    private AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator(2.5f);

    /***
     * 默认触发snap效果的阈值
     */
    private float DEFAULT_RATIO = 0.26f;
    /***
     * 向下滚动速度缩减比例
     */
    private float DEFUALT_FATE = 0.4f;
    private float actionBarSize;
    private float scrollSize;

    private float mTagTop;

    private float lastChildrenBottom;
    private float contentTop;
    private float firstChildBottom;
    private float realContentSize;


    private WeakReference<DrawerNestScrollView> dependencyWeakRef;
    private WeakReference<View> mActionbarBack;
    private WeakReference<View> mActionBar;
    private WeakReference<View> mDrawerContainer;
    private WeakReference<View> mTagFloatView;
    private WeakReference<View> mTagView;

    private float maxScrollPoint;
    private boolean disallowTouchEvent = false;

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, final View dependency) {
        if (dependency instanceof DrawerNestScrollView) {
            final DrawerNestScrollView scrollView = (DrawerNestScrollView) dependency;

            dependencyWeakRef = new WeakReference<>(scrollView);
            mDrawerContainer = new WeakReference<>(scrollView.findViewById(R.id.drawer_container));
            mTagFloatView = new WeakReference<>(parent.findViewById(R.id.float_tag_group));
            mActionBar = new WeakReference<View>(child);
            mTagView = new WeakReference<>(scrollView.findViewById(R.id.tag_group_container));
            mActionbarBack = new WeakReference<>(child.findViewById(R.id.actionbar_background));
            scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    handleScroll(v,0,scrollY,0,0,false);
                }
            });

            scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                private boolean isValueSet = false;

                @Override
                public void onGlobalLayout() {
                    if (!isValueSet) {
                        maxScrollPoint = scrollView.getHandlerView().getTop() - actionBarSize;
                        scrollSize = scrollView.getMeasuredHeight();
                        contentTop = mDrawerContainer.get().getTop();
                        lastChildrenBottom = scrollView.getChildAt(scrollView.getChildCount() - 1).getBottom();
                        realContentSize = scrollView.getHeight() - scrollView.getPaddingBottom() - scrollView.getPaddingTop();
                        firstChildBottom = scrollView.getChildAt(0).getBottom();
                        mTagTop = mTagView.get().getTop();
                        isValueSet = true;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, ViewGroup child, MotionEvent ev) {
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, ViewGroup child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, ViewGroup child, View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, ViewGroup child, View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, ViewGroup child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (target instanceof DrawerNestScrollView) {
            // 如果handler已经overScroll了，
            // 把事件消费掉，然后走overScroll逻辑
            // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
            // before allowing the list to scroll
            if (dy > 0 && overScrollY > 0) {
                if (dy > overScrollY) {
                    consumed[1] = dy - (int) overScrollY;
                    overScrollY = 0;
                } else {
                    overScrollY -= dy;
                    consumed[1] = dy;
                }
                handleScroll((NestedScrollView) target, 0, 0, 0, dy, true);
            }
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, ViewGroup child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        try {
            final int dy = dyUnconsumed;
            overScrollY += Math.abs(dy);
            handleScroll((NestedScrollView) target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, true);
        } catch (Exception e) {
        }
    }

    private void handleScroll(NestedScrollView v, float scrollX, float scrollY, int unConsumedX, int umConsumeY, boolean overScroll) {
        try {
            final View actionBar = mActionBar.get();
            if (canChildScrollUp(v) && v.getTranslationY() == 0) {//向上滑动
                final float maxScrollTop = maxScrollPoint;
                scrollY = v.getScrollY();
                if (maxScrollTop - scrollY > 0) {//控制alpha值变化的范围
                    //屏幕內修正actionbar位置
                    actionBar.setTranslationY(0);
                    if (scrollY > maxScrollTop / 2) {
                        //mActionbarBack.get()
                        //      .setAlpha(accelerateInterpolator.getInterpolation(scrollY * 1l / maxScrollTop));
                        mActionbarBack.get()
                                .setAlpha((scrollY - maxScrollTop / 2) * 2l / maxScrollTop);
                    } else {
                        mActionbarBack.get().setAlpha(0);
                    }

                } else {
                    mActionbarBack.get().setAlpha(1);
                    if(scrollY >= mTagTop - actionBarSize){
                        mTagFloatView.get().setVisibility(View.VISIBLE);
                    }else{
                        mTagFloatView.get().setVisibility(View.GONE);
                    }
                    if ( scrollY - maxScrollTop <= actionBar.getHeight()) {
                        //滚出屏幕
                        //actionBar.setTranslationY(maxScrollTop - scrollY);
                    } else{
                        //超出屏幕修正actionbar位置
                        //actionBar.setTranslationY(-actionBar.getHeight());
                        //显示和隐藏悬浮tag层
                    }
                }
            } else {
                // 向下滑動
                // scrollY = dyUnconsumed
                // 向下滚动速度，减少按照一定比率增加阻尼
                if (scrollY == 0) {
                    mDrawerContainer.get().setTranslationY(overScrollY * DEFUALT_FATE);
                } else if (scrollY >= overScrollY) {
                    overScrollY = 0;
                    mDrawerContainer.get().setTranslationY(0);
                } else {
                    mDrawerContainer.get().setTranslationY((overScrollY - scrollY) * DEFUALT_FATE);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, ViewGroup child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
        /*LogUtils.e(" overScrollY = " + overScrollY + "  |  "
                + "\n scrollSize = " + scrollSize
                + "\n maxScrollPoint = " + maxScrollPoint
                + "\n contentTop = " + contentTop);*/
        try {
            if (overScrollY > 0) {
                if (needSnapBack()) {
                    mDrawerContainer.get()
                            .animate()
                            .translationY(0)
                            .setListener(snapBackDrawerAnimatorLsn)
                            //.setInterpolator(accelerateInterpolator)
                            .start();
                } else if (needSnapOver()) {
                    dependencyWeakRef.get()
                            .animate()
                            .translationY(scrollSize)
                            .setListener(closeDrawerAnimatorLsn)
                            //.setInterpolator(accelerateInterpolator)
                            .start();
                }
            }
        } catch (NullPointerException e) {
            LogUtils.e(e);
        }
    }

    private void snapActionbarBack() {
        mActionbarBack.get().animate()
                .alpha(0)
                .setInterpolator(accelerateInterpolator)
                .start();
        mActionBar.get().animate()
                .translationY(0)
                .setInterpolator(accelerateInterpolator)
                .start();
    }

    /**
     * 动画期间
     * 不允许滚动
     */
    private void setAllowScroll(boolean allowScroll) {
        final DrawerNestScrollView sv = dependencyWeakRef.get();
    }

    /**
     * 关键参数
     * 代表超过overscroll触发之后的偏移量
     * 超出正常滚动范围的距离
     */
    private float overScrollY = 0l;

    private boolean needSnapOver() {
        return (scrollSize -  maxScrollPoint) * DEFAULT_RATIO < overScrollY;
    }

    private boolean needSnapBack() {
        return (scrollSize - maxScrollPoint)* DEFAULT_RATIO > overScrollY;
    }

    private boolean isClosed = false;

    private Animator.AnimatorListener closeDrawerAnimatorLsn = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            try {
                isClosed = true;
                dependencyWeakRef.get().onStart(false);
            } catch (Exception e) {
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            try {
                setAllowScroll(true);
                dependencyWeakRef.get().onClose();
                dependencyWeakRef.get().scrollTo(0, 0);
                mDrawerContainer.get().setTranslationY(0);
                overScrollY = 0;
            } catch (Exception e) {
            }
        }
    };


    private Animator.AnimatorListener snapBackDrawerAnimatorLsn = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(Animator animation) {
            try {
                mDrawerContainer.get().setTranslationY(0);
                overScrollY = 0;
            } catch (Exception e) {
            }
        }

    };

    public static boolean canChildScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
        }
    }
}
