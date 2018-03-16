package com.laoyuegou.android.common.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.laoyuegou.android.common.glide.gift.ProgressModelLoader;
import com.laoyuegou.android.common.glide.module.CustomImageSizeModel;
import com.laoyuegou.android.common.glide.module.CustomImageSizeModelFutureStudio;
import com.laoyuegou.android.common.glide.module.CustomImageSizeUrlLoader;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.NetworkUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.laoyuegou.android.lib.widget.RingProgressBar;
import com.lygame.libadapter.R;

import java.io.File;
import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by luy on 2017/7/14
 * modify by yizhihao 2017/8/22
 */
public class GlideImageLoader implements IGlideLoaderstrategy {


    @Override
    public void showImage(ImageLoaderOptions options) {
        GenericRequestBuilder mGenericRequestBuilder = init(options);
        if (mGenericRequestBuilder != null) {
            showImageLast(mGenericRequestBuilder, options);
        }
    }

    /**
     * 显示带圆形加载进度条的图片
     *
     * @param pb:圆形进度条控件
     */
    @Override
    public void showImage(ImageLoaderOptions options, final RingProgressBar pb) {
        final Context context = getContext(options);

        GenericRequestBuilder mGenericRequestBuilder = null;
        if (NetworkUtils.isImgFromNet(options.getUrl())) {
            mGenericRequestBuilder = init(options, new ProgressModelLoader(new RingProgressBarHandler(context, pb)));
        } else {
            mGenericRequestBuilder = init(options);
        }

        if (mGenericRequestBuilder != null) {
            mGenericRequestBuilder.listener(new RequestListener() {
                @Override
                public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                    if (null != pb) {
                        pb.setVisibility(View.GONE);
                    }
                    ToastUtils.showShort(context.getResources().getString(R.string.os_load_failure));
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (null != pb) {
                        pb.setVisibility(View.GONE);
                    }
                    return false;
                }
            });

            showImageLast(mGenericRequestBuilder, options);
        }
    }

    /**
     * 显示带加载进度数字的进度条的图片
     *
     * @param textView：显示进度的文本
     * @param progressBar：显示进度的progressbar
     */
    @Override
    public void showImage(ImageLoaderOptions options, final TextView textView, final ProgressBar progressBar) {
        final Context context = getContext(options);

        GenericRequestBuilder mGenericRequestBuilder = null;
        if (NetworkUtils.isImgFromNet(options.getUrl())) {
            mGenericRequestBuilder = init(options, new ProgressModelLoader(new NumProgressBarHandler(context, textView, progressBar)));
        } else {
            mGenericRequestBuilder = init(options);
        }

        if (mGenericRequestBuilder != null) {
            mGenericRequestBuilder.listener(new RequestListener() {
                @Override
                public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (null != textView) {
                        textView.setVisibility(View.GONE);
                    }
                    ToastUtils.showShort(context.getResources().getString(R.string.os_load_failure));
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (null != textView) {
                        textView.setVisibility(View.GONE);
                    }
                    return false;
                }
            });
            showImageLast(mGenericRequestBuilder, options);
        }
    }

    /**
     * 加载带loading资源的图片
     *
     * @param options
     * @param loadingImageView        :loading的图片
     * @param loadingRes：需要loading的资源
     */
    public void showImage(ImageLoaderOptions options, final ImageView loadingImageView, @DrawableRes int loadingRes) {
        final Context context = options.getViewContainer().getContext();
        loadingImageView.setImageDrawable(
                ResourcesCompat.getDrawable(
                        context.getResources(),
                        loadingRes,
                        loadingImageView.getContext().getTheme()));
        GenericRequestBuilder mGenericRequestBuilder = init(options);
        if (mGenericRequestBuilder != null) {
            mGenericRequestBuilder.listener(new RequestListener() {
                @Override
                public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                    loadingImageView.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                    loadingImageView.setVisibility(View.GONE);
                    return false;
                }
            });
            showImageLast(mGenericRequestBuilder, options);
        }
    }

    @Override
    public Bitmap getBitmap(Context context, String url) {
        Bitmap bitmap = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastUtils.showShort("you should call getBimap in thread!");
        } else {
            try {
                bitmap = getRequestManager(Utils.getContext()).load(url).asBitmap().into(SimpleTarget.SIZE_ORIGINAL, SimpleTarget.SIZE_ORIGINAL).get();
            } catch (Exception e) {
                LogUtils.e("GlideImageLoader getBitmap exception:" + e.getMessage());
            }
        }

        return bitmap;
    }

    @Override
    public File getFile(Context context, String url) {
        File file = null;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ToastUtils.showShort("you should call getBimap in thread!");
        } else {
            try {
                file = getRequestManager(Utils.getContext()).load(url).downloadOnly(SimpleTarget.SIZE_ORIGINAL, SimpleTarget.SIZE_ORIGINAL).get();
            } catch (Exception e) {
                LogUtils.e("GlideImageLoader getFile exception:" + e.getMessage());
            }
        }

        return file;
    }

    /**
     * 暂停加载
     */
    @Override
    public void pause(Context context) {
        RequestManager requestManager = getRequestManager(context);
        if (requestManager != null) {
            requestManager.pauseRequests();
        }
    }

    /**
     * 恢复加载
     */
    @Override
    public void resume(Context context) {
        RequestManager requestManager = getRequestManager(context);
        if (requestManager != null) {
            requestManager.resumeRequests();
        }
    }

    /**
     * 下载图片
     *
     * @param imageUrl:下载的url
     * @param iDownloadCallback：下载回调
     */
    @Override
    public void downloadImage(Context context, final String imageUrl, final IDownloadCallback iDownloadCallback) {
        final WeakReference<Context> ctx = new WeakReference<Context>(context);
        Observable.create(new ObservableOnSubscribe<File>() {

            @Override
            public void subscribe(ObservableEmitter<File> observable) {
                try {
                    RequestManager requestManager = getRequestManager(ctx.get());
                    if (requestManager != null) {
                        File file = requestManager.load(imageUrl).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                        observable.onNext(file);
                    } else {
                        iDownloadCallback.onDownFailed();
                    }
                } catch (Exception e) {
                    iDownloadCallback.onDownFailed();
                }
            }
        }).subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(File file) {
                        iDownloadCallback.onDownloadSuccess(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        iDownloadCallback.onDownFailed();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }


    @Override
    public void cleanMemory() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getGlide(Utils.getContext()).clearDiskCache();
                }
            }) {
            }.start();
        } else {
            getGlide(Utils.getContext()).clearDiskCache();
        }
    }

    @Override
    public void init(Context context) {
        Runtime rt = Runtime.getRuntime();
        long maxMemory = 0l;
        if (null != rt) {
            maxMemory = rt.maxMemory();
        }
        if ((maxMemory / (1024 * 1024)) >= 100) {
            Glide.get(context).setMemoryCategory(MemoryCategory.NORMAL);
        } else {
            Glide.get(context).setMemoryCategory(MemoryCategory.LOW);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        Glide.with(Utils.getContext()).onTrimMemory(level);
    }


    public DrawableTypeRequest getGenericRequestBuilder(RequestManager manager, ImageLoaderOptions options) {
        if (options.getAutoSizeUrl()) {
            CustomImageSizeModel customImageRequest = new CustomImageSizeModelFutureStudio(options.getUrl());
            RequestManager.ImageModelRequest imageModelRequest = manager.using(new CustomImageSizeUrlLoader(getContext(options)));
            if (!TextUtils.isEmpty(options.getUrl())) {
                return imageModelRequest.load(customImageRequest);
            }
            if (options.getFile() != null && options.getFile().exists()) {
                return imageModelRequest.load(options.getFile());
            }
            return imageModelRequest.load(options.getResource());
        } else {
            if (!TextUtils.isEmpty(options.getUrl())) {
                return manager.load(options.getUrl());
            }
            if (options.getFile() != null && options.getFile().exists()) {
                return manager.load(options.getFile());
            }
            return manager.load(options.getResource());
        }
    }

    public DrawableTypeRequest getGenericRequestBuilder(RequestManager manager, ImageLoaderOptions options, StreamModelLoader streamModelLoader) {
        RequestManager.ImageModelRequest imageModelRequest = manager.using(streamModelLoader);
        if (!TextUtils.isEmpty(options.getUrl())) {
            return imageModelRequest.load(options.getUrl());
        }
        if (options.getFile() != null && options.getFile().exists()) {
            return imageModelRequest.load(options.getFile());
        }
        return imageModelRequest.load(options.getResource());
    }

    public RequestManager getRequestManager(Context context) {
        return Glide.with(context);
    }

    public Glide getGlide(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context).isFinishing() ? null : Glide.get(context);
        } else {
            return Glide.get(context);
        }
    }

    private Context getContext(ImageLoaderOptions options) {
        View v = options.getViewContainer();
        return ((v != null) ? v.getContext() : Utils.getContext());
    }

    public GenericRequestBuilder init(ImageLoaderOptions options) {
        RequestManager manager = getRequestManager(getContext(options));
        if (manager != null) {
            GenericRequestBuilder mDrawableTypeRequest = getGenericRequestBuilder(manager, options).asBitmap();
            if (options.isAsGif()) {
                mDrawableTypeRequest = getGenericRequestBuilder(manager, options).asGif();
            }
            if (options.isFitCenter()) {
                mDrawableTypeRequest = getGenericRequestBuilder(manager, options).asBitmap().fitCenter();
            }
            //装载参数
            mDrawableTypeRequest = loadGenericParams(mDrawableTypeRequest, options);
            return mDrawableTypeRequest;
        }

        return null;
    }

    public GenericRequestBuilder init(ImageLoaderOptions options, StreamModelLoader streamModelLoader) {
        RequestManager manager = getRequestManager(getContext(options));
        if (manager != null) {
            GenericRequestBuilder mDrawableTypeRequest = getGenericRequestBuilder(manager, options, streamModelLoader).asBitmap();
            if (options.isAsGif()) {
                mDrawableTypeRequest = getGenericRequestBuilder(manager, options, streamModelLoader);
            }
            //装载参数
            mDrawableTypeRequest = loadGenericParams(mDrawableTypeRequest, options);
            return mDrawableTypeRequest;
        }
        return null;
    }


    private GenericRequestBuilder loadGenericParams(GenericRequestBuilder mGenericRequestBuilder, final ImageLoaderOptions options) {
        final View view = options.getViewContainer();
        GenericRequestBuilder builder = mGenericRequestBuilder;
        if (mGenericRequestBuilder instanceof DrawableTypeRequest) {
            if (options.isCrossFade()) {
                ((DrawableTypeRequest) mGenericRequestBuilder).crossFade();
            }
            if (options.isAsGif()) {
                builder = ((DrawableTypeRequest) mGenericRequestBuilder).asGif();
            }
        }
        builder.signature(new StringSignature(options.signature()));
        builder.skipMemoryCache(options.isSkipMemoryCache());
        if (options.getImageSize() != null) {
            int width = getSize(options.getImageSize().getWidth(), view);
            int height = getSize(options.getImageSize().getHeight(), view);
            LogUtils.i("tag ", "load params " + width + "  : " + height);
            builder.override(width, height);
        }
        if (options.getHolderDrawable() > 0) {
            builder.placeholder(options.getHolderDrawable());
        }
        if (options.getHDrawable() != null) {
            builder.placeholder(options.getHDrawable());
        }
        if (options.getErrorDrawable() > 0) {
            builder.error(options.getErrorDrawable());
        }
        if (options.getEDrawable() != null) {
            builder.error(options.getEDrawable());
        }

        if (options.getDiskCacheStrategy() != ImageLoaderOptions.DiskCacheStrategy.DEFAULT) {
            switch (options.getDiskCacheStrategy()) {
                case NONE:
                    builder.diskCacheStrategy(DiskCacheStrategy.NONE);
                    break;
                case All:
                    builder.diskCacheStrategy(DiskCacheStrategy.ALL);
                    break;
                case SOURCE:
                    builder.diskCacheStrategy(DiskCacheStrategy.SOURCE);
                    break;
                case RESULT:
                    builder.diskCacheStrategy(DiskCacheStrategy.RESULT);
                    break;
                default:
                    break;
            }
        }


        return builder;

    }

    private void showImageLast(GenericRequestBuilder mDrawableTypeRequest, ImageLoaderOptions options) {
        View view = options.getViewContainer();
        Context context = getContext(options);
        //是否转化
        if (!options.isTransform()) {
            mDrawableTypeRequest = mDrawableTypeRequest.dontTransform();
        } else {
            if (options.getIsCircle()) {
                mDrawableTypeRequest = mDrawableTypeRequest.transform(new GlideCircleTransform(context));
            }
        }
        if (options.getThumbnail() != null) {
            if (options.getThumbnail().getThumbnailSize() != 0) {
                mDrawableTypeRequest = mDrawableTypeRequest.thumbnail(options.getThumbnail().getThumbnailSize());
            } else if (options.getThumbnail().getThumbnailUrl() != null) {
                RequestManager requestManager = getRequestManager(context);
                if (requestManager != null) {
                    DrawableRequestBuilder<String> thumbnailRequest = requestManager.load(options.getThumbnail().getThumbnailUrl());
                    mDrawableTypeRequest = mDrawableTypeRequest.thumbnail(thumbnailRequest);
                }
            }
        }
        mDrawableTypeRequest.listener(new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        });
        if (view != null) {
            final ImageView img = (ImageView) view;

            // 是否展示一个gif
            if (options.isAsGif()) {
                ((GifRequestBuilder) mDrawableTypeRequest).dontAnimate().into(new SimpleTarget<GifDrawable>() {
                    @Override
                    public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {
                        img.setImageDrawable(resource);
                        resource.start();
                    }
                });
                return;
            }

            if (options.getTarget() != null) {
                mDrawableTypeRequest.into(options.getTarget());
                return;
            }
            mDrawableTypeRequest.into(img);
        } else {
            if (options.getTarget() != null) {
                mDrawableTypeRequest.into(options.getTarget());
            }
        }
    }

    /**
     * 获取资源尺寸
     *
     * @param resSize
     * @return 默认返回原始尺寸
     */
    private int getSize(int resSize, View container) {
        if (resSize <= 0) {
            return SimpleTarget.SIZE_ORIGINAL;
        } else {
            return resSize;
        }
    }

}
