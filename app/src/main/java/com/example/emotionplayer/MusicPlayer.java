package com.example.emotionplayer;

/**
 * Created by nikhil on 30-03-2017.
 */


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v7.app.*;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.app.*;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.DateTime;

import java.io.FileInputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReferenceArray;


class Emotion
{
    ArrayList<String> emotions;
    ArrayList<Double> scores;
}
public class MusicPlayer extends AppCompatActivity implements OnItemClickListener{

    //String classes[]={"Class1","Class2","Class3"}
    static public String PACKAGE_NAME;
    public static ListView listView;
    protected DrawerLayout drawerLayout;
    protected String[] classes;
    static boolean login=false;
    //UtilityObject utilityObject;
    private Toolbar toolbar;
    private String[] fullyQualifiedClassNames;
    private ActionBarDrawerToggle drawerListener;
    static Database database;
    private SQLiteDatabase dbRead;
    private SQLiteDatabase dbWrite;
    private String posts;
    public  static ProgressBar progressBar;
    public static TextView start_load_message;
    public static Emotion emotion;
    public static android.support.v7.app.ActionBar actionBar;
    static boolean isPaused;
    private static PieChart pieChart;
    static LinearLayout pie_chart_space;
    static RelativeLayout text_progress_layout;
    static private ArrayList<Integer> colors;

    static MediaPlayer player;
    static int currentTrack=0;
    static DatePicker datePicker;
    static TimePicker timePicker;
    static ArrayList<String> currentPlaylist;
    static long fb_posts_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emotion=new Emotion();
        isPaused=false;
       // player=new MediaPlayer();
        colors=new ArrayList<Integer>(5);
        colors.add(Color.BLUE);
        colors.add(Color.GRAY);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.YELLOW);

        emotion.emotions=new ArrayList<String>(5);
        emotion.scores=new ArrayList<Double>(5);
        pie_chart_space=(LinearLayout)findViewById(R.id.pie_chart_space);
        PACKAGE_NAME=getApplicationContext().getPackageName();
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1,classes));
        //	setListAdapter(new ArrayAdapter<String>(MusicPlayer.this,android.R.layout.simple_list_item_1, Arrays.asList(getResources().getStringArray(R.array.class_names))));
        setContentView(R.layout.drawer_layout);
         pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(false);
        text_progress_layout=(RelativeLayout)findViewById(R.id.text_progress_id);
        classes=getResources().getStringArray(R.array.class_names);
        fullyQualifiedClassNames=getResources().getStringArray(R.array.fully_qualified_class_names);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        //progressBar.setVisibility(View.VISIBLE);
        start_load_message=(TextView)findViewById(R.id.start_load_message);
        listView=(ListView)findViewById(R.id.core_options);
        listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,classes));
        listView.setOnItemClickListener(this);
        //listView.setVisibility(View.GONE);
        // utilityObject=Utility.getUtilityObject();
        // Toast.makeText(this,""+utilityObject,Toast.LENGTH_LONG);
        drawerListener=new ActionBarDrawerToggle(this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerListener);

        //toolbar=(Toolbar)findViewById(R.id.app_toolbar);
        //setSupportActionBar( (Toolbar)findViewById(R.id.app_toolbar));
        database=new Database(getApplicationContext());


        final AlertDialog date_dialog,time_dialog;

        final View view_time=getLayoutInflater().inflate(R.layout.time_picker,null,false);
        final AlertDialog.Builder time_builder=new AlertDialog.Builder(this);
        time_builder.setTitle("Time").setMessage("Set Time of Fb posts");
        time_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                timePicker=(TimePicker) view_time.findViewById(R.id.time_picker_id);
                SimpleDateFormat f=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                System.out.print("Here i am ");
                try {
                    Date date1=null;
                    int month,year,day;
                    month=(datePicker.getMonth()+1);
                    year=(datePicker.getYear());
                    day=(datePicker.getDayOfMonth());
                    String tempDay="",tempMonth="";
                    if(day<10)
                    {
                        tempDay="0"+day;
                    }
                    if(month<10)
                    {
                        tempMonth="0"+month;
                    }
                    if(Build.VERSION.SDK_INT>=23) {
                       date1= f.parse(tempMonth + "/" + tempDay + "/" + datePicker.getYear()+" "+
                                        timePicker.getHour()+":"+timePicker.getMinute()+":"+"00"
                       );
                        System.out.print("time\t"+timePicker.getHour()+timePicker.getMinute());
                    }
                    else
                    {
                        date1= f.parse(tempMonth + "/" + tempDay + "/" + datePicker.getYear()+" "+
                                timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute()+":"+"00");
                    }
                    System.out.print("Date is "+ date1);
                    DateTime actualDate = new DateTime(date1);
                    fb_posts_time=actualDate.getMillis();
                }
                catch (Exception e)
                {
                    System.out.print("Exception occured  "+e.getStackTrace());

                }
             /*   Calendar c = Calendar.getInstance();

                c.set(Calendar.YEAR, (datePicker.getYear()));
                c.set(Calendar.MONTH, (datePicker.getMonth()));
                c.set(Calendar.DAY_OF_MONTH, (datePicker.getDayOfMonth()));
                System.out.print("date\t"+c.YEAR+"\t"+c.MONTH+"\t"+c.DAY_OF_MONTH);
                if(Build.VERSION.SDK_INT>=23) {
                    c.set(Calendar.HOUR, timePicker.getHour());
                    c.set(Calendar.MINUTE, timePicker.getMinute());
                    System.out.print("time\t"+timePicker.getHour()+timePicker.getMinute());
                }
                else
                {
                    c.set(Calendar.HOUR, timePicker.getCurrentHour());
                    c.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    System.out.print("time deprecated\t"+timePicker.getCurrentHour()+timePicker.getCurrentMinute());
                }
                c.set(Calendar.SECOND,0);
                c.set(Calendar.MILLISECOND, 0);
*/
                //fb_posts_time=c.getTimeInMillis();
                System.out.print("Get time in millis "+fb_posts_time);
                dialog.dismiss();
                synchFb();

            }
        }).setView(view_time);
        time_dialog=time_builder.create();

        //time_dialog.show();



        final View view=getLayoutInflater().inflate(R.layout.date_picker,null,false);
        final AlertDialog.Builder date_builder=new AlertDialog.Builder(this);
        date_builder.setTitle("Date").setMessage("Set Date of Fb posts");
        date_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                System.out.print("Pressed cliked");
                datePicker=(DatePicker)view.findViewById(R.id.date_picker_id);
                System.out.print("Date picker is  "+datePicker.getYear());
                dialog.dismiss();
                time_dialog.show();

            }
        }).setView(view);
        date_dialog=date_builder.create();
        date_dialog.show();



    }


    @Override
    protected void onResume() {
        super.onResume();
        //synchFb();
        //LinearLayout la=(LinearLayout)findViewById(R.id.pie_chart_space);
        //la.setVisibility(View.VISIBLE);
    }
    static void setPieChart(int value) {

        PieDataSet dataSet;
        PieData data;
        switch (value) {
            case 0:
                pieChart.setDescription("Current Mood through Facebook posts");
                ArrayList<Entry> yvalues = new ArrayList<Entry>();
                ArrayList<String> xvalues = new ArrayList<String>();
                for (int i = 0; i < 5; i++) {
                    yvalues.add(new Entry(Float.parseFloat("" + emotion.scores.get(i)), i));
                    xvalues.add(emotion.emotions.get(i));
                }

                dataSet = new PieDataSet(yvalues, "Emotion");

                dataSet.setColors(colors);

                data = new PieData(xvalues, dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setDrawValues(false);
                pieChart.setData(data);
                pieChart.setDrawSliceText(false);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
                //pie_chart_space.setVisibility(View.VISIBLE);
                break;
             default:
                //pie_chart_space.setVisibility(View.GONE);
                break;
        }
    }
    private void synchFb()
    {
        if(SocialProfile.isLogin())
        {
            login=true;
            Toast.makeText(getApplicationContext(),"FB integration successful",Toast.LENGTH_LONG);
            System.out.print("getting fb posts\n");
            SocialProfile.getPosts();

            //Toast.makeText(this,"pOSTS DONE",Toast.LENGTH_LONG);
            //System.out.print(emotion.get(0).emotion+"\t"+emotion.get(0).score+"\n");
        }
        else
        {
           // progressBar.setVisibility(View.GONE);
          //  listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            start_load_message.setVisibility(View.GONE);
            setActionBar();
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        actionBar=getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.three_lines);


    }

    public static void setActionBar()
    {

    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerListener.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerListener.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        return true;
    }


    private void loginFb(int position)
    {
        System.out.print("fully qualified name "+ fullyQualifiedClassNames[position]);
        Intent i=new Intent(fullyQualifiedClassNames[position]);
        i.putExtra("position",classes[position]);
        startActivity(i);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        drawerLayout.closeDrawers();
     //   Toast.makeText(this,"psotion is "+position + "and name is"+classes[position],Toast.LENGTH_LONG).show();
       // System.out.print("item click");
        if(login==false && position!=3)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Login to Facebook")
                    .setTitle("Login Alert");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    loginFb(0);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        switch(position)
        {
            case 0:

               loginFb(position);
                break;


            case 1:
               Intent iplay=new Intent(fullyQualifiedClassNames[position]);
                iplay.putExtra("position",classes[position]);
                startActivity(iplay);
                break;

            case 3:
                finish();
                break;
            case 2:

                progressBar.setVisibility(View.VISIBLE);
                Intent i1 =new Intent(fullyQualifiedClassNames[position]);
                i1.putExtra("position",classes[position]);
                startActivity(i1);

                break;
                 // currentTrack=0;
                //new SongTbHelper().putInfo(database.getWritableDatabase(),"hi this is path",new ArrayList<Double>());
            //    ArrayList<PathEmotion> pathEmotions=
              //  createPlaylist(emotion,pathEmotions,3);
                //playCurrentPlaylist();
            //break;



        }


    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }

}




