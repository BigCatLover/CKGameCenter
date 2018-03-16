package com.jingyue.lygame.clickaction;

import android.content.Context;

import com.jingyue.lygame.clickaction.internal.BaseSelfCountAction;
import com.jingyue.lygame.constant.AppConstants;
import com.laoyuegou.android.lib.utils.Utils;

import java.util.HashMap;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-26 18:09
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class LYGGameCenterAction extends BaseSelfCountAction {

    @Override
    public String getEventId() {
        return AppConstants.EID;
    }

    private A1 a1;
    private A2 a2;
    private A3 a3;
    private A4 a4;
    private A5 a8;

    public LYGGameCenterAction(Context context) {
        super(context);
    }

    public LYGGameCenterAction() {
        super(Utils.getContext());
    }

    public A1 a1() {
        if (a1 == null) {
            a1 = new A1();
        }
        return a1;
    }

    public A2 a2() {
        if (a2 == null) {
            a2 = new A2();
        }
        return a2;
    }

    public A3 a3() {
        if (a3 == null) {
            a3 = new A3();
        }
        return a3;
    }

    public A4 a4() {
        if (a4 == null) {
            a4 = new A4();
        }
        return a4;
    }

    public LYGGameCenterAction.A5 a8() {
        if (a8 == null) {
            a8 = new A5();
        }
        return a8;
    }

    /**
     * 事件的名字
     */
    public class A1 {
        /**
         * screen
         *
         * @return
         */
        public LYGGameCenterAction screen() {
            params.put(A1, "screen");
            return LYGGameCenterAction.this;
        }

        /**
         * 游戏详情
         *
         * @return
         */
        public LYGGameCenterAction details() {
            params.put(A1, "details");
            return LYGGameCenterAction.this;
        }

        /**
         * 下載按鈕
         *
         * @return
         */
        public LYGGameCenterAction loginscreen() {
            params.put(A1, "loginscreen");
            return LYGGameCenterAction.this;
        }

        /**
         * 注册
         *
         * @return
         */
        public LYGGameCenterAction register() {
            params.put(A1, "register");
            return LYGGameCenterAction.this;
        }

        /**
         * 获取验证码
         * @return
         */
        public LYGGameCenterAction button() {
            params.put(A1, "button");
            return LYGGameCenterAction.this;
        }
        /**
         * 搜索
         * @return
         */
        public LYGGameCenterAction search() {
            params.put(A1, "search");
            return LYGGameCenterAction.this;
        }
    }

    /**
     * 主要用于传递 相关的id
     */
    public class A2 {
        public LYGGameCenterAction id(String id) {
            params.put(A2, id);
            return LYGGameCenterAction.this;
        }
    }

    public class A3 {
        public LYGGameCenterAction recommond() {
            params.put(A3, "recommond");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction find() {
            params.put(A3, "find");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction comments() {
            params.put(A3, "comments");
            return LYGGameCenterAction.this;
        }

        public LYGGameCenterAction game() {
            params.put(A3, "game");
            return LYGGameCenterAction.this;
        }

        public LYGGameCenterAction label() {
            params.put(A3, "label");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction reply() {
            params.put(A3, "reply");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction comment1() {
            params.put(A3, "comment1");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction comment2() {
            params.put(A3, "comment2");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction like() {
            params.put(A3, "like");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction random() {
            params.put(A3, "random");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction download() {
            params.put(A3, "download");
            return LYGGameCenterAction.this;
        }
        public LYGGameCenterAction sub() {
            params.put(A3, "sub");
            return LYGGameCenterAction.this;
        }

        public LYGGameCenterAction withValue(String event) {
            params.put(A3, event);
            return LYGGameCenterAction.this;
        }
    }

    public class A4 {
        public LYGGameCenterAction withValue(String event) {
            params.put(A4, event);
            return LYGGameCenterAction.this;
        }
    }

    public class A5 {
        public LYGGameCenterAction withName(String event) {
            params.put(A5, event);
            return LYGGameCenterAction.this;
        }
    }
}
