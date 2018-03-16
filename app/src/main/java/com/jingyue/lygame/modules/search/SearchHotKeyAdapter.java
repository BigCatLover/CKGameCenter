package com.jingyue.lygame.modules.search;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
 * Created by zhanglei on 2017/9/19.
 */
public class SearchHotKeyAdapter extends RecyclerView.Adapter {
    private List<String> datas = new ArrayList<>();
    private Handler mainhandler;
    private Context mContext;

    public SearchHotKeyAdapter(Context context, Handler handler) {
        this.mainhandler = handler;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_hotkey_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        vh.index.setText(mContext.getString(R.string.a_0299,position+1));
        vh.hotkey.setText(datas.get(position));
        vh.hotgameLl.setOnClickListener(new View.OnClickListener() {
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
        @BindView(R.id.index)
        TextView index;
        @BindView(R.id.hotkey)
        TextView hotkey;
        @BindView(R.id.hotgame_ll)
        RelativeLayout hotgameLl;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
