package com.jingyue.lygame.clickaction;

import com.jingyue.lygame.constant.AppConstants;

/**
 * Created by zhanglei on 2017/10/27.
 */
public class ScreenAction extends LYGGameCenterAction {
    public ScreenAction() {
        a1().screen();
    }

    @Override
    public String getEventId() {
        return AppConstants.EID;
    }
}
