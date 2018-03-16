package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;

import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-28 16:49
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class CodeinfoBean extends BaseAdapterModel{

    /**
     * id : 59bd35161df5c7fbf018eef23359506e
     * code : ["7","A","4","r"]
     * url : https://s3-test-m-game.gank.tv/mobile.php/mobile/app_api_dev2/codeimg
     */

    @SerializedName("id")
    public String id;
    @SerializedName("url")
    public String url;
    @SerializedName("code")
    public List<String> code;

    @Override
    public String toString() {
        return "CodeinfoBean{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", code=" + code +
                '}';
    }
}
