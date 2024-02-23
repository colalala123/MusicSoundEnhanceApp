package com.example.easytutomusicapp;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer[] instance = new MediaPlayer[99];

    public static MediaPlayer[] getInstance(){
        for(int i=0;i<99;i++){
            if(instance[i] == null){
                instance[i] = new MediaPlayer();
            }
        }

        return instance;
    }

    public static int currentIndex = 0;
}

//package com.example.easytutomusicapp;
//
//import android.content.Context;
//
//import com.google.android.exoplayer2.ExoPlayer;
//import com.google.android.exoplayer2.SimpleExoPlayer;
//
//public class MyMediaPlayer {
//    static ExoPlayer[] instance = new ExoPlayer[99];
//    static int currentIndex = 0;
//
//    public static ExoPlayer[] getInstance(Context context) {
//        for (int i = 0; i < instance.length; i++) {
//            if (instance[i] == null) {
//                // 注意，SimpleExoPlayer已经在ExoPlayer版本2.12.0中被弃用，请根据你的ExoPlayer版本调整
//                // 如果你使用的是ExoPlayer 2.12.0或更高版本，请使用ExoPlayer.Builder
//                instance[i] = new SimpleExoPlayer.Builder(context).build();
//            }
//        }
//        return instance;
//    }
//}
