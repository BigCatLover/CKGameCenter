package com.jingyue.lygame.modules.comment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.bean.GameScoreBean;
import com.jingyue.lygame.clickaction.ButtonAction;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.model.GameScoreRp;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.modules.comment.present.GameScorePresent;
import com.jingyue.lygame.modules.comment.present.ReportActPresent;
import com.jingyue.lygame.modules.comment.view.GameScoreView;
import com.jingyue.lygame.modules.comment.view.UserActionView;
import com.jingyue.lygame.utils.DeviceUtil;
import com.jingyue.lygame.utils.DialogUtils;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.jingyue.lygame.widget.CommonDialog;
import com.laoyuegou.android.lib.utils.ToastUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author zhanglei
 *         游戏评分页
 */
public class GameScoreActivity extends BaseActivity implements GameScoreView, UserActionView {
    public static final int DEFUALT_COMMENT_LENGTH = 20;
    @BindView(R.id.score_avg)
    TextView scoreAvg;
    @BindView(R.id.scoretext)
    TextView scoretext;
    @BindView(R.id.tv_story_plot)
    TextView tvStoryPlot;
    @BindView(R.id.sb_story_plot)
    SeekBar sbStoryPlot;
    @BindView(R.id.tv_sound_effects)
    TextView tvSoundEffects;
    @BindView(R.id.sb_sound_effects)
    SeekBar sbSoundEffects;
    @BindView(R.id.tv_operation_experience)
    TextView tvOperationExperience;
    @BindView(R.id.sb_operation_experience)
    SeekBar sbOperationExperience;
    @BindView(R.id.tv_easy_set)
    TextView tvEasySet;
    @BindView(R.id.sb_easy_set)
    SeekBar sbEasySet;
    @BindView(R.id.tv_operation_performance)
    TextView tvOperationPerformance;
    @BindView(R.id.sb_operation_performance)
    SeekBar sbOperationPerformance;

    @BindView(R.id.tv_play_depth)
    TextView tvPlayDepth;
    @BindView(R.id.sb_play_depth)
    SeekBar sbPlayDepth;

    @BindView(R.id.et_comment)
    EditText etComment;
    @BindView(R.id.instruction)
    TextView instruction;
    @BindView(R.id.question_ll)
    RelativeLayout questionLl;
    @BindView(R.id.score_ll)
    LinearLayout scoreLl;
    @BindView(R.id.question)
    ImageView question;
    @BindView(R.id.green)
    ImageView green;
    @BindView(R.id.viewall_iv)
    ImageView viewallIv;
    @BindView(R.id.title_addcomment)
    RelativeLayout titleAddcomment;
    @BindView(R.id.rcy_addcomment)
    RecyclerView rcyAddcomment;
    @BindView(R.id.add_comment_ll)
    RelativeLayout addCommentLl;
    @BindView(R.id.comment_added)
    TextView commentAdded;
    @BindView(R.id.show_all)
    TextView showAll;
    @BindView(R.id.added_time)
    TextView addedTime;
    @BindView(R.id.comment_ll)
    LinearLayout commentLl;
    @BindView(R.id.actionbar_back)
    ImageView actionbarBack;
    @BindView(R.id.actionbar_right_text)
    TextView actionbarReport;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.layout_ll)
    FrameLayout layoutLl;

    private GameScoreBean gameScoreBean;
    private int mPlayDepth = 0;
    private int mOperationPerformance = 0;
    private int mSoundEffects = 0;
    private int mEasySet = 0;
    private int mStoryPlot = 0;
    private int mOperationExperience = 0;
    private String[] oldScore;
    private String curScore;
    private String curScoreAvg;
    private CommonDialog changetipDialog;
    private List<CommentListBean.Comment> temp = new ArrayList<>();
    private List<CommentListBean.Comment> addcmtList = new ArrayList<>();
    private String appid;
    private AddCommentAdapter adapter;
    private long lastCommitTime = 0;
    private CommentListBean.Comment comment = new CommentListBean.Comment();
    private GameScorePresent gameScorePresent;
    private ReportActPresent reportActPresent;
    private ActionBarUtil mBar;
    //上次保存的草稿
    private String cacheContent;
    private String cacheScore;
    private SharedPreferences sharedPreferences;
    private String originGameScore;
    private boolean isFirstComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableEventBus();
        super.onCreate(savedInstanceState);
        mBar = ActionBarUtil.inject(this, true).title(R.string.a_0152)
                .hideShare()
                .hideCollect()
                .showRightText(R.string.a_0167, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogUtils.showTipDialog(GameScoreActivity.this, getString(R.string.a_0167));
                    }
                });
        appid = getIntent().getStringExtra(KeyConstant.KEY_APP_ID);
        originGameScore = getIntent().getStringExtra(KeyConstant.KEY_GAME_SCORE);
        if (appid != null) {
            recordPageEvent(new ButtonAction(appid).a3().comment1());
        }
        //layoutLl.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        etComment.setCursorVisible(false);
        etComment.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etComment.setSingleLine(false);
        etComment.setHorizontallyScrolling(false);
        etComment.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    etComment.setCursorVisible(true);
                    etComment.setHint("");
                }
                return false;
            }

        });

        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //文字变动 ， 有未发出的搜索请求，应取消
            }
        });

        getDraft();
        if (StringUtils.isEmptyOrNull(appid)) {
            ToastUtils.showShort(R.string.a_0015);
//            return;
        } else {
            getNetData(appid);
        }
    }

    private void getDraft() {
        if (!LoginManager.getInstance().isLogin()) {
            return;
        }
        if (sharedPreferences == null) {
            StringBuilder s = new StringBuilder();
            s.append(appid).append(LoginManager.getInstance().getLoginInfo().id);
            sharedPreferences = getSharedPreferences(s.toString(), Activity.MODE_PRIVATE);
        }
        cacheScore = sharedPreferences.getString(KeyConstant.KEY_COMMENT_SCORE, "");
        cacheContent = sharedPreferences.getString(KeyConstant.KEY_COMMENT_CONTENT, "");
    }

    private void getNetData(String appid) {
        add(gameScorePresent = new GameScorePresent(this, this, new GameScoreRp()));
        gameScorePresent.loadData(appid);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_game_score;
    }

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = layoutLl.getRootView().getHeight() - layoutLl.getHeight();
            int contentViewTop = layoutLl.getRootView().getHeight();
            if (heightDiff > contentViewTop / 3) {
                questionLl.setVisibility(View.GONE);
            } else {
                questionLl.setVisibility(View.VISIBLE);
            }
        }
    };


    private void setupUI() {
        sbStoryPlot.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeScoreAvg("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvStoryPlot.setText(getString(R.string.a_0298, getString(R.string.a_0067), progress));
                mStoryPlot = progress;
            }
        });

        sbOperationExperience.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeScoreAvg("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvOperationExperience.setText(getString(R.string.a_0298, getString(R.string.a_0063), progress));
                mOperationExperience = progress;
            }
        });

        sbEasySet.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeScoreAvg("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvEasySet.setText(getString(R.string.a_0298, getString(R.string.a_0064), progress));
                mEasySet = progress;
            }
        });

        sbOperationPerformance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeScoreAvg("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvOperationPerformance.setText(getString(R.string.a_0298, getString(R.string.a_0065), progress));
                mOperationPerformance = progress;
            }
        });

        sbSoundEffects.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeScoreAvg("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvSoundEffects.setText(getString(R.string.a_0298, getString(R.string.a_0062), progress));
                mSoundEffects = progress;
            }
        });

        sbPlayDepth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changeScoreAvg("");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                tvPlayDepth.setText(getString(R.string.a_0298, getString(R.string.a_0066), progress));
                mPlayDepth = progress;
            }
        });


        adapter = new AddCommentAdapter();
        rcyAddcomment.setAdapter(adapter);
        rcyAddcomment.setLayoutManager(new BaseLinearLayoutManager(this));
        if (gameScoreBean.getComment() == null || gameScoreBean.getComment().isEmpty()) {
            title.setText(StringUtils.generateTime(false));
            commentLl.setVisibility(View.GONE);
            if (cacheContent.isEmpty()) {
                etComment.setHint(R.string.a_0158);
            } else {
                etComment.setText(cacheContent);
            }
            isFirstComment = true;
        } else {
            isFirstComment = false;
            commentLl.setVisibility(View.VISIBLE);
            if (cacheContent.isEmpty()) {
                etComment.setHint(R.string.a_0157);
            } else {
                etComment.setText(cacheContent);
            }
            changeCommentStatus(true);
        }
        if (cacheScore.isEmpty()) {
            if (isFirstComment) {
                oldScore = originGameScore.split(",");
            } else {
                oldScore = gameScoreBean.last.score.split(",");
            }
        } else {
            oldScore = cacheScore.split(",");
        }
        for (int i = 0; i < oldScore.length; i++) {
            int newvalue = new BigDecimal(oldScore[i]).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            oldScore[i] = String.valueOf(newvalue);
        }
        sbStoryPlot.setProgress(Integer.valueOf(oldScore[0]));
        tvStoryPlot.setText(getString(R.string.a_0300, getString(R.string.a_0067), oldScore[0]));
        mStoryPlot = Integer.valueOf(oldScore[0]);

        sbSoundEffects.setProgress(Integer.valueOf(oldScore[1]));
        tvSoundEffects.setText(getString(R.string.a_0300, getString(R.string.a_0062), oldScore[1]));
        mSoundEffects = Integer.valueOf(oldScore[1]);

        sbOperationExperience.setProgress(Integer.valueOf(oldScore[2]));
        tvOperationExperience.setText(getString(R.string.a_0300, getString(R.string.a_0063), oldScore[2]));
        mOperationExperience = Integer.valueOf(oldScore[2]);

        sbEasySet.setProgress(Integer.valueOf(oldScore[3]));
        tvEasySet.setText(getString(R.string.a_0300, getString(R.string.a_0064), oldScore[3]));
        mEasySet = Integer.valueOf(oldScore[3]);

        sbOperationPerformance.setProgress(Integer.valueOf(oldScore[4]));
        tvOperationPerformance.setText(getString(R.string.a_0300, getString(R.string.a_0065), oldScore[4]));
        mOperationPerformance = Integer.valueOf(oldScore[4]);

        sbPlayDepth.setProgress(Integer.valueOf(oldScore[5]));
        tvPlayDepth.setText(getString(R.string.a_0300, getString(R.string.a_0066), oldScore[5]));
        mPlayDepth = Integer.valueOf(oldScore[5]);

        if (cacheScore.isEmpty()) {
            if (isFirstComment) {
                changeScoreAvg("");
            } else {
                changeScoreAvg(gameScoreBean.last.avg_score);
            }

        } else {
            changeScoreAvg("");
        }
    }

    private void changeCommentStatus(boolean isFromServer) {
        commentLl.setVisibility(View.VISIBLE);
        addcmtList.clear();
        temp.clear();
        for (CommentListBean.Comment mComment : gameScoreBean.getComment()) {
            if (mComment.is_add || mComment.is_add2) {
                addcmtList.add(mComment);
            } else {
                comment = mComment;
            }
        }

        Collections.sort(addcmtList, new Comparator<CommentListBean.Comment>() {
            @Override
            public int compare(CommentListBean.Comment o1, CommentListBean.Comment o2) {
                if (Long.valueOf(o2.create_time) > (Long.valueOf(o1.create_time))) {
                    return 1;
                } else if (Long.valueOf(o2.create_time) == (Long.valueOf(o1.create_time))) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
        addedTime.setText(StringUtils.generateTime(true));
        if (isFromServer) {
            title.setText(StringUtils.generateTime(comment.create_time, false));
        } else {
            title.setText(StringUtils.generateTime(false));
        }
        commentAdded.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //这个回调会调用多次，获取完行数记得注销监听
                commentAdded.getViewTreeObserver().removeOnPreDrawListener(this);
                int lines = commentAdded.getLineCount();
                if (lines < 5) {
                    showAll.setVisibility(View.GONE);
                } else {
                    commentAdded.setMaxLines(4);
                    showAll.setVisibility(View.VISIBLE);
                    showAll.setText(R.string.a_0135);
                    showAll.setOnClickListener(new View.OnClickListener() {
                        boolean flag = true;

                        @Override
                        public void onClick(View v) {
                            if (flag) {
                                showAll.setText(R.string.a_0136);
                                commentAdded.setMaxLines(100);
                                flag = false;
                            } else {
                                showAll.setText(R.string.a_0135);
                                commentAdded.setMaxLines(4);
                                flag = true;
                            }
                        }
                    });
                }
                return false;
            }
        });
        commentAdded.setText(StringUtils.formatString(comment.content));
        if (addcmtList.size() > 0) {
            addCommentLl.setVisibility(View.VISIBLE);
            temp.add(addcmtList.get(0));
            adapter.setDatas(temp);
            if (addcmtList.size() > 1) {
                titleAddcomment.setVisibility(View.VISIBLE);
                viewallIv.setOnClickListener(new View.OnClickListener() {
                    boolean flag = true;

                    @Override
                    public void onClick(View v) {
                        if (flag) {
                            adapter.setDatas(addcmtList);
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
                titleAddcomment.setVisibility(View.GONE);
                adapter.setDatas(addcmtList);
            }
        } else {
            addCommentLl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoginSucess() {
        if (gameScoreBean == null) {
            getDraft();
            getNetData(appid);
        }
    }

    @Override
    public void onLoginFailure() {
        finish();
    }

    private void changeScoreAvg(String score_avg) {
        double avg = 0;
        if (score_avg.isEmpty()) {
            float mavg = (mEasySet + mOperationExperience + mOperationPerformance +
                    mPlayDepth + mStoryPlot + mSoundEffects) / 6f;
            String result = String.format("%.1f", mavg);
            avg = Double.valueOf(result);
        } else {
            avg = Double.valueOf(score_avg);
        }
        if (avg >= 9) {
            scoretext.setText(R.string.a_0165);
        } else if (avg >= 8) {
            scoretext.setText(R.string.a_0164);
        } else if (avg >= 7) {
            scoretext.setText(R.string.a_0163);
        } else if (avg >= 6) {
            scoretext.setText(R.string.a_0162);
        } else if (avg >= 5) {
            scoretext.setText(R.string.a_0161);
        } else if (avg >= 4) {
            scoretext.setText(R.string.a_0160);
        } else {
            scoretext.setText(R.string.a_0159);
        }
        curScoreAvg = String.valueOf(avg);
        scoreAvg.setText(curScoreAvg);

    }

    @OnClick({R.id.actionbar_back, R.id.publish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.actionbar_back:
                if (!LoginManager.getInstance().isLogin()) {
                    finish();
                } else {
                    if (etComment.getText().toString().trim().length() > 0 || isScoreChange()) {
                        showTipDialog(getString(R.string.a_0213));
                    } else {
                        finish();
                    }
                }
                break;
            case R.id.publish:
                commitComment();
                break;
        }
    }

    private boolean isScoreChange() {
        if (oldScore == null) {
            return mSoundEffects > 0 || mOperationPerformance > 0
                    || mOperationExperience > 0 || mEasySet > 0 || mPlayDepth > 0 || mStoryPlot > 0;
        } else {
            return !(Integer.valueOf(oldScore[0]) == mStoryPlot && Integer.valueOf(oldScore[1]) == mSoundEffects
                    && Integer.valueOf(oldScore[2]) == mOperationExperience && Integer.valueOf(oldScore[3]) == mEasySet
                    && Integer.valueOf(oldScore[4]) == mOperationPerformance && Integer.valueOf(oldScore[5]) == mPlayDepth);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        layoutLl.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
    }

    private void showTipDialog(String error) {
        if (null != changetipDialog && changetipDialog.isShowing()) {
            return;
        }
        changetipDialog = new CommonDialog.Builder(this)
                .setContent(error)
                .setRightButtonInterface(getString(R.string.a_0173),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != changetipDialog && changetipDialog.isShowing()) {
                                    changetipDialog.dismiss();
                                    changetipDialog = null;
                                    saveDraft();
                                }
                            }
                        })
                .setLeftButtonInterface(getString(R.string.a_0075),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (null != changetipDialog && changetipDialog.isShowing()) {
                                    changetipDialog.dismiss();
                                    changetipDialog = null;
                                    clearDraft();
                                }
                            }
                        })
                .show();
        changetipDialog.setCancelable(false);
    }

    private void saveDraft() {
        if (!LoginManager.getInstance().isLogin()) {
            return;
        }
        if (sharedPreferences == null) {
            StringBuilder s = new StringBuilder();
            s.append(appid).append(LoginManager.getInstance().getLoginInfo().id);
            sharedPreferences = getSharedPreferences(s.toString(), Activity.MODE_PRIVATE);
        }
        StringBuilder s = new StringBuilder();
        s.append(mStoryPlot).append(",").append(mSoundEffects).append(",")
                .append(mOperationExperience).append(",")
                .append(mEasySet).append(",").append(mOperationPerformance).append(",").append(mPlayDepth);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isScoreChange()) {
            editor.putString(KeyConstant.KEY_COMMENT_SCORE, s.toString());
        }
        if (etComment.getText().toString().trim().length() > 0) {
            editor.putString(KeyConstant.KEY_COMMENT_CONTENT, etComment.getText().toString().trim());
        }
        editor.commit();
        finish();
    }

    private void clearDraft() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KeyConstant.KEY_COMMENT_SCORE, "");
        editor.putString(KeyConstant.KEY_COMMENT_CONTENT, "");
        editor.commit();
        finish();
    }

    public void commitComment() {
        // 先隐藏键盘
        ((InputMethodManager) etComment.getContext().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        if (!LoginManager.getInstance().isLogin()) {
            ToastUtils.showShort(R.string.a_0228);
            return;
        }
        if (etComment.getText().toString().trim().length() < DEFUALT_COMMENT_LENGTH) {
            ToastUtils.showShort(
                    new StringBuilder().append(getString(R.string.a_0279))
                            .append(String.valueOf(DEFUALT_COMMENT_LENGTH))
                            .append(getString(R.string.a_0280))
                            .toString());
            return;
        }
        if (!StringUtils.isEmptyOrNull(appid)) {
            recordPageEvent(new ButtonAction(appid).a3().comment2());
        }

        if (Double.valueOf(curScoreAvg) == 0) {
            ToastUtils.showShort(R.string.a_0212);
            return;
        }
        final StringBuilder score = new StringBuilder();
        score.append(mStoryPlot).append(",")
                .append(mSoundEffects).append(",")
                .append(mOperationExperience).append(",")
                .append(mEasySet).append(",")
                .append(mOperationPerformance).append(",")
                .append(mPlayDepth);
        curScore = score.toString();
        String phone_info = getString(R.string.a_0169);//默认显示iphone XXX
        if (!StringUtils.isEmptyOrNull(DeviceUtil.getPhoneModel())) {
            phone_info = DeviceUtil.getPhoneModel();
        }
        if (reportActPresent == null) {
            reportActPresent = new ReportActPresent(this, this);
            add(reportActPresent);
        }
        reportActPresent.loadData(appid, etComment.getText().toString().trim(), phone_info, curScore);
    }

    public static void start(Context context, String appid, String gameScore) {
        Intent starter = new Intent(context, GameScoreActivity.class);
        starter.putExtra(KeyConstant.KEY_APP_ID, appid);
        starter.putExtra(KeyConstant.KEY_GAME_SCORE, gameScore);
        context.startActivity(starter);
    }

    @Override
    public void setDataOnMain(GameScoreBean bean) {
        gameScoreBean = bean;
        gameScoreBean.appid = appid;
        setupUI();
    }

    @Override
    public void success(int action, String id) {
        if (action != UserActionView.ACTION_REPORT) {
            return;
        }
        ToastUtils.showShort(R.string.a_0168);
        lastCommitTime = System.currentTimeMillis() / 1000;
        oldScore = curScore.split(",");
        CommentListBean.Comment addBean = new CommentListBean.Comment();
        addBean.content = etComment.getText().toString().trim();
        addBean.app_id = Integer.valueOf(appid);
        addBean.mem_id = LoginManager.getInstance().getLoginInfo().id;
        addBean.create_time = (int) lastCommitTime;
        etComment.setText("");
        if (gameScoreBean == null || gameScoreBean.getComment() == null || gameScoreBean.getComment().size() == 0) {
            addBean.is_add2 = false;
            addBean.is_add = false;
        } else {
            addBean.is_add2 = true;
            addBean.is_add = true;
        }
        gameScoreBean.getComment().add(addBean);
        gameScoreBean.update();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KeyConstant.KEY_COMMENT_SCORE, "");
        editor.putString(KeyConstant.KEY_COMMENT_CONTENT, "");
        editor.commit();
        changeCommentStatus(false);
    }
}
