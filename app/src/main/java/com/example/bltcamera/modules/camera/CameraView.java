package com.example.bltcamera.modules.camera;

import android.hardware.Camera;

import com.example.bltcamera.commons.BaseView;

public interface CameraView extends BaseView{

    void initViews();

    void startRecording(Camera.PreviewCallback previewCallback);

    void stopRecording();

    void takePhotoViaCamera();

    void setCameraPreview(Camera.PreviewCallback previewCallback);

    void navigateBack();

    boolean isRecording();

    void turnFlashOn();

    void turnFlashOff();

}
