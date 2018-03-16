package com.jingyue.lygame.modules.personal;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CodeinfoBean;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.modules.personal.present.RegisterPresent;
import com.jingyue.lygame.modules.personal.view.ImageCodeView;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.laoyuegou.android.common.glide.ImageLoaderOptions;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.ImageLoader;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liu hong liang on 2016/10/6.
 */

public class UserNameRegisterFragment extends BaseFragment implements ImageCodeView {
    @BindView(R.id.et_reg_usname)
    EditText etRegUsname;
    @BindView(R.id.et_reg_uspwd)
    EditText etRegUspwd;
    @BindView(R.id.et_repeat_uspsd)
    EditText etRepeatUspsd;
    @BindView(R.id.et_yanzhengma)
    EditText etYanzhengma;
    @BindView(R.id.iv_showCode)
    ImageView ivShowCode;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_register_protocol)
    TextView tvRegisterProtocol;
    @BindView(R.id.iv_name_del)
    ImageView ivNameDel;
    @BindView(R.id.iv_passw_del)
    ImageView ivPasswDel;
    @BindView(R.id.iv_passw_commit_del)
    ImageView ivPasswCommitDel;
    @BindView(R.id.iv_passw_rightcode_del)
    ImageView ivPasswRightcodeDel;

    private RegisterPresent mRegisterPresent;
    private boolean isLayout = false;
    private CodeinfoBean bean;

    @Override
    protected int getLayoutId() {
        return R.layout.personal_username_register;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();

        add(mRegisterPresent = new RegisterPresent(this, this));

        mRegisterPresent.getImageCode();
    }

    private void setImageCode(String id) {
        ivShowCode.setImageDrawable(null);
        ImageLoader.getInstance()
                .showImage(new ImageLoaderOptions.Builder(ivShowCode,
                        new UrlConstant.Builder().
                                floatMoblieV2().
                                getCaptchaImg(
                                        (ivShowCode.getMeasuredWidth() - ivShowCode.getPaddingLeft() - ivShowCode.getPaddingRight()),
                                        (ivShowCode.getMeasuredHeight() - ivShowCode.getPaddingTop() - ivShowCode.getPaddingBottom()), id)).
                        isCrossFade(true).
                        signature(String.valueOf(System.currentTimeMillis())).
                        isSkipMemoryCache(true).
                        build());
    }


    private void setupUI() {

        setRegisterProtocol();

        /**
         * 获取验证码
         */
        ivShowCode.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (!isLayout) {
                    isLayout = true;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ivShowCode.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        //将验证码用图片的形式显示出来
        etRegUsname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivNameDel.setVisibility(View.VISIBLE);
                } else {
                    ivNameDel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etRegUsname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etRegUsname.getText().toString().trim().length() > 0) {
                        ivNameDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivNameDel != null) {
                        ivNameDel.setVisibility(View.INVISIBLE);
                    }


                }
            }
        });
        etRegUspwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivPasswDel.setVisibility(View.VISIBLE);
                } else {
                    ivPasswDel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etRegUspwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etRegUspwd.getText().toString().trim().length() > 0) {
                        ivPasswDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivPasswDel != null) {
                        ivPasswDel.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        etRepeatUspsd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivPasswCommitDel.setVisibility(View.VISIBLE);
                } else {
                    ivPasswCommitDel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etRepeatUspsd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etRepeatUspsd.getText().toString().trim().length() > 0) {
                        ivPasswCommitDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivPasswCommitDel != null) {
                        ivPasswCommitDel.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        etYanzhengma.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivPasswRightcodeDel.setVisibility(View.VISIBLE);
                } else {
                    ivPasswRightcodeDel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etYanzhengma.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etYanzhengma.getText().toString().trim().length() > 0) {
                        ivPasswRightcodeDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivPasswRightcodeDel != null) {
                        ivPasswRightcodeDel.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void setRegisterProtocol() {
        tvRegisterProtocol.setText(R.string.a_0191);
        final String click_text = getString(R.string.a_0192);
        SpannableString spStr = new SpannableString(click_text);
        spStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                //设置文件颜色
                ds.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
                //设置下划线
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                Utils.startActivity(getContext(), AgreementActivity.class);
            }
        }, 0, click_text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置点击后的颜色为透明，否则会一直出现高亮
        tvRegisterProtocol.setHighlightColor(Color.TRANSPARENT);
        tvRegisterProtocol.append(spStr);
        tvRegisterProtocol.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
    }

    @OnClick({R.id.iv_showCode, R.id.btn_register, R.id.iv_passw_rightcode_del, R.id.iv_passw_commit_del, R.id.iv_passw_del, R.id.iv_name_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_showCode:
                mRegisterPresent.getImageCode();
                break;
            case R.id.btn_register:
                gotoRegister();
                break;
            case R.id.iv_passw_rightcode_del:
                etYanzhengma.setText("");
                ivPasswRightcodeDel.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_passw_commit_del:
                etRepeatUspsd.setText("");
                ivPasswCommitDel.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_passw_del:
                etRegUspwd.setText("");
                ivPasswDel.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_name_del:
                etRegUsname.setText("");
                ivNameDel.setVisibility(View.INVISIBLE);
                break;

        }
    }

    private void gotoRegister() {
        delayClick(btnRegister);
        String phone = etRegUsname.getText().toString().trim();
        String password = etRegUspwd.getText().toString().trim();
        String authCode = etYanzhengma.getText().toString().trim();
        String repeatUspsd = etRepeatUspsd.getText().toString().trim();
        Pattern p = Pattern.compile("^[a-zA-Z][a-zA-Z0-9]{5,15}$");
        Pattern pattern = Pattern.compile("\\S{6,16}");
        if (!p.matcher(phone).matches()) {
            ToastUtils.showShort(R.string.a_0186);
            return;
        }
        if (TextUtils.isEmpty(authCode)) {
            ToastUtils.showShort(R.string.a_0187);
            return;
        }
        if (!authCode.equalsIgnoreCase(authCode)) {
            ToastUtils.showShort(R.string.a_0188);
            return;
        }

        if (!pattern.matcher(password).matches()) {
            ToastUtils.showShort(R.string.a_0189);
            return;
        }
        if (!password.equals(repeatUspsd)) {
            ToastUtils.showShort(R.string.a_0190);
            return;
        }
        //获取验证密码
        mRegisterPresent.regByName(bean.id,phone, password, authCode);
    }

    @Override
    public void onRegByUserSucess() {
        if (AppJumpUtils.goToGuide(getContext())) {
            baseActivity.finish();
        } else {
            //如果不需要引導直接跳轉到MainActivity
            Utils.startActivity(baseActivity, MainActivity.class);
        }
    }

    @Override
    public void onRegByPhoneSucess() {}

    private int reTrycount;

    @Override
    public void onGetCodeInfo(final CodeinfoBean bean) {
        this.bean = bean;
        if (isLayout) {
            setImageCode(bean.id);
        } else {
            if (reTrycount >= 3) {
                return;
            }
            ivShowCode.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onGetCodeInfo(bean);
                    reTrycount++;
                }
            }, 50);
        }
    }

    @Override
    public void onRegisterFailure() {
        mRegisterPresent.getImageCode();
    }

}
