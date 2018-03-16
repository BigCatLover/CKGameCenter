package com.jingyue.lygame.modules.comment.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.modules.comment.AddCommentAdapter;
import com.jingyue.lygame.modules.comment.CommentDetailActivity;
import com.jingyue.lygame.modules.comment.present.CommentActPresent;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.jingyue.lygame.widget.CircleImageView;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.lygame.libadapter.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglei on 2017/9/17.
 */
public class RcyCommentItemView extends LinearLayout implements UserActionView {

    @BindView(R.id.user_icon)
    CircleImageView userIcon;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_label)
    ImageView userLabel;
    @BindView(R.id.phone)
    ImageView phone;
    @BindView(R.id.phone_info)
    TextView phoneInfo;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.comment_title)
    RelativeLayout commentTitle;
    @BindView(R.id.comment_content)
    TextView commentContent;
    @BindView(R.id.show_all)
    TextView showAll;
    @BindView(R.id.gotodetail)
    LinearLayout gotodetail;
    @BindView(R.id.score)
    TextView score;
    @BindView(R.id.score_avg)
    TextView scoreAvg;
    @BindView(R.id.reply)
    ImageView reply;
    @BindView(R.id.reply_num)
    TextView replyNum;
    @BindView(R.id.reply_ll)
    RelativeLayout replyLl;
    @BindView(R.id.zan)
    ImageView zan;
    @BindView(R.id.like_num)
    TextView likeNum;
    @BindView(R.id.cool_click)
    RelativeLayout coolClick;
    @BindView(R.id.shit)
    ImageView shit;
    @BindView(R.id.dislike_num)
    TextView dislikeNum;
    @BindView(R.id.shit_click)
    RelativeLayout shitClick;
    @BindView(R.id.amazing)
    ImageView amazing;
    @BindView(R.id.viewall_iv)
    ImageView viewallIv;
    @BindView(R.id.title_addcomment)
    RelativeLayout titleAddcomment;
    @BindView(R.id.rcy_addcomment)
    RecyclerView rcyAddcomment;
    @BindView(R.id.green)
    ImageView green;
    @BindView(R.id.add_comment_ll)
    RelativeLayout addCommentLl;
    private Context context;
    private GameBean gameBean;
    private String appid;
    private CommentListBean listBean;
    private List<CommentListBean.Comment> temp = new ArrayList<>();
    private CommentActPresent actPresent;
    private AddCommentAdapter adapter;

    public RcyCommentItemView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public RcyCommentItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    public RcyCommentItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.comment_adapter_layout, this, true);
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

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setDatas(final GameBean gameBean, CommentListBean comment) {
        this.gameBean = gameBean;

        this.listBean = comment;
        if (listBean.comment.is_amazing) {
            amazing.setVisibility(View.VISIBLE);
        } else {
            amazing.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userLabel, listBean.comment.comment_honer.icon));
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userIcon, listBean.comment.avatar));
        userName.setText(listBean.comment.username);
        phoneInfo.setText(listBean.comment.phone_info);
        createTime.setText(StringUtils.generateSpaceTime(listBean.comment.create_time));

        commentContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //这个回调会调用多次，获取完行数记得注销监听
                commentContent.getViewTreeObserver().removeOnPreDrawListener(this);
                int lines = commentContent.getLineCount();
                if (lines < 5) {
                    showAll.setVisibility(View.GONE);
                } else {
                    commentContent.setMaxLines(4);
                    showAll.setVisibility(View.VISIBLE);
                    showAll.setText(R.string.a_0135);
                    showAll.setOnClickListener(new OnClickListener() {
                        boolean flag = true;

                        @Override
                        public void onClick(View v) {
                            if (flag) {
                                showAll.setText(R.string.a_0136);
                                commentContent.setMaxLines(100);
                                flag = false;
                            } else {
                                showAll.setText(R.string.a_0135);
                                commentContent.setMaxLines(4);
                                flag = true;
                            }
                        }
                    });
                }
                return false;
            }
        });

        commentContent.setText(StringUtils.formatString(listBean.comment.content));

        if (listBean.comment.is_like) {
            zan.setImageResource(R.mipmap.ic_cool_select);
        } else {
            zan.setImageResource(R.mipmap.ic_cool_unselect);
        }

        if (listBean.comment.is_dislike) {
            shit.setImageResource(R.mipmap.ic_shit_select);
        } else {
            shit.setImageResource(R.mipmap.ic_shit_unselect);
        }

        if (listBean.comment.is_reply) {
            //todo
        } else {
            reply.setImageResource(R.mipmap.ic_comment);
        }
        dislikeNum.setText(listBean.comment.dislike_num);
        likeNum.setText(listBean.comment.like_num);
        replyNum.setText(listBean.comment.reply_num);
        if (listBean.getAdd() != null && !listBean.getAdd().isEmpty()) {
            int index = listBean.getAdd().size() - 1;
            scoreAvg.setText(listBean.getAdd().get(index).avg_score);
        } else {
            scoreAvg.setText(listBean.comment.avg_score);
        }

        temp.clear();
        if (listBean.getAdd() != null && listBean.getAdd().size() > 0) {
            addCommentLl.setVisibility(VISIBLE);
            if (adapter == null) {
                adapter = new AddCommentAdapter();
            }
            rcyAddcomment.setAdapter(adapter);
            rcyAddcomment.setLayoutManager(new BaseLinearLayoutManager(context));
            temp.add(listBean.getAdd().get(0));
            adapter.setDatas(temp);
            if (listBean.getAdd().size() > 1) {
                titleAddcomment.setVisibility(VISIBLE);
                viewallIv.setOnClickListener(new OnClickListener() {
                    boolean flag = true;

                    @Override
                    public void onClick(View v) {
                        if (flag) {
                            adapter.setDatas(listBean.getAdd());
                            flag = false;
                            viewallIv.setImageResource(R.mipmap.arrow_top);
                        } else {
                            adapter.setDatas(temp);
                            flag = true;
                            viewallIv.setImageResource(R.mipmap.arrow_bottom);
                        }

                    }
                });
            } else {
                titleAddcomment.setVisibility(GONE);
                adapter.setDatas(listBean.getAdd());
            }

        } else {
            addCommentLl.setVisibility(GONE);
        }
    }

    @OnClick({R.id.gotodetail, R.id.cool_click, R.id.shit_click, R.id.reply_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reply_ll:
            case R.id.gotodetail:
                if (gameBean == null) {
                    CommentDetailActivity.start(context, appid, String.valueOf(listBean.comment.comment_id));
                } else {
                    CommentDetailActivity.start(context, gameBean, String.valueOf(listBean.comment.comment_id));
                }
                break;
            case R.id.cool_click:
                if (actPresent == null) {
                    if (context instanceof BaseImpl) {
                        actPresent = new CommentActPresent(this, (BaseImpl) context);
                    }
                }
                actPresent.doAction(String.valueOf(listBean.comment.comment_id), CommentActPresent.ACTION_LIKE);
                break;
            case R.id.shit_click:
                if (actPresent == null) {
                    if (context instanceof BaseImpl) {
                        actPresent = new CommentActPresent(this, (BaseImpl) context);
                    }
                }
                actPresent.doAction(String.valueOf(listBean.comment.comment_id), CommentActPresent.ACTION_DISLIKE);
                break;
        }
    }

    @Override
    public void success(int action, String id) {
        int change;
        if (action == UserActionView.ACTION_LIKE) {
            if (listBean.comment.is_like) {
                listBean.comment.is_like = false;
                zan.setImageResource(R.mipmap.ic_cool_unselect);
                change = Integer.valueOf(listBean.comment.like_num) - 1;
                listBean.comment.like_num = String.valueOf(change);
                likeNum.setText(listBean.comment.like_num);
            } else {
                listBean.comment.is_like = true;
                zan.setImageResource(R.mipmap.ic_cool_select);
                change = Integer.valueOf(listBean.comment.like_num) + 1;
                listBean.comment.like_num = String.valueOf(change);
                likeNum.setText(listBean.comment.like_num);
                if (listBean.comment.is_dislike) {
                    shit.setImageResource(R.mipmap.ic_shit_unselect);
                    listBean.comment.is_dislike = false;
                    change = Integer.valueOf(listBean.comment.dislike_num) - 1;
                    listBean.comment.dislike_num = String.valueOf(change);
                    dislikeNum.setText(listBean.comment.dislike_num);
                }
            }
        } else if (action == UserActionView.ACTION_UNLIKE) {
            if (listBean.comment.is_dislike) {
                listBean.comment.is_dislike = false;
                shit.setImageResource(R.mipmap.ic_shit_unselect);
                change = Integer.valueOf(listBean.comment.dislike_num) - 1;
                listBean.comment.dislike_num = String.valueOf(change);
                dislikeNum.setText(listBean.comment.dislike_num);
            } else {
                listBean.comment.is_dislike = true;
                shit.setImageResource(R.mipmap.ic_shit_select);
                change = Integer.valueOf(listBean.comment.dislike_num) + 1;
                listBean.comment.dislike_num = String.valueOf(change);
                dislikeNum.setText(listBean.comment.dislike_num);
                if (listBean.comment.is_like) {
                    zan.setImageResource(R.mipmap.ic_cool_unselect);
                    listBean.comment.is_like = false;
                    change = Integer.valueOf(listBean.comment.like_num) - 1;
                    listBean.comment.like_num = String.valueOf(change);
                    likeNum.setText(listBean.comment.like_num);
                }
            }
        }
        listBean.update();
    }

    @Override
    public void showProgress(String msg) {

    }

    @Override
    public void dismissProgress() {

    }
}
