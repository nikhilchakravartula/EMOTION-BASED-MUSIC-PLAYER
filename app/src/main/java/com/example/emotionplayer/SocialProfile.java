package com.example.emotionplayer;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.*;
import android.content.*;
public class SocialProfile extends Activity {

    String classes[] = {"Class1", "Class2", "Class3"};
    Button fbButton;
    Button twitterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_profile);
        fbButton = (Button) findViewById(R.id.fbButton);
        twitterButton = (Button) findViewById(R.id.twitterButton);
        addListeners();


    }

    protected void addListeners() {



        fbButton.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i = new Intent("com.example.emotionplayer.MUSICPLAYER");
                startActivity(i);
            }
        });

        twitterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent("com.example.emotionplayer.MUSICPLAYER");
                startActivity(i);

            }
        });


    }


}
