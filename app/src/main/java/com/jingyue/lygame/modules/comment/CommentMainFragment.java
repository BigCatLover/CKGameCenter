package com.jingyue.lygame.modules.comment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentCollectBean;
import com.jingyue.lygame.bean.ShareEntityBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.model.CommentCollectRp;
import com.jingyue.lygame.modules.comment.present.CommentCollectPresent;
import com.jingyue.lygame.modules.comment.view.CommentCollectView;
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
 */
public class CommentMainFragment extends BaseFragment implements CommentCollectView {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.paddingview)
    View paddingview;
    @BindView(R.id.retry_ll)
    RelativeLayout retryLl;

    private CommentCollectPresent commentCollectPresent;
    private CommentCollectAdapter adapter;
    private int currentPage = 1;
    private final static int DEFAULT_LIMIT_NUM = 8;
    private boolean isCreated = false;
    private List<CommentCollectBean> beans;
    private boolean isPull = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment_main;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ptrRefresh.refreshComplete();
        if (!isCreated) {
            ShareEntityBean config = new ShareEntityBean();
            final StringBuilder s = new StringBuilder();
            s.append(UrlConstant.BASE_H5_URL).append(UrlConstant.SHARE_URL_SUFFIX).append(AppConstants.SHARE_FROM.COMMENT);
            config.setUrl(s.toString());
            ActionBarUtil.inject(getActivity(), view).title(R.string.a_0079).hideBack().showUsercenter().showSearch().hideCollect().setShare(config);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ViewGroup.LayoutParams params1 = paddingview.getLayoutParams();
                params1.height = BarUtils.getStatusBarHeight(getContext());
                paddingview.setLayoutParams(params1);
            }
            currentPage = 1;
            ptrRefresh.setPtrHandler(new PtrDefaultHandler2() {
                @Override
                public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                    return !isPull && super.checkCanDoLoadMore(frame, content, footer);
                }

                @Override
                public void onLoadMoreBegin(PtrFrameLayout frame) {
                    currentPage++;
                    isPull = true;
                    loadData(false, false);
                }

                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    currentPage = 1;
                    isPull = false;
                    loadData(true, false);
                }
            });
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
            if (adapter == null) {
                adapter = new CommentCollectAdapter(getContext());
            }

            recyclerView.setAdapter(adapter);
            isCreated = true;
        }

    }

    public void gotoTop() {
        if (adapter != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void findView(View view) {
        add(commentCollectPresent = new CommentCollectPresent(this, this, new CommentCollectRp()));
        loadData(true, true);
    }

    private void loadData(boolean isRefresh, boolean needProgress) {
        commentCollectPresent.loadData(isRefresh, currentPage, DEFAULT_LIMIT_NUM, needProgress);
    }

    @Override
    public void setDataOnMain(final List<CommentCollectBean> beans, final boolean isRefresh) {
        this.beans = beans;
        if(beans.isEmpty()){
            isPull = true;
        }else {
            isPull = false;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (retryLl != null && retryLl.getVisibility() == View.VISIBLE) {
                    retryLl.setVisibility(View.GONE);
                }
                if (ptrRefresh != null) {
                    ptrRefresh.refreshComplete();
                }
                if (adapter != null) {
                    adapter.notifyDataChanged(beans, isRefresh);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void noMore() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (beans == null) {
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
        loadData(true, true);
        retryLl.setVisibility(View.GONE);
    }
}
