package com.jingyue.lygame.utils.youkuPlayer;

import android.app.Activity;
import android.view.View;

import com.youku.cloud.player.VideoDefinition;
import com.youku.cloud.player.YoukuPlayerView;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-31 19:00
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class YKPlayUtils {

    public static void initPlayer(YoukuPlayerView mediaPlayer){
        if(mediaPlayer == null){
            return;
        }
        mediaPlayer.attachActivity((Activity) mediaPlayer.getContext());
        mediaPlayer.setPreferVideoDefinition(VideoDefinition.VIDEO_HD);  // 设置播放器清晰度
        mediaPlayer.setShowBackBtn(false);      // 设置返回按钮显示
        mediaPlayer.setShowFullBtn(false);      // 设置全屏按钮显示
        mediaPlayer.hideControllerView();
        //mediaPlayer.hideSystemUI();           // 隐藏播放器的ui
        mediaPlayer.setUseOrientation(true);    // 禁用播放器本身的横竖屏设置
    }
}
