package com.example.emotionplayer;

/**
 * Created by nikhil on 30-03-2017.
 */

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.DrawableWrapper;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.app.*;
import android.widget.ListView;
import android.widget.Toast;


public class MusicPlayer extends Activity implements OnItemClickListener{

    //String classes[]={"Class1","Class2","Class3"};
    private ListView listView;
    private DrawerLayout drawerLayout;
    private String[] classes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1,classes));
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(R.array.class_names))));
        setContentView(R.layout.drawer_layout);
        classes=getResources().getStringArray(R.array.class_names);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        listView=(ListView)findViewById(R.id.core_options);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classes));
        listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Fragment fragment = new Hello();
    //    Bundle args = new Bundle();
     //   args.putInt("position", position);
       // fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
        listView.setItemChecked(position, true);
       // setTitle(mPlanetTitles[position]);
       // getActionBar().setTitle(classes[position]);
        drawerLayout.closeDrawer(listView);

    }
}
