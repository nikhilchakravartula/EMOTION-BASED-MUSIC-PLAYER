package com.example.emotionplayer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by nikhil on 30-03-2017.
 */

public class Random extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_layout);
        TextView tv=(TextView)findViewById(R.id.random_text_id);
        tv.setText(getIntent().getStringExtra("tone_detail"));
    }
}
