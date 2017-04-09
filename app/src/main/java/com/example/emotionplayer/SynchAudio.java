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

    View view;
    android.app.Fragment fragment;
    Fragment getFragment()
    {
        return fragment;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // fragment=new SocialProfileFragment();
        //view= fragment.getView();
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.synch_audio,null,false);
        drawerLayout.addView(view,0);
        getSupportActionBar().setTitle(getIntent().getStringExtra("position"));



    }


}

