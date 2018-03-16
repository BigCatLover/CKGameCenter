package com.jingyue.lygame.modules.comment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/11.
 */

public class AddCommentAdapter extends RecyclerView.Adapter {
    List<CommentListBean.Comment> datas = new ArrayList<>();

    public void setDatas(List<CommentListBean.Comment> mDatas) {
        this.datas = mDatas;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_comment_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        vh.addedContent.setText(StringUtils.formatString(datas.get(position).content));
        if(datas.get(position).content.length()>84){
            vh.showAll.setVisibility(View.VISIBLE);
            vh.showAll.setText(R.string.a_0135);
            vh.showAll.setOnClickListener(new View.OnClickListener() {
                boolean flag = true;

                @Override
                public void onClick(View v) {
                    if (flag) {
                        vh.addedContent.setMaxLines(100);
                        flag = false;
                        vh.showAll.setText(R.string.a_0136);
                    } else {
                        vh.addedContent.setMaxLines(3);
                        flag = true;
                        vh.showAll.setText(R.string.a_0135);
                    }
                }
            });
        }else {
            vh.showAll.setVisibility(View.GONE);
        }
        vh.addedTime.setText(StringUtils.generateTime(datas.get(position).create_time,true));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.added_time)
        TextView addedTime;
        @BindView(R.id.added_content)
        TextView addedContent;
        @BindView(R.id.show_all)
        TextView showAll;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
