package com.example.emotionplayer;
import android.app.*;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ElementTone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import okhttp3.internal.Util;

/**
 * Created by nikhil on 01-04-2017.
 */





class Emotion
{
    String emotion;
    Double score;
}


class ToneAnalyzerUtil extends AsyncTask<String, Integer, String>{
    private String toneDisplay = "";
    private String path = "";
    @Override
    protected String doInBackground(String... params) {
        publishProgress(0);
        ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setUsernameAndPassword("436bee8d-8736-459d-941f-fa6b441a4604", "kmtdptoC25d7");
        String text = params[0];
        //path = params[1];
    // Call the service and get the tone
        publishProgress(50);
        ToneAnalysis tone = service.getTone(text, null).execute();
        ElementTone etone = tone.getDocumentTone();
        List toneCategories = etone.getTones();
        List tones = null;
        for(Object tctemp: toneCategories){
            ToneCategory tc = (ToneCategory)tctemp;
            if(tc.getId().equals("emotion_tone")){
                tones = tc.getTones();
            }
        }

        for(Object tstemp: tones){
            ToneScore ts = (ToneScore)tstemp;
            Emotion e=new Emotion();
            e.emotion=ts.getName();
            e.score=(ts.getScore());
            //MusicPlayer.emotion.add(e);
            toneDisplay += ts.getName() + ": " + String.valueOf(ts.getScore()) + "\n";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        //MusicPlayer.progressBar.setVisibility(View.GONE);
       // MusicPlayer.listView.setVisibility(View.VISIBLE);

        MusicPlayer.setActionBar();
        System.out.println("result is"+toneDisplay);
        MusicPlayer.progressBar.setVisibility(View.GONE);

            // Insert to DB

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        MusicPlayer.progressBar.setProgress(values[0]);
    }
}

class MusixmatchUtil extends AsyncTask<String, Integer, String>{
    private String lyricDisplay = "";
    private String path = "";
    @Override
    protected String doInBackground(String... params) {
        String apiKey = "ad3f1bae155e113eed3805157baa45d2";
        MusixMatch musixMatch = new MusixMatch(apiKey);
        path = params[0];
        MediaMetadataRetriever metadata = new MediaMetadataRetriever();
        metadata.setDataSource(path);
        String trackName = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String artist = metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        //trackName = "Heavy";
        //artist = "Linkin park";
        if(trackName!=null && artist != null) {
            Track track = null;
            try {
                track = musixMatch.getMatchingTrack(trackName, artist);
                TrackData data = track.getTrack();
                int trackId = data.getTrackId();
                Lyrics lyrics = musixMatch.getLyrics(trackId);
                lyricDisplay = lyrics.getLyricsBody();
            } catch (MusixMatchException e) {
                lyricDisplay = "";
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        //TextView text1 = (TextView) findViewById(R.id.textView);
        //text1.setText(lyricDisplay);
        if(lyricDisplay != ""){
            new ToneAnalyzerUtil().execute(lyricDisplay, path);
        }
    }
}

final class Schemas
{
    private Schemas()
    {

    }


    public static class SongEmotionSchema implements BaseColumns
    {

        public static final String TABLE_NAME = "song_emotion_table";
        public  static  final String ID="_id";
        public static final String SONG_PATH = "path";
        public static final String ANGER = "anger";
        public static final String DISGUST = "disgust";
        public static final String FEAR = "fear";
        public static final String JOY = "joy";
        public  static  final String SADNESS="sadness";
    }
}

class Database extends SQLiteOpenHelper
{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "utility.db";



    private final String  CREATE_SONG_TABLE= "CREATE TABLE " + Schemas.SongEmotionSchema.TABLE_NAME + " (" +
            Schemas.SongEmotionSchema._ID + " INTEGER PRIMARY KEY," +
            Schemas.SongEmotionSchema.SONG_PATH + " TEXT," +
            Schemas.SongEmotionSchema.ANGER + " DOUBLE," +
            Schemas.SongEmotionSchema.DISGUST + " DOUBLE," +
            Schemas.SongEmotionSchema.FEAR + " DOUBLE," +
            Schemas.SongEmotionSchema.JOY + " DOUBLE," +
            Schemas.SongEmotionSchema.SADNESS + " DOUBLE )";

    public Database(Context context){

                super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SONG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}

class SongTbHelper
{
    void putInfo(SQLiteDatabase dbWrite,String path,Double score[])
    {


        //for(int i=0;i<emotion.length;i++)
        ContentValues values = new ContentValues();
        values.put(Schemas.SongEmotionSchema.SONG_PATH,"'"+path+"'");
        values.put(Schemas.SongEmotionSchema.ANGER,score[0]);
        values.put(Schemas.SongEmotionSchema.DISGUST,score[1]);
        values.put(Schemas.SongEmotionSchema.FEAR,score[2]);
        values.put(Schemas.SongEmotionSchema.JOY,score[3]);
        values.put(Schemas.SongEmotionSchema.SADNESS,score[4]);




        long newRowId =dbWrite.insert(Schemas.SongEmotionSchema.TABLE_NAME,null,values);
    }

    void deleteInfo(SQLiteDatabase dbWrite,String path)
    {
        dbWrite.delete(Schemas.SongEmotionSchema.TABLE_NAME,"WHERE COLUMN_NAME "+"LIKE ?",new String[]{"VALUEFORCOLUMN"});
    }
    void getInfo(SQLiteDatabase dbRead)
    {
        System.out.print("READIG FROM DB");
        Cursor cursor=dbRead.query(Schemas.SongEmotionSchema.TABLE_NAME,
                new String[]{Schemas.SongEmotionSchema.SONG_PATH,Schemas.SongEmotionSchema.ANGER},
                null,null,null,null,null

                );
        if(cursor!=null)
            cursor.moveToFirst();
        while(cursor!=null && cursor.moveToNext())
        {
            System.out.println("value is "+cursor.getString(cursor.getColumnIndexOrThrow(Schemas.SongEmotionSchema.SONG_PATH)));

        }
        System.out.print("READ DONE\n");
    }


}