package com.jingyue.lygame.modules.guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.utils.WebpUtils;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-18 10:22
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * <p>
 * 引导页
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener,
        View.OnClickListener,
        UserPreferView {

    public static final int ACTION_BACK = 0x1;
    public static final int ACTION_DEFAULT = 0x2;

    public static final void start(Context context, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(KeyConstant.FROM, ACTION_BACK);
        Utils.startActivityForResult(context, GuideActivity.class, requestCode);
    }

    @BindView(R.id.myguide)
    ViewPager mViewPager;
    @BindView(R.id.endtext)
    LinearLayout mEndtext;
    @BindView(R.id.next)
    TextView mNextBtn;
    @BindView(R.id.end)
    TextView mEndBtn;
    @BindView(R.id.ignore)
    TextView mIgnoreBtn;
    @BindView(R.id.last)
    TextView mLastBtn;

    private int mPageLength = 5;
    private ArrayList<View> mPageViews = new ArrayList<View>();
    private int currentPage;

    private UserPreferPresent mGuidePresent;
    private int action = ACTION_DEFAULT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        action = getIntent().getIntExtra(KeyConstant.FROM,ACTION_DEFAULT);

        add(mGuidePresent = new UserPreferPresent(this, this));
        mViewPager = (ViewPager) findViewById(R.id.myguide);
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        ImageView start = new ImageView(this);
        start.setLayoutParams(mParams);
        start.setScaleType(ImageView.ScaleType.FIT_XY);
        start.setImageDrawable(WebpUtils.webpToDrawable(R.drawable.guide_start));

        ImageView end = new ImageView(this);
        end.setLayoutParams(mParams);
        end.setScaleType(ImageView.ScaleType.FIT_XY);
        end.setImageDrawable(WebpUtils.webpToDrawable(R.drawable.guide_end));

        LayoutInflater inflater = LayoutInflater.from(this);

        View pageOne = inflater.inflate(R.layout.guide_page_1, null, false);
        View pageTwo = inflater.inflate(R.layout.guide_page_2, null, false);
        View pageThree = inflater.inflate(R.layout.guide_page_3, null, false);

        mPageViews.add(start);
        mPageViews.add(pageOne);
        mPageViews.add(pageTwo);
        mPageViews.add(pageThree);
        mPageViews.add(end);

        mViewPager.setAdapter(new GuidePageAdapter(mPageViews));
        mViewPager.addOnPageChangeListener(this);
        mNextBtn.setText(R.string.a_0014);

        mNextBtn.setOnClickListener(this);
        mEndBtn.setOnClickListener(this);
        mIgnoreBtn.setOnClickListener(this);
        mLastBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPageViews) {
            int size = mPageViews.size();
            for (int i = 0; i < size; i++) {
                mPageViews.get(i).setBackgroundResource(0);
            }
            mPageViews.clear();
        }
        mPageViews = null;
        System.gc();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guide_main;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (null == mEndBtn) {
            return;
        }
        currentPage = position;
        if (position == mPageLength - 1) {
            mEndtext.setVisibility(View.VISIBLE);
            mNextBtn.setVisibility(View.INVISIBLE);
            mEndBtn.setVisibility(View.VISIBLE);
            mLastBtn.setVisibility(View.INVISIBLE);
            mEndBtn.setText(getString(R.string.a_0009));
            mIgnoreBtn.setVisibility(View.INVISIBLE);
            AlphaAnimation ani = new AlphaAnimation(0.0f, 1.0f);
            ani.setDuration(300);
            mEndBtn.setAnimation(ani);
            ani.startNow();
        } else if (position == 0) {
            mEndtext.setVisibility(View.INVISIBLE);
            mLastBtn.setVisibility(View.INVISIBLE);
            mNextBtn.setText(getString(R.string.a_0014));
            mIgnoreBtn.setVisibility(View.VISIBLE);
        } else {
            mEndtext.setVisibility(View.INVISIBLE);
            mIgnoreBtn.setVisibility(View.INVISIBLE);
            mEndBtn.setVisibility(View.GONE);
            mNextBtn.setVisibility(View.VISIBLE);
            updateNextBtnText(position);
        }
    }

    private void updateNextBtnText(int position) {
        if (position == 1 || position == 2) {
            mNextBtn.setText(getString(R.string.a_0007));
        } else if (position == 3) {
            mNextBtn.setText(getString(R.string.a_0008));
        }
        if (position == 1) {
            mLastBtn.setVisibility(View.INVISIBLE);
        }
        if (position == 2 || position == 3) {
            mLastBtn.setVisibility(View.VISIBLE);
            mLastBtn.setText(getString(R.string.a_0006));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @OnClick({R.id.next, R.id.end, R.id.ignore, R.id.last})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                mViewPager.setCurrentItem(currentPage + 1, true);
                break;
            case R.id.last:
                mViewPager.setCurrentItem(currentPage - 1, true);
                break;
            //要求跳过下次继续登录继续进入引导页
            case R.id.end:
                mGuidePresent.uploadGuide();
                break;
            case R.id.ignore:
                end();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        onViewClicked(view);
    }

    @Override
    public void uploadGuideInfoSucess() {
        end();
    }

    private void end(){
        switch (action){
            case ACTION_BACK:
                setResult(RESULT_OK);
                finish();
                break;
            case ACTION_DEFAULT:
            default:
                Intent it = new Intent(GuideActivity.this, MainActivity.class);
                startActivity(it);
                break;
        }
    }
}
