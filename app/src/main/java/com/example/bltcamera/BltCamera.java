package com.example.bltcamera;

import android.app.Application;

public class BltCamera extends Application {

    private static BltCamera sBltCamera;

    public static BltCamera getInstance() {
        return sBltCamera;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sBltCamera = BltCamera.this;
    }
}
