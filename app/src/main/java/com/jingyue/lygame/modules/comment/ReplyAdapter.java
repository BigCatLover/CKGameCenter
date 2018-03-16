package com.jingyue.lygame.modules.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jingyue.lygame.bean.CommentDetialBean;
import com.jingyue.lygame.bean.CommentListBean;
import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.modules.comment.view.RcyCommentItemWithGameTopView;
import com.jingyue.lygame.modules.comment.view.RcyReplyItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/15.
 */
public class ReplyAdapter extends RecyclerView.Adapter {

    private final List<CommentListBean.Comment> datas = new ArrayList<>();
    private CommentDetialBean commentDetailBeen;
    private static final int TYPE_ITEM = 0x002;
    private static final int TYPE_HEAD = 0x001;
    private GameBean gameBean;
    private Context context;

    public ReplyAdapter(GameBean gameBean, CommentDetialBean bean, Context context) {
        commentDetailBeen = bean;
        datas.addAll(bean.getReply());
        this.gameBean = gameBean;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEAD) {
            view = new RcyCommentItemWithGameTopView(context);
            return new HeadViewHolder(view);
        } else {
            view = new RcyReplyItemView(context);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            final HeadViewHolder vh = (HeadViewHolder) holder;
            RcyCommentItemWithGameTopView iLayout = (RcyCommentItemWithGameTopView) vh.itemView;
            iLayout.setDatas(gameBean, commentDetailBeen, datas.isEmpty());
        } else {
            final ViewHolder vh = (ViewHolder) holder;
            final int index = position - 1;
            RcyReplyItemView iLayout = (RcyReplyItemView) vh.itemView;
            iLayout.setDatas(datas.get(index));
        }

    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    public void notifyDataChanged(List<CommentListBean.Comment> replys, boolean isRefresh) {
        if (isRefresh) {
            datas.clear();
        }
        datas.addAll(replys);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEAD;
        } else {
            return TYPE_ITEM;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View view) {
            super(view);
        }
    }

    static class HeadViewHolder extends RecyclerView.ViewHolder {
        HeadViewHolder(View view) {
            super(view);
        }
    }
}
