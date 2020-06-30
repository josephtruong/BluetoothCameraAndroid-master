package com.example.bltcamera.modules.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import com.example.bltcamera.R;
import com.example.bltcamera.commons.BaseActivity;
import com.example.bltcamera.utils.CodeSnippet;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Created by hmspl on 7/2/16.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraActivity extends BaseActivity implements CameraView, View.OnClickListener, SurfaceHolder.Callback {

    private Camera mCamera;

    private SurfaceView mSurfaceView;

    private View mRecordbutton;

    private boolean recording = false;

    private SurfaceHolder mSurfaceHolder;

    private MediaRecorder mMediaRecorder;

    private CameraPresenter mCameraPresenter;

    private CameraManager camManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mCameraPresenter = CameraPresenterImpl.newInstance(this);
        mCameraPresenter.onCreateView(getIntent().getExtras());
    }

    @Override
    public void initViews() {
        mSurfaceView = (SurfaceView) findViewById(R.id.activity_camera_surface);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mRecordbutton = findViewById(R.id.record);
        mRecordbutton.setOnClickListener(this);

        findViewById(R.id.camera).setOnClickListener(this);
    }

    @Override
    public void startRecording(Camera.PreviewCallback previewCallback) {
        recording = true;
        mCamera.unlock();
        mRecordbutton.setBackgroundResource(R.drawable.red_circle_background);
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setOutputFile("/sdcard/Video.mp4");
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mCamera.startPreview();
            mCamera.setPreviewCallback(previewCallback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void turnFlash(int turnFlash) {
        Toast.makeText(this, String.valueOf(turnFlash), Toast.LENGTH_SHORT).show();
         if (turnFlash == 1){
             turnFlashlightOn();
         } else {
           turnFlashlightOff();
         }
    }

    private void turnFlashlightOn()  {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
               String cameraId = null;
               if (camManager != null) {
                   try {
                       cameraId = camManager.getCameraIdList()[0];
                       camManager.setTorchMode(cameraId, true);
                   } catch (CameraAccessException e) {
                       e.printStackTrace();
                   }
               }
        } else {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(previewCallback);
        }
    }

    private void turnFlashlightOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            if (camManager != null) {
                try {
                    cameraId = camManager.getCameraIdList()[0];
                    camManager.setTorchMode(cameraId, false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(parameters);
            mCamera.stopPreview();
        }
    }

    @Override
    public void stopRecording() {
        recording = false;
        mRecordbutton.setBackgroundResource(R.drawable.circle_background);
        try {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mCamera.lock();
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    byte[] dataCamera;
    @Override
    public void takePhotoViaCamera() {
        mCamera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

            }
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] data, Camera camera) {
                dataCamera = data;
                if (ActivityCompat.checkSelfPermission(CameraActivity.this,  Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCameraPresenter.onPictureTaken(data);
                    mCamera.startPreview();
                } else {
                   requestPermission(data);
                }

            }
        });
    }

    private void requestPermission(byte[] data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1002);
        } else {
            mCameraPresenter.onPictureTaken(data);
            mCamera.startPreview();
        }
    }

    private void requestPermission(Camera.PreviewCallback data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},1001);
        } else {
            mCamera.setPreviewCallback(data);
        }
    }

    Camera.PreviewCallback previewCallback;
    @Override
    public void setCameraPreview(Camera.PreviewCallback previewCallback) {
        this.previewCallback = previewCallback;

        if (ActivityCompat.checkSelfPermission(CameraActivity.this,  Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCamera.setPreviewCallback(previewCallback);
        } else {
            requestPermission(previewCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == 1001){
             if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                 mCameraPresenter.onPictureTaken(dataCamera);
                 mCamera.startPreview();
             }else {
                 Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
             }
         }

        if (requestCode == 1002){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mCameraPresenter.onPictureTaken(dataCamera);
                mCamera.startPreview();
            }else {
                Toast.makeText(this, "Write image permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void navigateBack() {
        kill();
        finish();
    }

    @Override
    public boolean isRecording() {
        return recording;
    }

    private void kill() {
        try {
            stopRecording();
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCameraPresenter.stopEverything();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                mCameraPresenter.onClickCamera();
                break;
            case R.id.record:
                if (!recording) {
                    mCameraPresenter.onClickStartRecord();
                } else {
                    mCameraPresenter.onClickStopRecord();
                }
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        if (mCamera != null) {
            Camera.Parameters p = mCamera.getParameters();
            List<Camera.Size> supportedSizes = p.getSupportedPictureSizes();
            Camera.Size bestSize = null;
            for (Camera.Size camSize : supportedSizes) {
                Log.d("NhanNATC", "=== Camsize : " + camSize.width + " = " + camSize.height);
                if (bestSize == null || (bestSize != null && bestSize.width < camSize.width))
                    bestSize = camSize;
            }
            p.setPictureSize(bestSize.width, bestSize.height);
//            p.setPreviewSize(mDMetrics.widthPixels, mDMetrics.heightPixels);
            p.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            p.setPictureFormat(PixelFormat.JPEG);
            p.setJpegQuality(100);
            List<String> focusModes = p.getSupportedFocusModes();
            Log.d("NhanNATC","===focusModes=" + focusModes);
            if (focusModes.contains(p.FOCUS_MODE_CONTINUOUS_PICTURE))
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//            p.setPreviewSize(320, 240);
//            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(p);
            if (Integer.parseInt(Build.VERSION.SDK) >= 8)
                CodeSnippet.setDisplayOrientation(mCamera, 90);
            else {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    p.set("orientation", "portrait");
                    p.set("rotation", 90);
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    p.set("orientation", "landscape");
                    p.set("rotation", 90);
                }
            }
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        kill();
    }
}
