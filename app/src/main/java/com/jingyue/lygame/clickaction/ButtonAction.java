package com.jingyue.lygame.clickaction;

/**
 * Created by zhanglei on 2017/10/27.
 */
public class ButtonAction extends LYGGameCenterAction {
    /**
     * @param a2 appid
     */
    public ButtonAction(String a2) {
        a1().button().a2().id(a2);
    }
}
