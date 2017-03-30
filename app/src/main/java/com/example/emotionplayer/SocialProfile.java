package com.example.emotionplayer;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.*;
import android.content.*;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import org.w3c.dom.Text;
import com.facebook.HttpMethod;
import android.util.Log;

import java.util.Arrays;

public class SocialProfile extends Activity {

    private String classes[] = {"Class1", "Class2", "Class3"};
    LoginButton fbButton;
    Button twitterButton;
    CallbackManager callbackmanager;
    TextView statusView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_social_profile);
        fbButton = (LoginButton) findViewById(R.id.fb_login_button);
        fbButton.setReadPermissions(Arrays.asList("user_posts"));
        twitterButton = (Button) findViewById(R.id.twitterButton);
        statusView=(TextView)findViewById(R.id.login_status_view);
    callbackmanager=(CallbackManager.Factory.create());
        fbButton.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                statusView.setText("Success"+loginResult.getAccessToken().getUserId()+"\n"+
                                loginResult.getAccessToken().getToken());
                getPosts();

            }

            @Override
            public void onCancel() {
                statusView.setText("Cancel occured");
            }

            @Override
            public void onError(FacebookException error) {
                statusView.setText("Error occured"+error.toString());
            }
        });


    }

    private void getPosts(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/posts", null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        statusView.setText("Activity is" +response.toString());
                    }
                }
        ).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }
}
