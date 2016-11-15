package com.hecorat.addimage;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {
    RelativeLayout mMainLayout;
    FloatImage floatImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainLayout = (RelativeLayout) findViewById(R.id.activity_main);
        String imagePath = Environment.getExternalStorageDirectory() + "/a.png";
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        floatImage = new FloatImage(this, bitmap);
        mMainLayout.addView(floatImage);
    }


}
