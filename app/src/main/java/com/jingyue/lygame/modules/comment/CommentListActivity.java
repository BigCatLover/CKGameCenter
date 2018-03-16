package com.jingyue.lygame.modules.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.GameCollectEvent;
import com.jingyue.lygame.model.CommentListRp;
import com.jingyue.lygame.modules.comment.present.CommentListPresent;
import com.jingyue.lygame.modules.comment.present.GameCollectPresent;
import com.jingyue.lygame.modules.comment.view.CommentListView;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.laoyuegou.android.lib.utils.AnimationUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * @author zhanglei
 *         评论列表
 */
public class CommentListActivity extends BaseActivity implements CommentListView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.make_comment)
    ImageView makeComment;


    private GameBean gameBean;
    private CommentAdapter adapter;
    private CommentListPresent commentListPresent;
    private GameCollectPresent collectPresent;
    private ActionBarUtil mBar;
    private int currentPage = 1;
    private final static int DEFAULT_LIMIT_NUM = 3;
    private boolean notHide = true;
    private boolean isPull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableEventBus();
        super.onCreate(savedInstanceState);
        gameBean = getIntent().getParcelableExtra(KeyConstant.KEY_GAMEBEAN);
        mBar = ActionBarUtil.inject(this).title(R.string.a_0077).leftGravity().hideCollect().hideShare();
        add(collectPresent = new GameCollectPresent(mBar, this));
        mBar.setGameCollectPresent(collectPresent);
        if (gameBean != null) {
            mBar.setIslike(gameBean.isLike);
        }
        recyclerView.setLayoutManager(new BaseLinearLayoutManager(this));

        if (gameBean != null) {
            add(commentListPresent = new CommentListPresent(this, this, new CommentListRp()));
        }
        currentPage = 1;
        if (gameBean == null) {
            return;
        }
        loadData(true, true);

        ptrRefresh.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return !isPull && super.checkCanDoLoadMore(frame, content, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                isPull = true;
                currentPage++;
                loadData(false, false);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                isPull = false;
                currentPage = 1;
                loadData(true, false);
            }
        });
    }

    private void loadData(boolean isRefresh, boolean needProgress) {
        commentListPresent.loadData(isRefresh, String.valueOf(gameBean.id), currentPage, DEFAULT_LIMIT_NUM, needProgress);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_comment_list;
    }

    @OnClick({R.id.make_comment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.make_comment:
                if (notHide) {
                    if (gameBean == null) {
                        return;
                    }
                    GameScoreActivity.start(this, String.valueOf(gameBean.id), gameBean.score);
                } else {
                    runToBeside(false);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (notHide) {
            delayHide();
        }
    }

    public static void start(Context context, GameBean bean) {
        Intent starter = new Intent(context, CommentListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KeyConstant.KEY_GAMEBEAN, bean);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }

    private void runToBeside(boolean beside) {
        if (beside) {
            notHide = false;
            AnimationUtils.runToBeside(makeComment, 3, "runToBeside").start();
        } else {
            notHide = true;
            AnimationUtils.runBackFromBeside(makeComment, 3, "runBackFromBeside").start();
            delayHide();
        }
    }

    /**
     * 隱藏按鈕，防止擋住內容
     */
    private void delayHide() {
        //当view展示的时候，在适当的时机隐藏评论按钮
        getWindow().getDecorView().postDelayed(mHideTask, 2000);
    }

    private Runnable mHideTask = new Runnable() {
        @Override
        public void run() {
            runToBeside(true);
        }
    };

    @Override
    public void setCommentsOnMain(List<CommentListBean> beans, boolean isRefresh) {
        if (adapter == null) {
            adapter = new CommentAdapter(this, gameBean);
            recyclerView.setAdapter(adapter);
        }
        ptrRefresh.refreshComplete();
        if (adapter != null) {
            adapter.notifyDataChanged(beans, isRefresh);
        }
    }

    @Override
    public void noMore() {
        if (ptrRefresh != null) {
            ptrRefresh.refreshComplete();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ChangeCollectStatusEvent(GameCollectEvent event) {
        if (event.currentStatus == GameCollectEvent.TYPE_UNLIKE && event.appid.equals(gameBean.id)) {
            gameBean.isLike = false;
        } else if (event.currentStatus == GameCollectEvent.TYPE_LIKE && event.appid.equals(gameBean.id)) {
            gameBean.isLike = true;
        }
    }
}
