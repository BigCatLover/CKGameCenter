package com.jingyue.lygame.modules.rcmd;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.TagBusinessViewBean;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.events.TagChangeEvent;
import com.jingyue.lygame.model.LoginManager;
import com.jingyue.lygame.model.TagRp;
import com.jingyue.lygame.modules.personal.LoginActivity;
import com.jingyue.lygame.modules.rcmd.adapter.TagFlowAdapter;
import com.jingyue.lygame.modules.rcmd.present.TagPresent;
import com.jingyue.lygame.modules.rcmd.view.TagView;
import com.jingyue.lygame.widget.tagflow.TagFlowLayout;
import com.laoyuegou.android.lib.utils.AnimationUtils;
import com.laoyuegou.android.lib.utils.EBus;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-12 11:16
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TagActivity extends BaseActivity implements TagView, View.OnClickListener, TagFlowAdapter.OnItemSelectListener {

    public static void open(Context context, String appId, String name) {
        Bundle bundle = new Bundle();
        if (!LoginManager.getInstance().isLogin()) {
            bundle.putSerializable(KeyConstant.KEY_NEXT_PAGE, TagActivity.class);
            Utils.startActivity(context, LoginActivity.class, bundle);
            return;
        }
        if (!TextUtils.isEmpty(name)) {
            bundle.putString(KeyConstant.KEY_TITLE, name);
        }
        bundle.putString(KeyConstant.KEY_APP_ID, appId);
        Utils.startActivity(context, TagActivity.class, bundle);
    }

    @BindView(R.id.common_tag)
    TextView commonTag;
    @BindView(R.id.bottom_group)
    RelativeLayout bottomGroup;
    @BindView(R.id.tag_edit)
    EditText tagEdit;
    @BindView(R.id.send_btn)
    ImageView sendBtn;
    @BindView(R.id.empty_group)
    View emptyGroup;
    @BindView(R.id.container)
    ViewGroup container;

    @Override
    public int getLayoutId() {
        return R.layout.rcmd_tag_main;
    }

    private TagPresent mTagPresent;
    private String appId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appId = getIntent().getStringExtra(KeyConstant.KEY_APP_ID);
        String name = getIntent().getStringExtra(KeyConstant.KEY_TITLE);
        name = name != null ? name : "";

        ActionBarUtil.inject(this)
                .title(name)
                .leftGravity()
                .hideCollect()
                .hideShare();

        sendBtn.setOnClickListener(this);

        add(mTagPresent = new TagPresent(this, this, new TagRp()));

        mTagPresent.load(appId, true);
    }

    @BindView(R.id.system_tag_container)
    TagFlowLayout systemTagContainer;
    @BindView(R.id.custom_tag_container)
    TagFlowLayout customTagContainer;
    @BindView(R.id.choose_tag_container)
    TagFlowLayout chooseTagContainer;

    private TagFlowAdapter mDefualtTagFlowAdapter;
    private TagFlowAdapter mUserTagFlowAdapter;
    private TagFlowAdapter mChooseFlowAdapter;

    private List<com.jingyue.lygame.bean.GameBean.Tag> defaultX;
    private List<com.jingyue.lygame.bean.GameBean.Tag> user;
    private List<com.jingyue.lygame.bean.GameBean.Tag> choosed;

    @Override
    public void setDataOnMain(TagBusinessViewBean bean) {
        //lazy load
        //用户自定义标签
        if (ensureUserTags(bean)) {
            emptyGroup.setVisibility(View.GONE);
            bottomGroup.setVisibility(View.VISIBLE);
            user.clear();
            user.addAll(bean.user);
            mUserTagFlowAdapter.notifyDataChanged();
        }
        //默认标签
        if (ensureDefaultTags(bean)) {
            defaultX.clear();
            defaultX.addAll(bean.defaultX);
            mDefualtTagFlowAdapter.notifyDataChanged();
        }
        //已经选择的标签
        if (ensureChooseTag(bean)) {
            emptyGroup.setVisibility(View.GONE);
            bottomGroup.setVisibility(View.VISIBLE);
            mChooseFlowAdapter.notifyDataChanged();
        }
    }

    //輸入框添加標籤
    public void onTagAdded(GameBean.Tag tag) {
        ToastUtils.showShort("标签" + tag.name + "添加成功！");
        emptyGroup.setVisibility(View.GONE);
        bottomGroup.setVisibility(View.VISIBLE);
        if (user == null) {
            user = new ArrayList<>();
        }
        if (mUserTagFlowAdapter == null) {
            mUserTagFlowAdapter = new MyTagAdapter(user);
            mUserTagFlowAdapter.setCanDoSelect(true);
            mUserTagFlowAdapter.setOnClickListener(this);
            customTagContainer.setAdapter(mUserTagFlowAdapter);
        }
        user.add(tag);
        tagEdit.setText("");
        mUserTagFlowAdapter.notifyDataChanged();
        /*if (flyTagView == null) {
            flyTagView = mUserTagFlowAdapter.makeView(this, container,tag);
            container.addView(flyTagView);
            flyTagView.setTop(tagEdit.getTop() - tagEdit.getPaddingTop());
            flyTagView.setLeft(tagEdit.getLeft() - tagEdit.getPaddingLeft());
            AnimationUtils.throwToTarget(flyTagView, customTagContainer.getChildAt(customTagContainer.getChildCount() - 1),true);
        }*/
    }

    private TextView flyTagView = null;

    private boolean ensureChooseTag(TagBusinessViewBean bean) {
        if (choosed != null) choosed.clear();
        if (bean.defaultX != null) {
            for (final GameBean.Tag tag : bean.defaultX) {
                if (tag.isSelected()) {
                    if (choosed == null) {
                        choosed = new ArrayList<>();
                    }
                    choosed.add(tag);
                }
            }
        }
        if (bean.user != null) {
            for (final GameBean.Tag tag : bean.user) {
                if (tag.isSelected()) {
                    if (choosed == null) {
                        choosed = new ArrayList<>();
                    }
                    choosed.add(tag);
                }
            }
        }
        currentSelectedNumber = choosed != null ? choosed.size() : 0;
        if (mChooseFlowAdapter == null) {
            if (choosed != null && !choosed.isEmpty()) {
                mChooseFlowAdapter = new MyTagAdapter(choosed);
                mChooseFlowAdapter.setClickable(false);
                chooseTagContainer.setAdapter(mChooseFlowAdapter);
            }
            return false;
        }
        return choosed != null && !choosed.isEmpty();
    }


    private boolean ensureDefaultTags(TagBusinessViewBean bean) {
        if (mDefualtTagFlowAdapter == null) {
            if (bean.defaultX != null && !bean.defaultX.isEmpty()) {
                if (defaultX == null) {
                    defaultX = new ArrayList<>();
                }
                defaultX.clear();
                defaultX.addAll(bean.defaultX);
                mDefualtTagFlowAdapter = new MyTagAdapter(defaultX);
                mDefualtTagFlowAdapter.setCanDoSelect(true);
                systemTagContainer.setAdapter(mDefualtTagFlowAdapter);
                mDefualtTagFlowAdapter.setOnClickListener(this);
            }
            return false;
        }
        return bean.defaultX != null && !bean.defaultX.isEmpty();
    }

    private boolean ensureUserTags(TagBusinessViewBean bean) {
        if (mUserTagFlowAdapter == null) {
            if (bean.user != null && !bean.user.isEmpty()) {
                if (user == null) {
                    user = new ArrayList<>();
                }
                user.clear();
                user.addAll(bean.user);
                mUserTagFlowAdapter = new MyTagAdapter(user);
                mUserTagFlowAdapter.setCanDoSelect(true);
                customTagContainer.setAdapter(mUserTagFlowAdapter);
                mUserTagFlowAdapter.setOnClickListener(this);
                bottomGroup.setVisibility(View.VISIBLE);
                emptyGroup.setVisibility(View.GONE);
            }
            return false;
        }
        return bean.user != null && !bean.user.isEmpty();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                if (checkTagEdite()) {
                    mTagPresent.addTag(tagEdit.getText().toString(), appId, true);
                }
                break;
        }
    }

    private boolean checkTagEdite() {
        if (!TextUtils.isEmpty(tagEdit.getText().toString())) {
            return true;
        } else if (tagEdit.getText().toString().length() > 4) {
            ToastUtils.showShort(R.string.a_0226);
        } else {
            AnimationUtils.shakeAnimation(tagEdit).start();
        }
        return false;
    }

    public static final int DEFAULT_MAX_SELECTED = AppConstants.MAX_SELECTED_TAGS;
    private int currentSelectedNumber = 0;

    @Override
    public boolean onItemSelected(int position, boolean isSeleted, GameBean.Tag item) {
        //tag 不能相同，服务器返回的是tag应该做过tag重复过滤，所以客户端不考虑该逻辑
        if (isSeleted) {
            item.isSelected = 1;
            if (choosed == null) {
                choosed = new ArrayList<>(DEFAULT_MAX_SELECTED);
            }
            if (mChooseFlowAdapter == null) {
                mChooseFlowAdapter = new MyTagAdapter(choosed);
                chooseTagContainer.setAdapter(mChooseFlowAdapter);
            }
            //如果用戶标签和默认标签返回的选择 item属于已选择列表
            //return true 修正选中状态
            if (choosed.contains(item)) {
                return true;
            }
            if (currentSelectedNumber < DEFAULT_MAX_SELECTED) {
                item.setIsSelected(true);
                item.async().save();
                choosed.add(item);
                currentSelectedNumber++;
                mChooseFlowAdapter.notifyDataChanged();
                mTagPresent.selectTag(defaultX, user, appId, item, false);
                EBus.getDefault().post(new TagChangeEvent(item, Integer.valueOf(appId), TagChangeEvent.OP_SELECT));
                return true;
            } else {
                ToastUtils.showShort("最多可以选择" + DEFAULT_MAX_SELECTED + "个标签");
            }
        } else {
            if (choosed != null && choosed.contains(item)) {
                item.setIsSelected(false);
                item.async().save();
                choosed.remove(item);
                currentSelectedNumber--;
                mChooseFlowAdapter.notifyDataChanged();
                mTagPresent.selectTag(defaultX, user, appId, item, false);
                EBus.getDefault().post(new TagChangeEvent(item, Integer.valueOf(appId), TagChangeEvent.OP_UNSELECT));
            }
            return true;
        }
        return false;
    }

    public class MyTagAdapter extends TagFlowAdapter {

        public MyTagAdapter(List<GameBean.Tag> datas, String appId) {
            super(datas, appId);
        }

        public MyTagAdapter(List<GameBean.Tag> datas) {
            super(datas);
        }

        @Override
        public void onTagSet(View tag, GameBean.Tag tag1) {
            super.onTagSet(tag, tag1);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tag.getLayoutParams();
            lp.bottomMargin = (int) tag.getContext().getResources().getDimension(R.dimen.small_gap_between_view);
        }
    }
}
