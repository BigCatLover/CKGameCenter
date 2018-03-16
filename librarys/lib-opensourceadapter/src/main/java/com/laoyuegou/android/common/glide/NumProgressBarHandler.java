package com.laoyuegou.android.common.glide;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by luy on 2017/7/25.
 * 带数字的圆形进度条handler
 */

public class NumProgressBarHandler extends Handler {

    private final WeakReference<Context> mActivity;
    private TextView mTextView;
    private ProgressBar mProgressBar;

    public NumProgressBarHandler(Context activity, TextView textView, ProgressBar progressBar) {
        super(Looper.getMainLooper());
        mActivity = new WeakReference<>(activity);
        mTextView = textView;
        mProgressBar = progressBar;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        final Context activity = mActivity.get();
        if (activity != null) {
            switch (msg.what) {
                case 1:
                    mTextView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    int percent = msg.arg1*100/msg.arg2;
                    mTextView.setText(percent + "%");
                    break;
                case 2:
                    mTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                    mTextView.setTag(null);
                default:
                    break;
            }
        }
    }
}
