package com.example.bltcamera.modules.camera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;

public class CameraPresenterImpl implements CameraPresenter, CameraModelImpl.CameraModelListener {

    private CameraView mCameraView;

    private CameraModel mCameraModel;

    public CameraPresenterImpl(CameraView cameraView) {
        mCameraView = cameraView;
        mCameraModel = CameraModelImpl.newInstance(this);
    }

    public static CameraPresenter newInstance(CameraView cameraView) {
        return new CameraPresenterImpl(cameraView);
    }

    @Override
    public void onCreateView(Bundle extras, Activity activity) {
        mCameraModel.saveDeviceDetail(extras, activity);
        mCameraView.initViews();
    }

    @Override
    public void onClickCamera() {
        mCameraView.takePhotoViaCamera();
    }

    @Override
    public void onCameraCommandReceived() {
        mCameraView.takePhotoViaCamera();
    }

    @Override
    public void onRecordCommandReceived() {
        if (mCameraView.isRecording()) {
            mCameraView.stopRecording();
        } else {
            mCameraView.startRecording(mCameraModel.getPreviewCallback());
        }
    }

    @Override
    public void onClickStartRecord() {
        mCameraView.startRecording(mCameraModel.getPreviewCallback());
    }

    @Override
    public void onClickStopRecord() {
        mCameraView.stopRecording();
    }


    @Override
    public void onPictureTaken(byte[] data) {
        mCameraModel.saveImage(data);
    }

    @Override
    public void stopEverything() {
        mCameraModel.stopSocket();
    }

    @Override
    public void onConnectionSuccessful(Camera.PreviewCallback previewCallback) {
        mCameraView.setCameraPreview(previewCallback);
    }

    @Override
    public void onConnectionFailed() {
        mCameraView.showToast("Connection failed");
        mCameraView.navigateBack();
    }

    @Override
    public void onLostConnection() {
        mCameraView.showToast("Connection lost");
        mCameraView.navigateBack();
    }

    @Override
    public void turnFlashOn() {
        mCameraView.turnFlashOn();
    }

    @Override
    public void turnFlashOff() {
        mCameraView.turnFlashOff();
    }

    @Override
    public void turnFlash(int turnFlash) {

    }
}
