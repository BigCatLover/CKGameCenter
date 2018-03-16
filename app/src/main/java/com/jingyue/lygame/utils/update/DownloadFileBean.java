package com.jingyue.lygame.utils.update;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by yangjian on 2016/12/5.
 */

public class DownloadFileBean implements Parcelable {

    private String fileName;
    private int notifyId;
    private String filePath;
    private String downloadURL;
    private long notifyTime;
    private long fileLength;
    private String md5;

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public long getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(long notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fileName);
        dest.writeInt(this.notifyId);
        dest.writeString(this.filePath);
        dest.writeString(this.downloadURL);
        dest.writeLong(this.notifyTime);
        dest.writeLong(this.fileLength);
        dest.writeString(this.md5);
    }

    public DownloadFileBean() {
    }

    protected DownloadFileBean(Parcel in) {
        this.fileName = in.readString();
        this.notifyId = in.readInt();
        this.filePath = in.readString();
        this.downloadURL = in.readString();
        this.notifyTime = in.readLong();
        this.fileLength = in.readLong();
        this.md5 = in.readString();
    }

    public static final Creator<DownloadFileBean> CREATOR = new Creator<DownloadFileBean>() {
        @Override
        public DownloadFileBean createFromParcel(Parcel source) {
            return new DownloadFileBean(source);
        }

        @Override
        public DownloadFileBean[] newArray(int size) {
            return new DownloadFileBean[size];
        }
    };
}
