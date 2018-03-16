package com.jingyue.lygame;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.bean.ShareEntityBean;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.comment.present.GameCollectPresent;
import com.jingyue.lygame.modules.comment.view.UserActionView;
import com.jingyue.lygame.modules.download.DownloadCenterActivity;
import com.jingyue.lygame.modules.find.present.SubjectActionPresent;
import com.jingyue.lygame.modules.find.view.SubjectActionView;
import com.jingyue.lygame.modules.personal.LoginActivity;
import com.jingyue.lygame.modules.personal.UserCenterActivity;
import com.jingyue.lygame.modules.search.SearchActivity;
import com.jingyue.lygame.utils.ShareUtil;
import com.jingyue.lygame.utils.WebpUtils;
import com.laoyuegou.android.lib.utils.AnimationUtils;
import com.laoyuegou.android.lib.utils.BarUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-29 17:59
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * 通用actionbar配置工具
 */
public class ActionBarUtil implements OnApplyWindowInsetsListener, UserActionView, SubjectActionView {

    @BindView(R.id.actionbar_group)
    ViewGroup actionBarContainer;
    @BindView(R.id.actionbar_back)
    ImageView actionbarBack;
    @BindView(R.id.actionbar_right_text)
    TextView actionbarReport;
    @BindView(R.id.actionbar_title)
    TextView actionbarTitle;
    @BindView(R.id.actionbar_like)
    ImageView actionbarLike;
    @BindView(R.id.subject_like)
    ImageView subjectLike;
    @BindView(R.id.actionbar_subject)
    RelativeLayout actionbarSubject;
    @BindView(R.id.actionbar_share)
    ImageView actionbarShare;
    @BindView(R.id.actionbar_usercenter)
    ImageView actionbarUsercenter;
    @BindView(R.id.actionbar_search)
    ImageView actionbarSearch;
    @BindView(R.id.actionbar_background)
    View backGround;
    @BindView(R.id.space)
    View space;
    @BindView(R.id.subject_like_num)
    TextView subjectLikeNum;

    private WeakReference<Activity> activityWeakReference;
    private WeakReference<Fragment> fragmentWeakReference;
    private boolean isFragment = false;
    private boolean isMeasured = false;
    private int mSmallPaddingInView;
    private boolean showSearch = false;
    private boolean inited;

    @Override
    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
//        if (v != null && !inited) {
//            final View space = v.findViewById(R.id.space);
//            ViewGroup.LayoutParams params = v.getLayoutParams();
//            params.height += insets.getSystemWindowInsetTop();
//            space.getLayoutParams().height = insets.getSystemWindowInsetTop();
//            v.requestLayout();
//            inited = true;
//        }
        return insets;
    }

    public ActionBarUtil(Activity activity, boolean autofinish) {
        this(activity, null, autofinish);
    }


    /**
     * @param activity
     * @param view
     * @param blockBackEvent
     */
    public ActionBarUtil(Activity activity, View view, boolean blockBackEvent) {
        activityWeakReference = new WeakReference<>(activity);
        if (view != null) {
            ButterKnife.bind(this, view);
        } else {
            ButterKnife.bind(this, activity);
        }
        mSmallPaddingInView = (int) activity.getResources().getDimension(R.dimen.small_gap_between_view);

        //ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) actionBarContainer.getLayoutParams();
        //params.topMargin = BarUtils.getStatusBarHeight(activity);
//        BarUtils.setColor(activity, ResourcesCompat.getColor(activity.getResources(),R.color.colorAccent,activity.getTheme()));
        //BarUtils.setTransparent(activity);
        ViewCompat.setOnApplyWindowInsetsListener(actionBarContainer, this);
        if (!blockBackEvent) {
            actionbarBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity activity = activityWeakReference.get();
                    if (isFragment) {
                        activity = fragmentWeakReference.get().getActivity();
                    }
                    if (activity != null) activity.onBackPressed();
                }
            });
        }
    }

    public static ActionBarUtil inject(Activity activity, View view) {
        return new ActionBarUtil(activity, view, false);
    }

    public static ActionBarUtil inject(Activity activity) {
        return new ActionBarUtil(activity, false);
    }

    public static ActionBarUtil inject(Activity activity, boolean blockBackEvent) {
        return new ActionBarUtil(activity, blockBackEvent);
    }

    public ActionBarUtil title(String title) {
        if (!TextUtils.isEmpty(title)) {
            actionbarTitle.setText(title);
        }
        return this;
    }

    public ActionBarUtil leftGravity() {
        if (isMeasured) {
            setTitleGravity(Gravity.LEFT);
        }
        actionbarBack.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!isMeasured) {
                    isMeasured = true;
                    setTitleGravity(Gravity.LEFT);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    actionbarBack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        return this;
    }

    public ActionBarUtil hideCollect() {
        actionbarLike.setVisibility(View.GONE);
        return this;
    }

    public ActionBarUtil showRightText(@StringRes int resId, View.OnClickListener onClickListener) {
        actionbarReport.setVisibility(View.VISIBLE);
        actionbarReport.setText(resId);
        if (onClickListener != null) {
            actionbarReport.setOnClickListener(onClickListener);
        }
        return this;
    }

    public void setRightTextColor(int color) {
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            activity = fragmentWeakReference.get().getActivity();
        }
        actionbarReport.setTextColor(ResourcesCompat.getColor(activity.getResources(),
                color,
                activity.getTheme()));
    }

    public ActionBarUtil showSubject() {
        actionbarSubject.setVisibility(View.VISIBLE);
        return this;
    }

    public ActionBarUtil showSearch() {
        showSearch = true;
        actionbarSearch.setVisibility(View.VISIBLE);
        return this;
    }

    public ActionBarUtil showUsercenter() {
        actionbarUsercenter.setImageDrawable(WebpUtils.webpToDrawable(R.drawable.lyg_float));
        actionbarUsercenter.setVisibility(View.VISIBLE);
        return this;
    }

    public ActionBarUtil hideBack() {
        actionbarBack.setVisibility(View.GONE);
        return this;
    }

    private ShareEntityBean shareConfig;

    public void setShare(ShareEntityBean config) {
        this.shareConfig = config;
    }

    public ActionBarUtil hideShare() {
        if (actionbarLike.isShown()) {
            if (actionbarLike.getMeasuredWidth() != 0) {
                actionbarLike.animate().translationX(actionbarLike.getMeasuredWidth()).start();
                actionbarShare.animate().translationX(actionbarShare.getMeasuredWidth()).start();
            } else {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) actionbarLike.getLayoutParams();
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        } else {
            actionbarShare.setVisibility(View.GONE);
        }
        if (showSearch) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            actionbarSearch.setLayoutParams(lp);
        }
        return this;
    }

    public ActionBarUtil showShare() {
        //actionbarShare.setVisibility(View.VISIBLE);
        if (actionbarLike.getMeasuredWidth() != 0) {
            if (actionbarLike.getTranslationX() != 0)
                actionbarLike.animate().translationX(0).start();
            if (actionbarShare.getTranslationX() != 0)
                actionbarShare.animate().translationX(0).start();
        } else {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) actionbarLike.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_LEFT, R.id.actionbar_share);
        }
        return this;
    }

    public ActionBarUtil title(@StringRes int resId) {
        actionbarTitle.setText(resId);
        return this;
    }

    public ActionBarUtil transparent() {
        Activity activity = activityWeakReference.get();
        if (activity == null) {
            activity = fragmentWeakReference.get().getActivity();
        }
        BarUtils.setColor(activityWeakReference.get(),
                ResourcesCompat.getColor(activity.getResources(),
                        R.color.colorPoorTranslucent,
                        activity.getTheme()));
        ViewCompat.setAlpha(backGround, 0);
        return this;
    }

    private GameCollectPresent mGameCollectPresent;
    private SubjectActionPresent mSubActionPresent;

    public void setGameCollectPresent(GameCollectPresent gameCollectPresent) {
        mGameCollectPresent = gameCollectPresent;
    }

    public void setSubActionPresent(SubjectActionPresent subjectActionPresent) {
        mSubActionPresent = subjectActionPresent;
    }

    private String subjectId;

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @OnClick({R.id.actionbar_usercenter, R.id.actionbar_search, R.id.actionbar_title,
            R.id.actionbar_like, R.id.actionbar_share, R.id.actionbar_subject})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.actionbar_usercenter:
                Activity activity = activityWeakReference.get();
                if (isFragment) {
                    activity = fragmentWeakReference.get().getActivity();
                }
                if (LoginManager.getInstance().getLoginInfo().isLogin) {
                    UserCenterActivity.start(activity);
                } else {
                    LoginActivity.open(activity, LoginActivity.BACK_BACK_LOGIN_BACK);
                }
                break;
            case R.id.actionbar_search:
                Activity activity1 = activityWeakReference.get();
                if (isFragment) {
                    activity1 = fragmentWeakReference.get().getActivity();
                }
                SearchActivity.start(activity1);
                break;
            case R.id.actionbar_title:
                break;
            case R.id.actionbar_like:
                doLike();
                break;
            case R.id.actionbar_share:
                doShare();
                break;
            case R.id.actionbar_subject:
                doSubLike();
                break;
        }
    }

    protected void doSubLike() {
        if (subjectId == null) {
            return;
        }
        String action = "";
        if (isSublike) {
            action = SubjectActionPresent.ACTION_DISLIKE;
        } else {
            action = SubjectActionPresent.ACTION_LIKE;
        }
        mSubActionPresent.doAction(subjectId, action);
    }

    /**
     * 分享
     */
    protected void doShare() {
        ShareUtil.showShare(Utils.getContext(), shareConfig, "");
    }

    private boolean islike = false;

    public void setIslike(boolean islike) {
        this.islike = islike;
        actionbarLike.setSelected(islike);
    }

    private boolean isSublike = false;

    public void setIsSublike(boolean islike) {
        this.isSublike = islike;
        subjectLike.setSelected(islike);
    }

    private String subLikeNum;

    public void setSubLikeNum(String num) {
        this.subLikeNum = num;
        subjectLikeNum.setText(subLikeNum);
    }

    /**
     * 收藏
     */
    protected void doLike() {
        if (mGameCollectPresent != null) {
            mGameCollectPresent.doAction(islike);
        }
    }

    public void setTitleGravity(int titleGravity) {
        switch (titleGravity) {
            case Gravity.LEFT:
                actionbarTitle.setGravity(titleGravity | Gravity.CENTER_VERTICAL);
                actionbarTitle.setPadding(actionbarBack.getMeasuredWidth() + mSmallPaddingInView, 0, 0, 0);
                break;
            case Gravity.CENTER:
                actionbarTitle.setGravity(titleGravity);
                break;
        }
    }

    @Override
    public void success(int action, String id) {
        if (action == UserActionView.ACTION_UNLIKE) {
            ToastUtils.showShort(R.string.a_0201);
            islike = false;
            actionbarLike.setSelected(islike);
            AnimationUtils.biggerThenReturnAni(actionbarLike).start();
        } else if (action == UserActionView.ACTION_LIKE) {
            ToastUtils.showShort(R.string.a_0202);
            islike = true;
            actionbarLike.setSelected(islike);
            AnimationUtils.biggerThenReturnAni(actionbarLike).start();
        }
    }

    @Override
    public void ActionCallback(String action) {
        int newnum = Integer.valueOf(subLikeNum);
        if (action.equals(SubjectActionPresent.ACTION_DISLIKE)) {
            isSublike = false;
            subLikeNum = String.valueOf(newnum - 1);
            AnimationUtils.biggerThenReturnAni(subjectLike).start();
        } else if (action.equals(SubjectActionPresent.ACTION_LIKE)) {
            isSublike = true;
            subLikeNum = String.valueOf(newnum + 1);
            AnimationUtils.biggerThenReturnAni(subjectLike).start();
        }
        subjectLike.setSelected(isSublike);
        subjectLikeNum.setText(subLikeNum);
    }


    @Override
    public void showProgress(String msg) {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public Context getContext() {
        return null;
    }
}
