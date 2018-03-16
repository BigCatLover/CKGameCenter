package com.jingyue.lygame.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import com.jingyue.lygame.R;

public class LoadingDialog extends Dialog {
    private boolean isTouchCancel = true;
    private ImageView mImageView;
    private static AnimationDrawable mAniDrawable;

    public LoadingDialog(Context context) {
        super(context, R.style.common_dialog);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_progress_loading);
        mImageView = (ImageView) findViewById(R.id.progress_image);
    }

    /**
     * 是否可以取消
     *
     * @param cancel 是否可以取消
     * @return this
     */
    public LoadingDialog setTouchCanceled(boolean cancel) {
        isTouchCancel = cancel;
        return this;
    }

    @Override
    public void show() {
        super.show();
        if (null != mImageView) {
            mImageView.setImageResource(R.drawable.progress_bar_ani);
            mAniDrawable = (AnimationDrawable) mImageView.getDrawable();
            if(null != mAniDrawable){
                mAniDrawable.start();
            }
        }
        setCanceledOnTouchOutside(isTouchCancel);
    }
    @Override
    public void dismiss() {
        if (null != mAniDrawable) {
            if (mAniDrawable.isRunning()) {
                mAniDrawable.stop();
            }
            mAniDrawable = null;
        }
        super.dismiss();
    }
}
