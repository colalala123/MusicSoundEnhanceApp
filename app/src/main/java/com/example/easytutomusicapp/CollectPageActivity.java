package com.example.easytutomusicapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;

public class CollectPageActivity extends AppCompatActivity {

    ArrayList<AudioModel> songsListcollect = new ArrayList<>();;

    EditText playnum_collect;

    EditText song_gap_collect;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_page);

        playnum_collect = findViewById(R.id.playnum_collect);
        song_gap_collect = findViewById(R.id.song_gap_collect);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_collect);

        SharedPreferences sharedPreferences = getSharedPreferences("collect_songs", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        //读取历史数据
//        List<AudioModel> collectList = new ArrayList<>();

        Gson gson = new Gson();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = (String) entry.getValue();
            AudioModel audioModel = gson.fromJson(json, AudioModel.class);
            songsListcollect.add(audioModel);
        }

        //展示歌曲

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //赋予功能
        MusicListAdapter adapter = new MusicListAdapter(songsListcollect,getApplicationContext(),playnum_collect,song_gap_collect);
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();


        Intent intent = new Intent(CollectPageActivity.this, MainActivity.class);
        if(MainActivity.testActivity!=null){
            MainActivity.testActivity.finish();
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Intent intent = new Intent("com.example.myapp.REFRESH_COLLECT_MUSIC");
//        sendBroadcast(intent);
//    }






}
