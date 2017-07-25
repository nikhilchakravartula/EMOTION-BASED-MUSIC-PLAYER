package com.nocompany.neloy.someapp;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ElementTone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;

import org.apache.commons.lang3.ObjectUtils;
import org.jmusixmatch.MusixMatch;
import org.jmusixmatch.MusixMatchException;
import org.jmusixmatch.entity.lyrics.Lyrics;
import org.jmusixmatch.entity.track.Track;
import org.jmusixmatch.entity.track.TrackData;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ToneAnalyzerUtil().execute("");
        //new MusixmatchUtil().execute("");
    }

    void someFunction(View v){
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("Something!");
        EditText edit = (EditText) findViewById(R.id.someId);
        Editable num = edit.getText();
        int n;
        try {
            n = Integer.parseInt(String.valueOf(num));
        }
        catch(Exception e){
            n = 10;
        }
        while(n>9){
            int sum = 0;
            int x = n;
            while(x>0){
                sum+=x%10;
                x=x/10;
            }
            n = sum;
        }
        String returnText = "IronMan";
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        switch (n){
            case 0: returnText = "IronMan";
                imageView.setImageResource(R.drawable.ironman);
                break;
            case 1: returnText = "Batman";
                imageView.setImageResource(R.drawable.batman);
                break;
            case 2: returnText = "SpiderMan";
                imageView.setImageResource(R.drawable.spiderman);
                break;
            case 3: returnText = "Flash";
                imageView.setImageResource(R.drawable.flash);
                break;
            case 4: returnText = "Hulk";
                imageView.setImageResource(R.drawable.hulk);
                break;
            case 5: returnText = "Cap";
                imageView.setImageResource(R.drawable.cap);
                break;
            case 6: returnText = "Aquaman";
                imageView.setImageResource(R.drawable.aquaman);
                break;
            default: returnText = "No Superhero!";
                imageView.setImageResource(R.drawable.venky);
        }
        text.setText("Your SuperHero Name: "+returnText);
        MediaPlayer m = MediaPlayer.create(MainActivity.this, R.raw.someaudio);
        try{
            //m.start();
        }
        catch (Exception me){

        }
    }

    private class ToneAnalyzerUtil extends AsyncTask<String, Integer, String>{
        private String toneDisplay = "";
        @Override
        protected String doInBackground(String... params) {
            ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
            service.setUsernameAndPassword("436bee8d-8736-459d-941f-fa6b441a4604", "kmtdptoC25d7");

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
            TextView text1 = (TextView) findViewById(R.id.textView2);
            text1.setText(toneDisplay);
        }
    }

    private class MusixmatchUtil extends AsyncTask<String, Integer, String>{
        private String lyricDisplay = "";
        @Override
        protected String doInBackground(String... params) {
            String apiKey = "ad3f1bae155e113eed3805157baa45d2";
            MusixMatch musixMatch = new MusixMatch(apiKey);
            String trackName = "Heavy";
            String artist = "Linkin Park";
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
            TextView text1 = (TextView) findViewById(R.id.textView2);
            text1.setText(lyricDisplay);
        }
    }
}
