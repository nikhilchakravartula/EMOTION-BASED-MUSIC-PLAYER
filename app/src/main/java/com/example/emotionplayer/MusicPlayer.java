package com.example.emotionplayer;

/**
 * Created by nikhil on 30-03-2017.
 */


import android.content.Intent;

import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.app.*;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MusicPlayer extends AppCompatActivity implements OnItemClickListener{

    //String classes[]={"Class1","Class2","Class3"}
    static public String PACKAGE_NAME;
    private ListView listView;
    protected DrawerLayout drawerLayout;
    protected String[] classes;
    //UtilityObject utilityObject;
    private Toolbar toolbar;
    private String[] fullyQualifiedClassNames;
    private ActionBarDrawerToggle drawerListener;
    Database database;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    public static ArrayList<Emotion> emotion;
    private String posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PACKAGE_NAME=getApplicationContext().getPackageName();
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1,classes));
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(R.array.class_names))));
        setContentView(R.layout.drawer_layout);
        classes=getResources().getStringArray(R.array.class_names);
        fullyQualifiedClassNames=getResources().getStringArray(R.array.fully_qualified_class_names);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        listView=(ListView)findViewById(R.id.core_options);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classes));
        listView.setOnItemClickListener(this);

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
    }

    private void synchFb()
    {
        if(SocialProfile.isLogin())
        {
            Toast.makeText(getApplicationContext(),"FB integration successful",Toast.LENGTH_LONG);
            posts=SocialProfile.getPosts();
            emotion=new ArrayList<Emotion>(5);
            new ToneAnalyzerUtil().execute(posts);
            System.out.print(emotion.get(0).emotion+"\t"+emotion.get(0).score+"\n");
        }
        else
        {
            System.out.print("FB login fail");
            Toast.makeText(getApplicationContext(),"Cannot integragte FB.Please Login",Toast.LENGTH_LONG);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
       actionBar.setHomeAsUpIndicator(R.drawable.three_lines);

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(this,"psotion is "+position + "and name is"+classes[position],Toast.LENGTH_LONG).show();

        switch(position)
        {
            case 0:
                //SocialProfile socialProfile=new SocialProfile();
           /*     FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, socialProfile.getFragment())
                        .commit();*/
                //Toast.makeText(this,"psotion is "+socialProfile.getFragment(),Toast.LENGTH_LONG).show();
                System.out.print("fully qualified name "+ fullyQualifiedClassNames[position]);
                Intent i=new Intent(fullyQualifiedClassNames[position]);
                i.putExtra("position",classes[position]);
                startActivity(i);
                break;

            case 1:
                new ToneAnalyzerUtil().execute("");
                Toast.makeText(this,"here finally",Toast.LENGTH_LONG);
                new ToneAnalyzerUtil().execute("");
                break;

            case 2:
                Intent i1 =new Intent(fullyQualifiedClassNames[position]);
                i1.putExtra("position",classes[position]);
                startActivity(i1);

                break;
            case 3:
                new SongTbHelper().putInfo(database.getWritableDatabase(),"hi this is path",new Double[]{0.2,0.3,0.2,0.1,0.5});
                new SongTbHelper().getInfo(database.getReadableDatabase());
                break;
        }


    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}






