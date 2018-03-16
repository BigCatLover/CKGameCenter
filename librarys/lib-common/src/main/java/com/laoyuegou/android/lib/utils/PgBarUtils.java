package com.laoyuegou.android.lib.utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;


import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-07-02 10:57
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 *
 */
public class PgBarUtils {

    public static class Builder{

        public static final int NO_ID = -0x1;
        private View targetView;
        private Context context;
        private boolean hideTargetView;
        private int layoutId = NO_ID;
        private int mGravity;

        private Builder(){
            hideTargetView = true;
            layoutId = NO_ID;
            mGravity = Gravity.CENTER;
        }

        /**
         * key  :target view
         * view :injected progressbar
         */
        private HashMap<View,ProgressBar> mBarStack = new HashMap<>();

        public Builder layoutId(@LayoutRes int id){
            layoutId = id;
            return this;
        }

        public Builder(Context context) {
            this.context = context;
        }
        
        public Builder targertView(View targetView){
            this.targetView = targetView;
            return this;
        }

        /**
         * 当progressbar显示的时候是否，显示target view
         */
        public Builder hideTargetView(boolean showTargetView) {
            this.hideTargetView = showTargetView;
            return this;
        }

        public Builder gravity(int gravity){
            this.mGravity = gravity;
            return this;
        }

        public ProgressBar show(){
            ProgressBar bar = mBarStack.get(targetView);
            if(bar == null){
                if(layoutId != NO_ID){
                    bar = (ProgressBar) LayoutInflater.from(context).inflate(layoutId,null);
                }
                if(bar == null) bar = new ProgressBar(context);
                bar.setTag(context.getClass().getName());
                int size = Math.min(targetView.getWidth(),targetView.getHeight());
                ViewGroup viewParent = (ViewGroup) targetView.getParent();
                //replace target view with framelayout
                //then  add target view and progressbar to the
                //new framelayout contianer
                viewParent.removeView(targetView);
                FrameLayout container = new FrameLayout(context);
                //当为相对布局或者其他组件之间存在依赖关系的时候，需要用容器布局
                //替代他们的依赖，防止布局出现问题。
                container.setId(targetView.getId());
                container.setLayoutParams(targetView.getLayoutParams());

                //add target view to the new container
                container.addView(targetView,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                //create a progress bar and add to the new container
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size,size);
                final int padding = DisplayUtils.dip2px(context,5);
                bar.setPadding(padding,padding,padding,padding);
                //progress bar 居target view的中间位置
                switch (mGravity){
                    case Gravity.LEFT:
                        if(targetView.getHeight() == size){
                            lp.leftMargin += (targetView.getWidth() - size) / 3;
                            break;
                        }
                    case Gravity.RIGHT:
                        if(targetView.getHeight() == size){
                            lp.leftMargin += (targetView.getWidth() - size) * 2 / 3;
                            break;
                        }
                    case Gravity.TOP:
                        if(targetView.getWidth() == size){
                            lp.topMargin += (targetView.getHeight() - size) / 3;
                            break;
                        }
                    case Gravity.BOTTOM:
                        if(targetView.getWidth() == size){
                            lp.topMargin += (targetView.getHeight() - size) * 2 / 3;
                            break;
                        }
                    default:
                        if(targetView.getWidth() > targetView.getHeight()){
                            lp.leftMargin += (targetView.getWidth() - size) / 2;
                        }else{
                            lp.topMargin += (targetView.getHeight() - size) /2;
                        }
                        break;
                }

                container.addView(bar,lp);
                viewParent.addView(container);
                mBarStack.put(targetView,bar);
            }
            if(hideTargetView) targetView.setVisibility(View.INVISIBLE);
            bar.setVisibility(View.VISIBLE);
            return bar;
        }

        public void remove(){
            mBarStack.clear();
            mBarStack = null;
        }

        public void hide(View view){
            view.setVisibility(View.VISIBLE);
            ProgressBar bar = mBarStack.get(view);
            if(bar != null)  bar.setVisibility(View.GONE);
        }
    }

    private HashMap<String,Builder> builders = new HashMap<>();

    public void destroyActivity(Context context){
        final Builder builder = builders.remove(context.getClass().getName());
        if(builder != null) builder.remove();
    }

    public Builder getBuilder(Context context){
        Builder builder = builders.get(context.getClass().getName());
        if(builder == null){
            builder = new Builder(context);
            builders.put(context.getClass().getName(),builder);
        }
        return builder;
    }

    private static final AtomicReference<PgBarUtils> INSTANCE = new AtomicReference<PgBarUtils>();

    private PgBarUtils(){}

    public static PgBarUtils getInstance() {
        for (; ;) {
            PgBarUtils current = INSTANCE.get();
            if (current != null) {
                return current;
            }
            current = new PgBarUtils();
            if (INSTANCE.compareAndSet(null, current)) {
                return current;
            }
        }
    }

}
