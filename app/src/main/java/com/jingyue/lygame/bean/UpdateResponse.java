package com.jingyue.lygame.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jingyue.lygame.utils.StringUtils;


/**
 * 版本升级response
 * Created by wang on 16/5/26.
 */
public class UpdateResponse implements Parcelable {

    private String update_title;//升级title
    private String update_log;//更新日志
    private String apk_url;//apk链接下载地址
    private String target_size;//文件大小 用于界面显示
    private int force_update;//是否强制升级 0:否 1:是
    private String new_version = "";//新版本的版本号 eg:V2.3.8
    private long apk_length;    //文件大小，用于校验文件长度

    public String getUpdate_title() {
        return update_title;
    }

    public void setUpdate_title(String update_title) {
        this.update_title = update_title;
    }

    public String getUpdate_log() {
        return update_log;
    }

    public void setUpdate_log(String update_log) {
        this.update_log = update_log;
    }

    public String getApk_url() {
        return apk_url;
    }

    public void setApk_url(String apk_url) {
        this.apk_url = apk_url;
    }

    public String getTarget_size() {
        return target_size;
    }

    public void setTarget_size(String target_size) {
        this.target_size = target_size;
    }

    public int getForce_update() {
        return force_update;
    }

    public void setForce_update(int force_update) {
        this.force_update = force_update;
    }

    public String getNew_version() {
        return new_version;
    }

    public void setNew_version(String new_version) {
        this.new_version = new_version;
    }

    public long getApk_length() {
        return apk_length;
    }

    public void setApk_length(long apk_length) {
        this.apk_length = apk_length;
    }

    /**
     * 判断是否有效
     *
     * @return true 表示有效  false表示无效
     */
    public boolean isValid() {
        return !StringUtils.isEmptyOrNull(update_log) && !StringUtils.isEmptyOrNull(apk_url)
                && !StringUtils.isEmptyOrNull(target_size) && !StringUtils.isEmptyOrNull(new_version);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.update_title);
        dest.writeString(this.update_log);
        dest.writeString(this.apk_url);
        dest.writeString(this.target_size);
        dest.writeInt(this.force_update);
        dest.writeString(this.new_version);
        dest.writeLong(this.apk_length);
    }

    public UpdateResponse() {
    }

    protected UpdateResponse(Parcel in) {
        this.update_title = in.readString();
        this.update_log = in.readString();
        this.apk_url = in.readString();
        this.target_size = in.readString();
        this.force_update = in.readInt();
        this.new_version = in.readString();
        this.apk_length = in.readLong();
    }

    public static final Creator<UpdateResponse> CREATOR = new Creator<UpdateResponse>() {
        @Override
        public UpdateResponse createFromParcel(Parcel source) {
            return new UpdateResponse(source);
        }

        @Override
        public UpdateResponse[] newArray(int size) {
            return new UpdateResponse[size];
        }
    };
}
