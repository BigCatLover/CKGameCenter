package com.jingyue.lygame.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.webp.libwebp;
import com.laoyuegou.android.lib.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * webp工具类ß
 */
public class WebpUtils {

    static {
        System.loadLibrary("webp");
    }

    /**
     * webp转换成drawable
     *
     * @param resId
     * @return
     */
    public static Drawable webpToDrawable(int resId) {
        return new BitmapDrawable(getBitmap(resId));
    }

    /**
     * webp转换成drawable
     *
     * @param resId
     * @return
     */
    public static Bitmap getBitmap(int resId) {
        InputStream rawImageStream = Utils.getContext().getResources().openRawResource(resId);
        byte[] data = streamToBytes(rawImageStream);
        return webpToBitmap(data);
    }

    /**
     * webp转换成drawable
     *
     * @return
     */
    public static Bitmap webpToBitmap(byte[] encoded) {

        int[] width = new int[]{0};
        int[] height = new int[]{0};
        byte[] decoded = libwebp.WebPDecodeARGB(encoded, encoded.length, width,
                height);

        int[] pixels = new int[decoded.length / 4];
        ByteBuffer.wrap(decoded).asIntBuffer().get(pixels);

        return Bitmap.createBitmap(pixels, width[0], height[0],
                Bitmap.Config.ARGB_8888);

    }

    public static byte[] streamToBytes(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);
                out.flush();
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out.toByteArray();
    }
}
