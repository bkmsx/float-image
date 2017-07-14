package com.hecorat.addimage;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FloatImage.OnFloatImageListener {
    RelativeLayout mMainLayout;
    List<FloatImage> stickers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        for (int i = 0; i < 2; i++) {
            FloatImage floatImage = new FloatImage(this);
            floatImage.setCallback(this);
            mMainLayout.addView(floatImage);
            stickers.add(floatImage);
        }
    }

    @Override
    public void onDeleteStickerClick(FloatImage sticker) {
        mMainLayout.removeView(sticker);
    }

    @Override
    public void onStickerClick (float x, float y, FloatImage sticker) {
        for (FloatImage floatImage : stickers) {
            if (!floatImage.equals(sticker)) {
                if (x < floatImage.highX && x > floatImage.lowX && y < floatImage.highY && y > floatImage.lowY) {
                    floatImage.selectSticker();
                } else {
                    floatImage.unselectSticker();
                }
            }
        }
    }
}
