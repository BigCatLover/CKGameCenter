package com.jingyue.lygame.events;

import com.jingyue.lygame.bean.GameBean;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-27 23:46
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TagChangeEvent {
    public GameBean.Tag tag;
    public int opertation;
    //影响的游戏
    private int gameId;

    public boolean isCurrentGameTagChaneg(int gameid){
        return this.gameId == gameid;
    }

    public static final int OP_SELECT = 0x1;
    public static final int OP_UNSELECT = 0x2;

    public TagChangeEvent() {}

    public TagChangeEvent(GameBean.Tag tag,int gameId, int opertation) {
        this.gameId = gameId;
        this.tag = tag;
        this.opertation = opertation;
    }
}
