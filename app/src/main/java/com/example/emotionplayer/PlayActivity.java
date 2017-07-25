package com.example.emotionplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.util.MutableInt;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.zip.Inflater;

public class PlayActivity extends AppCompatActivity {




    private int playlistLength;
    private ImageButton play,skip_next,skip_previous;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_layout);
        play=(ImageButton)findViewById(R.id.play_id);
        skip_next=(ImageButton)findViewById(R.id.skip_next_id);
        skip_previous=(ImageButton)findViewById(R.id.skip_previous_id);
        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(MusicPlayer.isPaused==true)
                {

                }
                else
                {
                    playCurrentPlaylist();
                }
            }
        });
        skip_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.currentTrack=(MusicPlayer.currentTrack+1)%playlistLength;
                playCurrentPlaylist();


            }
        });
        skip_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.currentTrack=(MusicPlayer.currentTrack-1+playlistLength)%playlistLength;
                playCurrentPlaylist();
            }
        });
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Just a sec");
        builder.setMessage("Please specify number of songs in the playlist");
        LayoutInflater inflater=this.getLayoutInflater();
        View view=inflater.inflate(R.layout.no_songs_picker_layout,null);
        builder.setView(view);
        final NumberPicker numberPicker=(NumberPicker)view.findViewById(R.id.no_fb_posts);
        numberPicker.setMaxValue(20);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                playlistLength=numberPicker.getValue();
                MusicPlayer.currentPlaylist=new ArrayList<String>();
               ArrayList<PathEmotion> pathEmotions=new SongTbHelper().getInfo(MusicPlayer.database.getReadableDatabase());
                createPlaylist(MusicPlayer.emotion,pathEmotions,playlistLength);
                ListView lv=(ListView)findViewById(R.id.current_playlist_id);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(PlayActivity.this, android.R.layout.simple_list_item_1, MusicPlayer.currentPlaylist);
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
                if(MusicPlayer.player==null)
                MusicPlayer.player=new MediaPlayer();
                else if(MusicPlayer.player.isPlaying())
                    MusicPlayer.player.stop();
                MusicPlayer.currentTrack=0;
               // playCurrentPlaylist();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
        getSupportActionBar().setTitle(getIntent().getStringExtra("position"));

        //new SongTbHelper().putInfo(database.getWritableDatabase(),"hi this is path",new ArrayList<Double>());
       //
    }

    protected void playSongWithId(int currentmood)
    {
        if(MusicPlayer.player.isPlaying())
        {
            MusicPlayer.player.stop();
        }
        switch(currentmood)
        {
            case 0:
                MusicPlayer.player=MediaPlayer.create(this,R.raw.i_hate_everything_about_u);
                        break;

            case 1:
                MusicPlayer.player=MediaPlayer.create(this,R.raw.life_on_mars);
                break;

            case 2:
                MusicPlayer.player=MediaPlayer.create(this,R.raw.waterloo_sunset);
                break;

            case 3:
                MusicPlayer.player=MediaPlayer.create(this,R.raw.the_beach_boys);
                break;

            case 4:
                MusicPlayer.player=MediaPlayer.create(this,R.raw.take_on_me);
                break;


        }
        MusicPlayer.player.start();

    }
    protected void createPlaylist(Emotion e, ArrayList<PathEmotion> pathEmotions, int lengthPlaylist)
    {
        if(e==null|| (e.scores!=null&&e.scores.isEmpty()))
        {
            System.out.print("Here\n");
            Toast.makeText(this,"Can't create playlist without user emotion",Toast.LENGTH_LONG);
            return;
        }
        MusicPlayer.currentPlaylist=new ArrayList<String>();
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
            for(int i=0;i<pe.scores.size();i++)
            {
                tempValue+=(Math.pow(pe.scores.get(i)-e.scores.get(i),2));
            }
            tempValue = Math.sqrt(tempValue);

            pq.add(new HeapNode(pe.path, tempValue));
        }

        while(lengthPlaylist>0 && pq.size()>0)
        {
            MusicPlayer.currentPlaylist.add(pq.poll().path);
            lengthPlaylist-=1;

        }
    }
    private void playSong(String path)
    {
        try
        {
            if(MusicPlayer.player.isPlaying())
            {
                MusicPlayer.player.stop();
            }
            MusicPlayer.player.reset();
            System.out.println("path is "+ path);
            MusicPlayer.player.setDataSource(new FileInputStream(path).getFD());
            MusicPlayer.player.prepare();
            MusicPlayer.player.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void playCurrentPlaylist() {


        MusicPlayer.player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            if(MusicPlayer.currentTrack<MusicPlayer.currentPlaylist.size())
            playSong(MusicPlayer.currentPlaylist.get(MusicPlayer.currentTrack));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occured " + e.getStackTrace());
        }

        MusicPlayer.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    MusicPlayer.currentTrack += 1;
                    if (MusicPlayer.currentTrack < MusicPlayer.currentPlaylist.size()) {
                        playSong(MusicPlayer.currentPlaylist.get(MusicPlayer.currentTrack));
                    } else {
                        MusicPlayer.player.stop();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
