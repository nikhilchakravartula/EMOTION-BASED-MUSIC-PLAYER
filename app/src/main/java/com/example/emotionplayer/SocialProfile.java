package com.example.emotionplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.pm.ActivityInfoCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.MutableInt;
import android.widget.Button;
import android.view.*;
import android.content.*;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;
import com.facebook.HttpMethod;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.logging.Level;

import okhttp3.internal.Util;


public class SocialProfile extends AppCompatActivity {


    LoginButton fbButton;
    Button twitterButton;
    CallbackManager callbackmanager;
    TextView statusView;
    private static String posts;
    View view;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_profile);
        builder = new AlertDialog.Builder(this);
        // fragment=new SocialProfileFragment();
       //view= fragment.getView();
        //LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       // view=inflater.inflate(R.layout.activity_social_profile,null,false);
       // drawerLayout.addView(view,0);
      //  LinearLayout la=(LinearLayout)findViewById(R.id.pie_chart_space);
    //    la.setVisibility(View.GONE);
      getSupportActionBar().setTitle(getIntent().getStringExtra("position"));
        fbButton=(LoginButton)findViewById(R.id.fb_login_button);
        fbButton.setReadPermissions(Arrays.asList("user_posts"));
       // twitterButton = (Button) view.findViewById(R.id.twitterButton);
        //twitterButton.setEnabled(false);
        if(isLogin())
        {
            System.out.println("Login already\n");
            //getPosts();
        }
        statusView=(TextView)findViewById(R.id.login_status_view);
        callbackmanager=(CallbackManager.Factory.create());
        fbButton.registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                statusView.setText("Connected to Facebook");

                builder.setMessage("Login success. See main page for your emotion chart")
                        .setTitle("Login Alert");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                System.out.print("SUCCESS");
                MusicPlayer.login=true;
                MusicPlayer.progressBar.setVisibility(View.VISIBLE);
                MusicPlayer.start_load_message.setVisibility(View.VISIBLE);
                getPosts();

            }

            @Override
            public void onCancel() {
                statusView.setText("Cancel occured");
            }

            @Override
            public void onError(FacebookException error) {
                statusView.setText("Error occured "+error.toString());
                Toast.makeText(getApplicationContext(),"Cannot connected to Facebook",Toast.LENGTH_LONG);
            }
        });


    }

    public static void getPosts(){
        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me/posts?since="+ MusicPlayer.fb_posts_time/1000, null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        posts="";
                        try {

                            System.out.print("timestamp is ");
                            System.out.print("+MusicPlayer.fb_posts_time"+MusicPlayer.fb_posts_time);
                            posts="";
                            JSONObject object=response.getJSONObject();
                            JSONArray arr=object.getJSONArray("data");
                            for(int i=0;i<arr.length();i++)
                            {
                                if(arr.getJSONObject(i).has("message"))
                                {
                                    posts+=arr.getJSONObject(i).getString("message");
                                    posts+=".";
                                }
                            }
                            System.out.print("getting tone of fb posts "+posts);
                            if(posts.length()!=0)
                            new ToneAnalyzerUtil().execute("FBPOSTS",posts);
                            else throw new Exception();
                            //statusView.setText("Result is "+posts);
                           // System.out.println("value is" + object.get("graphObject").toString());

                        }
                        catch(Exception e)
                        {
                           // statusView.setText("EXCEPTION"+ e.toString());
                            MusicPlayer.progressBar.setVisibility(View.GONE);
                            MusicPlayer.text_progress_layout.setVisibility(View.GONE);
                            System.out.print("Excepton "+e.toString());

                        }
                    }
                }
        ).executeAsync();

    }

    public static boolean isLogin()
    {
        return AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile()!=null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }

}

