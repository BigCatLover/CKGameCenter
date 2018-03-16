package com.laoyuegou.android.common.glide;

/**
 * Created by luy on 2017/7/25.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;


import com.laoyuegou.android.lib.widget.RingProgressBar;

import java.lang.ref.WeakReference;

/**
 * 圆形进度条handler
 */
public class RingProgressBarHandler extends Handler {

    private final WeakReference<Context> mActivity;
    private final RingProgressBar mProgressImageView;

    public RingProgressBarHandler(Context activity, RingProgressBar progressImageView) {
        super(Looper.getMainLooper());
        mActivity = new WeakReference<>(activity);
        mProgressImageView = progressImageView;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        final Context activity = mActivity.get();
        if (activity != null) {
            switch (msg.what) {
                case 1:
                    int percent = msg.arg1*100/msg.arg2;
                    mProgressImageView.setProgress(percent);
                    break;
                case 2:
                    mProgressImageView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }
}