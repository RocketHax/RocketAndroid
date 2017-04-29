package com.techclutch.rocket.rocketandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import butterknife.ButterKnife;

/**
 * Created by Arman on 4/29/2017.
 */

public class CameraActivity extends Activity implements CameraFragment.IOnImageCaptured {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, CameraFragment.newInstance())
                    .commit();
        }

    }

    @Override
    public void imageCaptured(String imagePath, double bearing) {
        Intent intent = new Intent();
        intent.putExtra("imagepath", imagePath);
        intent.putExtra("bearing", bearing);
        setResult(RESULT_OK, intent);
        finish();
    }
}
