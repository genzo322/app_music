package com.example.app_music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.security.Provider;
import java.util.List;
import java.util.Map;

public class Myservice extends Service {

    private MyBinder myBinder = new MyBinder();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }


    @Override
    public void onCreate() {
        mediaPlayer = MediaPlayer.create(this, R.raw.haiphuthon);
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mediaPlayer.start();
        return myBinder;
    }

    public void fastforward(){
        mediaPlayer.seekTo(10000 + mediaPlayer.getCurrentPosition());
    }
    public void backforward(){
        mediaPlayer.seekTo(-10000 + mediaPlayer.getCurrentPosition());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.reset();
    }

    public void playMusic(){
        mediaPlayer.start();
    }

    public void pauseMusic(){
        mediaPlayer.pause();
    }

    public class MyBinder extends Binder {
        Myservice getMyService() {
            return Myservice.this;
        }
    }

}
