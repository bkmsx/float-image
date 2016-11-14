package com.hecorat.addimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by TienDam on 11/14/2016.
 */

public class FloatImage extends ImageView {
    Bitmap bitmap, scaleBitmap;
    Paint paint;

    int width, height;
    int x, y;
    float rotation = 0;
    float point[];
    public FloatImage(Context context, String imagePath) {
        super(context);
        width = 300;
        height = 300;
        x = 500;
        y = 500;
        point = new float[]{0, 0};
        bitmap = BitmapFactory.decodeFile(imagePath);
        scaleBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        paint = new Paint();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
        setBackgroundColor(Color.DKGRAY);
        setOnTouchListener(onTouchListener);
        log(point[0]+" : "+point[1]);
    }

    private double getAngle(double xTouch, double yTouch) {
        double x = xTouch - point[0];
        double y = point[1] - yTouch;

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
    public void scaleImage(int moveX, int moveY){
        width += moveX;
        height += moveY;
        int min = Math.min(width, height);
        width = min;
        height = min;
        scaleBitmap = Bitmap.createScaledBitmap(bitmap, min, min, false);
        invalidate();
    }

    public void moveImage(int moveX, int moveY) {
        x += moveX;
        y += moveY;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        matrix.postTranslate(x, y);
        point[0] = 0;
        point[1] = 0;
        matrix.mapPoints(point);

        matrix.postRotate(-rotation, point[0], point[1]);
        log("new point: "+point[0]+" : "+point[1]);
        canvas.drawBitmap(scaleBitmap, matrix, paint);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        float oldX, oldY, moveX, moveY;
        double startAngle, currentAngle;
        int touch;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    startAngle = getAngle(oldX, oldY);
                    int eps = 150;
                    if (oldX < point[0]+eps && oldX > point[0]-eps && oldY < point[1]+eps && oldY > point[1]-eps) {
                        touch = 0;
                    } else {
                        touch = 1;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
                    if (touch == 0) {

                        moveImage((int)moveX, (int)moveY);

                    }
                    if (touch == 1) {
                        currentAngle = getAngle(motionEvent.getX(), motionEvent.getY());
                        rotation += (currentAngle-startAngle);
                        invalidate();
                        startAngle = currentAngle;
                        scaleImage((int)moveX, (int)moveY);
                    }
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return true;
        }
    };

    private void log(String msg){
        Log.e("Log for FloatImage",msg);
    }
}
