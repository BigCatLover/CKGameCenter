package com.jingyue.lygame.clickaction;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-10-27 18:23
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class UploadPicAction extends LYGGameCenterAction{
    public UploadPicAction(String id){
        a1().button().a2().id(id).a3().withValue("upload");
    }
}
