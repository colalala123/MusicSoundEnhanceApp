package com.example.easytutomusicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;


public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder>{

    ArrayList<AudioModel> songsList;
    Context context;

    SharedPreferences sharedPreferences;

    EditText playnum;

    EditText song_gap;

    Integer pnums;

    Integer songGap;

    public MusicListAdapter(ArrayList<AudioModel> songsList, Context context, EditText playnum, EditText song_gap) {
        this.songsList = songsList;
        this.context = context;
        this.playnum = playnum;
        this.song_gap = song_gap;
        sharedPreferences = context.getSharedPreferences("collected_state", Context.MODE_PRIVATE);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public Button btnCollect;
        TextView titleTextView;
        ImageView iconImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.music_title_text);
            iconImageView = itemView.findViewById(R.id.icon_view);
            btnCollect = (Button) itemView.findViewById(R.id.collect_button);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder( MusicListAdapter.ViewHolder holder, int position) {
        //被点击的文件信息
        AudioModel songData = songsList.get(position);
        holder.titleTextView.setText(songData.getTitle());
        //读取存储数据
        SharedPreferences sharedPreferences = context.getSharedPreferences("collect_songs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String songJson = sharedPreferences.getString(songData.getPath(), "");
        if (!songJson.isEmpty()) {
            AudioModel collectedSong = gson.fromJson(songJson, AudioModel.class);
            songData.setCollected(collectedSong.isCollected());
        }
        //根据存储数据变色
        if (songData.isCollected()) {
            holder.btnCollect.setBackgroundColor(Color.YELLOW);
        } else {
            holder.btnCollect.setBackgroundColor(Color.WHITE);
        }
        //收藏按钮被点击后的事件
        holder.btnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!songData.isCollected()) {
                    songData.setCollected(true);
                    holder.btnCollect.setBackgroundColor(Color.YELLOW);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("collect_songs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String songJson = gson.toJson(songData);
                    editor.putString(songData.getPath(), songJson);
                    editor.apply();
                } else {
                    songData.setCollected(false);
                    holder.btnCollect.setBackgroundColor(Color.WHITE);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("collect_songs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(songData.getPath());
                    editor.apply();
                    //updateMusicStatus(songData);


                }


            }
        });

        //读取存储数据
//        if (!songJson.isEmpty()) {
//            AudioModel collectedSong = gson.fromJson(songJson, AudioModel.class);
//            songData.setCollected(collectedSong.isCollected());
//        }

//        if (songData.isCollected()) {
//            holder.btnCollect.setBackgroundColor(Color.YELLOW);
//        } else {
//            holder.btnCollect.setBackgroundColor(Color.WHITE);
//        }

        if(MyMediaPlayer.currentIndex==position){
            holder.titleTextView.setTextColor(Color.parseColor("#FF0000"));

        }else{
            holder.titleTextView.setTextColor(Color.parseColor("#000000"));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigate to another acitivty
                if(TextUtils.isEmpty(playnum.getText().toString())){
                    pnums = 1;
                }else{
                    pnums = Integer.parseInt(playnum.getText().toString());
                    if(pnums<=0)
                        pnums = 1;

                }

                if(TextUtils.isEmpty(song_gap.getText().toString())){
                    songGap = 0;
                }else{
                    songGap = Integer.parseInt(song_gap.getText().toString());
                    if(songGap<100)
                        songGap = 0;
                }
                MyMediaPlayer.currentIndex = position;
                //启动播放
                Intent intent = new Intent(context,MusicPlayerActivity.class);
                intent.putExtra("LIST",songsList);
                intent.putExtra("PNUM",pnums);
                intent.putExtra("SONGGAP",songGap);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public void updateMusicStatus(AudioModel audioModel) {
        int position = songsList.indexOf(audioModel);
//        audioModel.setCollected(!audioModel.isCollected());
        songsList.set(position, audioModel);
        notifyItemChanged(position);
    }




}
