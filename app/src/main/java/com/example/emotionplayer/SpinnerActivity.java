package com.example.emotionplayer;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

/**
 * Created by nikhil on 09-04-2017.
 */

public class SpinnerActivity extends AppCompatActivity{

    public static ProgressBar spinner;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState)
    {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.circular_progress_bar);
        spinner=(ProgressBar)findViewById(R.id.progress_bar);

    }

}
