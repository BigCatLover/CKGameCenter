package com.jingyue.lygame.modules.find;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.ShareEntityBean;
import com.jingyue.lygame.bean.SubjectListBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.SubjectListRp;
import com.jingyue.lygame.modules.find.present.SubjectListPresent;
import com.jingyue.lygame.modules.find.view.SubjectListView;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.laoyuegou.android.lib.utils.BarUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-18 14:48
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * 专题不会很多，不需要分页
 */
public class FindMainFragment extends BaseFragment implements SubjectListView {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.paddingview)
    View paddingview;
    @BindView(R.id.retry_ll)
    RelativeLayout retryLl;

    private SubjectListPresent subjectListPresent;
    private SubjectListAdapter adapter;
    private boolean isCreated = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ptrRefresh.refreshComplete();
        if (!isCreated) {
            ShareEntityBean config = new ShareEntityBean();
            final StringBuilder s = new StringBuilder();
            s.append(UrlConstant.BASE_H5_URL).append(UrlConstant.SHARE_URL_SUFFIX).append(AppConstants.SHARE_FROM.FIND);
            config.setUrl(s.toString());
            ActionBarUtil.inject(getActivity(), view).title(R.string.a_0080)
                    .hideBack().showUsercenter().showSearch().hideCollect().setShare(config);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewGroup.LayoutParams params1 = paddingview.getLayoutParams();
                params1.height = BarUtils.getStatusBarHeight(getContext());
                paddingview.setLayoutParams(params1);
            }

            ptrRefresh.setPtrHandler(new PtrDefaultHandler2() {
                @Override
                public void onLoadMoreBegin(PtrFrameLayout frame) {
                    ptrRefresh.refreshComplete();
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    loadData(false);
                }
            });
            recyclerView.setLayoutManager(new BaseLinearLayoutManager(getContext()));
            if (adapter == null) {
                adapter = new SubjectListAdapter(getContext());
            }
            recyclerView.setAdapter(adapter);
            isCreated = true;
        }

    }

    @Override
    protected void findView(View view) {
        add(subjectListPresent = new SubjectListPresent(this, this, new SubjectListRp()));
        loadData(true);
    }

    private void loadData(boolean needProgress) {
        subjectListPresent.loadData(needProgress);
    }

    private List<SubjectListBean> beens;

    @Override
    public void setDataOnMain(final List<SubjectListBean> beans) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FindMainFragment.this.beens = beans;
                if (ptrRefresh != null) {
                    ptrRefresh.refreshComplete();
                }
                if (retryLl != null && retryLl.getVisibility() == View.VISIBLE) {
                    retryLl.setVisibility(View.GONE);
                }
                adapter.notifyDataChanged(beans, true);
            }
        });
    }

    @Override
    public void setNoMore() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (beens == null) {
                    if (retryLl != null) {
                        retryLl.setVisibility(View.VISIBLE);
                    }
                } else {
                    ToastUtils.showShort(R.string.a_0210);
                    if (ptrRefresh != null) {
                        ptrRefresh.refreshComplete();
                    }
                }
            }
        });
    }

    @OnClick(R.id.retry_ll)
    public void onViewClicked() {
        loadData(true);
        retryLl.setVisibility(View.GONE);
    }
}
