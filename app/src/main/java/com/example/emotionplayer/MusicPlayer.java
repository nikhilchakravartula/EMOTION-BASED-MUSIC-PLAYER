package com.example.emotionplayer;

/**
 * Created by nikhil on 30-03-2017.
 */


import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.app.*;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicReferenceArray;


class Emotion
{
    ArrayList<String> emotions;
    ArrayList<Double> scores;
}
public class MusicPlayer extends AppCompatActivity implements OnItemClickListener{

    //String classes[]={"Class1","Class2","Class3"}
    static public String PACKAGE_NAME;
    public static ListView listView;
    protected DrawerLayout drawerLayout;
    protected String[] classes;
    static boolean login=false;
    //UtilityObject utilityObject;
    private Toolbar toolbar;
    private String[] fullyQualifiedClassNames;
    private ActionBarDrawerToggle drawerListener;
    static Database database;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private String posts;
    public  static ProgressBar progressBar;
    public static TextView start_load_message;
    public static Emotion emotion;
    private ArrayList<String> currentPlaylist;
    private int currentTrack;
    public static android.support.v7.app.ActionBar actionBar;
    private MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emotion=new Emotion();
        player=new MediaPlayer();
        emotion.emotions=new ArrayList<String>(5);
        emotion.scores=new ArrayList<Double>(5);
        PACKAGE_NAME=getApplicationContext().getPackageName();
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1,classes));
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(R.array.class_names))));
        setContentView(R.layout.drawer_layout);
        classes=getResources().getStringArray(R.array.class_names);
        fullyQualifiedClassNames=getResources().getStringArray(R.array.fully_qualified_class_names);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        //progressBar.setVisibility(View.VISIBLE);
        start_load_message=(TextView)findViewById(R.id.start_load_message);
        listView=(ListView)findViewById(R.id.core_options);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classes));
        listView.setOnItemClickListener(this);
        //listView.setVisibility(View.GONE);
        // utilityObject=Utility.getUtilityObject();
        // Toast.makeText(this,""+utilityObject,Toast.LENGTH_LONG);
        drawerListener=new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerListener);

        //toolbar=(Toolbar)findViewById(R.id.app_toolbar);
        //setSupportActionBar( (Toolbar)findViewById(R.id.app_toolbar));
        database=new Database(getApplicationContext());
        // dbWrite=database.getWritableDatabase();
        //dbRead=database.getReadableDatabase();


        synchFb();

       /*setActionBar();
        progressBar.setVisibility(View.GONE);
        start_load_message.setVisibility(View.GONE);
        login=true;*/
    }

    private void synchFb()
    {
        if(SocialProfile.isLogin())
        {
            login=true;
            Toast.makeText(getApplicationContext(),"FB integration successful",Toast.LENGTH_LONG);
            System.out.print("getting fb posts\n");
            SocialProfile.getPosts();
            //Toast.makeText(this,"pOSTS DONE",Toast.LENGTH_LONG);
            //System.out.print(emotion.get(0).emotion+"\t"+emotion.get(0).score+"\n");
        }
        else
        {
           // progressBar.setVisibility(View.GONE);
          //  listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            start_load_message.setVisibility(View.GONE);
            setActionBar();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.three_lines);


    }

    public static void setActionBar()
    {

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerListener.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerListener.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        return true;
    }


    private void loginFb(int position)
    {
        System.out.print("fully qualified name "+ fullyQualifiedClassNames[position]);
        Intent i=new Intent(fullyQualifiedClassNames[position]);
        i.putExtra("position",classes[position]);
        startActivity(i);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(this,"psotion is "+position + "and name is"+classes[position],Toast.LENGTH_LONG).show();
        System.out.print("item click");
        if(login==false)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Login to Facebook")
                    .setTitle("Login Alert");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    loginFb(0);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        switch(position)
        {
            case 0:
                //SocialProfile socialProfile=new SocialProfile();
           /*     FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, socialProfile.getFragment())
                        .commit();*/
                //Toast.makeText(this,"psotion is "+socialProfile.getFragment(),Toast.LENGTH_LONG).show();
               loginFb(position);
         //       synchFb();
                break;

            case 1:
                //new ToneAnalyzerUtil().execute("");
                Toast.makeText(this,"here finally",Toast.LENGTH_LONG);
                //new ToneAnalyzerUtil().execute("");
                break;

            case 2:
                Intent i1 =new Intent(fullyQualifiedClassNames[position]);
                i1.putExtra("position",classes[position]);
                startActivity(i1);

                break;
            case 3:
                  currentTrack=0;
                //new SongTbHelper().putInfo(database.getWritableDatabase(),"hi this is path",new ArrayList<Double>());
                ArrayList<PathEmotion> pathEmotions=new SongTbHelper().getInfo(database.getReadableDatabase());
                createPlaylist(emotion,pathEmotions,3);
                playCurrentPlaylist();


                break;
        }


    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

    protected void createPlaylist(Emotion e,ArrayList<PathEmotion> pathEmotions,int lengthPlaylist)
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
    private void playCurrentPlaylist()
    {

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
          playSong(currentPlaylist.get(currentTrack));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Exception occured "+ e.getStackTrace());
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try
                {
                    currentTrack+=1;
                    if(currentTrack<currentPlaylist.size()) {
                      playSong(currentPlaylist.get(currentTrack));
                    }
                    else
                    {
                        player.stop();
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }
        });
    }
}



