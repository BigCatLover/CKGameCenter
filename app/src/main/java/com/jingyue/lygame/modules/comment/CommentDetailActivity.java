package com.jingyue.lygame.modules.comment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentDetialBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.clickaction.ButtonAction;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.model.CommentDetailRp;
import com.jingyue.lygame.modules.comment.present.CommentDetailPresent;
import com.jingyue.lygame.modules.comment.present.GameCollectPresent;
import com.jingyue.lygame.modules.comment.present.ReplyActPresent;
import com.jingyue.lygame.modules.comment.view.CommentDetailView;
import com.jingyue.lygame.modules.comment.view.UserActionView;
import com.jingyue.lygame.utils.DeviceUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.laoyuegou.android.lib.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * @author zhanglei
 *         评论详情页
 *         回复评论
 */
public class CommentDetailActivity extends BaseActivity implements CommentDetailView, UserActionView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ptrRefresh)
    PtrClassicFrameLayout ptrRefresh;
    @BindView(R.id.reply_et)
    EditText replyEt;
    @BindView(R.id.reply_btn)
    TextView replyBtn;
    @BindView(R.id.reply_ll)
    RelativeLayout replyLl;

    private GameBean gameBean;
    private String appid;
    private String commentid;
    private CommentDetialBean commentDetailBeen;
    private CommentDetailPresent commentDetailPresent;
    private ReplyAdapter adapter;
    private ReplyActPresent replyActPresent;
    private GameCollectPresent collectPresent;
    private ActionBarUtil mBar;
    private boolean isPull = false;

    private int currentPage = 1;
    private final static int DEFAULT_LIMIT_NUM = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameBean = getIntent().getParcelableExtra(KeyConstant.KEY_GAMEBEAN);
        if (gameBean != null) {
            appid = String.valueOf(gameBean.id);
        } else {
            appid = getIntent().getStringExtra(KeyConstant.KEY_APP_ID);
        }
        commentid = getIntent().getStringExtra(KeyConstant.KEY_COMMENT_ID);
        replyLl.setVisibility(View.GONE);
        replyEt.setCursorVisible(false);
        replyEt.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        replyEt.setSingleLine(false);
        replyEt.setHorizontallyScrolling(false);
        mBar = ActionBarUtil.inject(this).title(R.string.a_0078).leftGravity().hideShare().hideCollect();
        add(collectPresent = new GameCollectPresent(mBar, this));
        collectPresent.setAppId(appid);
        mBar.setGameCollectPresent(collectPresent);
        if (gameBean != null) {
            mBar.setIslike(gameBean.isLike);
        }
        add(commentDetailPresent = new CommentDetailPresent(this, this, new CommentDetailRp()));

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
                currentPage = 1;
                loadData(true, false);
            }
        });
        recyclerView.setLayoutManager(new BaseLinearLayoutManager(this));
        loadData(true, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_comment_detail;
    }

    public static void start(Context context, GameBean bean, String comment_id) {
        Intent starter = new Intent(context, CommentDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(KeyConstant.KEY_GAMEBEAN, bean);
        starter.putExtra(KeyConstant.KEY_COMMENT_ID, comment_id);
        starter.putExtras(bundle);
        context.startActivity(starter);
    }

    public static void start(Context context, String appid, String comment_id) {
        Intent starter = new Intent(context, CommentDetailActivity.class);
        starter.putExtra(KeyConstant.KEY_APP_ID, appid);
        starter.putExtra(KeyConstant.KEY_COMMENT_ID, comment_id);
        context.startActivity(starter);
    }

    private void loadData(boolean isRefresh, boolean needProgress) {
        commentDetailPresent.loadData(isRefresh, commentid, currentPage, DEFAULT_LIMIT_NUM, needProgress);
    }

    @OnClick({R.id.reply_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reply_btn:
                if (replyEt.getText().toString().trim().isEmpty()) {
                    ToastUtils.showShort(R.string.a_0156);
                    return;
                } else {
                    commitReply(replyEt.getText().toString().trim());
                }
                break;
        }
    }

    private void commitReply(String content) {
        if (StringUtils.isEmptyOrNull(appid)) {
            return;
        }
        recordPageEvent(new ButtonAction(appid).a3().reply());
        String phone_info = "";
        if (!StringUtils.isEmptyOrNull(DeviceUtil.getPhoneModel())) {
            phone_info = DeviceUtil.getPhoneModel();
        }
        add(replyActPresent = new ReplyActPresent(this, this));
        replyActPresent.doAction(appid, content, phone_info, commentid);

    }

    @Override
    public void setDataOnMain(CommentDetialBean bean, boolean isRefresh) {
        ptrRefresh.refreshComplete();
        commentDetailBeen = bean;
        if (commentDetailBeen.comment.content != null) {
            replyLl.setVisibility(View.VISIBLE);
        }
        if (adapter == null) {
            adapter = new ReplyAdapter(gameBean, bean, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataChanged(commentDetailBeen.getReply(), isRefresh);
        }
    }

    @Override
    public void noMore() {
        if (ptrRefresh != null) {
            ptrRefresh.refreshComplete();
        }
    }

    @Override
    public void success(int action, String id) {
        if (action == UserActionView.ACTION_REPLY) {
            ToastUtils.showShort(R.string.a_0227);
            replyEt.setText("");
//            loadData(true, true);//回复成功后刷新数据展示用户新添加的回复
        }
    }
}
