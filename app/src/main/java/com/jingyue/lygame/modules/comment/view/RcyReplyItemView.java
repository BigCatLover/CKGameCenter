package com.jingyue.lygame.modules.comment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.modules.comment.present.CommentActPresent;
import com.jingyue.lygame.utils.StringUtils;
import com.laoyuegou.android.lib.mvp.BaseImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglei on 2017/9/17.
 */
public class RcyReplyItemView extends LinearLayout implements UserActionView {


    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.createtime)
    TextView createtime;
    @BindView(R.id.like_num)
    TextView likeNum;
    @BindView(R.id.make_cool_iv)
    ImageView makeCoolIv;
    @BindView(R.id.make_cool)
    RelativeLayout makeCool;
    @BindView(R.id.reply_title)
    RelativeLayout replyTitle;
    @BindView(R.id.reply_content)
    TextView replyContent;
    @BindView(R.id.viewall)
    TextView viewall;
    @BindView(R.id.reply_ll)
    RelativeLayout replyLl;
    private Context context;
    private CommentListBean.Comment comment;
    private CommentActPresent actPresent;

    public RcyReplyItemView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public RcyReplyItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    public RcyReplyItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.comment_reply_adapter_layout, this, true);
        ButterKnife.bind(this);
    }

    public void setDatas(CommentListBean.Comment reply) {
        comment = reply;
        createtime.setText(StringUtils.generateSpaceTime(reply.create_time));
        if (reply.is_like) {
            makeCoolIv.setImageResource(R.mipmap.ic_cool_select);
        } else {
            makeCoolIv.setImageResource(R.mipmap.ic_cool_unselect);
        }
        if (reply.username == null) {
            username.setText(R.string.a_0155);
        } else {
            username.setText(reply.username);
        }
        replyContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //这个回调会调用多次，获取完行数记得注销监听
                replyContent.getViewTreeObserver().removeOnPreDrawListener(this);
                int lines = replyContent.getLineCount();
                if (lines < 5) {
                    viewall.setVisibility(View.GONE);
                } else {
                    replyContent.setMaxLines(4);
                    viewall.setVisibility(View.VISIBLE);
                    viewall.setOnClickListener(new OnClickListener() {
                        boolean flag = true;

                        @Override
                        public void onClick(View v) {
                            if (flag) {
                                viewall.setText(R.string.a_0136);
                                replyContent.setMaxLines(100);
                                flag = false;
                            } else {
                                viewall.setText(R.string.a_0135);
                                replyContent.setMaxLines(4);
                                flag = true;
                            }
                        }
                    });
                }
                return false;
            }
        });
        replyContent.setText(StringUtils.formatString(reply.content));
        likeNum.setText(reply.like_num);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void success(int action, String id) {
        int change;
        if (action == UserActionView.ACTION_LIKE) {
            if (comment.is_like) {
                comment.is_like = false;
                makeCoolIv.setImageResource(R.mipmap.ic_cool_unselect);
                change = Integer.valueOf(comment.like_num) - 1;
                comment.like_num = String.valueOf(change);
                likeNum.setText(comment.like_num);
            } else {
                comment.is_like = true;
                makeCoolIv.setImageResource(R.mipmap.ic_cool_select);
                change = Integer.valueOf(comment.like_num) + 1;
                comment.like_num = String.valueOf(change);
                likeNum.setText(comment.like_num);
            }
            comment.update();
        }

    }

    @OnClick(R.id.make_cool)
    public void onViewClicked() {
        if (actPresent == null) {
            if (context instanceof BaseImpl) {
                actPresent = new CommentActPresent(this, (BaseImpl) context);
            }
        }
        actPresent.doAction(String.valueOf(comment.comment_id), CommentActPresent.ACTION_DISLIKE);
    }

    @Override
    public void showProgress(String msg) {

    }

    @Override
    public void dismissProgress() {

    }
}
