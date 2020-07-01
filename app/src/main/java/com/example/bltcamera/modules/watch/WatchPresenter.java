package com.example.bltcamera.modules.watch;

import android.os.Bundle;

public interface WatchPresenter {

    void onCreate(Bundle extras);

    void initiateBluetooth();

    void onClickCamera();

    void onClickRecord();

    void onTurnFlash(int turnFlash);

}
