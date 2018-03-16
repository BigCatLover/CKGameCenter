package com.jingyue.lygame.widget.guide;

/**
 * Created by zhanglei on 2017/6/15.
 */

import android.annotation.SuppressLint;
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

@SuppressLint("NewApi")
public class ImageClipView extends View {
    private final String[] indexStr = new String[]{"中国风", "日韩风", "写实风", "欧美风"};
    private Bitmap firstImage;
    private Bitmap lefttopImage;
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
    private Context c;
    private int currentSelected = 2;

    public ImageClipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        c = context;
    }

    public ImageClipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        c = context;
    }

    public ImageClipView(Context context) {
        super(context);
        c = context;
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        currentSelected = 2;
        WebDataControl.saveUserLabel1(indexStr[currentSelected - 1]);
        try {
            lefttopImage = BitmapFactory.decodeStream(getContext().getAssets().open("style1.webp"));
            righttopImage = BitmapFactory.decodeStream(getContext().getAssets().open("style2.webp"));
            leftdownImage = BitmapFactory.decodeStream(getContext().getAssets().open("style3.webp"));
            rightdownImage = BitmapFactory.decodeStream(getContext().getAssets().open("style4.webp"));
            firstImage = righttopImage;//BitmapFactory.decodeStream(getContext().getAssets().open("style2.webp"));
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
        invalidate();
        WebDataControl.saveUserLabel1(indexStr[currentSelected - 1]);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (state > 0) {
            return;
        }
        state = START;
        currentSelected = 1;
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
        float bx1 = w * 5 / 8;
        float by = h / 2;
        float bx2 = w / 2;
        float bx3 = w * 11 / 16;

        lefttop = new Path();
        righttop = new Path();
        leftdown = new Path();
        rightdown = new Path();

        lefttop.moveTo(0, 0);
        lefttop.lineTo(bx1, 0);
        lefttop.lineTo(bx2, by);
        lefttop.lineTo(0, by);
        lefttop.lineTo(0, 0);
        lefttop.close();

        righttop.moveTo(bx1, 0);
        righttop.lineTo(w, 0);
        righttop.lineTo(w, by);
        righttop.lineTo(bx2, by);
        righttop.lineTo(bx1, 0);
        righttop.close();

        leftdown.moveTo(0, by);
        leftdown.lineTo(bx3, by);
        leftdown.lineTo(bx3, h);
        leftdown.lineTo(0, h);
        leftdown.lineTo(0, by);
        leftdown.close();

        rightdown.moveTo(bx3, by);
        rightdown.lineTo(w, by);
        rightdown.lineTo(w, h);
        rightdown.lineTo(bx3, h);
        rightdown.lineTo(bx3, by);
        rightdown.close();

    }

    private boolean isInsideLeftTop(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();
        float by = h / 2;

        if (y < 0 || x < 0) {
            return false;
        }
        return (8 * x / w + y / h < 5) && (y < by);
    }

    private boolean isInsideRightTop(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();
        float by = h / 2;

        if (x > w || y < 0) {
            return false;
        }
        return (8 * x / w + y / h > 5) && (y < by);
    }

    private boolean isInsideRightDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();
        float bx = w * 11 / 16;
        float by = h / 2;

        if (y > h || x > w) {
            return false;
        }
        return x > bx && y > by;
    }

    private boolean isInsideLeftDown(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float w = getWidth();
        float h = getHeight();
        float bx = w * 11 / 16;
        float by = h / 2;

        if (x < 0 || y > h) {
            return false;
        }
        return x < bx && y > by;
    }
}
