package com.jingyue.lygame.modules.comment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.events.DownloadEvent;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.widget.DownloadProgessView;
import com.laoyuegou.android.lib.utils.EBus;
import com.lygame.libadapter.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/10/31.
 */
public class RcyCommentDetailHeadView extends LinearLayout {

    @BindView(R.id.game_icon)
    ImageView gameIcon;
    @BindView(R.id.download)
    DownloadProgessView download;
    @BindView(R.id.game_name)
    TextView gameName;
    @BindView(R.id.game_label)
    TextView gameLabel;
    @BindView(R.id.game_top)
    RelativeLayout gameTop;
    @BindView(R.id.box)
    ImageView box;
    @BindView(R.id.nocomment_ll)
    RelativeLayout nocommentLl;
    private Context context;
    private GameBean gameBean;

    public RcyCommentDetailHeadView(Context context) {
        super(context);
        this.context = context;
        initUI();
    }

    public RcyCommentDetailHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initUI();
    }

    public RcyCommentDetailHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initUI();
    }

    private void initUI() {
        LayoutInflater.from(getContext()).inflate(R.layout.commentlist_head_layout, this, true);
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
        if (event.downcnt - 1 >= 10000) {
            return;
        }
        if (event.downcnt >= 10000) {
            gameLabel.setText(context.getString(R.string.a_0301, StringUtils.formatNumebr(String.valueOf(event.downcnt))));
        } else {
            gameLabel.setText(context.getString(R.string.a_0302, event.downcnt));
        }
    }

    public void setDatas(final GameBean gameBean, boolean isReplyEmpty) {
        this.gameBean = gameBean;
        if (isReplyEmpty) {
            nocommentLl.setVisibility(View.VISIBLE);
        } else {
            nocommentLl.setVisibility(View.GONE);
        }
        if (gameBean == null) {
            gameTop.setVisibility(View.GONE);
        } else {
            download.setGameBeans(gameBean, false);
            gameName.setText(gameBean.name);
            String cnt = gameBean.downCnt;
            if (!StringUtils.isEmptyOrNull(gameBean.downCnt)) {
                if (Integer.valueOf(cnt) >= 10000) {
                    gameLabel.setText(context.getString(R.string.a_0301, StringUtils.formatNumebr(String.valueOf(cnt))));
                } else {
                    gameLabel.setText(context.getString(R.string.a_0301, cnt));
                }
            }

            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(gameIcon,
                    gameBean.icon));
        }
    }
}
