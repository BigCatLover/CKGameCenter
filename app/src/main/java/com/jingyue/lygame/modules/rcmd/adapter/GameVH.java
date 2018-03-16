package com.jingyue.lygame.modules.rcmd.adapter;

import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.internal.BaseItem;
import com.jingyue.lygame.modules.rcmd.RcmdDetailActivity;
import com.laoyuegou.android.lib.utils.DisplayUtils;
import com.lygame.libadapter.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-14 16:13
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class GameVH extends BaseItem.BVH<GameBean> implements View.OnClickListener {

    @BindView(R.id.relate_game_icon)
    ImageView relateGameIcon;
    @BindView(R.id.relate_game_name)
    TextView relateGameName;
    @BindView(R.id.relate_game_rank)
    TextView relateGameRank;

    public GameVH(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final GameBean item = (GameBean) v.getTag();
        if(item != null){
            RcmdDetailActivity.open(v.getContext(),String.valueOf(item.id),item.name);
        }
    }

    @Override
    public void onBindViewHolder(GameBean itemInfo, int position) {
        itemView.setTag(itemInfo);
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(relateGameIcon, itemInfo.icon));
        relateGameName.setText(String.valueOf(itemInfo.name));
        String avgStr = String.valueOf(itemInfo.avgScore);
        Spannable spannable = new SpannableString(itemView.getContext().getString(R.string.a_0281) + avgStr);
        spannable.setSpan(new ForegroundColorSpan(
                        ResourcesCompat.getColor(
                                itemView.getResources(),
                                R.color.color_sub_text,
                                itemView.getContext().getTheme())),
                0, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(
                        ResourcesCompat.getColor(
                                itemView.getResources(),
                                R.color.colorAccent,
                                itemView.getContext().getTheme())),
                3, 3 + avgStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new AbsoluteSizeSpan(DisplayUtils.dip2px(itemView.getContext(),15)),3,3+avgStr.length(),Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        relateGameRank.setText(spannable);
    }

}
