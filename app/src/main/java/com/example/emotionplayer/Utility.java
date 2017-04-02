package com.example.emotionplayer;

import android.content.Intent;
import android.os.AsyncTask;

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

import java.util.List;

import okhttp3.internal.Util;

/**
 * Created by nikhil on 01-04-2017.
 */
class UtilityObject {
    private MusixmatchUtil musixMatch;
    private ToneAnalyzerUtil toneAnalyzer;

    void setMusixMatchUtil(MusixmatchUtil o)
    {
        musixMatch=o;
    }
    void setToneAnalyzerUtil(ToneAnalyzerUtil o)
    {
        toneAnalyzer=o;
    }
    MusixmatchUtil getMusixMatchUtil()
    {
        return musixMatch;
    }
    ToneAnalyzerUtil getToneAnalyzerUtil()
    {
        return toneAnalyzer;
    }

    private void syncAudio(){
        String state = android.os.Environment.getExternalStorageState();
        String externalStorageRoot = null;
        if (android.os.Environment.MEDIA_MOUNTED.equals(state) ||
                android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            externalStorageRoot = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            syncAudioUtil(externalStorageRoot);
        }
        else{
            // Raise Exception No Storage found
        }
    }

    private void syncAudioUtil(String rootPath){
        try{
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    syncAudioUtil(file.getAbsolutePath());
                } else{
                    MediaMetadataRetriever fileMetadata = new MediaMetadataRetriever();
                    fileMetadata.setDataSource(file.getAbsolutePath());
                    if(fileMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO) != null
                            && fileMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO) == null) {
                        fileList.add(file.getAbsolutePath());
                        saveSongTupleToDB(file.getAbsolutePath());
                    }
                }
            }
        }catch(Exception e){
            return;
        }
    }

    private void saveSongTupleToDB(String songPath){

    }


}
public class Utility {

    private static UtilityObject utilityObject=null;
    private Utility()
    {

    }

    public static UtilityObject getUtilityObject() {
        if(utilityObject==null)
        {
            utilityObject=new UtilityObject();
            utilityObject.setMusixMatchUtil(new MusixmatchUtil());
            utilityObject.setToneAnalyzerUtil(new ToneAnalyzerUtil());
        }
        return utilityObject;
    }




}


 class ToneAnalyzerUtil extends AsyncTask<String, Integer, String> {
    private String toneDisplay = "";
     private String username="436bee8d-8736-459d-941f-fa6b441a4604";
     private String password="kmtdptoC25d7";
    @Override
    protected String doInBackground(String... params) {
        ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setUsernameAndPassword(username, password);

        String text =
                "I know the times are difficult! Our sales have been "
                        + "disappointing for the past three quarters for our data analytics "
                        + "product suite. We have a competitive data analytics product "
                        + "suite in the industry. But we need to do our job selling it! "
                        + "We need to acknowledge and fix our sales challenges. "
                        + "We can’t blame the economy for our lack of execution! "
                        + "We are missing critical sales opportunities. "
                        + "Our product is in no way inferior to the competitor products. "
                        + "Our clients are hungry for analytical tools to improve their "
                        + "business outcomes. Economy has nothing to do with it.";

// Call the service and get the tone
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
            toneDisplay += ts.getName() + ": " + String.valueOf(ts.getScore()) + "\n";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent i=new Intent("com.example.emotionplayer.RANDOM");
        i.putExtra("tone_detail",toneDisplay);
        // startActivity(i);
    }
}


 class MusixmatchUtil extends AsyncTask<String, Integer, String>{
    private String lyricDisplay = "";
     private String apiKey = "ad3f1bae155e113eed3805157baa45d2";
     private String trackName = "Heavy";
    private String artist = "Linkin Park";
    @Override
    protected String doInBackground(String... params) {

        MusixMatch musixMatch = new MusixMatch(apiKey);

        Track track = null;
        try {
            track = musixMatch.getMatchingTrack(trackName, artist);
            TrackData data = track.getTrack();
            int trackId = data.getTrackId();
            Lyrics lyrics = musixMatch.getLyrics(trackId);
            lyricDisplay = lyrics.getLyricsBody();
        } catch (MusixMatchException e) {
            lyricDisplay = "Oops!";
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent i=new Intent("com.example.emotionplayer.RANDOM");
        i.putExtra("tone_detail",lyricDisplay);
 //       startActivity(i);
    }
}

