package com.jingyue.lygame.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.jingyue.lygame.BuildConfig;
import com.jingyue.lygame.R;
import com.jingyue.lygame.constant.AppConstants;
import com.jingyue.lygame.utils.rxJava.BaseObserver;
import com.laoyuegou.android.common.glide.IGlideLoaderstrategy;
import com.laoyuegou.android.lib.utils.LogUtils;
import com.laoyuegou.android.lib.utils.ToastUtils;
import com.laoyuegou.android.lib.utils.Utils;
import com.lygame.libadapter.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class ImageUtils {

    public static void saveImage(final String url) {
        if (TextUtils.isEmpty(url)) {
            LogUtils.e("url is empty!!");
        }
        ImageLoader.getInstance().downloadImage(Utils.getContext(), url, new IGlideLoaderstrategy.IDownloadCallback() {
            @Override
            public void onDownloadSuccess(final File file) {
                if (file == null) {
                    ToastUtils.showShort(R.string.a_0174);
                    return;
                }
                Observable.create(new ObservableOnSubscribe<Uri>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Uri> e) throws Exception {
                        final File newFile = new File(AppConstants.getSavePath(url));
                        FileUtils.copyfile(file, newFile, true);
                        Uri localUri = Uri.fromFile(newFile);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            localUri = FileProvider.getUriForFile(Utils.getContext(),
                                    BuildConfig.APPLICATION_ID + ".fileProvider", file);
                        }
                        MediaStore.Images.Media.insertImage(Utils.getContext().getContentResolver(),
                                newFile.getAbsolutePath(),newFile.getName(),null);
                        Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
                        Utils.getContext().sendBroadcast(localIntent);
                        e.onNext(localUri);
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserver<Uri>("ImageUtils#onDownloadSuccess") {
                            @Override
                            public void onNext(@NonNull Uri localUri) {
                                StringBuilder sb = new StringBuilder();
                                sb.append(Utils.getContext().getResources().getString(R.string.a_0175));
                                sb.append(localUri.toString());
                                ToastUtils.showLongSafe(sb.toString());
                            }
                        });
            }

            @Override
            public void onDownFailed() {
                ToastUtils.showShort(R.string.a_0176);
            }
        });
    }

    /**
     * file转换到Bitmap
     *
     * @param file
     * @return
     */
    public static Bitmap fileToBitmap(File file) {
        if (null == file || !file.exists()) {
            return null;
        }
        String filePath = file.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        return bitmap;
    }

    /**
     * bitmap 转换成二进制
     *
     * @param bmp
     * @param width   宽
     * @param height  高
     * @param maxSize 最大lenght k
     * @return
     */
    public static byte[] getShareThumbData(Bitmap bmp, int width, int height, int maxSize) {
        if (null == bmp) {
            return new byte[0];
        }
        Bitmap bitmap = Bitmap.createScaledBitmap(bmp, width, height, true);
        if (!bmp.isRecycled()) {
            bmp.recycle();
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 100;
        // Store the bitmap into output stream(no compress)
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options = Math.max(0, options - 5);
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, os);
            if (options <= 0) {
                break;
            }
        }
        byte[] b = os.toByteArray();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        if (null != os) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

}
