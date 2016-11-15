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
    Bitmap bitmap, mainBitmap, rotateBitmap, scaleBitmap;
    Paint paint;
    RelativeLayout.LayoutParams params;
    Rect rectBorder;

    int width, height;
    int x, y, translateX, translateY;
    float rotation = 0;
    float[] scalePoint, centerPoint, rotatePoint;
    Point initScalePoint, initCenterPoint, initRotatePoint;
    float scaleValue = 1f;
    float widthScale, heightScale;
    boolean isCompact;
    boolean drawBorder;
    int maxDimensionLayout;

    public static final int MAX_DIMENSION = 300;
    public static final int ROTATE_CONSTANT = 30;
    public static final int INIT_X = 300, INIT_Y = 300;

    public FloatImage(Context context, Bitmap bitmap) {
        super(context);
        boolean maxWidth = bitmap.getWidth()>bitmap.getHeight();
        width = maxWidth? MAX_DIMENSION: MAX_DIMENSION*bitmap.getWidth()/bitmap.getHeight();
        height = maxWidth? MAX_DIMENSION*bitmap.getHeight()/bitmap.getWidth():MAX_DIMENSION;
        widthScale = width;
        heightScale = height;
        x = INIT_X;
        y = INIT_Y;
        initRotatePoint = new Point(0, 0);
        initCenterPoint = new Point(width/2, height/2);
        initScalePoint = new Point(width, height);
        rotatePoint = new float[2];
        scalePoint = new float[2];
        centerPoint = new float[2];
        rotatePoint[0] = initRotatePoint.x;
        rotatePoint[1] = initRotatePoint.y;
        scalePoint[0] = initScalePoint.x;
        scalePoint[1] = initScalePoint.y;
        centerPoint[0] = initCenterPoint.x;
        centerPoint[1] = initCenterPoint.y;

        rotateBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rotate);
        scaleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_scale);
        mainBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        paint = new Paint();
        maxDimensionLayout = (int)Math.sqrt(width*width + height*height);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setFullLayout();
        setBackgroundColor(Color.DKGRAY);
        setOnTouchListener(onTouchListener);
        rectBorder = new Rect(0, 0, (int)widthScale, (int)heightScale);
        drawBorder = true;
    }

    private void setCompactLayout(){
        translateX = (int) (maxDimensionLayout-widthScale)/2;
        translateY = (int) (maxDimensionLayout-heightScale)/2;
        log("translateX = "+translateX+" translateY = "+translateY);
        params.width = maxDimensionLayout;
        params.height = maxDimensionLayout;

        params.leftMargin = x - translateX;
        params.topMargin = y - translateY;
        setLayoutParams(params);
        invalidate();
        isCompact = true;
    }

    private void setFullLayout(){
        translateX = x;
        translateY = y;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.topMargin = 0;
        params.leftMargin = 0;
        setLayoutParams(params);
        invalidate();
        isCompact = false;
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
    public void scaleImage(float moveX, float moveY){
        if (Math.abs(scalePoint[0]-centerPoint[0])>100){
            if (scalePoint[0] >= centerPoint[0]) {
                widthScale += moveX;
            } else {
                widthScale -= moveX;
            }
            scaleValue = widthScale/width;
            heightScale = scaleValue*height;
        } else {
            if (scalePoint[1] >= centerPoint[1]){
                heightScale += moveY;
            } else {
                heightScale -= moveY;
            }
            scaleValue = heightScale/height;
            widthScale = scaleValue*width;
        }
        maxDimensionLayout = (int) Math.sqrt(widthScale*widthScale+heightScale*heightScale);
//        rectBorder = new Rect(0, 0, (int)widthScale, (int)heightScale);
        invalidate();
    }

    public void moveImage(int moveX, int moveY) {
        x += moveX;
        y += moveY;
        translateX = x;
        translateY = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Matrix matrix = new Matrix();

        matrix.postTranslate(translateX, translateY);

        rotatePoint[0] = initRotatePoint.x;
        rotatePoint[1] = initRotatePoint.y;
        scalePoint[0] = initScalePoint.x;
        scalePoint[1] = initScalePoint.y;
        centerPoint[0] = initCenterPoint.x;
        centerPoint[1] = initCenterPoint.y;
        log("center init: "+centerPoint[0]+" : "+centerPoint[1]);
        log("rotate init: "+rotatePoint[0]+" : "+rotatePoint[1]);
        log("scale init: "+scalePoint[0]+" : "+scalePoint[1]);
        matrix.mapPoints(centerPoint);

        matrix.postScale(scaleValue, scaleValue, centerPoint[0], centerPoint[1]);

        matrix.postRotate(-rotation, centerPoint[0], centerPoint[1]);
        matrix.mapPoints(scalePoint);
        matrix.mapPoints(rotatePoint);
        log("center: "+centerPoint[0]+" : "+centerPoint[1]);
        log("rotate: "+rotatePoint[0]+" : "+rotatePoint[1]);
        log("scale: "+scalePoint[0]+" : "+scalePoint[1]);
        canvas.drawBitmap(mainBitmap, matrix, paint);
        if(!drawBorder) {
            return;
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.CYAN);
        paint.setPathEffect(new DashPathEffect(new float[] {6,4}, 0));
        canvas.save();
        canvas.setMatrix(matrix);
        canvas.drawRect(rectBorder, paint);
        canvas.restore();
        canvas.drawBitmap(rotateBitmap, (int)rotatePoint[0]-ROTATE_CONSTANT, (int)rotatePoint[1]-ROTATE_CONSTANT, paint);
        canvas.drawBitmap(scaleBitmap, (int) scalePoint[0] - ROTATE_CONSTANT, (int) scalePoint[1]-ROTATE_CONSTANT, paint);
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
                    if (oldX < scalePoint[0]+eps && oldX > scalePoint[0]-eps && oldY < scalePoint[1]+eps && oldY > scalePoint[1]-eps){
                        touch = 1;
                    } else if (oldX < centerPoint[0]+eps && oldX > centerPoint[0]-eps && oldY < centerPoint[1]+eps && oldY > centerPoint[1]-eps) {
                        touch = 2;
                    } else if (oldX < rotatePoint[0]+eps && oldX > rotatePoint[0]-eps && oldY < rotatePoint[1]+eps && oldY > rotatePoint[1]-eps){
                        touch = 3;
                    } else {
                        touch = 0;
                    }

                    isTouch = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    moveX = motionEvent.getX() - oldX;
                    moveY = motionEvent.getY() - oldY;
//                    log("move X= "+moveX+" moveY= "+moveY);
                    if (Math.abs(moveX) >= delta && Math.abs(moveY)>= delta) {
                        isTouch = true;
                    }

                    if (!isTouch || !drawBorder) {
                        return false;
                    }

                    if (touch == 1) {
                        scaleImage(moveX, moveY);
                    }
                    if (touch == 2) {
                        moveImage((int)moveX, (int)moveY);
                    }
                    if (touch == 3) {
                        currentAngle = getAngle(motionEvent.getX(), motionEvent.getY());
                        rotation += (currentAngle-startAngle);
                        invalidate();
                        startAngle = currentAngle;
                    }
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isTouch){
                        performClick();
                    }
                    break;
            }
            return true;
        }

    };
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
    private void log(String msg){
        Log.e("Log for FloatImage",msg);
    }
}
