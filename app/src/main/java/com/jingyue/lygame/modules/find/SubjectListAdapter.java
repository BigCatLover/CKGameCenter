package com.jingyue.lygame.modules.find;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.SubjectListBean;
import com.jingyue.lygame.widget.RoundImageView;
import com.lygame.libadapter.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class SubjectListAdapter extends RecyclerView.Adapter {

    private List<SubjectListBean> datas = new ArrayList<>();
    private Context context;

    public SubjectListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_list_adapter_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ViewHolder vh = (ViewHolder) holder;
        ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(vh.subjectIv, datas.get(position).img, R.drawable.ic_loading, R.mipmap.ic_load_err_land));
        vh.subjectIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectDetailActivity.start(context, datas.get(position).id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void notifyDataChanged(List<SubjectListBean> subjectListBeen, boolean isRefresh) {
        if (isRefresh) {
            datas.clear();
        }
        datas.addAll(subjectListBeen);
        notifyDataSetChanged();
    }

    public List<SubjectListBean> getDatas() {
        return datas;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.subject_iv)
        RoundImageView subjectIv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
