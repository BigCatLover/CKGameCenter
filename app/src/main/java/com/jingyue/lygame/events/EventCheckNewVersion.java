package com.jingyue.lygame.events;

/**
 * 检查新版本
 * Created by wang on 16/5/27.
 */
public class EventCheckNewVersion {

    private String new_version = "";

    public EventCheckNewVersion(String new_version) {
        this.new_version = new_version;
    }

    public String getNew_version() {
        return new_version;
    }

    public void setNew_version(String new_version) {
        this.new_version = new_version;
    }
}
