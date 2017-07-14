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
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by on 11/14/2016.
 */

public class SimpleFloatImage extends AppCompatImageView implements View.OnTouchListener{

	private Bitmap				scaleBitmap;
	private Paint				paint;
	private Matrix				matrix;
	private float				oldX, oldY;
	private float				moveX, moveY;
	private float[]				centerPoint;

	private static final int	WIDTH	= 300, HEIGHT = 300;
	private static final float	X_INIT	= 100, Y_INIT = 100;

	public SimpleFloatImage(Context context){
		super(context);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rotate);
		scaleBitmap = Bitmap.createScaledBitmap(bitmap, WIDTH, HEIGHT, false);
		paint = new Paint();
		setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		matrix = new Matrix();
		matrix.setTranslate(X_INIT, Y_INIT);
		centerPoint = new float[2];
		log("init point = " + centerPoint[0] + " x " + centerPoint[1]);
		setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		centerPoint[0] = X_INIT + WIDTH / 2;
		centerPoint[1] = Y_INIT + HEIGHT / 2;
		matrix.postTranslate(moveX, moveY);
		matrix.mapPoints(centerPoint);
		log("current point = " + centerPoint[0] + " x " + centerPoint[1]);
		canvas.drawBitmap(scaleBitmap, matrix, paint);
	}

	private void log(String msg){
		Log.e("SimpleFloatImage", msg);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event){
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			oldX = event.getX();
			oldY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			moveX = event.getX() - oldX;
			moveY = event.getY() - oldY;
			invalidate();
			oldX = event.getX();
			oldY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}
		return true;
	}
}
