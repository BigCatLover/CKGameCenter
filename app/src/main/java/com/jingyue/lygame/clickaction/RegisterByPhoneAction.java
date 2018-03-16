package com.jingyue.lygame.clickaction;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-27 17:52
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class RegisterByPhoneAction extends LYGGameCenterAction {

    public RegisterByPhoneAction(boolean sucess) {
        a1().register().a3().withValue("1").a4().withValue(sucess ? "1" : "2");
    }
}
