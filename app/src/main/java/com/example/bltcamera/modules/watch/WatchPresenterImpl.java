package com.example.bltcamera.modules.watch;

import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;

import com.example.bltcamera.commons.ThreadHandler;

public class WatchPresenterImpl implements WatchPresenter, WatchModelImpl.WatchModelListener {

    private WatchView mWatchView;

    private WatchModel mWatchModel;

    public WatchPresenterImpl(WatchView watchView) {
        mWatchView = watchView;
        mWatchModel = WatchModelImpl.newInstance(this);
    }

    public static WatchPresenter newInstance(WatchView watchView) {
        return new WatchPresenterImpl(watchView);
    }

    @Override
    public void onCreate(Bundle extras) {

    }

    @Override
    public void initiateBluetooth() {
        checkForBluetoothConnection();
    }

    @Override
    public void onClickCamera() {
        mWatchModel.commandToTakePhoto();
    }

    @Override
    public void onTurnFlash(int turnFlash) {
        mWatchModel.turnFlash(turnFlash);
    }

    @Override
    public void onClickRecord() {
        mWatchModel.commandToStartStopRecording();
    }

    private void checkForBluetoothConnection() {
        if (mWatchView.isBluetoothEnabled()) {
            if (!mWatchModel.isConnected()) {
                mWatchModel.listenForBluetoothConnection();
                mWatchView.showProgressDialog();
            }
        } else {
            mWatchView.promptUserToEnableBluetooth();
        }
    }

    @Override
    public void onConnectionFailed() {
        mWatchView.showToast("Connection lost");
        mWatchView.closeWatchView();
    }

    @Override
    public void onGotFrameBitmap(Bitmap image) {
        mWatchView.showFrameInImageView(image);
    }

    private   Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        mWatchView.setProgressMessage(device.getName());
        ThreadHandler.getInstance().doInBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dismissProgressAfterTimer();
            }
        });
    }

    private void dismissProgressAfterTimer() {
        ThreadHandler.getInstance().doInForground(new Runnable() {
            @Override
            public void run() {
                mWatchView.hideProgressDialog();
            }
        });
    }
}
