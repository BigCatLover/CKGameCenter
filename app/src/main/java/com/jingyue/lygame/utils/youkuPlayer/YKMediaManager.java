package com.jingyue.lygame.utils.youkuPlayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import com.jingyue.lygame.utils.youkuPlayer.listener.YKMediaListener;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.youku.cloud.module.CustomInfo;
import com.youku.cloud.module.PlayerErrorInfo;
import com.youku.cloud.module.VideoInfo;
import com.youku.cloud.player.PlayerListener;
import com.youku.cloud.player.VideoDefinition;
import com.youku.cloud.player.YoukuPlayerView;

import java.lang.ref.WeakReference;

public class YKMediaManager extends PlayerListener {


    public static final String TAG = YKMediaManager.class.getSimpleName();

    //MediaHandler的消息
    public static final int MESSAGE_PREPARE = 0;        //准备播放
    public static final int MESSAGE_RELEASE = 1;        //释放资源
    public static final int MESSAGE_START = 2;
    public static final int MESSAGE_PAUSE = 3;
    public static final int MESSAGE_ERROR = 4;
    //单例
    public static YKMediaManager instance;
    public int currentVideoWidth = 0;
    public int currentVideoHeigth = 0;
    private YoukuPlayerView mediaPlayer;         //Ijk播放器
    private HandlerThread mMediaHandlerThread;  //创建播放器线程
    private MediaHandler mMediaHandler;         //播放器线程的Handler，用来操作播放器
    private Handler mainThreadHandler;          //主线程的Handler，用来刷新UI
    private WeakReference<YKMediaListener> listener;
    private Context mContext;
    private String vid;


    private YKMediaManager(Context context) {
        mContext = context;
        mediaPlayer = new YoukuPlayerView(context);
        mMediaHandlerThread = new HandlerThread(TAG);
        mMediaHandlerThread.start();
        mMediaHandler = new MediaHandler(mMediaHandlerThread.getLooper());
        mainThreadHandler = new Handler();
    }

    public static synchronized YKMediaManager getInstance(Context context) {
        if (instance == null) {
            instance = new YKMediaManager(context);
        }
        return instance;
    }

    public void release() {
        if (instance != null) {
            instance = null;
        }
    }

    @Override
    public void onAdBegin(final int index) {
        LogUtils.e(TAG, "onAdBegin");
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onAdBegin(index);
                }
            }
        });
    }

    @Override
    public void onAdCountUpdate(int second) {
        LogUtils.e(TAG, "onAdCountUpdate:" + second);
    }

    @Override
    public void onAdEnd(final int index) {
        LogUtils.e(TAG, "onAdEnd");
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onAdEnd(index);
                }
            }
        });
    }

    @Override
    public void OnCurrentPositionChanged(int msec) {
        super.OnCurrentPositionChanged(msec);
//        LogUtils.e(TAG,"OnCurrentPositionChanged:"+msec);
    }

    @Override
    public void needPay(String vid) {
        LogUtils.e(TAG, "needPay:");
    }

    @Override
    public void onBufferingUpdate(final int percent) {
        LogUtils.e(TAG, "onBufferingUpdate:" + percent);
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onBufferingUpdate(percent);
                }
            }
        });
    }

    @Override
    public void onComplete() {
        LogUtils.e(TAG, "onComplete:");
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onComplete();
                }
            }
        });
    }

    @Override
    public void onError(final int code, final PlayerErrorInfo info) {
        LogUtils.e(TAG, "err:" + info.getDesc() + " code:" + code);
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onError(code, info.getDesc());
                }
            }
        });
    }

    @Override
    public void onLoaded() {
        LogUtils.e(TAG, "onLoaded");
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onLoaded();
                }
            }
        });
    }

    @Override
    public void onLoading() {
        LogUtils.e(TAG, "onLoading ");
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onLoading();
                }
            }
        });
    }

    @Override
    public void onNetSpeedChanged(int count) {
    }

    @Override
    public void onPrepared() {
        LogUtils.e(TAG, "onPrepared");
    }

    @Override
    public void onRealVideoStart() {
        LogUtils.e(TAG, "onRealVideoStart");
        if (null != mediaPlayer) {
            mediaPlayer.hideControllerView();
            mediaPlayer.setUseOrientation(false);
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        getListener().onRealVideoStart();
                    }
                }
            });
        }
    }

    @Override
    public void onSeekComplete() {
        LogUtils.e(TAG, "onSeekComplete");
    }

    @Override
    public void onStartRenderVideo() {
        LogUtils.e(TAG, "onStartRenderVideo");
    }

    @Override
    public void onTimeout() {
        LogUtils.e(TAG, "onTimeout");
    }

    @Override
    public void onVideoDefinitionChanged() {
        LogUtils.e(TAG, "onVideoDefinitionChanged");
    }

    @Override
    public void onVideoInfoGetted(boolean arg0, VideoInfo videoInfo) {
        LogUtils.e(TAG, "onVideoInfoGetted");
    }

    @Override
    public void onVideoNeedPassword(int code) {
        LogUtils.e(TAG, "onVideoNeedPassword");
        super.onVideoNeedPassword(code);
    }

    @Override
    public void onVideoSizeChanged(final int width, final int height) {
        LogUtils.e(TAG, "------onVideoSizeChanged:------:" + width + "------h:" + height + "------");

        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    getListener().onVideoSizeChanged(width, height);
                }
            }
        });
        super.onVideoSizeChanged(width, height);
    }

    @Override
    public void onCustomInfoGetted(CustomInfo customInfo) {
        LogUtils.e(TAG, "onCustomInfoGetted");
    }

    public YKMediaListener getListener() {
        if (listener == null) {
            return null;
        }
        return listener.get();
    }
    //**********↑↑↑↑↑↑↑↑↑↑ --IMediaPlayer Listeners override methods end-- ↑↑↑↑↑↑↑↑↑↑**********

    public void setListener(YKMediaListener listener) {
        if (listener == null) {
            this.listener = null;
        } else {
            this.listener = new WeakReference<YKMediaListener>(listener);
        }
    }

    public YoukuPlayerView getMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new YoukuPlayerView(mContext);
        }
        return mediaPlayer;
    }

    /**
     * 通知Media线程，准备播放器
     *
     * @param url 播放地址
     */
    public void prepare(String url) {
        if (!TextUtils.isEmpty(url)) {
            mMediaHandler.obtainMessage(MESSAGE_PREPARE, url).sendToTarget();
        } else {
            mMediaHandler.obtainMessage(MESSAGE_ERROR).sendToTarget();
        }

    }


    /**
     * 通知Media线程，释放播放器资源
     */
    public void releaseMediaPlayer() {
        mMediaHandler.obtainMessage(MESSAGE_RELEASE).sendToTarget();
    }

    /**
     * 获取视频尺寸
     *
     * @return 视频的宽和高
     */
    public Point getVideoSize() {
        if (currentVideoWidth != 0 && currentVideoHeigth != 0) {
            return new Point(currentVideoWidth, currentVideoHeigth);
        } else {
            return null;
        }
    }

    public boolean isPlaying() {
        boolean flag = false;
        if (mediaPlayer != null) {
            flag = mediaPlayer.isPlaying();
        }
        return flag;
    }

    /**
     * 开始播放
     */
    public void startPlayer() {
        mMediaHandler.obtainMessage(MESSAGE_START).sendToTarget();
    }

    /**
     * 暂停播放
     */
    public void pausePlayer() {
        mMediaHandler.obtainMessage(MESSAGE_PAUSE).sendToTarget();
    }

    /**
     * MediaHandler在media线程工作，用于播放器相关的操作，与主线程分离
     */
    private class MediaHandler extends Handler {

        public MediaHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                //准备播放器
                case MESSAGE_PREPARE:
                    vid = (String) msg.obj;
                    if (mediaPlayer == null) {
                        mediaPlayer = new YoukuPlayerView(mContext);
                    }

                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.attachActivity((Activity) mContext);
                            mediaPlayer.setPreferVideoDefinition(VideoDefinition.VIDEO_HD);  // 设置播放器清晰度
                            mediaPlayer.setShowBackBtn(false);     // 设置返回按钮显示
                            mediaPlayer.setShowFullBtn(false);     // 设置全屏按钮显示
                            mediaPlayer.hideControllerView();
//                    mediaPlayer.hideSystemUI();           // 隐藏播放器的ui
                            mediaPlayer.setUseOrientation(false); // 禁用播放器本身的横竖屏设置
                            mediaPlayer.setPlayerListener(YKMediaManager.this);
                            mediaPlayer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (listener != null) {
                                        getListener().controlFrame();
                                    }
                                }
                            });
                            mediaPlayer.playYoukuVideo(vid);
                            if (listener != null) {
                                getListener().playPrepare();
                            }
                        }
                    });
                    break;
                //释放
                case MESSAGE_RELEASE:
//                    Log.d(TAG, "MediaHandler: MESSAGE_RELEASE");
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mediaPlayer) {
                                mediaPlayer.release();
                                mediaPlayer.onDestroy();
                            }
                        }
                    });
                    break;
                //开始
                case MESSAGE_START:
                    if (mediaPlayer != null) {
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mediaPlayer.onResume();
                                if (listener != null) {
                                    getListener().onPlayStart();
                                }
                            }
                        });
                    }
                    break;
                case MESSAGE_ERROR:
                    mainThreadHandler.postAtFrontOfQueue(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                getListener().showErrorDialog();
                            }
                            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                            }
                        }
                    });
                    break;
                //暂停
                case MESSAGE_PAUSE:
                    if (mediaPlayer != null && listener != null) {
                        synchronized (listener) {
                            mainThreadHandler.postAtFrontOfQueue(new Runnable() {
                                @Override
                                public void run() {
                                    mediaPlayer.pause();
                                    getListener().onPlayPause();
                                }
                            });
                        }
                    }

                    break;
            }
        }
    }
}
