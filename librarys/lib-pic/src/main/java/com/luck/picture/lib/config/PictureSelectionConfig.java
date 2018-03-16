package com.luck.picture.lib.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.lib.config
 * email：893855882@qq.com
 * data：2017/5/24
 */

public final class PictureSelectionConfig implements Parcelable {
    public int mimeType;
    public boolean camera;
    public String outputCameraPath;
    public int selectionMode;
    public int maxSelectNum;
    public int minSelectNum;
    public int cropCompressQuality;
    public int mLeastCompressSize;
    public int imageSpanCount;
    public int overrideWidth;
    public int overrideHeight;
    public int aspect_ratio_x;
    public int aspect_ratio_y;
    public float sizeMultiplier;
    public int cropWidth;
    public int cropHeight;
    public boolean zoomAnim;
    public boolean isCompress;
    public boolean isCamera;
    public boolean isGif;
    public boolean enablePreview;
    public boolean enableCrop;
    public boolean freeStyleCropEnabled;
    public boolean circleDimmedLayer;
    public boolean showCropFrame;
    public boolean showCropGrid;
    public boolean hideBottomControls;
    public boolean rotateEnabled;
    public boolean scaleEnabled;
    public boolean previewEggs;

    public List<LocalMedia> selectionMedias;

    private void reset() {
        mimeType = PictureConfig.TYPE_IMAGE;
        camera = false;
        selectionMode = PictureConfig.MULTIPLE;
        maxSelectNum = 9;
        minSelectNum = 0;
        cropCompressQuality = 90;
        mLeastCompressSize = 100;
        imageSpanCount = 4;
        overrideWidth = 0;
        overrideHeight = 0;
        isCompress = false;
        aspect_ratio_x = 0;
        aspect_ratio_y = 0;
        cropWidth = 0;
        cropHeight = 0;
        isCamera = true;
        isGif = false;
        enablePreview = true;
        enableCrop = false;
        freeStyleCropEnabled = false;
        circleDimmedLayer = false;
        showCropFrame = true;
        showCropGrid = true;
        hideBottomControls = true;
        rotateEnabled = true;
        scaleEnabled = true;
        previewEggs = false;
        zoomAnim = true;
        outputCameraPath = "";
        sizeMultiplier = 0.8f;
        selectionMedias = new ArrayList<>();
    }

    public static PictureSelectionConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static PictureSelectionConfig getCleanInstance() {
        PictureSelectionConfig selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private static final class InstanceHolder {
        private static final PictureSelectionConfig INSTANCE = new PictureSelectionConfig();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mimeType);
        dest.writeByte(this.camera ? (byte) 1 : (byte) 0);
        dest.writeString(this.outputCameraPath);
        dest.writeInt(this.selectionMode);
        dest.writeInt(this.maxSelectNum);
        dest.writeInt(this.minSelectNum);
        dest.writeInt(this.cropCompressQuality);
        dest.writeInt(this.mLeastCompressSize);
        dest.writeInt(this.imageSpanCount);
        dest.writeInt(this.overrideWidth);
        dest.writeInt(this.overrideHeight);
        dest.writeInt(this.aspect_ratio_x);
        dest.writeInt(this.aspect_ratio_y);
        dest.writeFloat(this.sizeMultiplier);
        dest.writeInt(this.cropWidth);
        dest.writeInt(this.cropHeight);
        dest.writeByte(this.zoomAnim ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCompress ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isGif ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enablePreview ? (byte) 1 : (byte) 0);
        dest.writeByte(this.enableCrop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.freeStyleCropEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.circleDimmedLayer ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showCropFrame ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showCropGrid ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hideBottomControls ? (byte) 1 : (byte) 0);
        dest.writeByte(this.rotateEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.scaleEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.previewEggs ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.selectionMedias);
    }

    public PictureSelectionConfig() {
    }

    protected PictureSelectionConfig(Parcel in) {
        this.mimeType = in.readInt();
        this.camera = in.readByte() != 0;
        this.outputCameraPath = in.readString();
        this.selectionMode = in.readInt();
        this.maxSelectNum = in.readInt();
        this.minSelectNum = in.readInt();
        this.cropCompressQuality = in.readInt();
        this.mLeastCompressSize = in.readInt();
        this.imageSpanCount = in.readInt();
        this.overrideWidth = in.readInt();
        this.overrideHeight = in.readInt();
        this.aspect_ratio_x = in.readInt();
        this.aspect_ratio_y = in.readInt();
        this.sizeMultiplier = in.readFloat();
        this.cropWidth = in.readInt();
        this.cropHeight = in.readInt();
        this.zoomAnim = in.readByte() != 0;
        this.isCompress = in.readByte() != 0;
        this.isCamera = in.readByte() != 0;
        this.isGif = in.readByte() != 0;
        this.enablePreview = in.readByte() != 0;
        this.enableCrop = in.readByte() != 0;
        this.freeStyleCropEnabled = in.readByte() != 0;
        this.circleDimmedLayer = in.readByte() != 0;
        this.showCropFrame = in.readByte() != 0;
        this.showCropGrid = in.readByte() != 0;
        this.hideBottomControls = in.readByte() != 0;
        this.rotateEnabled = in.readByte() != 0;
        this.scaleEnabled = in.readByte() != 0;
        this.previewEggs = in.readByte() != 0;
        this.selectionMedias = in.createTypedArrayList(LocalMedia.CREATOR);
    }

    public static final Parcelable.Creator<PictureSelectionConfig> CREATOR = new Parcelable.Creator<PictureSelectionConfig>() {
        @Override
        public PictureSelectionConfig createFromParcel(Parcel source) {
            return new PictureSelectionConfig(source);
        }

        @Override
        public PictureSelectionConfig[] newArray(int size) {
            return new PictureSelectionConfig[size];
        }
    };
}
