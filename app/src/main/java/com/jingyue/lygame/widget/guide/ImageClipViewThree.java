package com.jingyue.lygame.widget.guide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.jingyue.lygame.utils.WebDataControl;

import java.io.IOException;

/**
 * Created by zhanglei on 2017/6/16.
 */

public class ImageClipViewThree extends View {
    private String[] indeStr = new String[]{"电子竞技", "动作", "回合", "塔防"};
    private Bitmap lefttopImage;
    private Bitmap firstImage;
    private Bitmap leftdownImage;
    private Bitmap righttopImage;
    private Bitmap rightdownImage;
    private Matrix mMatrix;
    private Path lefttop;
    private Path righttop;
    private Path leftdown;
    private Path rightdown;
    private Paint paint;


    private int state = -1;
    private final int START = 1;
    private static int currentSelected = 2;
    private Context c;

    public ImageClipViewThree(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        c = context;
    }

    public ImageClipViewThree(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
    }

    public ImageClipViewThree(Context context) {
        super(context);
        c = context;
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        currentSelected = 2;
        WebDataControl.saveUserLabel3(indeStr[currentSelected - 1]);

        try {
            lefttopImage = BitmapFactory.decodeStream(getContext().getAssets().open("play_way1.webp"));
            righttopImage = BitmapFactory.decodeStream(getContext().getAssets().open("play_way2.webp"));
            leftdownImage = BitmapFactory.decodeStream(getContext().getAssets().open("play_way3.webp"));
            rightdownImage = BitmapFactory.decodeStream(getContext().getAssets().open("play_way4.webp"));
            firstImage = righttopImage;//BitmapFactory.decodeStream(getContext().getAssets().open("play_way2.webp"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        initMatrix();
        initPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (firstImage != null) {
            canvas.save();
            canvas.clipPath(lefttop);
            canvas.drawBitmap(firstImage, mMatrix, paint);
            canvas.restore();
            canvas.save();
            canvas.clipPath(righttop);
            canvas.drawBitmap(firstImage, mMatrix, paint);
            canvas.restore();
            canvas.save();
            canvas.clipPath(leftdown);
            canvas.drawBitmap(firstImage, mMatrix, paint);
            canvas.restore();
            canvas.save();
            canvas.clipPath(rightdown);
            canvas.drawBitmap(firstImage, mMatrix, paint);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInsideLeftTop(event)) {
                    if (currentSelected == 1) {
                    } else {
                        currentSelected = 1;
                        firstImage = lefttopImage;
                    }
                } else if (isInsideRightTop(event)) {
                    if (currentSelected == 2) {
                    } else {
                        currentSelected = 2;
                        firstImage = righttopImage;
                    }

                } else if (isInsideLeftDown(event)) {
                    if (currentSelected == 3) {
                    } else {
                        currentSelected = 3;
                        firstImage = leftdownImage;
                    }
                } else if (isInsideRightDown(event)) {
                    if (currentSelected == 4) {
                    } else {
                        currentSelected = 4;
                        firstImage = rightdownImage;
                    }

                }
                break;
        }
        WebDataControl.saveUserLabel3(indeStr[currentSelected - 1]);
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (state > 0) {
            return;
        }
        currentSelected = 1;
        state = START;
        init();
    }

    private void initMatrix() {
        mMatrix = new Matrix();

        float w = getWidth();
        float h = getHeight();

        float scaleX = w / lefttopImage.getWidth();
        float scaleY = h / lefttopImage.getHeight();
        mMatrix.setScale(scaleX, scaleY);
    }

    private void initPath() {
        float w = getWidth();
        float h = getHeight();
        float bx = w / 2;

        float by1 = 5 * h / 18;
        float by2 = h * 2 / 3;
        float by3 = h / 3;
        float by4 = h / 2;

        lefttop = new Path();
        righttop = new Path();
        leftdown = new Path();
        rightdown = new Path();

        lefttop.moveTo(0, 0);
        lefttop.lineTo(w, 0);
        lefttop.lineTo(w, by1);
        lefttop.lineTo(0, by3);
        lefttop.lineTo(0, 0);
        lefttop.close();

        righttop.moveTo(0, by3);
        righttop.lineTo(w, by1);
        righttop.lineTo(w, by2);
        righttop.lineTo(0, by3);
        righttop.close();

        leftdown.moveTo(0, by3);
        leftdown.lineTo(bx, by4);
        leftdown.lineTo(bx, h);
        leftdown.lineTo(0, h);
        leftdown.lineTo(0, by3);
        leftdown.close();

        rightdown.moveTo(bx, by4);
        rightdown.lineTo(w, by2);
        rightdown.lineTo(w, h);
        rightdown.lineTo(bx, h);
        rightdown.lineTo(bx, by4);
        rightdown.close();

    }

    private boolean isInsideLeftTop(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();

        if (x > w || y > h) {
            return false;
        }
        return x / w + 18 * y / h < 6 && x > 0 && y > 0;
    }

    private boolean isInsideRightTop(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();

        if (x < 0) {
            return false;
        }
        return (x / w + 18 * y / h > 6) && (((x / w) + 1) > (3 * y / h)) && x < w;
    }

    private boolean isInsideRightDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();
        float bx = w / 2;
        float by = h / 2;

        if (x < bx || x > w || y > h) {
            return false;
        }
        return ((x / w) + 1) < 3 * y / h && (x > bx);
    }

    private boolean isInsideLeftDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();
        float bx = w / 2;
        float by = h / 2;

        if (x < 0 || x > bx || y > h) {
            return false;
        }
        return ((x / w) + 1) < 3 * y / h || (x < bx);
    }
}
