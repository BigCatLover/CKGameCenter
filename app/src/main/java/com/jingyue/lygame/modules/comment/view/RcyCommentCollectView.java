package com.jingyue.lygame.modules.comment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentCollectBean;
import com.jingyue.lygame.modules.comment.present.CommentActPresent;
import com.jingyue.lygame.modules.rcmd.RcmdDetailActivity;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.lygame.libadapter.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglei on 2017/9/18.
 */
public class RcyCommentCollectView extends RelativeLayout implements UserActionView {

    @BindView(R.id.game_icon)
    ImageView gameIcon;
    @BindView(R.id.game_name)
    TextView gameName;
    @BindView(R.id.score_tv)
    TextView scoreTv;
    @BindView(R.id.score_avg)
    TextView scoreAvg;
    @BindView(R.id.comment_content)
    TextView commentContent;
    @BindView(R.id.like_iv)
    ImageView likeIv;
    @BindView(R.id.like_num)
    TextView likeNum;
    @BindView(R.id.makecool_click)
    RelativeLayout makecoolClick;
    @BindView(R.id.author_name)
    TextView authorName;
    @BindView(R.id.gotogamedetail)
    RelativeLayout gotogamedetail;

    private Context context;
    private CommentCollectBean cmtCollectBean;
    private CommentActPresent actPresent;

    public RcyCommentCollectView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public RcyCommentCollectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    public RcyCommentCollectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.comment_main_item_layout, this, true);
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setDatas(final CommentCollectBean commentCollectBean) {
        String gamename = commentCollectBean.getGame().getName().isEmpty() ? context.getString(R.string.a_0155) : commentCollectBean.getGame().getName();
        gameName.setText(gamename);
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(gameIcon,
                commentCollectBean.getGame().getIcon(), R.mipmap.lygame_launcher));
        cmtCollectBean = commentCollectBean;
        authorName.setText(commentCollectBean.getUsername());
        scoreAvg.setText(commentCollectBean.getAvg_score());
        commentContent.setText(commentCollectBean.getContent());
        likeNum.setText(commentCollectBean.getLike_num());
        if (commentCollectBean.is_like()) {
            likeIv.setImageResource(R.mipmap.ic_cool_select);
        } else {
            likeIv.setImageResource(R.mipmap.ic_cool_unselect);
        }
    }

    @Override
    public void success(int action, String id) {
        int change = 0;
        if (action == UserActionView.ACTION_LIKE) {
            if (cmtCollectBean.is_like()) {
                cmtCollectBean.set_like(false);
                likeIv.setImageResource(R.mipmap.ic_cool_unselect);
                change = Integer.valueOf(cmtCollectBean.getLike_num()) - 1;
                cmtCollectBean.setLike_num(String.valueOf(change));
                likeNum.setText(cmtCollectBean.getLike_num());

            } else {
                cmtCollectBean.set_like(true);
                likeIv.setImageResource(R.mipmap.ic_cool_select);
                change = Integer.valueOf(cmtCollectBean.getLike_num()) + 1;
                cmtCollectBean.setLike_num(String.valueOf(change));
                likeNum.setText(cmtCollectBean.getLike_num());
            }
            cmtCollectBean.update();
        }
    }

    @OnClick({R.id.makecool_click, R.id.gotogamedetail})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.makecool_click:
                if (actPresent == null) {
                    if (context instanceof BaseImpl) {
                        actPresent = new CommentActPresent(this, (BaseImpl) context);
                    }
                }
                actPresent.doAction(String.valueOf(cmtCollectBean.getComment_id()), CommentActPresent.ACTION_LIKE);
                break;
            case R.id.gotogamedetail:
                RcmdDetailActivity.open(context
                        , String.valueOf(cmtCollectBean.getGame().getId())
                        , cmtCollectBean.getGame().getName());
                break;
        }
    }

    @Override
    public void showProgress(String msg) {

    }

    @Override
    public void dismissProgress() {

    }
}
