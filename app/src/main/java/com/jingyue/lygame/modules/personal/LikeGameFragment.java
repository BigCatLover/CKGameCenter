package com.jingyue.lygame.modules.personal;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingyue.lygame.BaseFragment;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.events.GameCollectEvent;
import com.jingyue.lygame.events.GameSubEvent;
import com.jingyue.lygame.model.LikeGameListRp;
import com.jingyue.lygame.modules.personal.present.UserGameListPresent;
import com.jingyue.lygame.modules.rcmd.adapter.TagGameListAdapter;
import com.jingyue.lygame.modules.search.view.SearchView;
import com.jingyue.lygame.utils.recycleview.ItemDividerDecoration;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeGameFragment extends BaseFragment implements SearchView {
    @BindView(R.id.rcy_likegame)
    RecyclerView rcyLikegame;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;

    public final static String TYPE = "type";
    public final static int TYPE_COLLECT = 0;
    public final static int TYPE_SUB = 1;
    private int type;
    private UserGameListPresent gameListPresent;
    private int currentPage = 1;
    private final static int DEFAULT_LIMIT_NUM = 7;
    private TagGameListAdapter mListAdapter;

    public static BaseFragment getInstance(int type) {
        LikeGameFragment fragment = new LikeGameFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_like_game;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt(TYPE);
        }
        setEnableEventBus(true);
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    protected void findView(View view) {
        add(gameListPresent = new UserGameListPresent(this, this, new LikeGameListRp()));
        loadData(true);
    }

    private boolean isPull = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentPage = 1;

        ptrRefresh.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                isPull = true;
                currentPage++;
                loadData(false);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                isPull = false;
                currentPage = 1;
                loadData(true);
            }
        });
        rcyLikegame.setLayoutManager(new BaseLinearLayoutManager(getContext()));
        rcyLikegame.addItemDecoration(new ItemDividerDecoration(getContext(), ItemDividerDecoration.VERTICAL_LIST));
        rcyLikegame.setAdapter(mListAdapter = new TagGameListAdapter(getContext()));
    }

    private void loadData(boolean isRefresh) {
        gameListPresent.loadData(isRefresh, type, currentPage, DEFAULT_LIMIT_NUM);
    }

    @Override
    public void setDataOnMain(List<GameBean> beans, boolean isRefresh) {
        ptrRefresh.refreshComplete();
        if (mListAdapter != null) {
            mListAdapter.addAll(beans, isRefresh);
        }
    }

    @Override
    public void noMore() {
        if (ptrRefresh != null) {
            ptrRefresh.refreshComplete();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangeLikeGameListEvent(GameCollectEvent event) {
        if (event.currentStatus == GameCollectEvent.TYPE_UNLIKE && type == TYPE_COLLECT) {
            mListAdapter.deleteItem(event.appid);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangeSubGameListEvent(GameSubEvent event) {
        if (event.currentStatus == GameSubEvent.TYPE_UNSUB && type == TYPE_SUB) {
            mListAdapter.deleteItem(event.appid);
        }
    }
}
