package com.hecorat.addimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by on 11/14/2016.
 */

public class FloatImage extends AppCompatImageView {

    public float rotation = 0;
    public float scaleValue = 1f;
    public int translateX = 0;
    public int translateY = 0;
    public String stickerPath = "http://www.unixstickers.com/image/data/stickers/gruntjs/Grunt.sh.png";

    private Bitmap bitmapSticker, mainBitmap, rotateBitmap, scaleBitmap, deleteBitmap;
    private Paint paint = new Paint();
    private RelativeLayout.LayoutParams params;
    private Rect rectBorder;

    private int width, height;
    private float widthScale, heightScale;
    private int x, y;
    public float lowX, highX, lowY, highY;

    private float[] scalePoint = new float[2];
    private float[] centerPoint = new float[2];
    private float[] rotatePoint = new float[2];
    private float[] deletePoint = new float[2];
    private Point initScalePoint, initCenterPoint, initRotatePoint, initDeletePoint;

    private boolean isCompact;
    private boolean drawBorder;
    private int maxDimensionLayout;
    private Matrix matrix = new Matrix();
    private DashPathEffect dashPathEffect = new DashPathEffect(new float[]{6, 4}, 0);
    private OnFloatImageListener callback;

    public static final int MAX_DIMENSION = 300;
    public static final int ROTATE_CONSTANT = 30;
    public static final int INIT_X = 300, INIT_Y = 300;

    public FloatImage(Context context) {
        super(context);
        Picasso.with(context).load(stickerPath).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                bitmapSticker = bitmap;
                init();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void init() {
        boolean maxWidth = bitmapSticker.getWidth() > bitmapSticker.getHeight();
        width = maxWidth ? MAX_DIMENSION : MAX_DIMENSION * bitmapSticker.getWidth() / bitmapSticker.getHeight();
        height = maxWidth ? MAX_DIMENSION * bitmapSticker.getHeight() / bitmapSticker.getWidth() : MAX_DIMENSION;
        widthScale = width;
        heightScale = height;
        x = INIT_X;
        y = INIT_Y;
        initRotatePoint = new Point(0, 0);
        initCenterPoint = new Point(width / 2, height / 2);
        initScalePoint = new Point(width, height);
        initDeletePoint = new Point(width, 0);

        rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rotate);
        scaleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_scale);
        deleteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete);
        mainBitmap = Bitmap.createScaledBitmap(bitmapSticker, width, height, false);

        maxDimensionLayout = (int) Math.sqrt(width * width + height * height);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setFullLayout();
        setOnTouchListener(onTouchListener);
        setOnClickListener(onClickListener);
        rectBorder = new Rect(0, 0, (int) widthScale, (int) heightScale);
        drawBorder = true;
        matrix = new Matrix();
        paint = new Paint();
        updateStickerLimit();
    }

    public void selectSticker() {
        setFullLayout();
        drawBorder = true;
    }

    public void unselectSticker() {
        setCompactLayout();
        drawBorder = false;
    }

    private void setCompactLayout() {
        translateX = (int) (maxDimensionLayout - widthScale) / 2;
        translateY = (int) (maxDimensionLayout - heightScale) / 2;
        params.width = maxDimensionLayout;
        params.height = maxDimensionLayout;
        params.leftMargin = x - translateX;
        params.topMargin = y - translateY;
        setLayoutParams(params);
        updateStickerLimit();
        invalidate();
        isCompact = true;
    }

    private void setFullLayout() {
        translateX = x;
        translateY = y;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 0;
        params.leftMargin = 0;
        setLayoutParams(params);
        invalidate();
        isCompact = false;
        bringToFront();
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - centerPoint[0];
        double y = centerPoint[1] - yTouch;

        switch (getQuadrant(x, y)) {

            case 1:
                return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 2:
                return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            case 3:
                return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
            case 4:
                return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
            default:
                return 0;
        }
    }

    private static int getQuadrant(double x, double y) {
        if (x >= 0) {
            return y >= 0 ? 1 : 4;
        } else {
            return y >= 0 ? 2 : 3;
        }
    }

    public void scaleImage(float moveX, float moveY) {
        if (Math.abs(scalePoint[0] - centerPoint[0]) > 100) {
            if (scalePoint[0] >= centerPoint[0]) {
                widthScale += moveX;
            } else {
                widthScale -= moveX;
            }
            scaleValue = widthScale / width;
            heightScale = scaleValue * height;
        } else {
            if (scalePoint[1] >= centerPoint[1]) {
                heightScale += moveY;
            } else {
                heightScale -= moveY;
            }
            scaleValue = heightScale / height;
            widthScale = scaleValue * width;
        }
        maxDimensionLayout = (int) Math.sqrt(widthScale * widthScale + heightScale * heightScale);
        updateStickerLimit();
        invalidate();
    }

    public void moveImage(int moveX, int moveY) {
        x += moveX;
        y += moveY;
        translateX = x;
        translateY = y;

        invalidate();
    }

    private void updateStickerLimit() {
        lowX = params.leftMargin;
        lowY = params.topMargin;
        highX = lowX + maxDimensionLayout;
        highY = lowY + maxDimensionLayout;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        matrix.reset();
        // translate pic
        matrix.postTranslate(translateX, translateY);

        rotatePoint[0] = initRotatePoint.x;
        rotatePoint[1] = initRotatePoint.y;
        scalePoint[0] = initScalePoint.x;
        scalePoint[1] = initScalePoint.y;
        centerPoint[0] = initCenterPoint.x;
        centerPoint[1] = initCenterPoint.y;
        deletePoint[0] = initDeletePoint.x;
        deletePoint[1] = initDeletePoint.y;

        matrix.mapPoints(centerPoint);
        // scale and rotate pic
        matrix.postScale(scaleValue, scaleValue, centerPoint[0], centerPoint[1]);
        matrix.postRotate(-rotation, centerPoint[0], centerPoint[1]);

        matrix.mapPoints(scalePoint);
        matrix.mapPoints(rotatePoint);
        matrix.mapPoints(deletePoint);
        // draw main pic
        canvas.drawBitmap(mainBitmap, matrix, paint);
        if (!drawBorder) {
            return;
        }
        // draw dash frame
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.CYAN);
        paint.setPathEffect(dashPathEffect);
        canvas.save();
        canvas.setMatrix(matrix);
        canvas.drawRect(rectBorder, paint);
        canvas.restore();
        // draw rotate, delete, scale button
        canvas.drawBitmap(rotateBitmap, (int) rotatePoint[0] - ROTATE_CONSTANT, (int) rotatePoint[1] - ROTATE_CONSTANT, paint);
        canvas.drawBitmap(scaleBitmap, (int) scalePoint[0] - ROTATE_CONSTANT, (int) scalePoint[1] - ROTATE_CONSTANT, paint);
        canvas.drawBitmap(deleteBitmap, (int) deletePoint[0] - ROTATE_CONSTANT, (int) deletePoint[1] - ROTATE_CONSTANT, paint);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {

        float oldX, oldY, moveX, moveY;
        double startAngle, currentAngle;
        int touch = 0;
        float delta = 10;
        boolean isTouch;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    bringToFront();
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    startAngle = getAngle(oldX, oldY);
                    int eps = 75;
                    if (oldX < scalePoint[0] + eps && oldX > scalePoint[0] - eps && oldY < scalePoint[1] + eps && oldY > scalePoint[1] - eps) {
                        touch = 1;
                    } else if (oldX < centerPoint[0] + eps && oldX > centerPoint[0] - eps && oldY < centerPoint[1] + eps && oldY > centerPoint[1] - eps) {
                        touch = 2;
                    } else if (oldX < rotatePoint[0] + eps && oldX > rotatePoint[0] - eps && oldY < rotatePoint[1] + eps && oldY > rotatePoint[1] - eps) {
                        touch = 3;
                    } else if (oldX < deletePoint[0] + eps && oldX > deletePoint[0] - eps && oldY < deletePoint[1] + eps && oldY > deletePoint[1] - eps) {
                        touch = 4;
                    } else {
                        touch = 0;
                    }

                    isTouch = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    // log("move X= "+moveX+" moveY= "+moveY);
                    if (Math.abs(moveX) >= delta && Math.abs(moveY) >= delta) {
                        isTouch = true;
                    }

                    if (!isTouch || !drawBorder) {
                        return false;
                    }

                    if (touch == 1) {
                        scaleImage(moveX, moveY);
                    }
                    if (touch == 2) {
                        moveImage((int) moveX, (int) moveY);
                    }
                    if (touch == 3) {
                        currentAngle = getAngle(motionEvent.getX(), motionEvent.getY());
                        rotation += (currentAngle - startAngle);
                        invalidate();
                        startAngle = currentAngle;
                    }
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isTouch) {
                        if (touch == 4) {
                            deleteSticker();
                        } else {
                            performClick();
                            if (callback != null) {
                                callback.onStickerClick(motionEvent.getX(), motionEvent.getY(), FloatImage.this);
                            }
                        }
                    }
                    break;
            }
            return true;
        }

    };

    private void deleteSticker() {
        if (callback != null) {
            callback.onDeleteStickerClick(this);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (drawBorder) {
                drawBorder = false;
                setCompactLayout();

            } else {
                drawBorder = true;
                setFullLayout();
                bringToFront();
            }

        }
    };

    public void setCallback(OnFloatImageListener callback) {
        this.callback = callback;
    }

    public interface OnFloatImageListener {
        void onDeleteStickerClick(FloatImage sticker);
        void onStickerClick(float x, float y, FloatImage sticker);
    }

    private void log(String msg) {
        Log.e("Log for FloatImage", msg);
    }
}
