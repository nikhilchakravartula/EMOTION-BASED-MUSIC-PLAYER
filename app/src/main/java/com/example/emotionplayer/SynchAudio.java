package com.example.emotionplayer;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

/**
 * Created by nikhil on 09-04-2017.
 */

public class SynchAudio extends MusicPlayer implements View.OnClickListener {

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
        setContentView(R.layout.synch_audio);
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
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, titleList);
        lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(adapter);
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


    private void syncAudio(){
        String state = android.os.Environment.getExternalStorageState();
        String externalStorageRoot = null;
        if (android.os.Environment.MEDIA_MOUNTED.equals(state) ||
                android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageRoot = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            syncAudioUtil(externalStorageRoot);
            syncAudioUtil("/storage/sdcard1/");
        }
        else{
            // Raise Exception No Storage found
        }
    }

    private void syncAudioMediaStore(Context context){
        ContentResolver cr = context.getContentResolver();
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
//                    TextView t = (TextView) findViewById(R.id.textView2);
//                    String tmp = (String) t.getText();
//                    t.setText(tmp + "\n" + data);
                    saveSongTupleToDB(data);
                }
            }
        }
        cur.close();
    }

    private void syncAudioUtil(String rootPath){
        try{
            String s = "";
            //TextView t1 = (TextView) findViewById(R.id.textView);
            //t1.setText(rootPath);
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isFile()){
                    MediaMetadataRetriever fileMetadata = new MediaMetadataRetriever();
                    fileMetadata.setDataSource(file.getAbsolutePath());
                    if("yes".equals(fileMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)) &&
                            fileMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) == null) {
                        saveSongTupleToDB(file.getAbsolutePath());
                        //s += file.getName() + "\n";
                    }
                }
                else {
                    syncAudioUtil(file.getAbsolutePath());
                }
            }
//            TextView t = (TextView) findViewById(R.id.textView2);
//            String tmp = (String) t.getText();
//            if(tmp != null)
//                s = tmp + s;
//            t.setText(s);
        }catch(Exception e){
            return;
        }
    }


    private void saveSongTupleToDB(String songPath) {
    }


}

