package com.jingyue.lygame.modules.search;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/18.
 */
public class TipWordsAdapter extends RecyclerView.Adapter {

    private List<String> datas = new ArrayList<>();
    private ClickListener mListener;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_keywords_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        vh.searchTipText.setText(datas.get(position));
        vh.itemLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.search(datas.get(position));
                }
            }
        });
    }

    public void setDatas(List<String> tipdatas) {
        datas = tipdatas;
        notifyDataSetChanged();
    }

    public void setOnClickListener(ClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_tip_text)
        TextView searchTipText;
        @BindView(R.id.item_ll)
        RelativeLayout itemLl;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface ClickListener {
        void search(String search);
    }
}
