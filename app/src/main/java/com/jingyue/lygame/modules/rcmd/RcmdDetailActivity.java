package com.jingyue.lygame.modules.rcmd;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.ShareEntityBean;
import com.jingyue.lygame.bean.ShopImageBean;
import com.jingyue.lygame.clickaction.GameDetailAction;
import com.jingyue.lygame.clickaction.UploadPicAction;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.constant.UrlConstant;
import com.jingyue.lygame.events.DownloadEvent;
import com.jingyue.lygame.events.GameCollectEvent;
import com.jingyue.lygame.events.TagChangeEvent;
import com.jingyue.lygame.model.CommentListRp;
import com.jingyue.lygame.model.GameDetailRp;
import com.jingyue.lygame.modules.comment.CommentListActivity;
import com.jingyue.lygame.modules.comment.GameScoreActivity;
import com.jingyue.lygame.modules.comment.present.CommentListPresent;
import com.jingyue.lygame.modules.comment.present.GameCollectPresent;
import com.jingyue.lygame.modules.comment.view.CommentListView;
import com.jingyue.lygame.modules.comment.view.RcyCommentItemView;
import com.jingyue.lygame.modules.personal.present.UploadFilePresent;
import com.jingyue.lygame.modules.personal.view.UploadFileView;
import com.jingyue.lygame.modules.rcmd.adapter.RelateGameAdapter;
import com.jingyue.lygame.modules.rcmd.adapter.ShopImageAdatper;
import com.jingyue.lygame.modules.rcmd.adapter.TagFlowAdapter;
import com.jingyue.lygame.modules.rcmd.present.GameDetailPresent;
import com.jingyue.lygame.modules.rcmd.present.ShopImagePresenter;
import com.jingyue.lygame.modules.rcmd.view.GameView;
import com.jingyue.lygame.modules.rcmd.view.ShopImageView;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.recycleview.ScrollSpeedLinearLayoutManger;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.jingyue.lygame.widget.DownloadProgessView;
import com.jingyue.lygame.widget.DrawerNestScrollView;
import com.jingyue.lygame.widget.tagflow.TagFlowLayout;
import com.laoyuegou.android.lib.utils.AnimationUtils;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lygame.libadapter.ImageLoader;
import com.youku.cloud.player.YoukuPlayerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-28 09:50
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * <p>
 * 游戏详情页
 */
public class RcmdDetailActivity extends BaseActivity implements GameView,
        ShopImageView,
        CommentListView,
        View.OnClickListener,
        UploadFileView {

    public static final int DEFAULT_MAX_TAG_SIZE = 0x5;

    public static void open(Context c, String appId, String name) {

        if (TextUtils.isEmpty(appId)) {
            ToastUtils.showShort(R.string.a_0216);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstant.KEY_NAME, name);
        bundle.putString(KeyConstant.KEY_APP_ID, appId);
        Utils.startActivity(c, RcmdDetailActivity.class, bundle);
    }

    private String appId;
    private GameBean gameBean;

    @Override
    public int getLayoutId() {
        return R.layout.rcmd_detail_main;
    }

    @Override
    public void setDataOnMain(GameBean bean, boolean fromCache) {
        this.gameBean = bean;
        mBar.setIslike(bean.isLike);
        mPlay.setVisibility(TextUtils.isEmpty(bean.videoId) ? View.GONE : View.VISIBLE);

        //游戏自荐
        setGameRcmd(bean);
        //版本更新
        setUpdateInfo(bean);
        //游戏详情
        setDetailInfo(bean);
        //相关游戏
        setRelateGame(bean);
        //游戏评分
        setRankInfo(bean);
        //设置把手视图
        setHandleView(bean);
        //設置标签
        setTagView(bean);
        //播放器
        setVideoView(bean);
    }

    @Override
    public void setDataOnMain(List<ShopImageBean> bean, boolean append) {
        //cache data.
        //only load data when drawer is opened
        if (!drawer.isOpen()) {
            if (!append) {
                shopImageAdatper.clear();
            }
            shopImagePresenter.setValueSet(true);
            shopImageAdatper.addAll(bean);
            shopImageAdatper.notifyDataSetChanged();
        } else {
            if (bean.size() > 0 && shopImageAdatper != null) {
                shopImageAdatper.addAll(bean.subList(0, 1));
                shopImageAdatper.notifyDataSetChanged();
            }
        }
    }


    @BindView(R.id.comment_group_more)
    TextView commentGroupMore;
    @BindView(R.id.close_drawer_handler)
    View drawerCardView;

    /**
     * 评论数据
     *
     * @param beans
     */
    @Override
    public void setCommentsOnMain(List<CommentListBean> beans, boolean isRefresh) {
        if (beans == null || beans.isEmpty()) {
            commentGroupMore.setVisibility(View.GONE);
            commetContent.setVisibility(View.GONE);
            nocommentLl.setVisibility(View.VISIBLE);
        } else {
            commetContent.setVisibility(View.VISIBLE);
            nocommentLl.setVisibility(View.GONE);
            RcyCommentItemView rcyCommentItemView;
            for (CommentListBean bean : beans) {
                rcyCommentItemView = new RcyCommentItemView(this);
                if (gameBean == null) {
                    rcyCommentItemView.setAppid(appId);
                }
                rcyCommentItemView.setDatas(gameBean, bean);
                commetContent.addView(rcyCommentItemView);
            }
        }

    }

    @Override
    public void noMore() {

    }

    @BindView(R.id.comment_ctnt)
    LinearLayout commetContent;
    @BindView(R.id.comment_group_title)
    TextView commentGroupTitle;
    @BindView(R.id.summary_comment)
    RelativeLayout commentContainer;
    @BindView(R.id.nocomment_ll)
    RelativeLayout nocommentLl;

    @BindView(R.id.media_group)
    View mPlay;
    @BindView(R.id.pic_content)
    RecyclerView mPicContent;
    @BindView(R.id.up_level_container)
    DrawerNestScrollView drawer;
    @BindView(R.id.bottom_container)
    RelativeLayout bottomContainer;
    @BindView(R.id.write_comment)
    View writeComment;
    @BindView(R.id.white_mask)
    View whiteMask;


    @BindView(R.id.play)
    View play;
    @BindView(R.id.game_icon)
    ImageView gameIcon;
    @BindView(R.id.game_title)
    TextView gameTitle;
    @BindView(R.id.game_user_number)
    TextView gameUserNumber;
    @BindView(R.id.tag1)
    TextView tag1;
    @BindView(R.id.tag2)
    TextView tag2;
    @BindView(R.id.comment)
    TextView comment;
    @BindView(R.id.experience)
    DownloadProgessView experience;
    @BindView(R.id.slide_down)
    View indexView;

    //设置把手视图
    public void setHandleView(GameBean bean) {
        gameTitle.setText(StringUtils.optString(bean.name));
        gameUserNumber.setText(StringUtils.formatNumebr(bean.downCnt) + getString(R.string.a_0288));
        if (!TextUtils.isEmpty(bean.gameTag)) {
            tag1.setVisibility(View.VISIBLE);
            tag1.setText(StringUtils.optString(bean.gameTag));
        }
        comment.setText(StringUtils.optString(bean.publicity));
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(gameIcon, bean.icon));
        experience.setGameBeans(bean, false);
        indexAnimator = AnimationUtils.upAndDownAni(indexView, true, 1f);
        indexAnimator.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownCntChange(DownloadEvent event) {
        gameUserNumber.setText(StringUtils.formatNumebr(String.valueOf(event.downcnt)) + getString(R.string.a_0288));
        detailinfoGroupExprValue.setText(String.valueOf(event.downcnt));
        gameBean.downCnt = String.valueOf(event.downcnt);
        gameBean.save();
    }

    @BindView(R.id.player_stub)
    ViewStub playViewStub;

    private YoukuPlayerView playerView;
    private boolean isVideoValid = false;

    //视频播放
    private void setVideoView(GameBean bean) {
        mPlay.setVisibility(!isVideoValid ? View.INVISIBLE : View.VISIBLE);
    }

    //游戏自荐
    @BindView(R.id.summary_group_more)
    TextView selfIntroduceGroupMore;
    @BindView(R.id.summary_group_comment)
    TextView selfIntroduceGroupComment;

    private void setGameRcmd(GameBean bean) {
        final int commentNumber = Integer.valueOf(StringUtils.optInt(bean.commentNum));
        if (commentNumber > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getString(R.string.a_0026));
            stringBuilder.append("(");
            stringBuilder.append(String.valueOf(Math.min(9999, commentNumber)));
            stringBuilder.append(")");
            commentGroupMore.setText(stringBuilder.toString());
        } else {
            commentGroupMore.setText(getString(R.string.a_0026));
        }
        selfIntroduceGroupComment.setText(StringUtils.optString(bean.description, StringUtils.NONE));
        final int line = selfIntroduceGroupComment.getLineCount();
        selfIntroduceGroupMore.setVisibility(
                selfIntroduceGroupComment.getLayout().getEllipsisCount(line - 1) > 0 ?
                        View.VISIBLE : View.INVISIBLE);
    }

    //更新内容
    @BindView(R.id.updateinfo_group_more)
    TextView updateinfoGroupMore;
    @BindView(R.id.updateinfo_group_comment)
    TextView updateinfoGroupComment;

    private void setUpdateInfo(GameBean bean) {
        updateinfoGroupComment.setText(StringUtils.optString(bean.upinfo, StringUtils.NONE));
        final int line = updateinfoGroupComment.getLineCount();
        updateinfoGroupMore.setVisibility(
                updateinfoGroupComment.getLayout().getEllipsisCount(line - 1) > 0 ?
                        View.VISIBLE : View.INVISIBLE);
    }

    //详细信息
    @BindView(R.id.detailinfo_group_mau)
    TextView detailinfoGroupMau;
    @BindView(R.id.detailinfo_group_expr_value)
    TextView detailinfoGroupExprValue;
    @BindView(R.id.detailinfo_group_ver_value)
    TextView detailinfoGroupVerValue;
    @BindView(R.id.detailinfo_group_size_value)
    TextView detailinfoGroupSizeValue;
    @BindView(R.id.detailinfo_group_lowversion_value)
    TextView detailinfoGroupLowversionValue;
    @BindView(R.id.detailinfo_group_mau_value)
    TextView detailinfoGroupMauValue;
    @BindView(R.id.detailinfo_group_lang_value)
    TextView detailinfoGroupLang;

    private void setDetailInfo(GameBean bean) {
        detailinfoGroupExprValue.setText(StringUtils.optInt(bean.downCnt));
        detailinfoGroupVerValue.setText(StringUtils.optString(bean.version));
        detailinfoGroupSizeValue.setText(StringUtils.optString(bean.size));
        detailinfoGroupLang.setText(bean.lang);
        detailinfoGroupLowversionValue.setText(StringUtils.optString(bean.adxt));
        detailinfoGroupMauValue.setText(StringUtils.optString(bean.company));
    }

    //评分
    @BindView(R.id.avg_score)
    TextView avgScore;
    @BindView(R.id.chart)
    RadarChart chart;

    private void setRankInfo(GameBean bean) {
        avgScore.setText(String.valueOf(bean.avgScore));
        setChart(bean);
    }

    //相关游戏
    @BindView(R.id.content_game)
    RecyclerView gameContent;

    private RelateGameAdapter relateGameAdapter;

    private void setRelateGame(GameBean bean) {
        if (bean != null && bean.relatedGame != null) {
            relateGameAdapter.clear();
            relateGameAdapter.addAll(bean.relatedGame);
            relateGameAdapter.notifyDataSetChanged();
        }
    }

    //游戏标签
    @BindView(R.id.tag_group)
    TagFlowLayout tagview;
    @BindView(R.id.float_tag_group)
    TagFlowLayout floatTagGroup;

    private ShopImageAdatper shopImageAdatper;
    private TagFlowAdapter mTagFlowAdapter;
    private TagFlowAdapter mFloatTagAdapter;
    //相比內存上的优化  保证对象的唯一性防止数据错乱
    //更加重要  所有优先初始化加上final 修饰
    private final List<GameBean.Tag> tagList = new ArrayList<>();


    public void setTagView(GameBean bean) {
        tagList.clear();
        if (bean != null && bean.tags != null) {
            tagList.addAll(bean.tags);
        }
        if (tagList.size() > DEFAULT_MAX_TAG_SIZE) {
            for (int i = tagList.size() - 1; i > DEFAULT_MAX_TAG_SIZE - 1; i++) {
                tagList.remove(i);
            }
        }
        if (mTagFlowAdapter == null) {
            mTagFlowAdapter = new TagFlowAdapter(tagList, String.valueOf(bean.id));
            mFloatTagAdapter = new TagFlowAdapter(tagList, String.valueOf(bean.id));
            mTagFlowAdapter.setCanDoSelect(false);
            mFloatTagAdapter.setCanDoSelect(false);
            mTagFlowAdapter.setSelectedState(true);
            mFloatTagAdapter.setSelectedState(true);

            tagview.setSingleLine(true)
                    .setStayLastItem(true);
            floatTagGroup.setSingleLine(true)
                    .setStayLastItem(true);
            tagview.setAdapter(mTagFlowAdapter);
            floatTagGroup.setAdapter(mFloatTagAdapter);
        }
        tagList.add(TagFlowAdapter.generateAddTag(gameBean.name));
        mTagFlowAdapter.notifyDataChanged();
        mFloatTagAdapter.notifyDataChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //展开游戏自荐
            case R.id.comment_group_more:
                CommentListActivity.start(this, gameBean);
                break;
            case R.id.updateinfo_group_more:
                extendTextView(updateinfoGroupComment, updateinfoGroupMore);
                break;
            case R.id.summary_group_more:
                extendTextView(selfIntroduceGroupComment, selfIntroduceGroupMore);
                break;
            case R.id.media_group:
                if (playerView != null && !TextUtils.isEmpty(gameBean.videoId)) {
                    playerView.playYoukuVideo(gameBean.videoId);
                } else {
                    ToastUtils.showShort(R.string.a_0225);
                }
                break;
        }
    }

    /**
     * 展开或者关闭textview
     *
     * @param target
     */
    private void extendTextView(TextView target, TextView controller) {
        Object o = target.getTag();
        if (o == null) {
            target.setTag(true);
            target.setMaxLines(100);
            controller.setText(R.string.a_0136);
        } else {
            boolean extend = (boolean) o;
            extend = !extend;
            target.setTag(extend);
            controller.setText(extend ? R.string.a_0136 : R.string.a_0135);
            target.setMaxLines(extend ? 100 : 3);
        }
    }

    /**
     * for generate butterknife
     */
    private int[] layoutIds = new int[]{
            R.layout.rcmd_view_summary_group,
            R.layout.rcmd_view_detail_group,
            R.layout.rcmd_view_comment,
            R.layout.rcmd_view_updateinfo_group,
            R.layout.rcmd_detail_rank,
            R.layout.rcmd_detail_drawer_handler,
    };

    //游戏详情数据
    private GameDetailPresent gamePresent;
    //商店图
    private ShopImagePresenter shopImagePresenter;
    //评论
    private CommentListPresent commentPresenter;
    //上傳圖片
    private UploadFilePresent mUploadFilePresent;
    //收藏
    private GameCollectPresent mGameCollectPresent;

    private ActionBarUtil mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableEventBus();
        super.onCreate(savedInstanceState);
        //if(true) return;
        appId = getIntent().getStringExtra(KeyConstant.KEY_APP_ID);
        final String name = getIntent().getStringExtra(KeyConstant.KEY_NAME);

        recordPageEvent(new GameDetailAction(appId));

        mBar = ActionBarUtil.inject(this).transparent().title(name);
        if (!StringUtils.isEmptyOrNull(appId)) {
            ShareEntityBean config = new ShareEntityBean();
            final StringBuilder s = new StringBuilder();
            s.append(UrlConstant.BASE_H5_URL).append(UrlConstant.SHARE_URL_SUFFIX).append(AppConstants.SHARE_FROM.DETAIL).append("/").append(appId);
            config.setUrl(s.toString());
            mBar.setShare(config);
        }
        //商店图
        add(shopImagePresenter = new ShopImagePresenter(this, this));
        //游戏详情
        add(gamePresent = new GameDetailPresent(this, this, new GameDetailRp(Integer.valueOf(appId))));
        //评论
        add(commentPresenter = new CommentListPresent(this, this, new CommentListRp()));
        //上传文件
        add(mUploadFilePresent = new UploadFilePresent(this, this));
        //收藏
        add(mGameCollectPresent = new GameCollectPresent(mBar, this));

        mGameCollectPresent.setAppId(appId);
        mBar.setGameCollectPresent(mGameCollectPresent);

        initContentView();

        gamePresent.loadData(appId);
        shopImagePresenter.loadData(appId);
        commentPresenter.loadData(appId, 1);
    }

    private ValueAnimator indexAnimator;

    @Override
    protected void onDestroy() {
        if (shopImagePresenter != null) {
            shopImagePresenter.clear();
        }
        if (playerView != null) {
            playerView.release();
        }
        indexAnimator.cancel();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TagChangeEvent event) {
        if (event.isCurrentGameTagChaneg(gameBean.id)) {
            gamePresent.onTagChangeEvent(tagList, event);
            needNotifyTagDataChage = true;
        }
    }

    /**
     * 重新加载标签
     *
     * @param tags
     */
    @Override
    public void onTagReload(List<GameBean.Tag> tags) {
        if (tags != null) {
            tagList.clear();
            tagList.addAll(tags);
            Collections.sort(tagList);
            final int size = tagList.size();
            if (size > DEFAULT_MAX_TAG_SIZE + 1) {
                for (int i = size - 1; i >= DEFAULT_MAX_TAG_SIZE; i--) {
                    tagList.remove(i);
                }
            }
            tagList.add(TagFlowAdapter.generateAddTag(gameBean.name));
        }
        mFloatTagAdapter.notifyDataChanged();
        mTagFlowAdapter.notifyDataChanged();
    }

    private void initContentView() {
        initChart();
        //某些机型布局会出现层叠错乱 比如HUWEI PE-TL00M
        drawerCardView.bringToFront();
        //相关游戏
        relateGameAdapter = new RelateGameAdapter(this);
        gameContent.setLayoutManager(new BaseLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        gameContent.setAdapter(relateGameAdapter);

        //商店图
        shopImageAdatper = new ShopImageAdatper(this);
        mPicContent.setLayoutManager(new ScrollSpeedLinearLayoutManger(this));
        mPicContent.setAdapter(shopImageAdatper);

        drawer.addDrawerListener(new DrawerNestScrollView.DrawerAdapterListner() {

            @Override
            public void onstart(boolean open) {
                showEditComment(open);
                if (open) {
                    whiteMask.animate().cancel();
                    mPicContent.smoothScrollToPosition(0);
                    whiteMask.animate().alpha(1);
                    mBar.showShare();
                } else {
                    mBar.hideShare();
                    whiteMask.animate().alpha(0).start();
                }
            }

            @Override
            public void onClose() {
                loadShopImage();
                whiteMask.animate().alpha(0).start();
                mPlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onOpen() {
                if (isVideoValid) {
                    mPlay.setVisibility(View.VISIBLE);
                }
            }
        });

        mPlay.setOnClickListener(this);
        selfIntroduceGroupMore.setOnClickListener(this);
        updateinfoGroupMore.setOnClickListener(this);
        commentGroupMore.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getWindow().getDecorView().removeCallbacks(mHideTask);
        if (indexAnimator != null) {
            indexAnimator.cancel();
        }
    }

    /**
     * 显示商店图
     */
    private void loadShopImage() {
        shopImagePresenter.loadData(appId);
    }

    /**
     * 隐藏评论按钮
     */
    private void showEditComment(boolean show) {
        if (show) {
            writeComment.animate()
                    .translationY(0)
                    .alpha(1)
                    .start();
        } else {
            writeComment.animate()
                    .translationY(writeComment.getMeasuredHeight())
                    .alpha(0)
                    .start();
        }
    }

    /**
     * 是否默认状态，如果不是则设置为默认状态。
     *
     * @return
     */
    private boolean whetherDefaultAndResetDefault() {
        if (!drawer.isOpen()) {
            openDrawer();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!whetherDefaultAndResetDefault()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean notHide = true;

    @OnClick({R.id.play, R.id.add_pic, R.id.slide_up_container, R.id.close_drawer_handler, R.id.write_comment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_pic:
                if (gameBean == null) return;
                new UploadPicAction(String.valueOf(gameBean.id)).onRecord();
                PictureSelector.create(RcmdDetailActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .compress(true)// 是否压缩
                        .compressMaxKB(PictureFileUtils.COMPRESS_MAX_SIZE)
                        .isCamera(false)// 是否显示拍照按钮
                        .isGif(true)// 是否显示gif
                        .selectionMode(PictureConfig.SINGLE)// 图片选择模式
                        .forResult(PictureConfig.CHOOSE_REQUEST);
                break;
            case R.id.slide_up_container:
                openDrawer();
                break;
            case R.id.close_drawer_handler:
                closeDrawer();
                break;
            case R.id.write_comment:
                if (drawer.isOpen()) {
                    if (notHide) {
                        if (gameBean != null) {
                            GameScoreActivity.start(this, appId, gameBean.score);
                        }
                    } else {
                        runToBeside(false);
                    }
                }
                break;
        }
    }

    private void runToBeside(boolean beside) {
        notHide = !beside;
        if (beside) {
            AnimationUtils.runToBeside(writeComment, 3, "runToBeside").start();
        } else {
            AnimationUtils.runBackFromBeside(writeComment, 3, "runBackFromBeside").start();
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


    /**
     * 关闭底层详情页
     * 打开抽屉
     */
    private void openDrawer() {
        drawer.openDrawer();
    }

    /**
     * 打开下层游戏详情页
     * 关闭抽屉浮层
     */
    private void closeDrawer() {
        drawer.closeDrawer();
    }

    private String[] mParties;

    private void setChart(GameBean bean) {
        String[] scores = null;
        if (bean == null || TextUtils.isEmpty(bean.score)) {
            scores = new String[]{"0", "0", "0", "0", "0", "0"};
        } else {
            scores = bean.score.split(",");
        }
        int cnt = 6;
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        for (int i = 0; i < cnt; i++) {
            yVals1.add(new Entry(Float.valueOf(scores[i]), i));
        }
        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < cnt; i++)
            xVals.add(mParties[i % mParties.length]);

        RadarDataSet set1 = new RadarDataSet(yVals1, "set1");
        set1.setColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        set1.setFillColor(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getContext().getTheme()));
        set1.setDrawFilled(true);
        set1.setLineWidth(1f);

        ArrayList<RadarDataSet> sets = new ArrayList<RadarDataSet>();
        sets.add(set1);

        RadarData data = new RadarData(xVals, sets);
        data.setValueTextSize(10f);
        data.setDrawValues(true);

        chart.setData(data);

        chart.invalidate();
        chart.animateY(AppConstants.DEFAULT_ANIMATION_TIME);
        chart.animateY(AppConstants.DEFAULT_ANIMATION_TIME);
    }

    private void initChart() {
        if (chart == null) {
            return;
        }
        mParties = new String[]{
                getString(R.string.a_0067),
                getString(R.string.a_0062),
                getString(R.string.a_0063),
                getString(R.string.a_0064),
                getString(R.string.a_0065),
                getString(R.string.a_0066)
        };

        chart.setDescription("");
        chart.setWebLineWidth(0.75f);
        chart.setWebLineWidthInner(1f);
        chart.setWebAlpha(100);
        chart.setRotationAngle(-120f);
        chart.setValueColorInner(ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme()));
        chart.setBgColorInner(ResourcesCompat.getColor(getResources(), R.color.chart_blue, getTheme()));
        chart.setWebColorInner(ResourcesCompat.getColor(getResources(), R.color.chart_blue, getTheme()));
        chart.setWebColor(ResourcesCompat.getColor(getResources(), R.color.chart_blue, getTheme()));
        chart.setTouchEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setEnabled(true);
        YAxis yAxis = chart.getYAxis();
        yAxis.setEnabled(false);
        yAxis.setLabelCount(6, false);
        yAxis.setTextSize(12f);
        yAxis.setStartAtZero(true);

        xAxis.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_text, getTheme()));
        yAxis.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_text, getTheme()));
        setChart(gameBean);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //选择相册
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    List<String> photoList = new ArrayList<>();
                    if (selectList != null && !selectList.isEmpty()) {
                        for (LocalMedia media : selectList) {
                            boolean gif = PictureMimeType.isGif(media.getPictureType());
                            photoList.add(media.isCompressed() && !gif ? media.getCompressPath() : media.getPath());
                        }
                        mUploadFilePresent.uploadFiles(UrlConstant.UPLOAD_SHOP_IMG,
                                photoList,
                                appId);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needNotifyTagDataChage) {
            needNotifyTagDataChage = false;
            gamePresent.reloadTag(appId);
        }
        if (indexAnimator != null) {
            indexAnimator.start();
        }
        delayHide();
    }

    private boolean needNotifyTagDataChage = false;

    @Override
    public void uploadSuccess(String data) {
        ToastUtils.showShortSafe(R.string.a_0218);
    }

    @Override
    public void uploadProgress(int progress) {
        LogUtils.e("progress = " + progress);
    }

    @Override
    public void uploadFailure() {
        LogUtils.e(R.string.a_0219);
    }

}
