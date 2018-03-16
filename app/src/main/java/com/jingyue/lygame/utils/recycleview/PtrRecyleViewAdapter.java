package com.jingyue.lygame.utils.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-11-16 16:09
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class PtrRecyleViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int TYPE_FOOTER = Integer.MIN_VALUE;
    private final List<T> data = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        //底部加载更多组件
        if(position == data.size()){
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }
}
