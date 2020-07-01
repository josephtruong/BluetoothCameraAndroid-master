package com.example.bltcamera.modules.watch;

public interface WatchModel {

    void listenForBluetoothConnection();

    boolean isConnected();

    void commandToTakePhoto();

    void commandToStartStopRecording();

    void turnFlash(int turnFlash);


}
