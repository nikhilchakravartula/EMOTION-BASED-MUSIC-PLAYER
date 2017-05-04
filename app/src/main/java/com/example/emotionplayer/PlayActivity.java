package com.example.emotionplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class PlayActivity extends AppCompatActivity {

    private MediaPlayer player;
    private ArrayList<String> currentPlaylist;
    private int currentTrack;
    static ArrayList<PathEmotion> pathEmotions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);
        getSupportActionBar().setTitle(getIntent().getStringExtra("position"));
        player=new MediaPlayer();
         currentTrack=0;
        int index=0;
        for(int i=0;i<5;i++)
        {
            if(MusicPlayer.emotion.scores.get(i)>MusicPlayer.emotion.scores.get(index))
            {
                index=i;
            }
        }
        playSongWithId(index);
        //new SongTbHelper().putInfo(database.getWritableDatabase(),"hi this is path",new ArrayList<Double>());
       // pathEmotions=new ArrayList<PathEmotion>(5);

        //  createPlaylist(emotion,pathEmotions,3);
        //playCurrentPlaylist();
    }

    protected void playSongWithId(int currentmood)
    {
        if(player.isPlaying())
        {
            player.stop();
        }
        switch(currentmood)
        {
            case 0:
                player=MediaPlayer.create(this,R.raw.i_hate_everything_about_u);
                        break;

            case 1:
                player=MediaPlayer.create(this,R.raw.life_on_mars);
                break;

            case 2:
                player=MediaPlayer.create(this,R.raw.waterloo_sunset);
                break;

            case 3:
                player=MediaPlayer.create(this,R.raw.the_beach_boys);
                break;

            case 4:
                player=MediaPlayer.create(this,R.raw.take_on_me);
                break;


        }
        player.start();

    }
    protected void createPlaylist(Emotion e, ArrayList<PathEmotion> pathEmotions, int lengthPlaylist)
    {
        currentPlaylist=new ArrayList<String>();
        class HeapNode
        {
            HeapNode(String p,Double d)
            {
                path=p;
                value=d;
            }
            String path;
            Double value;
        }
        PriorityQueue<HeapNode> pq=new PriorityQueue<HeapNode>(1,new Comparator<HeapNode>() {
            @Override
            public int compare(HeapNode o1, HeapNode o2) {
                if(o1.value>o2.value)
                    return 1;
                else return 0;
            }
        });
        for(PathEmotion pe:pathEmotions) {
            Double tempValue = 0d;
            for (Double d : e.scores) {
                tempValue += d;
            }
            tempValue = Math.sqrt(tempValue);

            pq.add(new HeapNode(pe.path, tempValue));
        }

        while(lengthPlaylist>0 && pq.size()>0)
        {
            currentPlaylist.add(pq.poll().path);
            lengthPlaylist-=1;

        }
    }
    private void playSong(String path)
    {
        try
        {
            player.reset();
            System.out.println("path is "+ path);
            player.setDataSource(new FileInputStream(path).getFD());
            player.prepare();
            player.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void playCurrentPlaylist() {

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            playSong(currentPlaylist.get(currentTrack));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occured " + e.getStackTrace());
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    currentTrack += 1;
                    if (currentTrack < currentPlaylist.size()) {
                        playSong(currentPlaylist.get(currentTrack));
                    } else {
                        player.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
