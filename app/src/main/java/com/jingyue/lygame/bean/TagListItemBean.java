package com.jingyue.lygame.bean;

import android.view.View;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.jingyue.lygame.bean.internal.BaseItem;

import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-09-12 21:06
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class TagListItemBean extends BaseAdapterModel {

    @Override
    public int getLayout() {
        return 0;
    }

    @Override
    public BaseItem.BVH onCreateViewHolder(View parent, int viewType) {
        return null;
    }

    /**
     * list : [{"id":"100","type":"67,68,69","icon":"https://s3-test-statics-game.gank.tv/upload/20170415/58f19cd24a0cb.png","name":"APP","avg_score":null,"tags":[{"id":"67","name":"动作","is_default":1},{"id":"68","name":"三国","is_default":1},{"id":"69","name":"休闲","is_default":1}]},{"id":"362788","type":"72,77,67","icon":"https://s3-test-statics-game.gank.tv/upload/20170519/591eb37e40da8.png","name":"孤狼","avg_score":"0.00","tags":[{"id":"72","name":"角色","is_default":1},{"id":"77","name":"射击","is_default":1},{"id":"67","name":"动作","is_default":1}]},{"id":"362948","type":"67,68","icon":"https://s3-test-statics-game.gank.tv","name":"yck游戏测试1","avg_score":"0.00","tags":[{"id":"67","name":"动作","is_default":1},{"id":"68","name":"三国","is_default":1}]},{"id":"362950","type":"67,68,69,72","icon":"https://s3-test-statics-game.gank.tv/upload/20170804/59840f52c8d35.png","name":"胡莱三国2","avg_score":"0.00","tags":[{"id":"67","name":"动作","is_default":1},{"id":"68","name":"三国","is_default":1},{"id":"69","name":"休闲","is_default":1},{"id":"72","name":"角色","is_default":1}]}]
     * tag : {"id":"67","name":"动作"}
     */

    @SerializedName("tag")
    public TagBean tag;
    @SerializedName("list")
    public List<ListBean> list;

    public static class TagBean {
        /**
         * id : 67
         * name : 动作
         */

        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
    }

    public static class ListBean {
        /**
         * id : 100
         * type : 67,68,69
         * icon : https://s3-test-statics-game.gank.tv/upload/20170415/58f19cd24a0cb.png
         * name : APP
         * avg_score : null
         * tags : [{"id":"67","name":"动作","is_default":1},{"id":"68","name":"三国","is_default":1},{"id":"69","name":"休闲","is_default":1}]
         */

        @SerializedName("id")
        public String id;
        @SerializedName("type")
        public String type;
        @SerializedName("icon")
        public String icon;
        @SerializedName("name")
        public String name;
        @SerializedName("avg_score")
        public Object avgScore;
        @SerializedName("tags")
        public List<TagsBean> tags;

        public static class TagsBean {
            /**
             * id : 67
             * name : 动作
             * is_default : 1
             */

            @SerializedName("id")
            public String id;
            @SerializedName("name")
            public String name;
            @SerializedName("is_default")
            public int isDefault;
        }
    }
}
