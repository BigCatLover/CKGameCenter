package com.jingyue.lygame.bean;

import com.google.gson.annotations.SerializedName;
import com.jingyue.lygame.bean.internal.BaseAdapterModel;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by zhanglei on 2017/12/5.
 */
@Table(database = AppDataBase.class, name = "gamedownload")
public class DownloadBean extends BaseAdapterModel {

    @Column
    @PrimaryKey()
    @SerializedName("id")
    private int id;
    @Column
    @SerializedName("gameSize")
    private String gameSize;
    @Column
    @SerializedName("url")
    private String url;
    @Column
    @SerializedName("path")
    private String path;
    @Column
    @SerializedName("gameId")
    private String gameId;
    @Column
    @SerializedName("gameName")
    private String gameName;
    @Column
    @SerializedName("gameIcon")
    private String gameIcon;
    @Column
    @SerializedName("packageName")
    private String packageName;
    @Column
    @SerializedName("status")
    private int status;//下载状态

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameSize() {
        return gameSize;
    }

    public void setGameSize(String gameSize) {
        this.gameSize = gameSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

}
