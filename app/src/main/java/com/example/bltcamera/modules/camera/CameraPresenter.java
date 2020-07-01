package com.example.bltcamera.modules.camera;

import android.app.Activity;
import android.os.Bundle;

public interface CameraPresenter {

    void onCreateView(Bundle extras, Activity activity);

    void onClickCamera();

    void onClickStartRecord();

    void onClickStopRecord();

    void onPictureTaken(byte[] data);

    void stopEverything();

    void turnFlash(int turnFlash);

}
