package com.example.emotionplayer;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by nikhil on 09-04-2017.
 */

public class SynchAudio extends MusicPlayer {

    private String classes[] = {"Class1", "Class2", "Class3"};
    ArrayList<String> audioList;
    ArrayList<String> titleList;
    ListView lv;
    ArrayAdapter<String> adapter;
    Button btn;
    View view;
    android.app.Fragment fragment;
    Fragment getFragment()
    {
        return fragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioList = syncAudioMediaStore();
        titleList = new ArrayList<String>();
        ArrayList<String> tempAudioList = new ArrayList<String>();
        MediaMetadataRetriever fileMetadata = new MediaMetadataRetriever();
        String title;
        for(int i=0; i<audioList.size(); i++){
            fileMetadata.setDataSource(audioList.get(i));
            title = fileMetadata.extractMetadata(fileMetadata.METADATA_KEY_TITLE);
            if(title!=null) {
                titleList.add(title);
                tempAudioList.add(audioList.get(i));
            }
        }
        audioList = tempAudioList;
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, titleList);
        setListAdapter(adapter);
        lv = getListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SparseBooleanArray selectedItems = lv.getCheckedItemPositions();
        ArrayList<String> selectedItemsNames = new ArrayList<String>();
        int idx;
        for(int i=0; i<selectedItems.size(); i++){
            int position = selectedItems.keyAt(i);
            if (selectedItems.valueAt(i)) {
                idx = titleList.indexOf(adapter.getItem(position));
                new MusixmatchUtil().execute(audioList.get(idx));
            }
        }
    }


}

