package com.jingyue.lygame.modules.rcmd;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.jingyue.lygame.ActionBarUtil;
import com.jingyue.lygame.BaseActivity;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.clickaction.TagDetailAction;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.modules.rcmd.adapter.TagGameListAdapter;
import com.jingyue.lygame.modules.rcmd.present.TagListPresent;
import com.jingyue.lygame.modules.rcmd.view.TagListView;
import com.jingyue.lygame.utils.recycleview.ItemDividerDecoration;
import com.jingyue.lygame.widget.BaseLinearLayoutManager;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.List;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-19 10:47
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * <p>
 * 标签列表页
 */
public class TagGameListActivity extends BaseActivity implements TagListView {

    public static final void open(Context context, String tagId, String name) {
        if (TextUtils.isEmpty(tagId)) {
            ToastUtils.showShort(R.string.a_0217);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstant.KEY_APP_ID, tagId);
        bundle.putString(KeyConstant.KEY_NAME, name);
        Utils.startActivity(context, TagGameListActivity.class, bundle);
    }

    @BindView(R.id.tag_list)
    RecyclerView tagList;
    @BindView(R.id.tag_list_container)
    PtrFrameLayout tagRefreshContainer;

    @Override
    public int getLayoutId() {
        return R.layout.rcmd_tag_detail_list_main;
    }

    private TagGameListAdapter mListAdapter;
    private TagListPresent mTagPresent;
    private String tagId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String name = getIntent().getStringExtra(KeyConstant.KEY_NAME);

        ActionBarUtil.inject(this)
                .hideShare()
                .hideCollect()
                .title(name);

        tagList.setLayoutManager(new BaseLinearLayoutManager(this));
        tagList.addItemDecoration(new ItemDividerDecoration(this, ItemDividerDecoration.VERTICAL_LIST));
        tagList.setAdapter(mListAdapter = new TagGameListAdapter(this));

        tagId = getIntent().getStringExtra(KeyConstant.KEY_APP_ID);
        recordPageEvent(new TagDetailAction(tagId));
        if (!TextUtils.isEmpty(tagId)) {
            mTagPresent = new TagListPresent(this, this);
            mTagPresent.load(tagId, true);
        }
    }

    @Override
    public void setDataOnMain(List<GameBean> bean) {
        mListAdapter.clear();
        mListAdapter.addAll(bean);
        mListAdapter.notifyDataSetChanged();
    }
}
