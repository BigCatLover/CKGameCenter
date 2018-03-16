package com.jingyue.lygame.bean;

import java.io.Serializable;

/**
 * Created by zhanglei on 2017/6/14.
 */
public class GameLabelBean implements Serializable {
    private int gameid;
    private String icon;
    private String gamename;
    private String type;
    private String oneword;
    private String typename;
    private int downcnt;

    public int getGameid() {
        return gameid;
    }

    public void setGameid(int gameid) {
        this.gameid = gameid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGamename() {
        return gamename;
    }

    public void setGamename(String gamename) {
        this.gamename = gamename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOneword() {
        return oneword;
    }

    public void setOneword(String oneword) {
        this.oneword = oneword;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public int getDowncnt() {
        return downcnt;
    }

    public void setDowncnt(int downcnt) {
        this.downcnt = downcnt;
    }
}
