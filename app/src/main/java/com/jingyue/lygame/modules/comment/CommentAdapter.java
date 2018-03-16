package com.jingyue.lygame.modules.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.modules.comment.view.RcyCommentDetailHeadView;
import com.jingyue.lygame.modules.comment.view.RcyCommentItemView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/15.
 */
public class CommentAdapter extends RecyclerView.Adapter {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEAD = 2;

    private List<CommentListBean> datas = new ArrayList<>();
    private Context context;
    private GameBean gameBean;

    public CommentAdapter(Context context, GameBean gameBean) {
        this.context = context;
        this.gameBean = gameBean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEAD) {
            view = new RcyCommentDetailHeadView(context);
            return new HeadViewHolder(view);
        } else {
            view = new RcyCommentItemView(context);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadViewHolder) {
            final HeadViewHolder vh = (HeadViewHolder) holder;
            RcyCommentDetailHeadView iLayout = (RcyCommentDetailHeadView) vh.itemView;
            iLayout.setDatas(gameBean, datas.isEmpty());
        } else {
            final ViewHolder vh = (ViewHolder) holder;
            final int index = position - 1;
            RcyCommentItemView iLayout = (RcyCommentItemView) vh.itemView;
            iLayout.setDatas(gameBean, datas.get(index));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    public void notifyDataChanged(List<CommentListBean> commentBeen, boolean isRefresh) {
        if (commentBeen.isEmpty()) {
            return;
        }
        if (isRefresh) {
            datas.clear();
        }
        datas.addAll(commentBeen);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }

    static class HeadViewHolder extends RecyclerView.ViewHolder {

        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
