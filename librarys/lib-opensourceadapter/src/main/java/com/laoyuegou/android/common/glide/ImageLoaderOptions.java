package com.laoyuegou.android.common.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.bumptech.glide.request.target.BaseTarget;

import java.io.File;
import java.security.Signature;

/**
 * Created by luy on 2017/7/14
 */
public class ImageLoaderOptions {
    private View viewContainer;  // 图片容器
    private String url;  // 图片地址
    private Integer resource;  // 图片地址
    private File file;  //图片文件
    private int holderDrawable;  // 设置展位图
    private Drawable hDrawable; //设置占位图
    private ImageSize imageSize;  //设置图片的大小
    private int errorDrawable;  //是否展示加载错误的图片
    private Drawable eDrawble = null;  //加载错误的图片
    private boolean asGif = false;   //是否作为gif展示
    private boolean isCrossFade = true; //是否渐变平滑的显示图片,默认为true
    private boolean isFitCenter = false;  //图片大小是否等于View大小
    private boolean isSkipMemoryCache = false; //是否跳过内存缓存
    private DiskCacheStrategy mDiskCacheStrategy = DiskCacheStrategy.DEFAULT; //磁盘缓存策略
    private boolean blurImage = false; //是否使用高斯模糊
    private boolean isTransform = true; //是否转化
    private BaseTarget target = null; //target
    private Thumbnail thumbnail = null; //缩略图
    private boolean isCircle = false; //是否是圆形图片
    private boolean autoSizeUrl = false; //是否自动优化url的尺寸,目前只支持阿里oss
    private String signature;

    private ImageLoaderOptions(Builder builder) {
        this.asGif = builder.asGif;
        this.errorDrawable = builder.errorDrawable;
        this.eDrawble = builder.eDrawable;
        this.holderDrawable = builder.holderDrawable;
        this.hDrawable = builder.hDrawable;
        this.imageSize = builder.mImageSize;
        this.isCrossFade = builder.isCrossFade;
        this.isFitCenter = builder.isFitCenter;
        this.isSkipMemoryCache = builder.isSkipMemoryCache;
        this.mDiskCacheStrategy = builder.mDiskCacheStrategy;
        this.url = builder.url;
        this.resource = builder.resource;
        this.file = builder.file;
        this.viewContainer = builder.mViewContainer;
        this.blurImage = builder.blurImage;
        this.isTransform = builder.isTransform;
        this.target = builder.target;
        this.thumbnail = builder.thumbnail;
        this.isCircle = builder.isCircle;
        this.autoSizeUrl = builder.autoSizeUrl;
        this.signature = builder.signature;
    }

    public BaseTarget getTarget() {
        return target;
    }

    public Integer getResource() {
        return resource;
    }

    public boolean isBlurImage() {
        return blurImage;
    }

    public View getViewContainer() {
        return viewContainer;
    }

    public String getUrl() {
        return url;
    }


    public int getHolderDrawable() {
        return holderDrawable;
    }

    public Drawable getHDrawable() {
        return hDrawable;
    }

    public ImageSize getImageSize() {
        return imageSize;
    }


    public int getErrorDrawable() {
        return errorDrawable;
    }

    public Drawable getEDrawable() {
        return eDrawble;
    }

    public boolean isAsGif() {
        return asGif;
    }

    public String signature() {
        return signature;
    }

    public boolean isCrossFade() {
        return isCrossFade;
    }

    public boolean isFitCenter() {
        return isFitCenter;
    }

    public boolean isSkipMemoryCache() {
        return isSkipMemoryCache;
    }

    public File getFile() {
        return file;
    }

    public boolean isTransform() {
        return isTransform;
    }

    public DiskCacheStrategy getDiskCacheStrategy() {
        return mDiskCacheStrategy;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public boolean getIsCircle() {
        return isCircle;
    }

    public boolean getAutoSizeUrl() {
        return autoSizeUrl;
    }

    public final static class Builder {

        private int holderDrawable = -1;  // 设置展位图
        private Drawable hDrawable; //设置占位图
        private View mViewContainer;  // 图片容器
        private String url;  // 图片地址
        private Integer resource;  // 图片地址
        private File file;  //图片文件
        private ImageSize mImageSize;  //设置图片的大小
        private int errorDrawable = -1;  //是否展示加载错误的图片
        private Drawable eDrawable = null;  //错误的图片
        private boolean asGif = false;   //是否作为gif展示
        private boolean isCrossFade = false; //是否渐变平滑的显示图片
        private boolean isFitCenter = false; //是否图片大小=View大小
        private boolean isSkipMemoryCache = false; //是否跳过内存缓存
        private boolean blurImage = false; //是否使用高斯模糊
        private boolean isTransform = true; //是否转化
        private DiskCacheStrategy mDiskCacheStrategy = DiskCacheStrategy.DEFAULT; //磁盘缓存策略
        private BaseTarget target = null; //target
        private Thumbnail thumbnail = null; //缩略图
        private boolean isCircle = false; //是否是圆形图片
        private boolean autoSizeUrl = false; //是否自动优化url尺寸,目前只支持阿里oss
        private String signature = "";

        public Builder(@NonNull View v, @NonNull String url) {
            this.url = url;
            this.mViewContainer = v;
        }

        public Builder(@NonNull View v, @NonNull Integer resource) {
            this.resource = resource;
            this.mViewContainer = v;
        }

        public Builder(@NonNull View v, @NonNull File file) {
            this.file = file;
            this.mViewContainer = v;
        }

        public Builder placeholder(@DrawableRes int holderDrawable) {
            this.holderDrawable = holderDrawable;
            return this;
        }

        public Builder placeholder(Drawable drawable) {
            this.hDrawable = drawable;
            return this;
        }

        public Builder isCrossFade(boolean isCrossFade) {
            this.isCrossFade = isCrossFade;
            return this;
        }

        public Builder signature(String signature) {
            this.signature = signature;
            return this;
        }

        public Builder blurImage(boolean blurImage) {
            this.blurImage = blurImage;
            return this;
        }

        public Builder isSkipMemoryCache(boolean isSkipMemoryCache) {
            this.isSkipMemoryCache = isSkipMemoryCache;
            return this;
        }

        public Builder isFitCenter(boolean isFitCenter) {
            this.isFitCenter = isFitCenter;
            return this;
        }

        public Builder override(int width, int height) {
            this.mImageSize = new ImageSize(width, height);
            return this;
        }

        public Builder asGif(boolean asGif) {
            this.asGif = asGif;
            return this;
        }

        public Builder error(@DrawableRes int errorDrawable) {
            this.errorDrawable = errorDrawable;
            return this;
        }

        public Builder error(Drawable eDrawable) {
            this.eDrawable = eDrawable;
            return this;
        }

        public Builder isTransform(boolean isTransform) {
            this.isTransform = isTransform;
            return this;
        }

        public Builder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder target(BaseTarget target) {
            this.target = target;
            return this;
        }

        public Builder diskCacheStrategy(DiskCacheStrategy mDiskCacheStrategy) {
            this.mDiskCacheStrategy = mDiskCacheStrategy;
            return this;
        }

        public Builder isCircle(boolean isCircle) {
            this.isCircle = isCircle;
            return this;
        }

        public Builder autoSizeUrl(boolean autoSizeUrl) {
            this.autoSizeUrl = autoSizeUrl;
            return this;
        }

        public ImageLoaderOptions build() {
            return new ImageLoaderOptions(this);
        }


    }

    //对应重写图片size
    public final static class ImageSize {
        private int width = 0;
        private int height = 0;

        public ImageSize(int width, int heigh) {
            this.width = width;
            this.height = heigh;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }

    //对应磁盘缓存策略
    public enum DiskCacheStrategy {
        All, NONE, SOURCE, RESULT, DEFAULT
    }

    //缩略图
    public final static class Thumbnail {
        private float thumbnailSize = 0;    //缩略图缩放倍数
        private String thumbnailUrl = null;    //缩略图链接

        public Thumbnail(float thumbnailSize) {
            this.thumbnailSize = thumbnailSize;
        }

        public Thumbnail(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public float getThumbnailSize() {
            return thumbnailSize;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

    }
}
