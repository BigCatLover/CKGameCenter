package com.laoyuegou.android.lib.utils;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.laoyuegou.android.lib.R;

import java.lang.ref.WeakReference;

import static android.view.animation.Animation.INFINITE;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-07-01 17:45
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class AnimationUtils {

    public static ObjectAnimator shakeAnimation(View view) {
        final WeakReference<View> viewRef = new WeakReference<View>(view);
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.spacing_medium);
        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }


    /***
     * 会使用 throwedview的  tag来缓存动画避免不必要的开销
     * 如果是imageview 使用的glide 这里的tag可能会失效
     * 可以用套用viewgroup来充当throwedView 避免和glide冲突
     * @param throwedView
     * @param target
     * @param useThrowedView throwedView 上次调用动画之后是否改变过位置
     *                     如果改变true
     * @return
     */
    public static ValueAnimator throwToTarget(final View throwedView, View target, final boolean useThrowedView) {
        //查看是否有緩存
        /*ValueAnimator valueAnimator = null;
        try {
            valueAnimator = (ValueAnimator) throwedView.getTag();
            if (valueAnimator != null) {
                return valueAnimator;
            }
        } catch (Exception e) {
        }*/
        //沒有緩存則新建ValueAnimator
        float startX, startY, toX, toY;
        final int[] location = new int[2];
        throwedView.getLocationOnScreen(location);
        startX = location[0];
        startY = location[1];
        target.getLocationOnScreen(location);
        toX = location[0];
        toY = location[1];
        final ImageView animaterView;
        if (!useThrowedView && throwedView instanceof ImageView) {
            animaterView = new ImageView(throwedView.getContext());
            animaterView.setImageDrawable(((ImageView) throwedView).getDrawable());
        } else {
            animaterView = null;
        }
        //计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //开始绘制贝塞尔曲线
        Path path = new Path();
        //移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        //使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        //mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        final PathMeasure pathMeasure = new PathMeasure(path, false);

        //★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration(1000);
        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            final float[] mCurrentPosition = new float[2];

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // ★★★★★获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
                // 离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                pathMeasure.getPosTan(value, mCurrentPosition, null);//mCurrentPosition此时就是中间距离点的坐标值
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                if (useThrowedView) {
                    throwedView.setTranslationX(mCurrentPosition[0]);
                    throwedView.setTranslationY(mCurrentPosition[1]);
                } else {
                    if (animaterView != null) {
                        animaterView.setTranslationX(mCurrentPosition[0]);
                        animaterView.setTranslationY(mCurrentPosition[1]);
                    }
                }
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup vg = (ViewGroup) animaterView.getParent();
                vg.removeView(animaterView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        throwedView.setTag(valueAnimator);
        return valueAnimator;
    }

    /**
     * 上下摆动
     *
     * @param target 目标
     * @param toUp   摆动是否向上
     * @param rate   摆动占view高度的比率
     * @return
     */
    public static ValueAnimator upAndDownAni(final View target, final boolean toUp, float rate) {
        final WeakReference<View> viewRef = new WeakReference<View>(target);
        final View view = viewRef.get();
        return upAndDownAni(view, toUp, true, rate);
    }

    /**
     * 上下摆动
     *
     * @param target 目标
     * @param toUp   摆动是否向上
     * @param rate   摆动占view高度的比率
     * @return
     */
    public static ValueAnimator upAndDownAni(final View target, final boolean toUp, boolean repeat, float rate) {

        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, 1, 0).setDuration(800);
        if (repeat) {
            valueAnimator.setRepeatCount(INFINITE);
        }
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);

        final int length = (int) (target.getHeight() * rate);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = (float) animation.getAnimatedValue();
                float deltY = (toUp ? -value : value) * length;
                target.setTranslationY(deltY);
            }
        });
        return valueAnimator;
    }

    /**
     * 一般用于收藏  点赞功能
     * 效果是放大之后缩小
     *
     * @param target
     * @return
     */
    public static ValueAnimator biggerThenReturnAni(final View target) {
        final WeakReference<View> viewRef = new WeakReference<View>(target);
        ValueAnimator animator = ObjectAnimator.ofFloat(0, 0.5f, 0).setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = (float) animation.getAnimatedValue();
                View view = viewRef.get();
                if(view!=null){
                    view.setScaleX(1 + value);
                    view.setScaleY(1 + value);
                }
            }
        });
        return animator;
    }


    /**
     * 移动到边界
     *
     * @param writeComment
     * @param rotationCircleNum
     * @param cacheKey          加上缓存逻辑
     * @return
     */
    public static ValueAnimator runToBeside(final View writeComment, final float rotationCircleNum, String cacheKey) {
        final WeakReference<View> viewRef = new WeakReference<View>(writeComment);
        ValueAnimator anim = ValueAnimator.ofFloat(0, rotationCircleNum).setDuration(300);
        final int rightDistance = (DisplayUtils.getScreenWidthInPx(writeComment.getContext())
                - writeComment.getRight())
                + writeComment.getWidth() / 2;
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = (float) animation.getAnimatedValue();
                View view = viewRef.get();
                if(view!=null){
                    view.setTranslationX(rightDistance * (value / rotationCircleNum));
                    view.setRotation(value * 360);
                    if (value == 0) {
                        view.setEnabled(false);
                    } else if (value == rotationCircleNum) {
                        view.setEnabled(true);
                    }
                }
            }
        });
        return anim;
    }

    /**
     * 从边界移动到原来的位置
     *
     * @param writeComment
     * @param rotationCircleNum
     * @param cacheKey
     * @return
     */
    public static ValueAnimator runBackFromBeside(final View writeComment, final float rotationCircleNum, String cacheKey) {
        final WeakReference<View> viewRef = new WeakReference<View>(writeComment);
        ValueAnimator anim = ValueAnimator.ofFloat(0, rotationCircleNum).setDuration(300);
        final int rightDistance = (DisplayUtils.getScreenWidthInPx(writeComment.getContext())
                - writeComment.getRight())
                + writeComment.getWidth() / 2;
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = rotationCircleNum - (float) animation.getAnimatedValue();
                View view = viewRef.get();
                if(view!=null){
                    view.setTranslationX(rightDistance * (value / rotationCircleNum));
                    view.setRotation(value * 360);
                    if (value == 0) {
                        view.setEnabled(true);
                    } else if (value == rotationCircleNum) {
                        view.setEnabled(false);
                    }
                }
            }
        });
        return anim;
    }


    public void quadToTarget(View startTarget, final View endTarget, ViewGroup rootRl) {
        final ImageView view = new ImageView(startTarget.getContext());
        Bitmap temBitmap = Bitmap.createBitmap(startTarget.getMeasuredWidth(), startTarget.getMeasuredHeight(), Bitmap.Config.RGB_565);
        //设置缓存
        startTarget.setDrawingCacheEnabled(true);
        startTarget.buildDrawingCache();
        //从缓存中获取当前屏幕的图片
        temBitmap = view.getDrawingCache();
    }
}
