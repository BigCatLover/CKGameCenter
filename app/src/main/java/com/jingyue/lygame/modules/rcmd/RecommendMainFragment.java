package com.jingyue.lygame.modules.rcmd;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.RcmdGameListBean;
import com.jingyue.lygame.bean.ShareEntityBean;
import com.jingyue.lygame.clickaction.ButtonAction;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.events.LoginStatusChangeEvent;
import com.jingyue.lygame.model.RcmdGameRp;
import com.jingyue.lygame.modules.common.MainActivity;
import com.jingyue.lygame.modules.rcmd.present.RcmdGamePresenter;
import com.jingyue.lygame.modules.rcmd.view.GameListView;
import com.jingyue.lygame.utils.NavigationBarhelper;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKListUtil;
import com.jingyue.lygame.widget.BaseScrollView;
import com.jingyue.lygame.widget.BaseViewPager;
import com.jingyue.lygame.widget.CircleImageView;
import com.jingyue.lygame.widget.DepthPageTransformer;
import com.jingyue.lygame.widget.RcmdMoveListener;
import com.laoyuegou.android.lib.utils.BarUtils;
import com.laoyuegou.android.lib.utils.DisplayUtils;
import com.lygame.libadapter.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-18 14:49
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class RecommendMainFragment extends BaseFragment implements GameListView {

    @BindView(R.id.vp_contains)
    BaseViewPager vpContains;
    @BindView(R.id.iv_gameicon_layout)
    LinearLayout ivGameiconLayout;
    @BindView(R.id.skip_tv)
    TextView skipTv;
    @BindView(R.id.visible_layout)
    FrameLayout see;
    @BindView(R.id.basescroll)
    BaseScrollView baseScroll;
    @BindView(R.id.retry_ll)
    RelativeLayout retryLl;
    @BindView(R.id.paddingview)
    View paddingview;

    private final List<RcmdGameListBean> rcmdGameListBeen = new ArrayList<>();

    private RcmdGamePresenter rcmdGamePresenter;
    private YKListUtil mYkListutil;
    private RcmdMoveListener skipListener;
    private int currentViewPage = 0;//viewpaged当前页
    private int lastViewPage = 0;//上一个viewpage
    private int currentPage = 1;//需要加载第几页的游戏数据
    private final static int DEFAULT_LIMIT_NUM = 10;//服务端最大支持每页10条数据
    private Handler handler;
    private boolean isCreated = false;
    private boolean loaded = false;
    private RcmdGameRp rcmdGameRp;
    /**
     * 游戏Icon集合
     */
    private final HashMap<Integer, CircleImageView> pointViews = new HashMap<>();
    private GameViewPageAdapter adapter;
    private boolean hasnavigationBar = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_rcmd_main;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needChangeData) {
            reset();
        }
    }

    //账号发生改变，刷新列表同时各个标志重置
    private void reset() {
        needChangeData = false;
        adapter = null;
        pointViews.clear();
        mFragmentList.clear();
        currentPage = 1;
        currentViewPage = 0;
        if (rcmdGameRp != null) {
            rcmdGameRp.clearCache();
        }
        loadData(true);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            if (currentViewPage >= rcmdGameListBeen.size()) {
                return;
            }
            RcmdDetailActivity.open(getActivity(), String.valueOf(rcmdGameListBeen.get(currentViewPage).id), rcmdGameListBeen.get(currentViewPage).name);
            getActivity().overridePendingTransition(R.anim.push_bottom_in, R.anim.push_top_out);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setEnableEventBus(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (NavigationBarhelper.checkDeviceHasNavigationBar(getContext())) {
            NavigationBarhelper.assistActivity(getActivity().findViewById(R.id.visible_layout), getActivity());
            hasnavigationBar = true;
        } else {
            hasnavigationBar = false;
        }
        if (!isCreated) {
            ShareEntityBean config = new ShareEntityBean();
            final StringBuilder s = new StringBuilder();
            s.append(UrlConstant.BASE_H5_URL).append(UrlConstant.SHARE_URL_SUFFIX).append(AppConstants.SHARE_FROM.RCMD);
            config.setUrl(s.toString());
            ActionBarUtil.inject(getActivity(), view).hideBack()
                    .showUsercenter()
                    .showSearch()
                    .hideCollect()
                    .transparent().setShare(config);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                ViewGroup.LayoutParams params1 = paddingview.getLayoutParams();
                params1.height = BarUtils.getStatusBarHeight(getContext());
                paddingview.setLayoutParams(params1);
            }
            if (!hasnavigationBar) {
                int actionBarHeight = BarUtils.getActionBarHeight(getActivity());
                int screenHeight = DisplayUtils.getScreenHeightInPx(getContext());
                ViewGroup.LayoutParams params = see.getLayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    params.height = screenHeight - actionBarHeight;
                } else {
                    params.height = screenHeight - actionBarHeight - BarUtils.getStatusBarHeight(getContext());
                }
                see.setLayoutParams(params);
            }
            vpContains.setScroll(false);
            if (skipListener == null) {
                skipListener = new RcmdMoveListener() {
                    @Override
                    public void onSkip() {
                        if (skipTv != null) {
                            skipTv.setText(R.string.a_0171);
                        }
                        if (rcmdGameListBeen == null || rcmdGameListBeen.isEmpty()) {
                            return;
                        }
                        if (handler == null) {
                            handler = new Handler();
                        }
                        handler.post(runnable);
                    }

                    @Override
                    public void Drag2Top() {
                        if (skipTv != null) {
                            skipTv.setText(R.string.a_0170);
                        }
                    }

                    @Override
                    public void Drag2Bottom() {
                        if (skipTv != null) {
                            skipTv.setText(R.string.a_0171);
                        }
                    }
                };
                baseScroll.setListener(skipListener);
            }
            isCreated = true;
            currentPage = 1;
        }

    }

    private boolean needChangeData = false;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginStatusCahnge(LoginStatusChangeEvent event) {
        if (event.type == LoginStatusChangeEvent.LOGIN_STATUS_CAHNGE) {
            needChangeData = true;
        }
    }

    @Override
    protected void findView(View view) {
        super.findView(view);
        add(rcmdGamePresenter = new RcmdGamePresenter(this, this, rcmdGameRp = new RcmdGameRp()));
        loadData(true);
    }


    private void loadData(boolean needProgress) {
        rcmdGamePresenter.loadData(currentPage, DEFAULT_LIMIT_NUM, needProgress);
    }

    private boolean hasCacheShow = false;

    private void setData() {
        if (rcmdGameListBeen == null || rcmdGameListBeen.isEmpty()) {
            return;
        }
        InitPoint();
        if (adapter == null) {
            adapter = new GameViewPageAdapter(getChildFragmentManager(), rcmdGameListBeen);
            vpContains.setAdapter(adapter);
            vpContains.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    doRandom(String.valueOf(rcmdGameListBeen.get(arg0).id));
                    currentViewPage = arg0;
                    // 描绘分页点
                    drawPoint(arg0);
                    if ((arg0 > rcmdGameListBeen.size() - DEFAULT_LIMIT_NUM / 2) && !loaded) {
                        currentPage++;
                        loadData(false);
                    }
                    lastViewPage = arg0;
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (positionOffset > 0 && !hasCacheShow) {
                        mYkListutil = ((MainActivity) getActivity()).getYKListUtil();
                        mYkListutil.getYKVideoPlayer().showCache();
                        hasCacheShow = true;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    hasCacheShow = false;
                }
            });
            vpContains.setPageTransformer(false, new DepthPageTransformer());
        } else {
            adapter.setDatas(rcmdGameListBeen);
        }

    }

    @Override
    public void onPause() {
        mYkListutil = ((MainActivity) getActivity()).getYKListUtil();
        if (mYkListutil != null) {
            mYkListutil.getYKVideoPlayer().showCache();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //埋点统计
    private void doRandom(String appid) {
        new ButtonAction(appid).a3().random().onRecord();
    }

    @Override
    public void setDataOnMain(final List<RcmdGameListBean> beans, boolean isRefresh) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (beans == null || beans.isEmpty()) {
                    return;
                }
                if (currentPage == 1) {
                    rcmdGameListBeen.clear();
                }
                if (beans != null && !beans.isEmpty()) {
                    rcmdGameListBeen.addAll(beans);
                    if (currentPage > 1) {
                        loaded = false;
                    }
                }
                if (retryLl != null && retryLl.getVisibility() == View.VISIBLE) {
                    retryLl.setVisibility(View.GONE);
                }
                if (baseScroll != null && baseScroll.getVisibility() == View.GONE) {
                    baseScroll.setVisibility(View.VISIBLE);
                }
                setData();
            }
        });
    }

    @Override
    public void noData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (rcmdGameListBeen == null || rcmdGameListBeen.isEmpty()) {
                    if (retryLl != null) {
                        retryLl.setVisibility(View.VISIBLE);
                    }
                    if (baseScroll != null) {
                        baseScroll.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void InitPoint() {
        if (!pointViews.isEmpty()) return;
        if (ivGameiconLayout == null) {
            return;
        }
        ivGameiconLayout.removeAllViews();
        int length = rcmdGameListBeen.size() < 5 ? rcmdGameListBeen.size() : 5;

        for (int i = 0; i < length; i++) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            CircleImageView contentView = newCircleImage(i);
            if (i == 0) {
                layoutParams.leftMargin = 15;
                layoutParams.rightMargin = 15;
                layoutParams.width = 60;
                layoutParams.height = 60;
                contentView.setAlpha(1f);
            } else {
                layoutParams.leftMargin = 21;
                layoutParams.rightMargin = 21;
                layoutParams.topMargin = 6;
                layoutParams.bottomMargin = 6;
                layoutParams.width = 48;
                layoutParams.height = 48;
                contentView.setAlpha(0.5f);
            }
            ivGameiconLayout.addView(contentView, layoutParams);
            pointViews.put(i, contentView);
        }
    }

    private int maxkey;

    /**
     * 绘制游戏icon
     */
    public void drawPoint(int index) {
        CircleImageView imageView;
        maxkey = getMaxKey();
        if (!isInclude(index)) {
            if (isRight(index)) {
                imageView = pointViews.get(maxkey - 4);
                pointViews.remove(maxkey - 4);
                pointViews.put(index + 1, changeClick(imageView, index + 1));
            } else {
                imageView = pointViews.get(maxkey);
                pointViews.remove(maxkey);
                pointViews.put(index - 1, changeClick(imageView, index - 1));
            }
        }
        maxkey = getMaxKey();
        ivGameiconLayout.removeAllViews();
        int length = pointViews.size();
        for (int i = maxkey + 1 - length; i < maxkey + 1; i++) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            if (i == index) {
                layoutParams.leftMargin = 15;
                layoutParams.rightMargin = 15;
                layoutParams.width = 60;
                layoutParams.height = 60;
                pointViews.get(i).setAlpha(1f);
            } else {
                layoutParams.leftMargin = 21;
                layoutParams.rightMargin = 21;
                layoutParams.topMargin = 6;
                layoutParams.bottomMargin = 6;
                layoutParams.width = 48;
                layoutParams.height = 48;
                pointViews.get(i).setAlpha(0.5f);
            }
            ivGameiconLayout.addView(pointViews.get(i), layoutParams);
        }
    }

    private CircleImageView changeClick(CircleImageView view, final int position) {
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(view,
                rcmdGameListBeen.get(position).icon, R.mipmap.lygame_launcher));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpContains.setCurrentItem(position);
            }
        });
        return view;
    }

    private CircleImageView newCircleImage(final int position) {
        CircleImageView view = (CircleImageView) LayoutInflater.from(getContext()).inflate(R.layout.rcmd_point_icon, null);
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(view,
                rcmdGameListBeen.get(position).icon, R.mipmap.lygame_launcher));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpContains.setCurrentItem(position);
            }
        });
        return view;
    }

    private boolean isInclude(int index) {
        if (index == 0 || index == rcmdGameListBeen.size() - 1) {
            return true;
        }
        return index >= maxkey - 3 && index <= maxkey - 1;
    }

    private int getMaxKey() {
        int max = 0;
        for (int key : pointViews.keySet()) {
            if (key > max) {
                max = key;
            }
        }
        return max;
    }

    private boolean isRight(int index) {
        return index > maxkey - 1;
    }

    //    private SparseArray<Fragment> mFragmentList = new SparseArray<>();
    private Map<Integer, SoftReference<Fragment>> mFragmentList = new HashMap<Integer, SoftReference<Fragment>>();

    private class GameViewPageAdapter extends FragmentStatePagerAdapter {
        private List<RcmdGameListBean> datas;

        public GameViewPageAdapter(FragmentManager fm, List<RcmdGameListBean> gamelist) {
            super(fm);
            datas = gamelist;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (mFragmentList.containsKey(position)) {
                SoftReference<Fragment> softReference = mFragmentList.get(position);
                if (null != softReference) {
                    fragment = softReference.get();
                }
            } else {
                fragment = GameItemFragment.newInstance(rcmdGameListBeen.get(position), position);
                mFragmentList.put(position, new SoftReference<Fragment>(fragment));
            }
            if (fragment == null) {
                fragment = GameItemFragment.newInstance(rcmdGameListBeen.get(position), position);
                mFragmentList.put(position, new SoftReference<Fragment>(fragment));
            }
            return fragment;
        }

        public void setDatas(List<RcmdGameListBean> gamelist) {
            datas = gamelist;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return rcmdGameListBeen.size();
        }

    }

    @OnClick(R.id.retry_ll)
    public void onViewClicked() {
        loadData(true);
        retryLl.setVisibility(View.GONE);
        baseScroll.setVisibility(View.VISIBLE);
    }

}
