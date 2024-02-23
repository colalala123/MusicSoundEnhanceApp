package com.example.easytutomusicapp;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    ArrayList<AudioModel> songsListcollect = new ArrayList<>();

    EditText playnum;

    EditText song_gap;

    Button collect_page;


    private SearchView mSearchView;


    ArrayList<AudioModel> SongSearchList = new ArrayList<>();

    LinearLayoutManager layoutManager = new LinearLayoutManager(this);

    public static MainActivity testActivity;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById(R.id.no_songs_text);
        playnum = findViewById(R.id.playnum);
        song_gap = findViewById(R.id.song_gap);

        mSearchView = (SearchView) findViewById(R.id.searchView);

        collect_page = (Button)findViewById(R.id.collect_page);

        testActivity = this;





        if(checkPermission() == false){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        String selection = MediaStore.Audio.Media.IS_MUSIC +" != 0";

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);
        while(cursor.moveToNext()){
            AudioModel songData = new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));
            if(new File(songData.getPath()).exists())
                songsList.add(songData);
        }

        if(songsList.size()==0){
            noMusicTextView.setVisibility(View.VISIBLE);
        }else{
            // 刷新音乐的收藏按钮颜色
//                MusicListAdapter.notifyDataSetChanged();
//            SharedPreferences sharedPreferences = getSharedPreferences("collect_songs", Context.MODE_PRIVATE);
//            Map<String, ?> allEntries = sharedPreferences.getAll();
//
//            Gson gson = new Gson();
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                String json = (String) entry.getValue();
//                AudioModel audioModel = gson.fromJson(json, AudioModel.class);
//                songsListcollect.add(audioModel);
//            }
//            for (AudioModel audioModel : songsListcollect) {
//                for (AudioModel model : songsList) {
//                    if (model.getPath().equals(audioModel.getPath())) {
//                        model.setCollected(true);
//                        break;
//                    }
//                }
//            }

            //recyclerview
            //有歌曲显示歌曲
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            //使得歌曲可播放
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext(),playnum,song_gap));

        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String userInput = newText.toLowerCase();
                ArrayList<AudioModel> songSearchList = new ArrayList<>();
                for (AudioModel song : songsList){
                    if(song.getTitle().toLowerCase().contains(userInput)){
                        songSearchList.add(song);
                    }
                }
                //有歌曲显示歌曲
                recyclerView.setLayoutManager(layoutManager);
                //使得歌曲可播放
                recyclerView.setAdapter(new MusicListAdapter(songSearchList,getApplicationContext(),playnum,song_gap));

                return false;
            }
        });

        collect_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CollectPageActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });



    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_MEDIA_AUDIO);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_MEDIA_AUDIO)){
            Toast.makeText(MainActivity.this,"READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTTINGS",Toast.LENGTH_SHORT).show();
        }else
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_MEDIA_AUDIO},123);
    }

//    private BroadcastReceiver refreshCollectMusicReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals("com.example.myapp.REFRESH_COLLECT_MUSIC")) {
//                // 刷新音乐的收藏按钮颜色
////                MusicListAdapter.notifyDataSetChanged();
//                SharedPreferences sharedPreferences = getSharedPreferences("collect_songs", Context.MODE_PRIVATE);
//                Map<String, ?> allEntries = sharedPreferences.getAll();
//
//                Gson gson = new Gson();
//                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                    String json = (String) entry.getValue();
//                    AudioModel audioModel = gson.fromJson(json, AudioModel.class);
//                    songsListcollect.add(audioModel);
//                }
//                for (AudioModel audioModel : songsListcollect) {
//                    for (AudioModel model : songsList) {
//                        if (model.getPath().equals(audioModel.getPath())) {
//                            model.setCollected(true);
//                            break;
//                        }
//                    }
//                }
//
//                recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext(),playnum,song_gap));
//            }
//        }
//    };



    @Override
    protected void onResume() {
        super.onResume();
//        IntentFilter intentFilter = new IntentFilter("com.example.myapp.REFRESH_COLLECT_MUSIC");
//        registerReceiver(refreshCollectMusicReceiver, intentFilter);

        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext(),playnum,song_gap));
        }

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(refreshCollectMusicReceiver);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            SharedPreferences sharedPreferences = getSharedPreferences("collect_songs", Context.MODE_PRIVATE);
//            Map<String, ?> allEntries = sharedPreferences.getAll();
//
//            Gson gson = new Gson();
//            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//                String json = (String) entry.getValue();
//                AudioModel audioModel = gson.fromJson(json, AudioModel.class);
//                songsListcollect.add(audioModel);
//            }
//            for (AudioModel audioModel : songsListcollect) {
//                for (AudioModel model : songsList) {
//                    if (model.getPath().equals(audioModel.getPath())) {
//                        model.setCollected(true);
//                        break;
//                    }
//                }
//            }
//
//            //musicListAdapter.notifyDataSetChanged();
//        }
//    }






}