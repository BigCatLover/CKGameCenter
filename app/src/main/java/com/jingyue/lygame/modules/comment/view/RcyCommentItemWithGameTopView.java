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
import com.jingyue.lygame.bean.CommentDetialBean;
import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.events.DownloadEvent;
import com.jingyue.lygame.modules.comment.AddCommentAdapter;
import com.jingyue.lygame.modules.comment.present.CommentActPresent;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.jingyue.lygame.widget.CircleImageView;
import com.jingyue.lygame.widget.DownloadProgessView;
import com.laoyuegou.android.lib.mvp.BaseImpl;
import com.laoyuegou.android.lib.utils.EBus;
import com.lygame.libadapter.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglei on 2017/9/17.
 */
public class RcyCommentItemWithGameTopView extends LinearLayout implements UserActionView {

    @BindView(R.id.game_icon)
    ImageView gameIcon;
    @BindView(R.id.game_name)
    TextView gameName;
    @BindView(R.id.game_label)
    TextView gameLabel;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.user_icon)
    CircleImageView userIcon;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_label)
    ImageView userLabel;
    @BindView(R.id.author_info)
    RelativeLayout authorInfo;
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
    @BindView(R.id.reply_titile)
    TextView replyTitile;
    @BindView(R.id.viewall_iv)
    ImageView viewallIv;
    @BindView(R.id.title_addcomment)
    RelativeLayout titleAddcomment;
    @BindView(R.id.rcy_addcomment)
    RecyclerView rcyAddcomment;
    @BindView(R.id.add_comment_ll)
    RelativeLayout addCommentLl;
    @BindView(R.id.download)
    DownloadProgessView download;
    @BindView(R.id.green)
    ImageView green;
    @BindView(R.id.no_reply)
    RelativeLayout noReply;
    @BindView(R.id.game_top)
    RelativeLayout gameTop;
    @BindView(R.id.gap_below_game)
    View gapBelowGame;

    private Context context;
    private CommentDetialBean commentDetailBeen;
    private List<CommentListBean.Comment> temp = new ArrayList<>();
    private CommentActPresent actPresent;

    public RcyCommentItemWithGameTopView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public RcyCommentItemWithGameTopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    public RcyCommentItemWithGameTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.commentdetail_head_layout, this, true);
        ButterKnife.bind(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!EBus.getDefault().isRegistered(this)) {
            EBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (EBus.getDefault().isRegistered(this)) {
            EBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownCntChange(DownloadEvent event) {
        StringBuilder stb = new StringBuilder();
        if (event.downcnt - 1 >= 10000) {
            return;
        }
        if (event.downcnt >= 10000) {
            gameLabel.setText(context.getString(R.string.a_0301, StringUtils.formatNumebr(String.valueOf(event.downcnt))));
        } else {
            gameLabel.setText(context.getString(R.string.a_0302, event.downcnt));
        }
    }

    public void setDatas(final GameBean gameBean, CommentDetialBean detialBean, boolean isEmpty) {
        this.commentDetailBeen = detialBean;
        if (gameBean != null) {
            gameTop.setVisibility(VISIBLE);
            gapBelowGame.setVisibility(VISIBLE);
            gameName.setText(gameBean.name);
            String cnt = gameBean.downCnt;
            if (Integer.valueOf(cnt) >= 10000) {
                gameLabel.setText(context.getString(R.string.a_0301, StringUtils.formatNumebr(String.valueOf(cnt))));
            } else {
                gameLabel.setText(context.getString(R.string.a_0301, cnt));
            }
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(gameIcon,
                    gameBean.icon));
            //设置下载
            download.setGameBeans(gameBean, false);
        } else {
            gameTop.setVisibility(GONE);
            gapBelowGame.setVisibility(GONE);
        }
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userLabel,
                detialBean.comment.comment_honer.icon));
        if (commentDetailBeen.comment.is_amazing) {
            amazing.setVisibility(View.VISIBLE);
        } else {
            amazing.setVisibility(View.GONE);
        }
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(userIcon,
                commentDetailBeen.comment.avatar));
        userName.setText(commentDetailBeen.comment.username);
        phoneInfo.setText(commentDetailBeen.comment.phone_info);
        createTime.setText(StringUtils.generateSpaceTime(commentDetailBeen.comment.create_time));
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
        commentContent.setText(StringUtils.formatString(commentDetailBeen.comment.content));
        if (commentDetailBeen.comment.is_like) {
            zan.setImageResource(R.mipmap.ic_cool_select);
        } else {
            zan.setImageResource(R.mipmap.ic_cool_unselect);
        }

        if (commentDetailBeen.comment.is_dislike) {
            shit.setImageResource(R.mipmap.ic_shit_select);
        } else {
            shit.setImageResource(R.mipmap.ic_shit_unselect);
        }

        if (commentDetailBeen.comment.is_reply) {
            //todo
        } else {
            reply.setImageResource(R.mipmap.ic_comment);
        }
        dislikeNum.setText(commentDetailBeen.comment.dislike_num);
        likeNum.setText(commentDetailBeen.comment.like_num);
        replyNum.setText(commentDetailBeen.comment.reply_num);
        scoreAvg.setText(commentDetailBeen.comment.avg_score);
        noReply.setVisibility(GONE);
        if (isEmpty) {
            noReply.setVisibility(VISIBLE);
        }
        temp.clear();
        if (commentDetailBeen.getAdd() != null && commentDetailBeen.getAdd().size() > 0) {
            addCommentLl.setVisibility(VISIBLE);

            final AddCommentAdapter adapter = new AddCommentAdapter();
            rcyAddcomment.setAdapter(adapter);
            rcyAddcomment.setLayoutManager(new BaseLinearLayoutManager(context));
            temp.add(commentDetailBeen.getAdd().get(0));
            adapter.setDatas(temp);
            if (commentDetailBeen.getAdd().size() > 1) {
                titleAddcomment.setVisibility(VISIBLE);
                viewallIv.setOnClickListener(new OnClickListener() {
                    boolean flag = true;

                    @Override
                    public void onClick(View v) {
                        if (flag) {
                            adapter.setDatas(commentDetailBeen.getAdd());
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
                adapter.setDatas(commentDetailBeen.getAdd());
            }

        } else {
            addCommentLl.setVisibility(GONE);
        }
    }

    @Override
    public void success(int action, String id) {
        int change;
        if (action == UserActionView.ACTION_LIKE) {
            if (commentDetailBeen.comment.is_like) {
                commentDetailBeen.comment.is_like = false;
                zan.setImageResource(R.mipmap.ic_cool_unselect);
                change = Integer.valueOf(commentDetailBeen.comment.like_num) - 1;
                commentDetailBeen.comment.like_num = String.valueOf(change);
                likeNum.setText(commentDetailBeen.comment.like_num);
            } else {
                commentDetailBeen.comment.is_like = true;
                zan.setImageResource(R.mipmap.ic_cool_select);
                change = Integer.valueOf(commentDetailBeen.comment.like_num) + 1;
                commentDetailBeen.comment.like_num = String.valueOf(change);
                likeNum.setText(commentDetailBeen.comment.like_num);
                if (commentDetailBeen.comment.is_dislike) {
                    shit.setImageResource(R.mipmap.ic_shit_unselect);
                    commentDetailBeen.comment.is_dislike = false;
                    change = Integer.valueOf(commentDetailBeen.comment.dislike_num) - 1;
                    commentDetailBeen.comment.dislike_num = String.valueOf(change);
                    dislikeNum.setText(commentDetailBeen.comment.dislike_num);
                }
            }
        } else if (action == UserActionView.ACTION_UNLIKE) {
            if (commentDetailBeen.comment.is_dislike) {
                commentDetailBeen.comment.is_dislike = false;
                shit.setImageResource(R.mipmap.ic_shit_unselect);
                change = Integer.valueOf(commentDetailBeen.comment.dislike_num) - 1;
                commentDetailBeen.comment.dislike_num = String.valueOf(change);
                dislikeNum.setText(commentDetailBeen.comment.dislike_num);
            } else {
                commentDetailBeen.comment.is_dislike = true;
                shit.setImageResource(R.mipmap.ic_shit_select);
                change = Integer.valueOf(commentDetailBeen.comment.dislike_num) + 1;
                commentDetailBeen.comment.dislike_num = String.valueOf(change);
                dislikeNum.setText(commentDetailBeen.comment.dislike_num);

                if (commentDetailBeen.comment.is_like) {
                    zan.setImageResource(R.mipmap.ic_cool_unselect);
                    commentDetailBeen.comment.is_like = false;
                    change = Integer.valueOf(commentDetailBeen.comment.like_num) - 1;
                    commentDetailBeen.comment.like_num = String.valueOf(change);
                    likeNum.setText(commentDetailBeen.comment.like_num);
                }
            }
        }
        commentDetailBeen.update();
    }

    @OnClick({R.id.cool_click, R.id.shit_click})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cool_click:
                if (actPresent == null) {
                    if (context instanceof BaseImpl) {
                        actPresent = new CommentActPresent(this, (BaseImpl) context);
                    }
                }
                actPresent.doAction(String.valueOf(commentDetailBeen.comment.comment_id), CommentActPresent.ACTION_LIKE);
                break;
            case R.id.shit_click:
                if (actPresent == null) {
                    if (context instanceof BaseImpl) {
                        actPresent = new CommentActPresent(this, (BaseImpl) context);
                    }
                }
                actPresent.doAction(String.valueOf(commentDetailBeen.comment.comment_id), CommentActPresent.ACTION_DISLIKE);
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
