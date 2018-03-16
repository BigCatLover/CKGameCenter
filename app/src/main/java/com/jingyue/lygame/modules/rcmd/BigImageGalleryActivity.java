package com.jingyue.lygame.modules.rcmd;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.ShopImageBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.modules.rcmd.adapter.BigGalleryAdapter;
import com.jingyue.lygame.modules.rcmd.present.ShopImagePresenter;
import com.jingyue.lygame.modules.rcmd.view.ShopImageView;
import com.jingyue.lygame.utils.ImageUtils;
import com.jingyue.lygame.utils.StringUtils;
import com.laoyuegou.android.common.glide.ImageLoaderOptions;
import com.laoyuegou.android.lib.utils.BarUtils;
import com.laoyuegou.android.lib.utils.DisplayUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.ImageLoader;

import java.util.List;

import butterknife.BindView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-21 16:21
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class BigImageGalleryActivity extends BaseActivity implements ShopImageView, ViewPager.OnPageChangeListener{


    public static final void open(Context context, String targetUrl, String appId, View v) {
        open(context, targetUrl, appId, v, true);
    }


    /**
     * animation config
     */
    public static final class Config {
        int x;
        int y;
        int width;
        int height;

        public boolean isInvalid() {
            return width > 0 && height > 0;
        }
    }

    /**
     * @param context
     * @param targetUrl
     * @param appId     画廊游戏列表 用于获取图片列表
     * @param v
     * @param needSave  是否需要右上角保存按钮
     */
    public static final void open(Context context, String targetUrl, String appId, View v, boolean needSave) {
        if (context != null) {
            Bundle bundle = new Bundle();
            if (v != null) {
                final int[] location = new int[2];
                v.getLocationOnScreen(location);
                bundle.putInt(KeyConstant.KEY_X, location[0]);
                bundle.putInt(KeyConstant.KEY_Y, location[1]);
                bundle.putInt(KeyConstant.KEY_WIDTH, v.getMeasuredWidth());
                bundle.putInt(KeyConstant.KEY_HEIGHT, v.getMeasuredHeight());
            }
            bundle.putBoolean(KeyConstant.KEY_SAVE, needSave);
            bundle.putString(KeyConstant.KEY_APP_ID, appId);
            bundle.putString(KeyConstant.KEY_URL, targetUrl);
            Utils.startActivity(context, BigImageGalleryActivity.class, bundle);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            }
        }
    }

    private static final AccelerateDecelerateInterpolator DEFAULT_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    @BindView(R.id.pic_content)
    ViewPager picContent;
    @BindView(R.id.share_pic)
    ImageView sharePic;
    @BindView(R.id.container)
    ViewGroup mContainer;
    @BindView(R.id.bg)
    View mBg;
    @BindView(R.id.page_number)
    TextView mPageNumber;

    private String url;
    private String appId;
    private int mCurrentIndex;

    private ShopImagePresenter mShopImagePresenter;

    private BigGalleryAdapter mBigGalleryAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.rcmd_activity_big_image;
    }

    private ActionBarUtil mBarUtil;
    private Config mConfig;
    private boolean isViewAnimationed = false;
    private boolean isViewPagerSetted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.transparent);
        final boolean needSave = getIntent().getBooleanExtra(KeyConstant.KEY_SAVE, false);
        url = getIntent().getStringExtra(KeyConstant.KEY_URL);
        mBarUtil = ActionBarUtil.inject(this).transparent().hideCollect().hideShare();

        if(needSave){
            mBarUtil.showRightText(R.string.a_0173, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (StringUtils.isEmptyOrNull(appId)) {
                        ImageUtils.saveImage(url);
                    } else {
                        ImageUtils.saveImage(mBigGalleryAdapter.getItem(mCurrentIndex));
                    }
                }
            });
        }

        appId = getIntent().getStringExtra(KeyConstant.KEY_APP_ID);

        mConfig = new Config();
        mConfig.x = getIntent().getIntExtra(KeyConstant.KEY_X, 0);
        mConfig.y = getIntent().getIntExtra(KeyConstant.KEY_Y, 0);
        mConfig.width = getIntent().getIntExtra(KeyConstant.KEY_WIDTH, 0);
        mConfig.height = getIntent().getIntExtra(KeyConstant.KEY_HEIGHT, 0);

        ImageLoader.getInstance()
                .showImage(ImageLoader.defaultOptions(sharePic, url, R.mipmap.ic_load_err_land)
                        .diskCacheStrategy(ImageLoaderOptions.DiskCacheStrategy.SOURCE).build());
        if (TextUtils.isEmpty(appId)) {
            //如果没有传入appId则不去缓存中读取画廊
            //直接展示原图
            return;
        } else {
            initAnima();
        }

        picContent.setAdapter(mBigGalleryAdapter = new BigGalleryAdapter());
        picContent.addOnPageChangeListener(this);

        add(mShopImagePresenter = new ShopImagePresenter(this, this));

        mShopImagePresenter.loadData(appId);

    }

    private void initAnima() {
        if (mConfig.isInvalid()) {
            // 初始化布局初始状态
            // 设置 ImageView 的位置，使其和上一个界面中图片的位置重合
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mConfig.width, mConfig.height);
            params.setMargins(mConfig.x, mConfig.y, 0, 0);
            sharePic.setLayoutParams(params);

            sharePic.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    final float screenX = DisplayUtils.getScreenWidthInPx(getContext());
                    final float screenY = DisplayUtils.getScreenHeightInPx(getContext())
                            + BarUtils.getStatusBarHeight(getContext());
                    final float scale = screenX / mConfig.width;
                    final float deltX = screenX / 2 - (mConfig.x + mConfig.width / 2);
                    final float deltY = screenY / 2 - (mConfig.y + mConfig.height / 2);
                    sharePic.animate()
                            .setInterpolator(DEFAULT_INTERPOLATOR)
                            .scaleX(scale)
                            .scaleY(scale)
                            .translationX(deltX)
                            .translationY(deltY)
                            .setDuration(AppConstants.DEFAULT_ANIMATION_TIME)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isViewAnimationed = true;
                                    showContentView();

                                }
                            }).start();
                    sharePic.getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });

        }
    }

    @Override
    public void setDataOnMain(List<ShopImageBean> bean, boolean append) {
        if (bean != null && !bean.isEmpty()) {
            isViewPagerSetted = true;
            mBigGalleryAdapter.resetData(bean);
            final int index = mBigGalleryAdapter.getFirstIndex(url);
            mBigGalleryAdapter.notifyDataSetChanged();
            picContent.setCurrentItem(index);
            showContentView();
            mPageTotal = mBigGalleryAdapter.getCount();
            setPage(index);
        }
    }

    private void setPage(int currentIndex){
        if(mPageTotal == 0){
            mPageNumber.setVisibility(View.GONE);
        }else{
            mPageNumber.setVisibility(View.VISIBLE);
        }
        if(mCurrentPage == null){
            mCurrentPage = new StringBuilder(5);
        }
        mCurrentPage.delete(0,mCurrentPage.length());
        mCurrentPage.append(currentIndex + 1).append("/").append(mPageTotal);
        if(mPageNumber != null){
            mPageNumber.setText(mCurrentPage);
        }
    }

    private int mPageTotal;
    private StringBuilder mCurrentPage;

    private void showContentView() {
        if (isViewAnimationed && isViewPagerSetted) {
            sharePic.setVisibility(View.GONE);
            picContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentIndex = position;
        setPage(mCurrentIndex);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


}
