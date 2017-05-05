package com.example.emotionplayer;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.example.emotionplayer.MusicPlayer.database;


public class SynchAudio extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks {


    ArrayList<String> audioList=new ArrayList<String >();
    ArrayList<String> titleList;
    ListView lv;
    ArrayAdapter<String> adapter;
    Button btn;
    View view;
    android.app.Fragment fragment;
    ProgressBar progressBar;
    private TextView addSongMessage;
    Fragment getFragment()
    {
        return fragment;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.synch_audio);
      /*  progressBar= new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        progressBar.setIndeterminate(true);
        ViewGroup root=(ViewGroup)findViewById(android.R.id.content);
        root.addView(progressBar);*/
        //audioList = syncAudioMediaStore();
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);
        //addSongMessage=(TextView)findViewById(R.id.add_song_message);
        //addSongMessage.setVisibility(View.GONE);
        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
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
        });
        getSupportLoaderManager().initLoader(1,null,this);
    }

    @Override
    public android.support.v4.content.Loader onCreateLoader(int id, Bundle args) {
        //progressBar.setVisibility(View.VISIBLE);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        CursorLoader cursorLoader=new CursorLoader(this,uri,null,selection,null,sortOrder);

        //progressBar.setProgress(0);
        return cursorLoader;
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader loader, Object data) {

        Cursor cur=(Cursor)data;
        cur.moveToFirst();
        int count = 0;
        ArrayList<PathEmotion> pathEmotions = new SongTbHelper().getInfo(database.getReadableDatabase());
        if(cur != null){
            count = cur.getCount();
            if(count > 0) {
                PathEmotion temp = null;
                int i;
                while(cur.moveToNext()){
                    String d = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    for(i=0; i<pathEmotions.size(); i++) {
                        temp = pathEmotions.get(i);
                        if (temp.path.equals(d))
                            break;
                    }
                    if(i == pathEmotions.size())
                        audioList.add(d);
                }
            }
        }
        cur.close();

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
                //progressBar.setProgress(i);
            }
        }

        audioList = tempAudioList;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, titleList);
        lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(adapter);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        progressBar.setVisibility(View.GONE);
    }

    /*
    private ArrayList<String> syncAudioMediaStore(){
        ArrayList<String> audioList = new ArrayList<String>();
        ContentResolver cr = this.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cur = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;
        if(cur != null){
            count = cur.getCount();
            if(count > 0) {
                while(cur.moveToNext()){
                    String data = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                    audioList.add(data);
                }
            }
        }
        cur.close();
        return audioList;
    }

*/
}

