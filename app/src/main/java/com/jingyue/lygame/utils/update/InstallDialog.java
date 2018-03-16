package com.jingyue.lygame.utils.update;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.ToolUtils;
import com.jingyue.lygame.utils.WebpUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.laoyuegou.android.lib.widget.RoundAngleButton;

import static com.jingyue.lygame.utils.update.NewVersionUtil.NEW_VERSION_INSTALLED;
import static com.jingyue.lygame.utils.update.NewVersionUtil.UPDATE_SKIP;

/**
 * Created by liukun on 2017/6/15.
 */
public class InstallDialog extends Dialog {
    private static UpdateDialogController mController = null;

    public InstallDialog(Context context) {
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
        mController.updateDialogClose.setVisibility(View.INVISIBLE);
        mController.mSecondTitleTV = (TextView) findViewById(R.id.update_dialog_second_title);
        mController.updateDialogOneOk = (RoundAngleButton) findViewById(R.id.update_dialog_one_ok);


        mController.imgTitleBg = (ImageView) findViewById(R.id.img_title_bg);
        mController.imgTitleBg.setImageDrawable(WebpUtils.webpToDrawable(R.drawable.update_downloaded_dialog_bg));
        mController.imgTitleBg.setVisibility(View.VISIBLE);
        mController.installView();
    }

    public static class Builder {
        public Context mContext = null;

        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 是否显示关闭按钮
         */
        public void setBtnCloseStatus(boolean isShow) {
            if (mController.updateDialogClose != null) {
                if (isShow) {
                    mController.updateDialogClose.setVisibility(View.VISIBLE);
                } else {
                    mController.updateDialogClose.setVisibility(View.GONE);
                }
            }
        }

        /**
         * 设置button文本
         *
         * @param txt
         */
        public void setDialogBtnOkTxt(String txt) {
            if (!StringUtils.isEmptyOrNull(txt) && null != mController.updateDialogOneOk) {
                mController.updateDialogOneOk.setText(txt);
            }
        }

        public InstallDialog show() {
            final InstallDialog dialog = new InstallDialog(mContext);
            dialog.show();
            return dialog;
        }
    }

    private class UpdateDialogController {
        public boolean mForce = false;
        private TextView mTitleView = null;           // dialog头部文本
        private TextView mSecondTitleTV = null;       // dialog头部副标题
        private TextView mContentView = null;         // dialog内容
        private ImageView updateDialogClose;          // 关闭按钮
        private ImageView imgTitleBg;                 // 头部背景图片
        private RoundAngleButton updateDialogOneOk;   // 底部一个按钮

        private void installView() {


            final String newVersion = SettingUtil.readString(Utils.getContext(), NewVersionUtil.INSTALL_NEW_VERSION, "");
            int force = SettingUtil.readInt(Utils.getContext(), newVersion +
                    NewVersionUtil.FORCE_UPDATE, 0);
            String log = SettingUtil.readString(Utils.getContext(), newVersion +
                    NewVersionUtil.UPDATE_LOG, "");
            String title = SettingUtil.readString(Utils.getContext(), newVersion +
                    NewVersionUtil.UPDATE_TITLE, "");
            String fileSize = SettingUtil.readString(Utils.getContext(), newVersion +
                    NewVersionUtil.UPDATE_SIZE, "");


            if (mContentView != null) {  //对话框内容
                mContentView.setVisibility(View.VISIBLE);
                StringBuffer sb = new StringBuffer();
                sb.append(log);
                mContentView.setText(sb.toString());
            }

            if (mTitleView != null) {  //对话框内容
                mTitleView.setText(title);
            }

            //设置副标题
            if (mSecondTitleTV != null) {
                if (!TextUtils.isEmpty(newVersion)) {
                    mSecondTitleTV.setText(newVersion + getContext().getString(R.string.a_0118));
                }
            }

            mForce = force == 1;

            if (!mForce) {
                mController.updateDialogClose.setVisibility(View.VISIBLE);
            } else {
                mController.updateDialogClose.setVisibility(View.GONE);
            }

            mController.updateDialogClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingUtil.write(Utils.getContext(), UPDATE_SKIP + newVersion,true);
                    dismiss();
                }
            });

            mController.updateDialogOneOk.setText(getContext().getResources().getString(R.string.a_0123));
            mController.updateDialogOneOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingUtil.write(Utils.getContext(), NEW_VERSION_INSTALLED + newVersion, newVersion);
                    //安装新包
                    ToolUtils.installApk(getContext(), NewVersionUtil.getInstallNewVersionFile(getContext(),newVersion));
                }
            });

            setCanceledOnTouchOutside(false);
            setCancelable(!mForce);
        }
    }

}
