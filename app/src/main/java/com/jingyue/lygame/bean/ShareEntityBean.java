package com.jingyue.lygame.bean;

/**
 * Created by zhanglei on 2017/11/20.
 */
public class ShareEntityBean {

    private static final String DEFAULT_URL = "https://imgx.gank.tv";
    private static final String DEFAULT_TITLE = "捞月游戏——最懂你的游戏中心！";
    private static final String DEFAULT_CONTENT = "恭喜你捞中了一款超好玩的极品游戏！";
    private static final String DEFAULT_IMAGEURL = "http://a1.qpic.cn/psb?/V12MW92w0CXy9X/l7l5wy3Dcnkil7UN2qWjopc7011Oe2AXI*8OdcRrM2M!/b/dPMAAAAAAAAA&bo=AAIAAgAAAAADByI!&rf=viewer_4";

    private String url = DEFAULT_URL;
    private String title = DEFAULT_TITLE;
    private String content = DEFAULT_CONTENT;
    private String imageUrl = DEFAULT_IMAGEURL;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
