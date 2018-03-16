package com.jingyue.lygame.modules.rcmd.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.modules.rcmd.TagActivity;
import com.jingyue.lygame.modules.rcmd.TagGameListActivity;
import com.jingyue.lygame.widget.tagflow.FlowLayout;
import com.jingyue.lygame.widget.tagflow.TagAdapter;

import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-18 21:16
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TagFlowAdapter extends TagAdapter<GameBean.Tag> {

    private boolean isClickable = true;
    private boolean isCanDoSelect = true;
    private boolean isSelectedStateMode = false;

    private String appId;

    public TagFlowAdapter setCanDoSelect(boolean canDoSelect) {
        isCanDoSelect = canDoSelect;
        return this;
    }

    public TagFlowAdapter setSelectedState(boolean selectedState) {
        this.isSelectedStateMode = selectedState;
        return this;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public TagFlowAdapter(List<GameBean.Tag> datas, String appId) {
        super(datas);
        this.appId = appId;
    }

    public TagFlowAdapter(List<GameBean.Tag> datas) {
        super(datas);
    }

    /**
     * 生成一个 + 标签
     *
     * @return
     */
    public static GameBean.Tag generateAddTag(String gameName) {
        GameBean.Tag.AddTag mAddTag = new GameBean.Tag.AddTag();
        mAddTag.name = "  +  ";
        mAddTag.id = "-1";
        mAddTag.gameName = gameName;
        return mAddTag;
    }

    private OnItemSelectListener mOnItemSelectListener;

    public void setOnClickListener(OnItemSelectListener onItemSelectListener) {
        mOnItemSelectListener = onItemSelectListener;
    }

    public void onTagSet(View tag, GameBean.Tag tag1) {
    }

    /**
     * 这里不做视图缓存是因为tag会全屏显示，不会走recycleview的缓存逻辑。
     *
     * @param parent
     * @param position
     * @param tag
     * @return
     */
    @Override
    public View getView(final FlowLayout parent, final int position, final GameBean.Tag tag) {
        final TextView tagView = makeView(parent.getContext(), parent, tag);
        //final TextView tagView = (TextView) LayoutInflater.from().inflate(R.layout.view_tag_item, parent, false);
        onTagSet(tagView, tag);

        if (!isClickable) {
            tagView.setEnabled(false);
            tagView.setClickable(false);
        }

//        final GameBean.Tag tagBean = getItem(position);

//        tagView.setText(tag.name);
        tagView.setTag(position);

        if (isCanDoSelect || isSelectedStateMode) {
            if (tag.isSelected()) {
                tagView.setBackgroundResource(R.drawable.common_accent_bg_default);
                tagView.setTextColor(ResourcesCompat.getColor(tagView.getResources(), R.color.colorWhite, tagView.getContext().getTheme()));
            } else {
                tagView.setBackgroundResource(R.drawable.common_stroke_accent_bg_default);
                tagView.setTextColor(ResourcesCompat.getColor(tagView.getResources(), R.color.colorAccent, tagView.getContext().getTheme()));
            }
            if (tag.isSelected()) {
                tagView.setSelected(true);
            }
        }

        //非 + 号
        if (isCanDoSelect) {
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //选中是否需要改变tag状态，默认为false
                    boolean canSelected = false;
                    v.setSelected(!v.isSelected());
                    if (mOnItemSelectListener != null) {
                        tag.setIsSelected(v.isSelected());
                        canSelected = mOnItemSelectListener.onItemSelected(position, v.isSelected(), tag);
                    }
                    if (!canSelected) return;
                    final TextView textView = (TextView) v;
                    if (v.isSelected()) {
                        textView.setBackgroundResource(R.drawable.common_accent_bg_default);
                        textView.setTextColor(ResourcesCompat.getColor(v.getResources(), R.color.colorWhite, v.getContext().getTheme()));
                    } else {
                        textView.setBackgroundResource(R.drawable.common_stroke_accent_bg_default);
                        textView.setTextColor(ResourcesCompat.getColor(v.getResources(), R.color.colorAccent, v.getContext().getTheme()));
                    }
                }
            });

        } else {
            //点击 + 符号
            tagView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("-1".equals(tag.id)) {
                        String gameName = "";
                        if (tag instanceof GameBean.Tag.AddTag) {
                            gameName = ((GameBean.Tag.AddTag) tag).gameName;
                        }
                        TagActivity.open(v.getContext(), appId, gameName);
                    } else {
                        TagGameListActivity.open(v.getContext(), tag.id, tag.name);
                    }
                }
            });
        }
        return tagView;
    }

    public TextView makeView(Context context, ViewGroup container, GameBean.Tag tag) {
        final TextView tagView = (TextView) LayoutInflater.from(context).inflate(R.layout.view_tag_item, container, false);
        onTagSet(tagView, tag);
        tagView.setText(tag.name);
        return tagView;
    }

    public interface OnItemSelectListener {
        boolean onItemSelected(int position, boolean isSeleted, GameBean.Tag item);
    }

}
