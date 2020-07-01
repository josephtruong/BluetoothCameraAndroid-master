package com.example.bltcamera.modules.watch;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.bltcamera.R;
import com.example.bltcamera.commons.BaseActivity;
import com.example.bltcamera.commons.widgets.CTextView;
import com.example.bltcamera.preview.PreviewActivity;

import java.io.File;
import java.io.FileOutputStream;

public class WatchActivity extends BaseActivity implements WatchView, View.OnClickListener {

    private static final int BT_ENABLE_REQUEST = 33;

    private Dialog dialog;

    private View mDummyView;

    private View mExitButton;

    private CTextView mProgressText;

    private ImageView mPreviewImageView;

    private WatchPresenter mWatchPresenter;

    private ToggleButton toggleButton;

    private  boolean isPhoto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        mPreviewImageView = findViewById(R.id.activity_watch_imageView);
        findViewById(R.id.capture).setOnClickListener(this);
        findViewById(R.id.record).setOnClickListener(this);

        toggleButton = findViewById(R.id.turnFlash);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                 if (isChecked) {
                     mWatchPresenter.onTurnFlash(1);
                 } else {
                     mWatchPresenter.onTurnFlash(0);
                 }
            }
        });


    }

    @Override
    public void promptUserToEnableBluetooth() {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableIntent, BT_ENABLE_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWatchPresenter = WatchPresenterImpl.newInstance(this);
        mWatchPresenter.onCreate(getIntent().getExtras());
        mWatchPresenter.initiateBluetooth();
    }

    @Override
    public boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    @Override
    public void closeWatchView() {
        finish();
    }

    @Override
    public void showFrameInImageView(Bitmap image) {
        mPreviewImageView.setImageBitmap(image);
        if (isPhoto) {
           isPhoto = false;
           Toast.makeText(this, "Successful photography", Toast.LENGTH_SHORT).show();
       }
    }

    private void saveImage(Bitmap bitmap){
        ContextWrapper cw = new ContextWrapper(getContext());
        File directory = cw.getDir("EVN_ECMR_BSTAR_IMAGE", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        FileOutputStream fos = null;
        try {
            String imagePath = directory + File.separator + "image_camera_dv" + System.currentTimeMillis() + ".png";
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Intent intent = new Intent(getActivity(), PreviewActivity.class);
            intent.putExtra(PreviewActivity.EXTRA_IMAGE_LOCATION, imagePath);
            intent.putExtra(PreviewActivity.EXTRA_FILE_PATH, directory.getAbsolutePath());
            startActivity(intent);
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
    }

    @Override
    public void showProgressDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = View.inflate(this, R.layout.inflater_waiting_for_connection, null);
        mDummyView = dialogView.findViewById(R.id.dummy);
        mExitButton = dialogView.findViewById(R.id.progress_exit);
        mProgressText = dialogView.findViewById(R.id.progress_message);
        mExitButton.setOnClickListener(this);

        dialog.setCancelable(false);
        dialog.setContentView(dialogView);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @Override
    public void hideProgressDialog() {
        dialog.dismiss();
    }

    @Override
    public void setProgressMessage(String name) {
        mProgressText.setText(name);
        mDummyView.setVisibility(View.GONE);
        mExitButton.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.progress_exit:
                dialog.dismiss();
                onBackPressed();
                break;
            case R.id.capture:
                isPhoto = true;
                mWatchPresenter.onClickCamera();
                break;
            case R.id.record:
                mWatchPresenter.onClickRecord();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BT_ENABLE_REQUEST) {
            if (resultCode == RESULT_OK) {
                mWatchPresenter.initiateBluetooth();
            } else {
                finish();
            }
        }
    }
}
