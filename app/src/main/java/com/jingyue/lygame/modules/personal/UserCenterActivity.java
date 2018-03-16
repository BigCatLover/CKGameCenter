package com.jingyue.lygame.modules.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.events.LoginEvent;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.personal.present.UploadFilePresent;
import com.jingyue.lygame.modules.personal.view.UploadFileView;
import com.jingyue.lygame.modules.rcmd.BigImageGalleryActivity;
import com.jingyue.lygame.utils.DeviceUtil;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.WebpUtils;
import com.jingyue.lygame.widget.CircleImageView;
import com.jingyue.lygame.widget.RecycleViewDivider;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lygame.libadapter.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class UserCenterActivity extends BaseActivity implements UploadFileView {

    @BindView(R.id.user_label)
    RelativeLayout usetLabel;
    @BindView(R.id.user_headImg)
    CircleImageView userHeadImg;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.user_label_tv)
    TextView userLabelTv;
    @BindView(R.id.userlabel_iv)
    ImageView userLabelIv;
    @BindView(R.id.user_info)
    RelativeLayout userInfo;
    @BindView(R.id.user_ll)
    RelativeLayout userLl;
    @BindView(R.id.option_recyclerView)
    RecyclerView optionRecyclerView;
    @BindView(R.id.func)
    TextView func;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.setting_iv)
    ImageView settingIv;
    @BindView(R.id.enter_setting)
    RelativeLayout enterSetting;
    @BindView(R.id.sv_content)
    ScrollView svContent;

    private UploadFilePresent uploadAvatarPresent;
    private DefaultAvaterAdapter defaultAvaterAdapter;
    private PopupWindow window;
    private String avaterUrl;
    private final int REQUEST_SET_AVATAR = 0X01;//设置头像
    private String mAvatarPath;
    private boolean uploadAvater = false;
    private String lastAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableEventBus();
        super.onCreate(savedInstanceState);
        ActionBarUtil.inject(this).title(R.string.a_0111).hideShare().hideCollect();
        setupUI();
        add(uploadAvatarPresent = new UploadFilePresent(this, this));
        LoginManager.getInstance().updateUserInfo(this, false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserInfoUpdate(LoginEvent event) {
        if (event.loginEvent == LoginEvent.UPDATE_LOGIN_INFO) {
            avaterUrl = LoginManager.getInstance().getLoginInfo().avatar;
            if (uploadAvater) {
                uploadAvater = false;
                return;
            }
            usetLabel.setVisibility(View.VISIBLE);
            username.setText(LoginManager.getInstance().getLoginInfo().username);
            if (LoginManager.getInstance().getLoginInfo().commentHoner != null) {
                if (!StringUtils.isEmptyOrNull(LoginManager.getInstance().getLoginInfo().commentHoner.text)) {
                    userLabelTv.setText(LoginManager.getInstance().getLoginInfo().commentHoner.text);
                }
                if (!StringUtils.isEmptyOrNull(LoginManager.getInstance().getLoginInfo().commentHoner.iconX)) {
                    ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userLabelIv,
                            LoginManager.getInstance().getLoginInfo().commentHoner.iconX));
                }
                if(StringUtils.isEmptyOrNull(LoginManager.getInstance().getLoginInfo().commentHoner.text)&&
                        StringUtils.isEmptyOrNull(LoginManager.getInstance().getLoginInfo().commentHoner.iconX)){
                    usetLabel.setVisibility(View.GONE);
                }
            } else {
                usetLabel.setVisibility(View.GONE);
            }
            SettingUtil.write(this, LoginManager.getInstance().getLoginInfo().id, LoginManager.getInstance().getLoginInfo().avatar);
            if (!lastAvatar.equals(LoginManager.getInstance().getLoginInfo().avatar)) {
                ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userHeadImg,
                        LoginManager.getInstance().getLoginInfo().avatar, R.mipmap.userdefault));
            }
        }
    }

    private void setupUI() {
        optionRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        optionRecyclerView.setAdapter(new UserCenterOptionAdapter(this));
        optionRecyclerView.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.HORIZONTAL, 2,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), true));
        optionRecyclerView.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.VERTICAL, 2,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), false));
        if (LoginManager.getInstance().isLogin()) {
            lastAvatar = SettingUtil.readString(this, LoginManager.getInstance().getLoginInfo().id, "");
            if (!lastAvatar.isEmpty()) {
                ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userHeadImg,
                        LoginManager.getInstance().getLoginInfo().avatar, R.mipmap.userdefault));
            }
        }
    }

    //上传头像
    private void uploadAvater(File file) {
        uploadAvatarPresent.upload(file);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_center;
    }

    @OnClick({R.id.user_headImg, R.id.enter_setting, R.id.user_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.user_ll:
                showPopwindow();
                break;
            case R.id.user_headImg:
                BigImageGalleryActivity.open(this, avaterUrl, "", userHeadImg, true);
                break;
            case R.id.enter_setting:
                SettingActivity.start(this);
                break;
        }
    }

    @Override
    public void onLoginFailure() {
        finish();
    }

    private PopupWindow popup;

    private void showPopwindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View PopView = inflater.inflate(R.layout.pop_photo_select, null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        popup = new PopupWindow(PopView,
                WindowManager.LayoutParams.MATCH_PARENT,
                DeviceUtil.dip2px(UserCenterActivity.this, 160));
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popup.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        // 设置背景颜色
        ColorDrawable cd = new ColorDrawable(0x000000);
        popup.setBackgroundDrawable(cd);
        popup.setOutsideTouchable(true);
        popup.setAnimationStyle(R.style.dialog_bottom_up_style);
        popup.showAtLocation(svContent,
                Gravity.BOTTOM, 0, 0);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        PopView.findViewById(R.id.select_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(UserCenterActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)// 选择模式
                        .enableCrop(true)// 是否裁剪
                        .isGif(false)// 是否显示gif
                        .showCropGrid(false)// 是否显示裁剪网格
                        .showCropFrame(false)// 是否显示裁剪边框
                        .circleDimmedLayer(true)// 是否显示圆形裁剪
                        .withAspectRatio(1, 1)// 裁剪比例
                        .forResult(REQUEST_SET_AVATAR);
                popup.dismiss();
                popup = null;
            }
        });
        PopView.findViewById(R.id.select_default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                popup = null;
                ShowDefaultAvatarPopwindow();
            }
        });
    }

    private void ShowDefaultAvatarPopwindow() {
        if (defaultAvaterAdapter == null) {
            defaultAvaterAdapter = new DefaultAvaterAdapter(this, new DefaultAvaterAdapter.ClickListener() {
                @Override
                public void onItemClick(int resId) {
                    window.dismiss();
                    window = null;
                    saveBitmapFile(resId);
                }
            });
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View defaultAvatarView = inflater.inflate(R.layout.pop_defalutavater_select, null);
        window = new PopupWindow(defaultAvatarView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        window.setFocusable(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        ColorDrawable cd = new ColorDrawable(0x000000);
        window.setBackgroundDrawable(cd);
        window.setOutsideTouchable(true);
        getWindow().setAttributes(lp);
        window.setAnimationStyle(R.style.dialog_bottom_up_style);
        window.showAtLocation(svContent,
                Gravity.BOTTOM, 0, 0);
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        RecyclerView rcy = (RecyclerView) defaultAvatarView.findViewById(R.id.defalut_avater_rcy);
        rcy.setLayoutManager(new GridLayoutManager(this, 4));
        rcy.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.HORIZONTAL, 2,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), false));
        rcy.addItemDecoration(new RecycleViewDivider(this, GridLayoutManager.VERTICAL, 2,
                ResourcesCompat.getColor(getResources(), R.color.bg_common, getTheme()), false));
        rcy.setAdapter(defaultAvaterAdapter);
    }

    public void saveBitmapFile(int resId) {
        File dir = new File(AppConstants.AVATER_DEFAULT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Bitmap bitmap = WebpUtils.getBitmap(resId);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(AppConstants.AVATER_DEFAULT_PATH));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            mAvatarPath = AppConstants.AVATER_DEFAULT_PATH;
            uploadAvater(new File(mAvatarPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SET_AVATAR:
                if (resultCode == RESULT_OK) {
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);

                    if (null != selectList && selectList.size() > 0) {
                        LocalMedia media = selectList.get(0);
                        if (media.isCut()) {
                            mAvatarPath = media.getCutPath();
                        } else {
                            mAvatarPath = media.getPath();
                        }
                    }
                    File file = null;
                    if (!StringUtils.isEmptyOrNull(mAvatarPath)) {
                        file = new File(mAvatarPath);
                    }
                    if (null != file && file.exists()) {
                        uploadAvater(file);
                    } else {
                        ToastUtils.showShort("选择图片失败!");
                    }
                }
                break;
        }
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, UserCenterActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void uploadSuccess(String data) {
        if (data.equals("success")) {
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userHeadImg, mAvatarPath
                    , R.mipmap.userdefault));
            ToastUtils.showShort(getString(R.string.a_0146));
            uploadAvater = true;
            LoginManager.getInstance().updateUserInfo(this, false);
        }
    }

    @Override
    public void uploadProgress(int progress) {
    }

    @Override
    public void uploadFailure() {

    }
}
