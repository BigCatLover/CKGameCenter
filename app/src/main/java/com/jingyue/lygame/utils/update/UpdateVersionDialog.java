package com.jingyue.lygame.utils.update;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.UpdateResponse;
import com.jingyue.lygame.events.EventDownloadEnd;
import com.jingyue.lygame.events.EventDownloadStart;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.WebpUtils;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.Utils;
import com.laoyuegou.android.lib.widget.RoundAngleButton;

import org.greenrobot.eventbus.Subscribe;


/**
 * 更新版本
 * Created by wang on 15/10/14.
 */
public class UpdateVersionDialog extends Dialog {
    private UpdateDialogController mController = null;

    public UpdateVersionDialog(Context context) {
        super(context, R.style.common_dialog);
        mController = new UpdateDialogController();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.umeng_update_dialog);
        mController.mTitleView = (TextView) findViewById(R.id.txt_title);
        mController.mContentView = (TextView) findViewById(R.id.update_dialog_content);
        mController.updateDialogClose = (ImageView) findViewById(R.id.update_dialog_close);
        mController.updateDialogClose.setVisibility(View.VISIBLE);

        mController.mSecondTitleTV = (TextView) findViewById(R.id.update_dialog_second_title);

        mController.updateDialogOneOk = (RoundAngleButton) findViewById(R.id.update_dialog_one_ok);

        mController.imgTitleBg = (ImageView) findViewById(R.id.img_title_bg);
        mController.imgTitleBg.setImageDrawable(WebpUtils.webpToDrawable(R.drawable.update_undownloaded_dialog_bg));
        mController.imgTitleBg.setVisibility(View.VISIBLE);
        mController.installView();
    }

    public static class Builder {
        private UpdateDialogParams mParams = null;

        public Builder(Context context) {
            mParams = new UpdateDialogParams(context);
        }

        public Builder setUpdateResponse(UpdateResponse updateResponse) {
            mParams.mUpdateResponse = updateResponse;
            return this;
        }

        public Builder setForce(boolean force) {
            mParams.mForce = force;
            return this;
        }

        public UpdateVersionDialog show() {
            final UpdateVersionDialog dialog = new UpdateVersionDialog(mParams.mContext);
            mParams.apply(dialog.mController);
            dialog.show();
            return dialog;
        }

        private static class UpdateDialogParams {
            private UpdateResponse mUpdateResponse = null;
            public Context mContext = null;
            private boolean mForce = false;

            public UpdateDialogParams(Context context) {
                mContext = context;
            }

            public void apply(UpdateDialogController controller) {
                controller.mUpdateResponse = mUpdateResponse;
                controller.mForce = mForce;
            }
        }
    }

    private class UpdateDialogController {
        private UpdateResponse mUpdateResponse = null;
        public boolean mForce = false;
        private TextView mTitleView = null;           // dialog头部文本
        private TextView mSecondTitleTV = null;           // dialog头部副标题
        private TextView mContentView = null;         // dialog内容
        private ImageView updateDialogClose;          // 关闭按钮
        private ImageView imgTitleBg;                 // 头部背景图片
        private RoundAngleButton updateDialogOneOk;   // 底部一个按钮

        int force;
        String log;
        String title;
        String fileSize;
        String newVersion;
        String url;

        private void installView() {
            if (mUpdateResponse != null) {
                newVersion = mUpdateResponse.getNew_version();
                force = mUpdateResponse.getForce_update();
                log = mUpdateResponse.getUpdate_log();
                title = mUpdateResponse.getUpdate_title();
                fileSize = mUpdateResponse.getTarget_size();
                url = mUpdateResponse.getApk_url();
            } else {
                newVersion = SettingUtil.readString(Utils.getContext(), NewVersionUtil.INSTALL_NEW_VERSION, "");
                force = SettingUtil.readInt(Utils.getContext(), newVersion +
                        NewVersionUtil.FORCE_UPDATE, 0);
                log = SettingUtil.readString(Utils.getContext(), newVersion +
                        NewVersionUtil.UPDATE_LOG, "");
                title = SettingUtil.readString(Utils.getContext(), newVersion +
                        NewVersionUtil.UPDATE_TITLE, "");
                fileSize = SettingUtil.readString(Utils.getContext(), newVersion +
                        NewVersionUtil.UPDATE_SIZE, "");
                url = SettingUtil.readString(Utils.getContext(), newVersion +
                        NewVersionUtil.UPDATE_URL, "");
            }

            if (mContentView != null && !TextUtils.isEmpty(log)) {
                mContentView.setVisibility(View.VISIBLE);
                mContentView.setText(log);
            }

            //设置主标题
            if (mTitleView != null && !TextUtils.isEmpty(title)) {  //对话框内容
                mTitleView.setText(title);
            }

            //设置副标题
            if (mSecondTitleTV != null && !TextUtils.isEmpty(newVersion)) {
                mSecondTitleTV.setText(newVersion + getContext().getString(R.string.a_0129));
            }

            if (!StringUtils.isEmptyOrNull(url)) {
                String fileName = UpdateUtils.getFileNameByPath(url);
                if (!StringUtils.isEmptyOrNull(fileName)) {
                    updateDialogOneOk.setText(getContext().getResources().getString(R.string.a_0126));
                }
            }

            if (!mForce) {
                updateDialogClose.setVisibility(View.VISIBLE);
            } else {
                updateDialogClose.setVisibility(View.GONE);
            }

            updateDialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingUtil.write(getContext(),NewVersionUtil.UPDATE_SKIP+newVersion,true);
                    dismiss();
                }
            });

            mController.updateDialogOneOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewVersionUtil.checkNewVersionAndInstall(url, newVersion,true);
                    if (!mForce) {
                        dismiss();
                    }
                }
            });

            setCanceledOnTouchOutside(!mForce);
            setCancelable(!mForce);
        }
    }

    /**
     * 开始下载
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(EventDownloadStart event) {
        if (null == mController || null == mController.updateDialogOneOk) {
            return;
        }
        (mController.updateDialogOneOk).setText(getContext().getResources().getString(R.string.a_0126));
    }


    @Override
    protected void onStart() {
        if (!EBus.getDefault().isRegistered(this)) {
            EBus.getDefault().register(this);
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (EBus.getDefault().isRegistered(this)) {
            EBus.getDefault().unregister(this);
        }
        super.onStop();
    }

    /**
     * 结束下载
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(EventDownloadEnd event) {
        if (null == mController || null == mController.updateDialogOneOk) {
            return;
        }

        (mController.updateDialogOneOk).setText(getContext().getResources().getString(R.string.a_0126));
    }
}

