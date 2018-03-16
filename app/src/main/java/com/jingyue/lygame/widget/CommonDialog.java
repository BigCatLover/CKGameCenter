package com.jingyue.lygame.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.utils.StringUtils;

/**
 * 定制的通用对话框，使应用内对话框风格统一
 */
public class CommonDialog extends Dialog {
    private CommonDialogController mController = null;

    public CommonDialog(Context context) {
        super(context, R.style.common_dialog);
        mController = new CommonDialogController();
    }

    public void setCommentEditEnabled(boolean enable) {
        if (mController != null) {
            mController.setCommentEditEnabled(enable);
        }
    }

    public void setCommnetEditHint(String hint) {
        if (mController != null && hint != null) {
            mController.setCommentEditHint(hint);
        }
    }

    public void setCommentEditLength(int length) {
        if (mController != null) {
            mController.setCommentEditLength(length);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog);
        mController.mTitleView = (TextView) findViewById(R.id.dialog_title);
        mController.mContentView = (TextView) findViewById(R.id.dialog_content_message);
        mController.mLeftButton = (TextView) findViewById(R.id.dialog_left_button);
        mController.mRightButton = (TextView) findViewById(R.id.dialog_right_button);
        mController.mOneButton = (TextView) findViewById(R.id.dialog_button);
        mController.mComment = (EditText) findViewById(R.id.comment_message);
        mController.mLayoutOneButtons = (RelativeLayout) findViewById(R.id.dialog_one_button);
        mController.mLayoutTwoButtons = (RelativeLayout) findViewById(R.id.dialog_two_button);
        mController.installView();
    }

    public String getComment() {
        return mController != null ? mController.getComment() : "";
    }

    public static class Builder {
        private CommonDialogParams mParams = null;
        private CommonDialog dialog = null;
        public Builder(Context context) {
            mParams = new CommonDialogParams(context);
        }

        public Builder setTitle(String title) {
            mParams.mTitle = title;
            return this;
        }

        public Builder setContent(String content) {
            mParams.mContent = content;
            return this;
        }

        public Builder setContent(Spanned spanned) {
            mParams.mSpanned = spanned;
            return this;
        }

        public Builder setContentGravity(int gravity) {
            mParams.mGravity = gravity;
            return this;
        }

        public Builder setContentSize(int size) {//此参数请直接写sp的数值即可
            mParams.mSize = size;
            return this;
        }

        public Builder setOneButtonInterface(String buttonText, View.OnClickListener btn) {
            mParams.mOneButtonMsg = buttonText;
            mParams.mOneButtonInterface = btn;
            return this;
        }

        public Builder setLeftButtonInterface(String leftButtonText, View.OnClickListener left) {
            mParams.mLeftButtonMsg = leftButtonText;
            mParams.mLeftButtonInterface = left;
            return this;
        }

        public Builder setRightButtonInterface(String rightButtonText, View.OnClickListener right) {
            mParams.mRightButtonMsg = rightButtonText;
            mParams.mRightButtonInterface = right;
            return this;
        }

        public CommonDialog show() {
            final CommonDialog dialog = new CommonDialog(mParams.mContext);
            mParams.apply(dialog.mController);
            dialog.show();
            return dialog;
        }

//        public void showDialog() {
//            if (null == dialog) {
//                dialog = new CommonDialog(mParams.mContext);
//            }
//            mParams.apply(dialog.mController);
//            dialog.show();
//        }

        public void dismiss() {
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        }

        public Builder setTouchAble(boolean touchAble) {
            mParams.mTouchAble = touchAble;
            return this;
        }

        public Builder setNeedComment(boolean need) {
            mParams.mNeedMessage = need;
            return this;
        }

        private static class CommonDialogParams {
            public String mTitle = "";
            public String mContent = "";
            public Spanned mSpanned;
            public int mGravity;
            public int mSize;
            public String mLeftButtonMsg = "";
            public String mRightButtonMsg = "";
            public String mOneButtonMsg = "";
            public View.OnClickListener mLeftButtonInterface = null;
            public View.OnClickListener mRightButtonInterface = null;
            public View.OnClickListener mOneButtonInterface = null;
            public boolean mNeedMessage = false;
            public Context mContext = null;
            public boolean mTouchAble = false;

            public CommonDialogParams(Context context) {
                mContext = context;
            }

            public void apply(CommonDialogController controller) {
                controller.mTitle = mTitle;
                controller.mContent = mContent;
                controller.mSpanned = mSpanned;
                controller.mGravity = mGravity;
                controller.mSize = mSize;
                controller.mOneButtonMsg = mOneButtonMsg;
                controller.mLeftButtonMsg = mLeftButtonMsg;
                controller.mRightButtonMsg = mRightButtonMsg;
                controller.mLeftButtonInterface = mLeftButtonInterface;
                controller.mRightButtonInterface = mRightButtonInterface;
                controller.mOneButtonInterface = mOneButtonInterface;
                controller.mNeedComment = mNeedMessage;
                controller.mTouchable = mTouchAble;
            }
        }
    }

    private class CommonDialogController {
        public String mTitle = "";
        public String mContent = "";
        public Spanned mSpanned;
        public int mGravity;
        public int mSize;
        public String mLeftButtonMsg = "";
        public String mRightButtonMsg = "";
        public String mOneButtonMsg = "";
        public boolean mNeedComment = false;
        public View.OnClickListener mLeftButtonInterface = null;
        public View.OnClickListener mRightButtonInterface = null;
        public View.OnClickListener mOneButtonInterface = null;
        private TextView mTitleView = null;
        private TextView mContentView = null;
        private TextView mOneButton = null;
        private TextView mLeftButton = null;
        private TextView mRightButton = null;
        private EditText mComment = null;

        private RelativeLayout mLayoutOneButtons = null;
        private RelativeLayout mLayoutTwoButtons = null;

        private boolean mTouchable = false;

        private void setCommentEditEnabled(boolean enable) {
            if (mComment != null) {
                mComment.setEnabled(enable);
                mComment.requestFocus();
            }
        }

        private void setCommentEditHint(String hint) {
            if (mComment != null) {
                mComment.setHint(hint);
            }
        }

        private void setCommentEditLength(int size) {
            if (mComment != null) {
                mComment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(size)});
            }
        }

        private String getComment() {
            if (mComment != null && mComment.getText() != null) {
                return mComment.getText().toString();
            }
            return "";
        }

        private void installView() {
            if (mTitleView != null) { //标题栏
                if (mTitle == null || mTitle.equalsIgnoreCase("")) {
                    mTitleView.setVisibility(View.GONE);
                } else {
                    mTitleView.setVisibility(View.VISIBLE);
                    mTitleView.setText(mTitle);
                }
            }
            if (mContentView != null) {  //对话框内容
                if (StringUtils.isEmptyOrNull(mContent) && mSpanned == null) {
                    mContentView.setVisibility(View.GONE);
                } else {
                    mContentView.setVisibility(View.VISIBLE);
                    if(mSpanned == null) {
                        mContentView.setText(mContent);
                    } else {
                        mContentView.setText(mSpanned);
                    }

                    if (mGravity > 0) {
                        mContentView.setGravity(mGravity);
                    }
                    if (mSize > 0) {
                        mContentView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mSize);
                    }
                }
            }

            if (mOneButton != null && !StringUtils.isEmptyOrNull(mOneButtonMsg)) {
                mLayoutOneButtons.setVisibility(View.VISIBLE);
                mLayoutTwoButtons.setVisibility(View.GONE);
                mOneButton.setText(mOneButtonMsg);
                mOneButton.setOnClickListener(mOneButtonInterface);
            } else {
                mLayoutTwoButtons.setVisibility(View.VISIBLE);
                mLayoutOneButtons.setVisibility(View.GONE);
                if (mLeftButton != null) { //左侧按钮
                    if (mLeftButtonMsg == null || mLeftButtonMsg.equalsIgnoreCase("")
                            || mLeftButtonInterface == null) {
                        mLeftButton.setVisibility(View.GONE);
                    } else {
                        mLeftButton.setText(mLeftButtonMsg);
                        mLeftButton.setOnClickListener(mLeftButtonInterface);
                    }
                }
                if (mRightButton != null) {  //右侧按钮
                    if (mRightButtonMsg == null || mRightButtonMsg.equalsIgnoreCase("")
                            || mRightButtonInterface == null) {
                        mRightButton.setVisibility(View.GONE);
                    } else {
                        mRightButton.setText(mRightButtonMsg);
                        mRightButton.setOnClickListener(mRightButtonInterface);
                    }
                }
            }


            if (mComment != null) {
                if (mNeedComment) {
                    mComment.setVisibility(View.VISIBLE);
                } else {
                    mComment.setVisibility(View.GONE);
                }
            }
            setCanceledOnTouchOutside(mTouchable);
            setCancelable(mTouchable);
        }
    }
}
