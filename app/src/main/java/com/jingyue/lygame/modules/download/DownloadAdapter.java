package com.jingyue.lygame.modules.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.DownloadBean;
import com.jingyue.lygame.events.DownStatusChangeEvent;
import com.jingyue.lygame.modules.rcmd.RcmdDetailActivity;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.download.TasksManager;
import com.jingyue.lygame.widget.DownloadProgessView;
import com.laoyuegou.android.lib.utils.EBus;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.lygame.libadapter.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/12/7.
 */
public class DownloadAdapter extends RecyclerView.Adapter {
    private Context context;

    private List<DownloadBean> datas = new ArrayList<>();

    public DownloadAdapter(Context context,List<DownloadBean> datas) {
        this.context=context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_center_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder vh = (ViewHolder) holder;
        vh.gamename.setText(datas.get(position).getGameName());
        vh.rlDownItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vh.llDelete.getVisibility() == View.VISIBLE) {
                    vh.llDelete.setVisibility(View.GONE);
                } else {
                    vh.llDelete.setVisibility(View.VISIBLE);
                }
            }
        });
        vh.llDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasksManager.getImpl().deleteTaskByModel(datas.get(position));
                EBus.getDefault().post(new DownStatusChangeEvent(datas.get(position).getId(), datas.get(position).getGameId()));
                datas.remove(position);
                notifyDataSetChanged();
            }
        });
        vh.ivGameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RcmdDetailActivity.open(v.getContext(), datas.get(position).getGameId(), datas.get(position).getGameName());
            }
        });
        if (!StringUtils.isEmptyOrNull(datas.get(position).getGameIcon())) {
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(vh.ivGameIcon, datas.get(position).getGameIcon()));
        }
        vh.total.setText(StringUtils.optString(datas.get(position).getGameSize()));
        vh.llBtn.setGameId(datas.get(position).getGameId(), new FileDownloadSampleListener() {

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                vh.llBtn.updateProgress(soFarBytes, totalBytes, false);
                if(vh.total.getText().toString().trim().equals(context.getString(R.string.a_0155))){
                    vh.total.setText(StringUtils.formateSize(totalBytes));
                    datas.get(position).setGameSize(StringUtils.formateSize(totalBytes));
                    TasksManager.getImpl().updateTask(datas.get(position));
                }
                vh.speedInfo.setVisibility(View.VISIBLE);
                vh.sofar.setText(StringUtils.formateSize(soFarBytes));
                vh.speed.setText(context.getString(R.string.a_0305, StringUtils.formateSpeed(task.getSpeed()),
                        StringUtils.getRemainTime(totalBytes, soFarBytes, task.getSpeed())));
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                datas.remove(position);
                notifyDataSetChanged();
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                vh.llBtn.updateProgress(soFarBytes, totalBytes, true);
                vh.speedInfo.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_btn)
        DownloadProgessView llBtn;
        @BindView(R.id.iv_game_icon)
        ImageView ivGameIcon;
        @BindView(R.id.gamename)
        TextView gamename;
        @BindView(R.id.sofar)
        TextView sofar;
        @BindView(R.id.total)
        TextView total;
        @BindView(R.id.rl_down_item)
        RelativeLayout rlDownItem;
        @BindView(R.id.info_content)
        RelativeLayout infoContent;
        @BindView(R.id.speed)
        TextView speed;
        @BindView(R.id.speed_info)
        RelativeLayout speedInfo;
        @BindView(R.id.ll_delete)
        LinearLayout llDelete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
