package com.example.app_music;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private boolean isServiceConnected;
    ImageView imgplay, imgcd, imgstop, imgback, imgnext;
    SeekBar sk_song;
    TextView tv_timestart, tv_timeout, tv_title, tv_title2;
    private Myservice myservice;
    Animation animation;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Myservice.MyBinder myBinder = (Myservice.MyBinder) iBinder;
            myservice = myBinder.getMyService();
            isServiceConnected = true;
            Time();
            TimeOut();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myservice = null;
            isServiceConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_title2 = findViewById(R.id.tv_title2);
        tv_title = findViewById(R.id.tv_title);
        sk_song = findViewById(R.id.sk_bar);
        tv_timestart = findViewById(R.id.tv_timstart);
        tv_timeout = findViewById(R.id.tv_timeout);
        imgcd = findViewById(R.id.imgcd);
        imgplay = findViewById(R.id.imgplay);
        imgstop = findViewById(R.id.imgStop);
        imgback = findViewById(R.id.imgback);
        imgnext = findViewById(R.id.imgnext);

        animation = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);


        sk_song.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myservice.getMediaPlayer().seekTo(sk_song.getProgress());
            }
        });

        imgnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceConnected)
                    myservice.fastforward();
                else{
                    Toast.makeText(MainActivity.this, "Service chưa hoạt động", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceConnected)
                    myservice.backforward();
                else{
                    Toast.makeText(MainActivity.this, "Service chưa hoạt động", Toast.LENGTH_SHORT).show();
                }
            }

        });

        imgstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStopService();
            }
        });

        final Intent intent = new Intent(MainActivity.this, Myservice.class);

        imgplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isServiceConnected){
                    if (myservice.isPlaying()) {
                        myservice.pauseMusic();
                        imgplay.setImageResource(R.drawable.play);
                        imgcd.clearAnimation();
                        tv_title.setVisibility(View.GONE);
                        tv_title2.setVisibility(View.VISIBLE);
                    } else {
                        myservice.playMusic();
                        imgplay.setImageResource(R.drawable.ic_pause);
                        imgcd.startAnimation(animation);
                        tv_title2.setVisibility(View.GONE);
                        tv_title.setVisibility(View.VISIBLE);
                    }
                    Time();
                    TimeOut();
                }else {
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                    imgplay.setImageResource(R.drawable.ic_pause);
                    imgcd.startAnimation(animation);
                    tv_title2.setVisibility(View.GONE);
                    tv_title.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void onClickStopService() {
       if (myservice.isPlaying()){
           unbindService(serviceConnection);
           isServiceConnected = false;
           imgplay.setImageResource(R.drawable.play);
           imgcd.clearAnimation();
           tv_title2.setVisibility(View.GONE);
           tv_title.setVisibility(View.GONE);
       }else{
           unbindService(serviceConnection);
           isServiceConnected = false;
       }

    }

    private void Time() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                tv_timestart.setText(format.format(myservice.getMediaPlayer().getCurrentPosition()));
                sk_song.setProgress(myservice.getMediaPlayer().getCurrentPosition());
                myservice.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        imgcd.clearAnimation();
                        imgplay.setImageResource(R.drawable.play);
                        Time();
                        TimeOut();
                    }
                });
                handler.postDelayed(this, 1000);
            }
        }, 100);
    }

    private void TimeOut() {
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        tv_timeout.setText(format.format(myservice.getMediaPlayer().getDuration()));
        sk_song.setMax(myservice.getMediaPlayer().getDuration());
    }


}