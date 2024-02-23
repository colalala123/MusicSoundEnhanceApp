package com.example.easytutomusicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv,currentTimeTv,totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,previousBtn,musicIcon;
    ArrayList<AudioModel> songsList;

    Integer pnums;

    Integer songGap;
    AudioModel currentSong;

    MediaPlayer mediaPlayer[] = MyMediaPlayer.getInstance();


//    ExoPlayer mediaPlayer[] = MyMediaPlayer.getInstance(this);

    Thread[] prepareThreads = new Thread[99];
    int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);

        titleTv.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");
        pnums = (Integer) getIntent().getSerializableExtra("PNUM");
        songGap = (Integer) getIntent().getSerializableExtra("SONGGAP");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer[0]!=null){
                    seekBar.setProgress(mediaPlayer[0].getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer[0].getCurrentPosition()+""));

                    if(mediaPlayer[0].isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }

                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer[0]!=null && fromUser){
                    for (int i =0;i<pnums;i++)
                    {
                        mediaPlayer[i].seekTo(progress);
                    }

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    void setResourcesWithMusic(){
//        if (MyMediaPlayer.currentIndex >= 0 && MyMediaPlayer.currentIndex < songsList.size()) {
//            currentSong = songsList.get(MyMediaPlayer.currentIndex);
//        } else {
//            // 可以将 "currentIndex" 设置为合法值，例如0
//            MyMediaPlayer.currentIndex = 0;
//            currentSong = songsList.get(MyMediaPlayer.currentIndex);
//        }
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());

        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
//        nextBtn.setOnClickListener(v-> playNextSong());
//        previousBtn.setOnClickListener(v-> playPreviousSong());

        playMusic();


    }





//    private Handler handler = new Handler(Looper.getMainLooper()); // 确保Handler附着于主线程
    private void playMusic(){
        for(int i = 0;i < 99;i++){
            mediaPlayer[i].reset();
        }

        for (int i = 0; i < pnums; i++) {

            int j = i;
            prepareThreads[j] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mediaPlayer[j].setDataSource(currentSong.getPath());
                        mediaPlayer[j].prepare();
                        mediaPlayer[j].setLooping(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            prepareThreads[j].start();
        }

        for (int i = 0; i < pnums; i++) {
            try {
                prepareThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mediaPlayer[i].start();

//            // 对于第一首歌，设置延迟任务以重置所有歌曲的进度
//            if (i == 0) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (int k = 0; k < pnums; k++) {
//                            mediaPlayer[k].seekTo(0); // 将所有歌曲的播放进度拖到0秒
////                            mediaPlayer[k].start(); // 重新开始播放
//                        }
//                    }
//                }, 120*pnums); // 延迟1秒执行
//            }

        }


            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer[0].getDuration());
        try {
            // 将 Thread.sleep 包含在 try-catch 块中
            Thread.sleep(120*pnums);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(songGap>=100){
            for (int i =0;i<pnums;i++)
            {
                mediaPlayer[i].seekTo(0+i*songGap);
            }

        }else{
            for (int i =0;i<pnums;i++)
            {
                mediaPlayer[i].seekTo(0);
            }
        }

        handler.post(checkLoopingRunnable); // 开始检查循环

    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    private final Handler handler = new Handler();
    private boolean isLooping = false; // 标志位，用来表示是否在循环播放

    private int lastPosition = 0;
    private final Runnable checkLoopingRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer[pnums-1] != null) {
                int currentPosition = mediaPlayer[pnums-1].getCurrentPosition();
//                int duration = mediaPlayer[0].getDuration();

                if(lastPosition==0){
                    lastPosition = currentPosition;
                }

                if(currentPosition<4000 && lastPosition>currentPosition){
                    Log.e("looping!", "true!!!");

                    isLooping = true;
                    lastPosition=0;
                    if(songGap>=100){
                        for (int i =0;i<pnums;i++)
                        {
                            mediaPlayer[i].seekTo(0+i*songGap);
                        }

                    }else{
                        for (int i =0;i<pnums;i++)
                        {
                            mediaPlayer[i].seekTo(0);
                        }
                    }


                    showToast(getApplicationContext(),"已进入循环播放！");
                    Log.e("lastPositionloop", String.format("%d", lastPosition));
                    Log.e("currentPositionloop", String.format("%d", currentPosition));
                }

                Log.e("lastPosition", String.format("%d", lastPosition));

                // 如果当前位置超过4秒，重置循环播放标志
                if (currentPosition > 4000) {
                    isLooping = false;
                    lastPosition = currentPosition;
                }

                // 每2秒检查一次
                handler.postDelayed(this, 2000);
                Log.e("currentPosition", String.format("%d", currentPosition));


            }
        }
    };



    private void playNextSong(){

        if(MyMediaPlayer.currentIndex== songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
        mediaPlayer[0].reset();
        setResourcesWithMusic();

    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex== 0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer[0].reset();
        setResourcesWithMusic();
    }


    private int currentPausePosition = 0;
    private void pausePlay(){
        if(mediaPlayer[pnums-1].isPlaying()){
            currentPausePosition = mediaPlayer[0].getCurrentPosition();
            for(int i = 0;i < pnums;i++){
                mediaPlayer[i].pause();
            }
        }
        else{
            for(int i = 0;i < pnums;i++){
                mediaPlayer[i].start();
            }

            try {
                // 将 Thread.sleep 包含在 try-catch 块中
                Thread.sleep(100*pnums);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(songGap>=100){
                for (int i =0;i<pnums;i++)
                {
                    mediaPlayer[i].seekTo(currentPausePosition+i*songGap);
                }

            }else{
                for (int i =0;i<pnums;i++)
                {
                    mediaPlayer[i].seekTo(currentPausePosition);
                }
            }

        }

    }


    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}