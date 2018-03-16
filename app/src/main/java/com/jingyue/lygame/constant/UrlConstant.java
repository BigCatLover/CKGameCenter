package com.jingyue.lygame.constant;

import android.text.TextUtils;

import com.jingyue.lygame.BuildConfig;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-8-17 16:29
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 * <p>
 * keyUrl 相关静态类
 */
public class UrlConstant {

    public static final String BI_URL = BuildConfig.BI_URL;
    public static final String BASE_H5_URL = BuildConfig.BASE_H5_URL;
    public static final String BASE_H5_URL2 = BuildConfig.BASE_H5_URL2;
    public static final String BASE_URL = BuildConfig.BASE_URL;
    public static final String APP_URL_SUFFIX = "/appapi/public/index.php/v1";
    public static final String SHARE_URL_SUFFIX = "/dao/dist/gamecenter/#/";

    /**
     * 忘记密码
     */
    public static final String FORGET_PWD=String.format("%s/float.php/Mobile/Forgetpwd/index",BASE_H5_URL2);

    /**
     * 客服中心
     */
    public static final String HELP_INDEX=String.format("%s/float.php/Mobile/Help/index",BASE_H5_URL2);
    public static final String USER_POINTS=String.format("%s/float.php/mobile/score/index.html",BASE_H5_URL2);
    /**
     * 密码修改
     */
    public static final String UPDATE_PWD=String.format("%s/float.php/Mobile/Password/uppwd",BASE_H5_URL2);
    /**
     * 密保邮箱
     */
    public static final String SECURITY_EMAIL=String.format("%s/float.php/Mobile/Security/email",BASE_H5_URL2);

    /**
     * 密保手机
     */
    public static final String SECURITY_MOBILE=String.format("%s/float.php/Mobile/Security/mobile",BASE_H5_URL2);
    /**
    /**
     * 支付url
     */
    public static final String UPLOAD_SHOP_IMG = "uploadShopImg";

    public static class Builder {
        private StringBuilder sb;
        private SecondLevel sl;
        private FinalLevel fl;

        public Builder() {
            this(true);
        }

        public Builder(boolean baseUrl) {
            sb = new StringBuilder(120);
            if (baseUrl) {
                sb.append(BASE_URL);
            }
        }

        /***
         * 接口前缀
         * @return
         */
        public SecondLevel shuffix() {
            if (fl == null) {
                fl = new FinalLevel(sb, this);
                sl = new SecondLevel(sb, fl);
            }
            sb.append(APP_URL_SUFFIX);
            return sl;
        }

        /**
         * 没有接口前缀
         *
         * @return
         */
        public SecondLevel empty() {
            return sl;
        }

        public static class SecondLevel {
            private StringBuilder sb;
            private FinalLevel fl;

            public SecondLevel(StringBuilder sb, FinalLevel fl) {
                this.sb = sb;
                this.fl = fl;
            }

            /**
             * 系统相关
             */
            public FinalLevel system() {
                sb.append("/system");
                return fl;
            }

            /**
             * 游戏相关
             */
            public FinalLevel game() {
                sb.append("/game");
                return fl;
            }

            /**
             * 礼包相关
             */
            public FinalLevel gift() {
                sb.append("/gift");
                return fl;
            }

            /**
             * cdkey
             */
            public FinalLevel cdKey() {
                sb.append("/cdkey");
                return fl;
            }

            /**
             * 用户登录相关
             */
            public FinalLevel user() {
                sb.append("/user");
                return fl;
            }

            public FinalLevel slide() {
                sb.append("/slide");
                return fl;
            }

            public FinalLevel smsCode() {
                sb.append("/smscode");
                return fl;
            }

            /**
             * 搜索相关
             */
            public FinalLevel search() {
                sb.append("/search");
                return fl;
            }

            /**
             * 咨询相关
             */
            public FinalLevel news() {
                sb.append("/news");
                return fl;
            }
        }

        /**
         * 接口功能类
         */
        public static class FinalLevel {

            private StringBuilder sb;
            private Builder builder;

            public FinalLevel(StringBuilder sb, Builder builder) {
                this.sb = sb;
                this.builder = builder;
            }

            /**
             * 资讯列表 {@link SecondLevel#news()}
             * 激活码列表 {@link SecondLevel#cdKey()}
             * 获取礼包列表{@link SecondLevel#gift()}
             * 搜索列表{@link SecondLevel#search()}
             * 获取轮播图{@link SecondLevel#slide()}
             */
            public Builder list() {
                sb.append("/list");
                return builder;
            }

            /**
             * 游戏列表(用户带有标签)
             *
             * @game
             */
            public Builder userPlaylist() {
                sb.append("/user_playlist");
                return builder;
            }

            /**
             * 游戏详情
             * game
             */
            public Builder detail() {
                sb.append("/detail");
                return builder;
            }

            /**
             * 获取游戏评论列表
             * game
             */
            public Builder commentList() {
                sb.append("/comment_list");
                return builder;
            }

            /**
             * 添加游戏评论
             * game
             */
            public Builder commentAdd() {
                sb.append("/comment_add");
                return builder;
            }

            /**
             * 获取游戏类型列表
             * game
             */
            public Builder typeList() {
                sb.append("type_list");
                return builder;
            }

            /**
             * 提交安装数
             * game
             */
            public Builder install() {
                sb.append("install");
                return builder;
            }

            /**
             * 用户注册 user
             * 领取礼包 cdkey
             * 领取激活码
             */
            public Builder add() {
                sb.append("add");
                return builder;
            }

            /**
             * 用户登录
             * slide
             */
            public Builder shot() {
                sb.append("shot");
                return builder;
            }

            /**
             * 获取闪屏图
             * user
             */
            public Builder login() {
                sb.append("login");
                return builder;
            }

            /**
             * 获取发送手机验证码
             * smscode
             */
            public Builder send() {
                sb.append("send");
                return builder;
            }

            /**
             * 验证短信验证码
             * smscode
             */
            public Builder check() {
                sb.append("check");
                return builder;
            }

            /**
             * 获取客服信息
             * {@link SecondLevel#system()}
             */
            public Builder getHelpInfo() {
                sb.append("get_help_info");
                return builder;
            }

            /**
             * 获取服务器时间
             * {@link SecondLevel#system()}
             */
            public Builder getServerTime() {
                sb.append("get_server_time");
                return builder;
            }

            /**
             * 获取开机闪屏图
             */
            public Builder getSplash() {
                sb.append("get_splash");
                return builder;
            }

            /**
             * 打开APP
             * {@link SecondLevel#system()}
             */
            public Builder openApp() {
                sb.append("openapp");
                return builder;
            }

            /**
             * 获取版本信息
             * system
             */
            public Builder getVersionInfo() {
                sb.append("get_version_info");
                return builder;
            }

            /**
             * 获取版本信息
             * system
             */
            public Builder hasNewVersion() {
                sb.append("has_new_version");
                return builder;
            }

            /**
             * 游戏推荐
             * game
             */
            public Builder recommend() {
                sb.append("recommend");
                return builder;
            }

            /**
             * 游戏推荐
             * game
             */
            public Builder searchHot() {
                sb.append("search_hot");
                return builder;
            }

            /**
             * 获取资讯详情
             * news
             */
            public Builder getDetail() {
                sb.append("getdetail");
                return builder;
            }

            /**
             * 获取资讯详情
             * news
             */
            public Builder webDetail() {
                sb.append("webdetail");
                return builder;
            }


            /**
             * 获取搜索热词
             * game
             */
            public Builder hotwordList() {
                sb.append("hotword_list");
                return builder;
            }

            /**
             * 获取搜索热词
             * game
             */
            public Builder down() {
                sb.append("down");
                return builder;
            }

            /**
             * 搜索游戏
             * game
             */
            public Builder searchlist() {
                sb.append("searchlist");
                return builder;
            }

            /**
             * 搜索游戏
             * news
             */
            public Builder search() {
                sb.append("search");
                return builder;
            }

            /**
             * 提交用户标签
             * game
             */
            public Builder loveTypes() {
                sb.append("love_types");
                return builder;
            }

            /**
             * 获取用户钱包金额
             * wallet
             */
            public Builder getBalance() {
                sb.append("get_balance");
                return builder;
            }

            /**
             * 检测用户状态
             * user
             */
            public Builder userCheck() {
                sb.append("user_check");
                return builder;
            }

            /**
             * 用户预约游戏列表
             * game
             */
            public Builder getSubmit() {
                sb.append("get_submit");
                return builder;
            }

            /**
             * 更新游戏预约或下载数
             * game
             */
            public Builder update() {
                sb.append("update");
                return builder;
            }
        }

        //
        //  存在兩套restfull接口
        //  下面是个人中心中的restful 接口
        //
        private SecondLevelMobile slm;
        private FinalLevelMobile flm;

        public SecondLevelMobile floatMoblie() {
            if (slm == null) {
                flm = new FinalLevelMobile(sb, this);
                slm = new SecondLevelMobile(sb, flm);
            }
            sb.append("/float.php/Mobile");
            return slm;
        }

        public static class SecondLevelMobile {
            private StringBuilder sb;
            private FinalLevelMobile flm;

            public SecondLevelMobile(StringBuilder sb, FinalLevelMobile flm) {
                this.sb = sb;
                this.flm = flm;
            }

            public FinalLevelMobile help() {
                sb.append("/Help");
                return flm;
            }

            public FinalLevelMobile password() {
                sb.append("/Password");
                return flm;
            }

            public FinalLevelMobile security() {
                sb.append("/Security");
                return flm;
            }

            public FinalLevelMobile wallet() {
                sb.append("/Wallet");
                return flm;
            }

            public FinalLevelMobile Forgetpwd() {
                sb.append("/Forgetpwd");
                return flm;
            }

            public FinalLevelMobile score() {
                sb.append("/score");
                return flm;
            }
        }

        public static class FinalLevelMobile {
            private StringBuilder sb;
            private Builder builder;

            public FinalLevelMobile(StringBuilder sb, Builder builder) {
                this.sb = sb;
                this.builder = builder;
            }

            /**
             * 忘记密码  Forgetpwd
             * 客服中心  score Help
             */
            public Builder index() {
                sb.append("/index");
                return builder;
            }

            /**
             * 充值记录
             * Wallet
             */
            public Builder chargeDetail() {
                sb.append("/charge_detail");
                return builder;
            }


            /**
             * 消费记录
             * Wallet
             */
            public Builder payDetail() {
                sb.append("/pay_detail");
                return builder;
            }

            /**
             * 钱包
             * Wallet
             */
            public Builder charge() {
                sb.append("charge");
                return builder;
            }

            /**
             * 密保手机
             * Security
             */
            public Builder mobile() {
                sb.append("mobile");
                return builder;
            }

            /**
             * 密保邮箱
             * Security
             */
            public Builder email() {
                sb.append("email");
                return builder;
            }

            /**
             * 密码修改
             * Password
             */
            public Builder uppwd() {
                sb.append("/uppwd");
                return builder;
            }
        }

        private SecondLevelAppV2 slm2;
        private FinalLevelMobileV2 flm2;

        /**
         * https://s3-test-m-game.gank.tv/mobile.php/AppApiDev2/loginState
         *
         * @return
         */
        public SecondLevelAppV2 floatMoblieV2() {
            if (slm2 == null) {
                flm2 = new FinalLevelMobileV2(sb, this);
                slm2 = new SecondLevelAppV2(sb, flm2);
            }
            sb.append("/mobile.php/AppApiDev2");
            return slm2;
        }

        public static class SecondLevelAppV2 {
            private StringBuilder sb;
            private FinalLevelMobileV2 finalLevelMobileV2;

            public SecondLevelAppV2(StringBuilder sb, FinalLevelMobileV2 flm2) {
                this.sb = sb;
                finalLevelMobileV2 = flm2;
            }

            /**
             * 获取商店图
             */
            public String shopImg() {
                return sb.append("/shopImg").toString();
            }

            /**
             * 登录状态
             *
             * @return
             */
            public String loginState() {
                return sb.append("/loginState").toString();
            }

            /**
             * 获取指定appid的游戏标签
             */
            public String gameTags() {
                return sb.append("/gameTags").toString();
            }

            public String getCaptchaImg(int width, int height, String id) {
                sb.append("/codeimg");
                //https://s3-test-m-game.gank.tv/mobile.php/AppApiDev2/codeimg?width=300&height=100&id=358ba5f1d49f99ce9c8ab0c49d35c333
                if (width > 0 && height > 0) {
                    sb.append("?width=").append(Math.max(width,400)).append("&height=").append(Math.max(height,100));
                }
                if (!TextUtils.isEmpty(id)) {
                    sb.append("&id=").append(id);
                }
                return sb.toString();
            }

            /**
             * 登录接口
             *
             * @return
             */
            public String login() {
                return sb.append("/login").toString();
            }

            /**
             * 推荐接口
             *
             * @return
             */
            public String recommend() {
                return sb.append("/recommend").toString();
            }

            /**
             * 评论列表接口
             *
             * @return
             */
            public String commentList() {
                return sb.append("/commentList").toString();
            }

            /**
             * 单个评论详情接口
             *
             * @return
             */
            public String commentDetail() {
                return sb.append("/commentDetail").toString();
            }

            /**
             * 单个游戏评论打分详情接口
             *
             * @return
             */
            public String commentScoreDetail() {
                return sb.append("/commentScoreDetail").toString();
            }

            /**
             * 游评汇
             *
             * @return
             */
            public String commentsCollect() {
                return sb.append("/commentsCollect").toString();
            }

            /**
             * 游戏详情接口
             *
             * @return
             */
            public String gameDetail() {
                return sb.append("/gameDetail").toString();
            }

            /**
             * 搜索热词
             *
             * @return
             */
            public String hotKey() {
                return sb.append("/gameDetail").toString();
            }

            /**
             * 搜索
             *
             * @return
             */
            public String search() {
                return sb.append("/search").toString();
            }

            /**
             * 搜索热词
             *
             * @return
             */
            public String searchHotKey() {
                return sb.append("/hotKeyWordsV2").toString();
            }

            /**
             * 搜索热搜词
             *
             * @return
             */
            public String searchWords() {
                return sb.append("/searchWordsList").toString();
            }

            /**
             * 专题列表
             *
             * @return
             */
            public String subjectlist() {
                return sb.append("/subjectlist").toString();
            }

            /**
             * 专题详情
             *
             * @return
             */
            public String subject() {
                return sb.append("/subject").toString();
            }

            /**
             * 个人收藏列表
             *
             * @return
             */
            public String likeGameList() {
                return sb.append("/likeGameList").toString();
            }

            /**
             * 个人预约列表
             *
             * @return
             */
            public String subGameList() {
                return sb.append("/subGameList").toString();
            }

        }

        public static class FinalLevelMobileV2 {
            private StringBuilder sb;
            private Builder builder;

            public FinalLevelMobileV2(StringBuilder sb, Builder builder) {
                this.sb = sb;
                this.builder = builder;
            }

        }

        public Builder html() {
            sb.append(".html");
            return this;
        }

        public String build() {
            return sb.toString();
        }
    }
}
