package com.jingyue.lygame.modules.find;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jingyue.lygame.R;
import com.jingyue.lygame.bean.SubjectDetailBean;
import com.jingyue.lygame.constant.KeyConstant;
import com.jingyue.lygame.modules.rcmd.RcmdDetailActivity;
import com.jingyue.lygame.utils.SettingUtil;
import com.jingyue.lygame.utils.StringUtils;
import com.jingyue.lygame.utils.youkuPlayer.utils.YKListUtil;
import com.jingyue.lygame.widget.DownloadProgessView;
import com.lygame.libadapter.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglei on 2017/9/21.
 */
public class SubjectDetailAdapter extends RecyclerView.Adapter {
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEAD = 2;

    private List<SubjectDetailBean.ListBean> datas = new ArrayList<>();
    private Context context;
    private SubjectDetailBean.Subject subject;
    private YKListUtil ykListUtil;
    private SubjectDetailActivity.ListEventCallback mEventCallback;
    private SubjectDetailActivity.ListFullScreenEventCallback mFScreenEventCallback;
    private AudioManager mAudioManager;

    public SubjectDetailAdapter(Context context, YKListUtil ykListUtil, SubjectDetailActivity.ListEventCallback listEventCallback,
                                SubjectDetailActivity.ListFullScreenEventCallback fullScreenEventCallback,
                                SubjectDetailBean.Subject subject) {
        this.context = context;
        this.ykListUtil = ykListUtil;
        this.mEventCallback = listEventCallback;
        this.mFScreenEventCallback = fullScreenEventCallback;
        this.subject = subject;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEAD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_detail_head_layout, parent, false);
            return new HeadViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_detail_body_layout, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof HeadViewHolder) {
            final HeadViewHolder vh = (HeadViewHolder) holder;
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(vh.topicImg, subject.bigImg));
            vh.topicContent.setText(subject.description);
        } else {
            final VideoViewHolder vh = (VideoViewHolder) holder;
            final int index = position - 1;
            vh.description.setText(datas.get(index).description);
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(vh.gameIcon, datas.get(index).icon));
            vh.gameName.setText(datas.get(index).name);
            vh.avgScore.setText(datas.get(index).avgScore);
            if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
                vh.sound.setImageResource(R.mipmap.sound_open);
            } else {
                vh.sound.setImageResource(R.mipmap.sound_close);
            }
            vh.sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    if (mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, SettingUtil.readInt(context,
                                KeyConstant.KEY_VIDEO_VOICE_VALUE, max / 2), AudioManager.STREAM_MUSIC);
                        vh.sound.setImageResource(R.mipmap.sound_open);
                    } else {
                        vh.sound.setImageResource(R.mipmap.sound_close);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.STREAM_MUSIC);
                    }
                }
            });
            ImageLoader.getInstance().showImage(ImageLoader.getDefaultOptions(vh.cacheImage, datas.get(index).videoImg));
            if (StringUtils.isEmptyOrNull(datas.get(index).videoId)) {
                vh.ivPlay.setVisibility(View.GONE);
            } else {
                vh.ivPlay.setVisibility(View.VISIBLE);
            }
            vh.cacheImage.setOnClickListener(new StartListener(vh, position, datas.get(index).videoId));
            vh.ivPlay.setOnClickListener(new StartListener(vh, position, datas.get(index).videoId));
            vh.noConnectText.setOnClickListener(new StartListener(vh, position, datas.get(index).videoId));
            vh.fullscreeIv.setOnClickListener(new FullScreenListener(vh, position, datas.get(index).videoId, datas.get(index).videoImg));
            vh.sbVideoPlayer.setOnSeekBarChangeListener(new SeekProgressListener(vh, position, datas.get(index).videoId));
            vh.titleClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RcmdDetailActivity.open(context, datas.get(index).id, datas.get(index).name);
                }
            });
            vh.btnDownload.setGameBeans(datas.get(index));
        }

    }

    /**
     * 播放按钮监听
     */
    private class StartListener implements View.OnClickListener {
        private VideoViewHolder holder;
        private int position;
        private String url;


        public StartListener(VideoViewHolder holder, int position, String url) {
            this.holder = holder;
            this.position = position;
            this.url = url;
        }

        @Override
        public void onClick(View v) {
            mEventCallback.bindHolder(holder, position);
            ykListUtil.togglePlay(url, position, holder.baseviewLl, mEventCallback);
        }
    }

    /**
     * 全屏按钮监听
     */
    private class FullScreenListener implements View.OnClickListener {
        private VideoViewHolder holder;
        private int position;
        private String url;
        private String imgurl;

        public FullScreenListener(VideoViewHolder holder, int position, String url, String imgurl) {
            this.holder = holder;
            this.position = position;
            this.url = url;
            this.imgurl = imgurl;
        }

        @Override
        public void onClick(View v) {
            mEventCallback.bindHolder(holder, position);
            mFScreenEventCallback.setHolder(holder);
            ykListUtil.startFullScreen(url, position, holder.baseviewLl, mEventCallback, mFScreenEventCallback, imgurl);
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

    public void notifyDataChanged(List<SubjectDetailBean.ListBean> listBeen) {
//        int preDataSize = datas.size();
        if (!listBeen.isEmpty()) {
            datas.clear();
            datas.addAll(listBeen);
        }
        notifyDataSetChanged();
//        notifyItemRangeInserted(preDataSize,datas.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }

    static class HeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.topic_img)
        ImageView topicImg;
        @BindView(R.id.topic_content)
        TextView topicContent;

        HeadViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.baseview_ll)
        FrameLayout baseviewLl;
        @BindView(R.id.cache_image)
        ImageView cacheImage;
        @BindView(R.id.fullscreen_click)
        ImageView fullscreeIv;
        @BindView(R.id.tvVideoPlayerTotalTime)
        TextView tvTotalTime;
        @BindView(R.id.tvVideoPlayerCurrentTime)
        TextView tvCurrentTime;
        @BindView(R.id.sound)
        ImageView sound;
        @BindView(R.id.sbVideoPlayer)
        SeekBar sbVideoPlayer;
        @BindView(R.id.seekbarFrame)
        RelativeLayout seekbarFrame;
        @BindView(R.id.ivVideoPlayerPauseResume)
        ImageView ivPlay;
        @BindView(R.id.rlVideoPlayerControlFrame)
        RelativeLayout controlFrame;
        //        @BindView(R.id.ivVideoPlayerLoading)
//        ImageView ivLoading;
        @BindView(R.id.no_connect_text)
        TextView noConnectText;
        @BindView(R.id.flVideoPlayer)
        FrameLayout flVideoPlayer;
        @BindView(R.id.game_icon)
        ImageView gameIcon;
        @BindView(R.id.exper)
        DownloadProgessView btnDownload;
        @BindView(R.id.game_name)
        TextView gameName;
        @BindView(R.id.game_score)
        TextView gameScore;
        @BindView(R.id.avg_score)
        TextView avgScore;
        @BindView(R.id.title_click)
        RelativeLayout titleClick;
        @BindView(R.id.description)
        TextView description;

        VideoViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private class SeekProgressListener implements SeekBar.OnSeekBarChangeListener {

        private VideoViewHolder holder;
        private int position;
        private String url;

        public SeekProgressListener(VideoViewHolder holder, int position, String url) {
            this.holder = holder;
            this.position = position;
            this.url = url;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            mEventCallback.bindHolder(holder, position);

            ykListUtil.seekTo(url, position, holder.baseviewLl,
                    mEventCallback, holder.sbVideoPlayer.getProgress(), holder.sbVideoPlayer.getMax());
        }
    }
}
