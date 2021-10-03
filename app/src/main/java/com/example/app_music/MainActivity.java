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

import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private boolean isServiceConnected;
    ImageView imgplay, imgcd;
    Button bt_start;
    SeekBar sk_song;
    TextView tv_timestart, tv_timeout, tv_title;
    private Myservice myservice;
    Animation animation;



    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            Myservice.MyBinder myBinder = (Myservice.MyBinder) iBinder;
            myservice = myBinder.getMyService();
            isServiceConnected = true;
            TimeOut();
            Time();
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

        tv_title = findViewById(R.id.tv_title);
        sk_song = findViewById(R.id.sk_bar);
        tv_timestart = findViewById(R.id.tv_timstart);
        tv_timeout = findViewById(R.id.tv_timeout);
        imgcd = findViewById(R.id.imgcd);
        imgplay = findViewById(R.id.imgplay);
        bt_start = findViewById(R.id.btn_start);

        animation = AnimationUtils.loadAnimation(this, R.anim.disc_rotate);

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickStartService();
                imgcd.startAnimation(animation);

            }
        });

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


        imgplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myservice.isPlaying()){
                    myservice.pauseMusic();
                    imgplay.setImageResource(R.drawable.play);
                    imgcd.clearAnimation();
                }
                else{
                    myservice.resumeMusic();
                    imgplay.setImageResource(R.drawable.ic_pause);
                    imgcd.startAnimation(animation);
                }
                Time();
                TimeOut();
            }
        });
    }
    private void onClickStartService() {
        Intent intent = new Intent(this, Myservice.class);
        Song song = new Song("Cricus - Remix", R.raw.cuoithoi);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_song", song);
        intent.putExtras(bundle);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        imgplay.setImageResource(R.drawable.ic_pause);
        imgplay.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        bt_start.setVisibility(View.GONE);
    }

    private void TimeOut(){
        SimpleDateFormat format = new SimpleDateFormat("mm:ss");
        tv_timeout.setText(format.format(myservice.getMediaPlayer().getDuration()));
        sk_song.setMax(myservice.getMediaPlayer().getDuration());
    }

    private void Time(){
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
                        bt_start.setVisibility(View.VISIBLE);
                        tv_title.setVisibility(View.GONE);
                        Time();
                        TimeOut();
                    }
                });
                handler.postDelayed(this, 1000);
            }
        }, 100);
    }

}