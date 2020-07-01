package com.example.bltcamera.modules.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;

public interface CameraModel {

    void saveDeviceDetail(Bundle extras, Activity activity);

    void saveImage(byte[] data);

    void stopSocket();

    Camera.PreviewCallback getPreviewCallback();

}
