package com.jingyue.lygame.clickaction;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-27 17:52
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class RegisterByUserNameAction extends LYGGameCenterAction {

    public RegisterByUserNameAction(boolean sucess) {
        a1().register().a3().withValue("2").a4().withValue(sucess ? "1" : "2");
    }
}
