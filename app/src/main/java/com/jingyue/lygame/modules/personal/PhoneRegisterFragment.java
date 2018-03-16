package com.jingyue.lygame.modules.personal;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CodeinfoBean;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.modules.personal.present.RegisterPresent;
import com.jingyue.lygame.modules.personal.view.ImageCodeView;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.laoyuegou.android.lib.utils.RegexUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.luck.picture.lib.tools.DoubleUtils;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by liu hong liang on 2016/10/6.
 */

public class PhoneRegisterFragment extends BaseFragment implements ImageCodeView {

    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.iv_phone_del)
    ImageView ivPhoneDel;
    @BindView(R.id.et_auth_code)
    EditText etAuthCode;
    @BindView(R.id.iv_code_del)
    ImageView ivCodeDel;
    @BindView(R.id.btn_getCode)
    Button btnGetCode;
    @BindView(R.id.et_phone_password)
    EditText etPhonePassword;
    @BindView(R.id.iv_password_del)
    ImageView ivPasswordDel;
    @BindView(R.id.btn_register)
    Button btnRegister;
    Unbinder unbinder;
    @BindView(R.id.tv_protocol)
    TextView tvProtocol;
    private String sessionId;
    Handler handler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.personal_phone_register;
    }

    private RegisterPresent mImageCodePresent;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        add(mImageCodePresent = new RegisterPresent(this, this));

        setupUI();
    }

    private void setupUI() {
        ivPhoneDel.setVisibility(View.INVISIBLE);
        ivPasswordDel.setVisibility(View.INVISIBLE);
        ivCodeDel.setVisibility(View.INVISIBLE);
        tvProtocol.setText("注册即表示同意");
        String click_text = "捞月游戏平台用户协议";

        SpannableString spStr = new SpannableString(click_text);

        spStr.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));       //设置文件颜色
                ds.setUnderlineText(false);      //设置下划线
            }

            @Override
            public void onClick(View widget) {
                Utils.startActivity(baseActivity, AgreementActivity.class);
            }
        }, 0, click_text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvProtocol.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        tvProtocol.append(spStr);
        tvProtocol.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivPhoneDel.setVisibility(View.VISIBLE);
                } else {
                    ivPhoneDel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etPhone.getText().toString().trim().length() > 0) {
                        ivPhoneDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivPhoneDel != null) {
                        ivPhoneDel.setVisibility(View.INVISIBLE);
                    }


                }
            }
        });
        etAuthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivCodeDel.setVisibility(View.VISIBLE);
                } else {
                    ivCodeDel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etAuthCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etAuthCode.getText().toString().trim().length() > 0) {
                        ivCodeDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivCodeDel != null) {
                        ivCodeDel.setVisibility(View.INVISIBLE);
                    }


                }
            }
        });
        etPhonePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    ivPasswordDel.setVisibility(View.VISIBLE);
                } else {
                    ivPasswordDel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPhonePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etPhonePassword.getText().toString().trim().length() > 0) {
                        ivPasswordDel.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (ivPasswordDel != null) {
                        ivPasswordDel.setVisibility(View.INVISIBLE);
                    }


                }
            }
        });

    }

    @OnClick({R.id.btn_getCode, R.id.btn_register, R.id.iv_phone_del, R.id.iv_code_del, R.id.iv_password_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_getCode:
                getAuthCode();
                break;
            case R.id.btn_register:
                gotoRegister();
                break;
            case R.id.iv_phone_del:
                etPhone.setText("");
                ivPhoneDel.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_code_del:
                etAuthCode.setText("");
                ivCodeDel.setVisibility(View.INVISIBLE);
                break;
            case R.id.iv_password_del:
                etPhonePassword.setText("");
                ivPasswordDel.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void gotoRegister() {
        delayClick(btnRegister);
        String phone = etPhone.getText().toString().trim();
        String password = etPhonePassword.getText().toString().trim();
        String authCode = etAuthCode.getText().toString().trim();
        if (TextUtils.isEmpty(authCode)) {
            ToastUtils.showShort("请先获取验证码！");
            return;
        }
        if (!RegexUtils.isMobileSimple(phone)) {
            ToastUtils.showShort("手机号有误");
            return;
        }
        if (!RegexUtils.isMatch("\\S{6,16}", password)) {
            ToastUtils.showShort(getString(R.string.a_0189));
            return;
        }
        if (TextUtils.isEmpty(authCode) && authCode.length() != 4) {
            ToastUtils.showShort("请输入4位的短信验证码");
            return;
        }
        mImageCodePresent.regbyPhone(authCode, phone, password);
    }

    private void getAuthCode() {
        String phone = etPhone.getText().toString().trim();
        if (!RegexUtils.isMobileSimple(phone)) {
            ToastUtils.showShort("手机号有误");
            return;
        }
        if(!DoubleUtils.isFastDoubleClick()){
            //获取验证码
            mImageCodePresent.sendPhoneCode(phone);
        }
    }

    private void changeCodeBtn(int delayTime) {
        btnGetCode.setTag(delayTime);
        if (delayTime <= 0) {
            btnGetCode.setEnabled(true);
            btnGetCode.setText("重新发送");
            return;
        } else {
            btnGetCode.setText("获取验证码(" + delayTime + ")");
            btnGetCode.setEnabled(false);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int delayTime = (int) btnGetCode.getTag();
                changeCodeBtn(--delayTime);
            }
        }, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRegByUserSucess() {

    }

    @Override
    public void onRegByPhoneSucess() {
        if(AppJumpUtils.goToGuide(getContext())){
            baseActivity.finish();
        }else {
            //如果不需要引導直接跳轉到MainActivity
            Utils.startActivity(baseActivity, MainActivity.class);
        }
    }

    @Override
    public void onGetCodeInfo(CodeinfoBean bean) {
        changeCodeBtn(60);
    }

    @Override
    public void onRegisterFailure() {}
}
