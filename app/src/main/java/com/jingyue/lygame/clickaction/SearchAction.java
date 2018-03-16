package com.jingyue.lygame.clickaction;

/**
 * Created by zhanglei on 2017/10/27.
 */
public class SearchAction extends LYGGameCenterAction {
    /**
     * @param a3 搜索内容
     */
    public SearchAction(String a3) {
        a1().loginscreen().a3().withValue(a3);
    }
}
