package com.jingyue.lygame.modules.search;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.utils.SearchHistoryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/19.
 */
public class SearchHisAdaper extends RecyclerView.Adapter {
    private List<String> datas = new ArrayList<>();
    private Handler mainhandler;
    private Context mContext;

    public SearchHisAdaper(Context context, Handler handler) {
        this.mainhandler = handler;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        vh.searchHisText.setText(datas.get(position));
        vh.delIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> historys = (Map<String, String>) SearchHistoryUtils.getAll(mContext);
                for (Map.Entry<String, String> entry : historys.entrySet()) {
                    if (datas.get(position).equals(entry.getValue())) {
                        SearchHistoryUtils.remove(mContext, entry.getKey());
                    }
                }
                datas.remove(position);
                notifyDataSetChanged();
            }
        });
        vh.searchhisLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message s = new Message();
                s.what = SearchActivity.SHOW_SEARCH_RESULT;
                s.obj = datas.get(position);
                mainhandler.sendMessage(s);
            }
        });
    }

    public void setDatas(List<String> tipdatas) {
        datas = tipdatas;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.search_his_text)
        TextView searchHisText;
        @BindView(R.id.del_iv)
        ImageView delIv;
        @BindView(R.id.searchhis_ll)
        RelativeLayout searchhisLl;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
