package com.jingyue.lygame.constant;

import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.utils.FileUtils;
import com.laoyuegou.android.lib.utils.Utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-18 10:19
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class AppConstants {

    public static boolean ALLOW_TAG_CLICK = true;

    /**
     * 初始化参数
     */
    public static final String ACTION_STARTED = "com.laoyuegou.action.started";
    public static final String ACTION_INITED = "com.laoyuegou.action.inited";
    public static final String PRO_DIR = "lyGameCenter";
    public static boolean isInited = false;
    public final static String APP_SECRET = "lyg#100#andapp";

    /**
     * 限制高度收缩的 TextView 默认显示行高
     */
    public static final int DEFAULT_LINE_COUNT = 4;

    /**
     * 事件id
     */
    public static final String EID = BuildConfig.BI_EID;

    /**
     * 头像存储路径
     */
    public static final String AVATER_DEFAULT_DIR = Environment.getExternalStorageDirectory().toString()
            + "/cache";
    public static final String AVATER_DEFAULT_PATH = AVATER_DEFAULT_DIR + File.separator + "default.jpg";
    public static final String LAST_SCHEMA = ".jpg";

    public static String getSavePath(String url) {
        String name;
        if (TextUtils.isEmpty(url)) {
            name = String.valueOf(System.currentTimeMillis());
        } else {
            name = Uri.parse(url).getLastPathSegment();
        }
        return Utils.getContext().getExternalFilesDir("Download") +
                File.separator +
                name +
                (name.indexOf(".") != -1 ? "" : LAST_SCHEMA);
    }

    /**
     * App 退出时间
     */
    public static final int EXIT_ALLOW_TIME = 800;
    /**
     * 默认长动画持续时间
     */
    public static final int DEFAULT_LONG_ANIMATION_TIME = 800;

    /**
     * 默认适中动画持续时间
     */
    public static final int DEFAULT_ANIMATION_TIME = 500;
    /**
     * 默认上拉加载更新UI时间
     */
    public static final int DEFAULT_UPDATEUI_TIME = 1000;

    /**
     * 默认短动画持续时间
     */
    public static final int DEFAULT_SHORT_ANIMATION_TIME = 300;

    /**
     * 最大默认选择表签数
     */
    public static final int MAX_SELECTED_TAGS = 3;
    public static final int MAX_LOGIN_HISTORY_SIZE = 5;

    /**
     * 默认的APP更新的notificationId
     */
    public static final int APP_UPDATE_NOTIFICATIONID = 0x3;

    /**
     * 触发snap滑动事件的阈值
     */
    public static final float HANDLER_FATE = 0.3f;
    public static final long DEFAULT_CLICK_DELAY_TIME = TimeUnit.SECONDS.toMillis(1);

    /**
     * 默认缓存时间
     */
    public static long DEFAULT_CACHE_TIME = TimeUnit.DAYS.toMillis(1);
    /**
     * 推荐页
     */
    public final static String NAME_MAIN_RCMD = "main_rcmd";
    /**
     * 发现
     */
    public final static String NAME_MAIN_FIND = "main_find";

    /**
     * 游评会
     */
    public final static String NAME_MAIN_GAMECOMMENT = "main_game_comment";

    /**
     * 底部导航
     */
    public final static String NAME_BOTTOM_NAVIGATE = "bottom_navigate";


    /**
     * 分享来源
     */
    public static final class SHARE_FROM {
        /**
         * 推荐页
         */
        public final static String RCMD = "mainpage/comments";
        /**
         * 发现页
         */
        public final static String FIND = "mainpage/find";
        /**
         * 游评会
         */
        public final static String COMMENT = "mainpage/gamecomments";
        /**
         * 游戏详情页
         */
        public final static String DETAIL = "gamedetailmain";
        /**
         * 专题详情页
         */
        public final static String GAMETHEME_PAGE = "gamethemepage";
    }

    /**
     * webview上下文
     */
    public static final String JS_CONTEXT = "lygame";
    public static final String WEB_APP = "web_app";

    /**
     * 商务要求当前版本不加引导页
     * 10月23号应商务要求重新把引导页加回来
     */
    public static final boolean IGNORE_GUIDE = false;

    public static class ItemType {
        //相关游戏
        public static final int GAME_DETAIL = 0x1;
        //商店图1
        public static final int SHOP_IMAGE_DEFAULT = 0x2;
        //商店图2
        public static final int SHOP_IMAGE_2 = 0x3;
        //商店图3
        public static final int SHOP_IMAGE_3 = 0x4;
        //游戏标签
        public static final int GAME_TAG_LIST = 0x5;
        //用户游戏
        public static final int USERLIKE_GAME_DETAIL = 0x6;
        //搜索列表
        public static final int SEARCH_GAME_LIST = 0x7;
    }

    /**
     * Share platform
     */
    public static class SHARE_PLATFORM {
        public static final int All = 0;
        public static final int WECHAT_MOMENTS = 5;
        public static final int WECHAT = 4;
        public static final int SINAWEIBO = 3;
        public static final int QQ = 1;
        public static final int QZONE = 2;
    }
}
