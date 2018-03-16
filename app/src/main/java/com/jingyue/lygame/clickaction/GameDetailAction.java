package com.jingyue.lygame.clickaction;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-26 18:14
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class GameDetailAction extends LYGGameCenterAction {

    public GameDetailAction(String gameId) {
        a2().id(gameId).a1().details().a3().game();
    }
}


