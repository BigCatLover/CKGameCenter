package com.jingyue.lygame.modules.personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.LoginInfoBean;
import com.jingyue.lygame.clickaction.LoginAction;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.events.LoginEvent;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.modules.personal.present.LoginPresent;
import com.jingyue.lygame.modules.personal.view.LoginView;
import com.jingyue.lygame.modules.common.webview.WebViewActivity;
import com.jingyue.lygame.utils.AppJumpUtils;
import com.jingyue.lygame.utils.RecordUserPopUtil;
import com.laoyuegou.android.lib.utils.AnimationUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-04 10:41
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class LoginActivity extends BaseActivity implements LoginView {

    /**
     * 决定loginActivity的 back 和 finish事件之后的走向
     */
    @IntDef({BACK_BACK_LOGIN_BACK, BACK_EXIT_LOGIN_MAIN, BACK_MAIN_LOGIN_MAIN})
    @interface Actoin {}

    /**
     * 返回键退出app
     * 登录成功去主页面
     */
    public static final int BACK_EXIT_LOGIN_MAIN = 0x1;

    /**
     * 结束返回上一级
     * 登录成功返回上一级
     */
    public static final int BACK_BACK_LOGIN_BACK = 0x2;

    /**
     * 登出到登录页的时候传递参数
     * 结束返回主页面
     * 登录成功返回主界面
     */
    public static final int BACK_MAIN_LOGIN_MAIN = 0x3;

    public static final void open(Context context, @Actoin int from) {
        Bundle bundle = new Bundle();
        bundle.putInt(KeyConstant.FROM, from);
        switch (from) {
            case BACK_BACK_LOGIN_BACK:
                Utils.startActivityForResult(context, LoginActivity.class, bundle, KeyConstant.LOGIN_REQUEST_CODE);
                break;
            case BACK_MAIN_LOGIN_MAIN:
                AppJumpUtils.startLogin(context);
            default:
                Utils.startActivity(context, LoginActivity.class, bundle);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (BACK_EXIT_LOGIN_MAIN == from) {
            AppJumpUtils.exitApp(this);
            return;
        } else if (BACK_MAIN_LOGIN_MAIN == from) {
            MainActivity.start(this);
            return;
        }
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @BindView(R.id.et_login_username)
    EditText etLoginUsername;
    @BindView(R.id.iv_more)
    ImageView ivMore;
    @BindView(R.id.iv_username_del)
    ImageView ivUsernameDel;
    @BindView(R.id.et_login_password)
    EditText etLoginPassword;
    @BindView(R.id.iv_password_del)
    ImageView ivPasswordDel;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_forgot_password)
    TextView tvForgotPassword;
    @BindView(R.id.tv_user_register)
    TextView tvUserRegister;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_main;
    }

    private int from;
    private LoginPresent mPresent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enableEventBus();
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            from = getIntent().getIntExtra(KeyConstant.FROM, BACK_BACK_LOGIN_BACK);
        }

        add(mPresent = new LoginPresent(this, this));

        if (mPresent.isLogin() && BACK_EXIT_LOGIN_MAIN == from) {
            Utils.startActivity(this, MainActivity.class);
            finish();
            return;
        }
        recordPageEvent(new LoginAction());
        initView();
    }


    @Override
    public void onGetLoginHistory(final List<LoginInfoBean> loginInfoList) {
        if (loginInfoList != null && loginInfoList.size() > 0) {
            ivMore.setVisibility(View.VISIBLE);
            ivMore.setImageResource(R.mipmap.ic_arrow_bottom);
        } else {
            ivMore.setVisibility(View.GONE);
        }
        etLoginUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if (loginInfoList.size() < 1) {
                        ivUsernameDel.setVisibility(View.GONE);
                        ivMore.setVisibility(View.VISIBLE);
                        ivMore.setImageResource(R.mipmap.ic_cancel);
                    } else {
                        ivUsernameDel.setVisibility(View.VISIBLE);
                        ivUsernameDel.setImageResource(R.mipmap.ic_cancel);
                        ivMore.setVisibility(View.VISIBLE);
                        ivMore.setImageResource(R.mipmap.ic_arrow_bottom);
                    }
                } else {
                    ivUsernameDel.setVisibility(View.GONE);
                    if (loginInfoList.size() < 1) {
                        ivMore.setVisibility(View.GONE);
                    } else {
                        ivMore.setVisibility(View.VISIBLE);
                        ivMore.setImageResource(R.mipmap.ic_arrow_bottom);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etLoginUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (loginInfoList.size() < 1) {
                        if (etLoginUsername.getText().toString().trim().length() > 0) {
                            ivUsernameDel.setVisibility(View.GONE);
                            ivMore.setVisibility(View.VISIBLE);
                            ivMore.setImageResource(R.mipmap.ic_cancel);
                        } else {
                            ivUsernameDel.setVisibility(View.GONE);
                            ivMore.setVisibility(View.GONE);
                        }
                    } else {
                        if (etLoginUsername.getText().toString().trim().length() > 0) {
                            ivUsernameDel.setVisibility(View.VISIBLE);
                            ivUsernameDel.setImageResource(R.mipmap.ic_cancel);
                            ivMore.setVisibility(View.VISIBLE);
                            ivMore.setImageResource(R.mipmap.ic_arrow_bottom);
                        } else {
                            ivUsernameDel.setVisibility(View.GONE);
                            ivMore.setVisibility(View.VISIBLE);
                            ivMore.setImageResource(R.mipmap.ic_arrow_bottom);
                        }
                    }
                } else {
                    ivUsernameDel.setVisibility(View.GONE);
                    if (loginInfoList.size() < 1) {
                        ivMore.setVisibility(View.GONE);
                    } else {
                        ivMore.setVisibility(View.VISIBLE);
                        ivMore.setImageResource(R.mipmap.ic_arrow_bottom);
                    }
                }
            }
        });

        etLoginPassword.addTextChangedListener(new TextWatcher() {
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
        etLoginPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (etLoginPassword.getText().toString().trim().length() > 0) {
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

    private void initView() {
        ActionBarUtil.inject(this).title(R.string.a_0032).hideShare().hideCollect();

        mPresent.loadLoginHistory();

        //custom control config from gradle
        if ("0".equals(BuildConfig.USE_MESSGAE)) {
            etLoginUsername.setHint(R.string.a_0283);
        }
    }

    @OnClick({R.id.btn_login, R.id.tv_forgot_password, R.id.tv_user_register, R.id.iv_more, R.id.iv_password_del, R.id.iv_username_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_password_del:
                etLoginPassword.setText("");
                break;
            case R.id.iv_username_del:
                etLoginUsername.setText("");
                etLoginPassword.setText("");
                ivUsernameDel.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_forgot_password:
                //start forget password
                StringBuilder sb = new StringBuilder();
                sb.append(UrlConstant.BASE_H5_URL)
                        .append(UrlConstant.SHARE_URL_SUFFIX);
                sb.append("ForgotPasswordPage");
                WebViewActivity.start(this, getString(R.string.a_0284), sb.toString());
                break;
            case R.id.tv_user_register:
                RegisterActivity.start(this);
                break;
            case R.id.btn_login:
                if (checkLoginParams()) {
                    mPresent.startLogin(getUserName(), getPassWord());
                }
                break;
            case R.id.iv_more:
                if (mPresent.needShowUserNamePop()) {
                    RecordUserPopUtil.showRecordUserListPop(this,
                            etLoginUsername,
                            etLoginPassword,
                            ivMore,
                            ivUsernameDel,
                            ivMore,
                            getUserName(),
                            mPresent.getLoginInfoList());
                } else {
                    etLoginUsername.setText("");
                    etLoginPassword.setText("");
                    ivMore.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RecordUserPopUtil.releasae();
    }

    /**
     * 检查登录并且
     *
     * @return
     */
    private boolean checkLoginParams() {
        if (TextUtils.isEmpty(getUserName())) {
            etLoginUsername.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, getTheme()));
            AnimationUtils.shakeAnimation(etLoginUsername).start();
            etLoginUsername.removeCallbacks(usnR);
            etLoginUsername.postDelayed(usnR, AppConstants.DEFAULT_ANIMATION_TIME);
            return false;
        } else if (TextUtils.isEmpty(getPassWord())) {
            etLoginPassword.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.colorRed, getTheme()));
            AnimationUtils.shakeAnimation(etLoginPassword).start();
            etLoginPassword.removeCallbacks(pswR);
            etLoginPassword.postDelayed(pswR, AppConstants.DEFAULT_ANIMATION_TIME);
            return false;
        }
        return true;
    }

    private Runnable usnR = new Runnable() {
        @Override
        public void run() {
            etLoginUsername.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.color_text_hint, getTheme()));
        }
    };

    private Runnable pswR = new Runnable() {
        @Override
        public void run() {
            etLoginPassword.setHintTextColor(ResourcesCompat.getColor(getResources(), R.color.color_text_hint, getTheme()));
        }
    };

    public String getUserName() {
        return etLoginUsername.getText().toString().trim();
    }

    public String getPassWord() {
        return etLoginPassword.getText().toString().trim();
    }

    @Subscribe
    public void onLogin(LoginEvent event) {
        if (event.loginEvent == LoginEvent.LOGIN) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showShort(R.string.a_0285);
                    //从其它页面跳转
                    switch (from) {
                        case BACK_BACK_LOGIN_BACK:
                            if (!AppJumpUtils.goToGuideForResult(LoginActivity.this, REQUEST_GUIDE_CODE)) {
                                setResult(RESULT_OK);
                                finish();
                            }
                            break;
                        default:
                            Utils.startActivity(LoginActivity.this, MainActivity.class);
                            finish();
                            break;
                    }
                }
            });

        }
    }

    public static final int REQUEST_GUIDE_CODE = 0x1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GUIDE_CODE && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }
}
