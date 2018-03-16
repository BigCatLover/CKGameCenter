package com.jingyue.lygame.modules.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.jingyue.lygame.bean.CommentCollectBean;
import com.jingyue.lygame.modules.comment.view.RcyCommentCollectView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanglei on 2017/9/16.
 */
public class CommentCollectAdapter extends RecyclerView.Adapter {
    private final List<CommentCollectBean> datas = new ArrayList<>();
    private static Context context;


    public CommentCollectAdapter(Context context) {
        CommentCollectAdapter.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = new RcyCommentCollectView(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        RcyCommentCollectView iLayout = (RcyCommentCollectView) vh.itemView;
        iLayout.setDatas(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void notifyDataChanged(List<CommentCollectBean> commentCollectBeen, boolean isRefresh) {
        if (isRefresh) {
            datas.clear();
        }
        datas.addAll(commentCollectBeen);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }
}
