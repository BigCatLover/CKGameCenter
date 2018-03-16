package com.jingyue.lygame.utils.recycleview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.jingyue.lygame.constant.AppConstants;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-17 17:22
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class ScrollSpeedLinearLayoutManger extends LinearLayoutManager {
    private Context contxt;
    private long mTime;

    public ScrollSpeedLinearLayoutManger(Context context) {
        this(context,AppConstants.DEFAULT_ANIMATION_TIME);
    }

    public ScrollSpeedLinearLayoutManger(Context context,long time) {
        super(context);
        this.contxt = context;
        this.mTime = time;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {

                    @Override
                    protected int calculateTimeForDeceleration(int dx) {
                        return (int) mTime;
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return super.calculateSpeedPerPixel(displayMetrics);
                    }

                    @Override
                    protected int calculateTimeForScrolling(int dx) {
                        return (int) mTime;
                    }

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return ScrollSpeedLinearLayoutManger.this
                                .computeScrollVectorForPosition(targetPosition);
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

}
