package com.jingyue.lygame.modules.rcmd.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.modules.rcmd.RcmdDetailActivity;
import com.jingyue.lygame.modules.rcmd.TagGameListActivity;
import com.jingyue.lygame.widget.DownloadProgessView;
import com.jingyue.lygame.widget.tagflow.TagFlowLayout;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.lygame.libadapter.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-19 13:58
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TagGameVH extends BaseItem.BVH<GameBean> implements View.OnClickListener, TagFlowAdapter.OnItemSelectListener {

    private boolean allowTagClick = AppConstants.ALLOW_TAG_CLICK;
    private static int DEFAULT_MAX_EMS = 4;

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.tag_title)
    TextView tagTitle;
    @BindView(R.id.rank_value)
    TextView rankValue;
    @BindView(R.id.tag_flow)
    TagFlowLayout tagFlow;
    @BindView(R.id.active)
    ImageView active;
    @BindView(R.id.transport)
    ImageView transport;
    @BindView(R.id.exper)
    DownloadProgessView exper;

    private int type = 0;

    private List<GameBean.Tag> tags;
    private TagFlowAdapter mTagFlowAdapter;

    public TagGameVH(final View itemView, final int type) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        exper.setOnClickListener(this);
        this.type = type;
        itemView.setOnClickListener(this);
        tagFlow.setSingleLine(true);
        tags = new ArrayList<>();
        mTagFlowAdapter = new TagFlowAdapter(tags) {
            @Override
            public void onTagSet(View tag,final GameBean.Tag tag1) {
                final TextView textView = (TextView) tag;
                textView.setTextSize(12);
            }
        };
        tagFlow.setAdapter(mTagFlowAdapter);
        mTagFlowAdapter.setOnClickListener(this);
    }

    @Override
    public void onBindViewHolder(GameBean itemInfo, int position) {
        itemView.setTag(itemInfo);

        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(image, itemInfo.icon));
        rankValue.setText(String.valueOf(itemInfo.avgScore));
        tagTitle.setText(itemInfo.name);
        if (type == AppConstants.ItemType.USERLIKE_GAME_DETAIL) {
            exper.setGameBeans(itemInfo, true);
        } else {
            exper.setGameBeans(itemInfo, false);
        }
        setViewVisility(transport, itemInfo.isTransport);

        setViewVisility(active, itemInfo.isActive);

        tags.clear();
        tags.addAll(itemInfo.tags);
        mTagFlowAdapter.notifyDataChanged();
    }

    private void setViewVisility(View view, boolean visility) {
        if (visility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == itemView) {
            final GameBean bean = (GameBean) v.getTag();
            if (bean != null && bean.id >= 0) {
                RcmdDetailActivity.open(itemView.getContext(), String.valueOf(bean.id), bean.name);
            } else {
                ToastUtils.showShort("非法的游戏id");
            }
        }
    }

    @Override
    public boolean onItemSelected(int position, boolean isSeleted, GameBean.Tag item) {
        if(type != AppConstants.ItemType.SEARCH_GAME_LIST) {
            TagGameListActivity.open(itemView.getContext(), item.id, item.name);
        }
        return false;
    }
}
