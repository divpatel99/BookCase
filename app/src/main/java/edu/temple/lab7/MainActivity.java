package edu.temple.lab7;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.content.res.Configuration;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import edu.temple.lab7.dbHelper.DatabaseHelper;
import edu.temple.lab7.listner.UpdateSeekBar;
import edu.temple.lab7.listner.checkPermission;


public class MainActivity extends AppCompatActivity implements BookListFragment.OnItemSelectedListener, BookDetailsFragment.Callback, customAdapter.Callback_Adapter {

    boolean inFrame = false;
    ViewPager table;
    EditText editText;
    Button button_Search;
    BookListFragment listFrag;
    private static final int MY_READ_REQUEST = 1;
    AudiobookService audiobookService;
    boolean mServiceBound = false;
    AudiobookService.MediaControlBinder mediaControlBinder;
    private Prefes prefes;
    SeekBar seekBar;
    Handler handler;
    Runnable r;
    private DatabaseHelper db;

    private customAdapter customAdapters;
    private TextView timeTV;
    private UpdateSeekBar updateSeekBar;
    private edu.temple.lab7.listner.checkPermission checkPermissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askPermission();
        prefes=new Prefes(getApplicationContext(),Utils.SAVE_MEDIA_PLAYER_PREF);
        timeTV=(TextView)findViewById(R.id.time);
        handler=new Handler();
        r = new Runnable(){
            public void run() {

                // Toast.makeText(getApplicationContext(),"Testing......."+AudiobookService.,Toast.LENGTH_SHORT).show();
                handler.postDelayed(r, 1000);
            }
        };

        db = new DatabaseHelper(this);
        Utils.arraylistBookDB.clear();
        Utils.arraylistBookDB.addAll(db.getAllNotes());

        /////////////////
        editText =findViewById(R.id.edit_text);
        button_Search=findViewById(R.id.button_search);
        //////////////
        table = findViewById(R.id.page);

        checkPermissions=new checkPermission() {
            @Override
            public void checkRW(boolean permission) {
                askPermission();
            }
        };

        {
            getBooks_request(prefes.getValue(Utils.SEARCH_API,""));
        }


        button_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(TextUtils.isEmpty( editText.getText().toString())) {
                    editText.setError("Enter title, author, or published year.");
                    return;
                }
                {
                    mediaControlBinder.setProgressHandler(null);
                    mediaControlBinder.saveCurrentMP();
                    seekBar.setMax(0);
                    seekBar.setProgress(0);
                    prefes.setValue(Utils.SEARCH_API,editText.getText().toString());
                    getBooks_request(editText.getText().toString());
                }

            }
        });
        seekBar=findViewById(R.id.seekbar_);

        updateSeekBar=new UpdateSeekBar() {
            @Override
            public void updateSeekbar(final int position, final int time) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final long minutes_total=(time)/60;
                        final int seconds_total= (int) ((time)%60);
                        final long minutes_current=(position)/60;
                        final int seconds_current= (int) ((position)%60);
                        timeTV.setText(minutes_current+":"+seconds_current+"/"+minutes_total+":"+seconds_total);
                        seekBar.setMax(time);
                        seekBar.setProgress(position);



                    }
                });
            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                /* seekBar.setMax(AudiobookService.mediaPlayer.getDuration());*/
                //   seekBar.setProgress(progress);
                if(fromUser) {
                    mediaControlBinder.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        DirectoryHelper.createDirectory(this);

    }

    public void askPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_READ_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_READ_REQUEST);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case MY_READ_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        DirectoryHelper.createDirectory(this);
                    }
                }else {

                }
                return;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            arrList.isLan = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            arrList.isVer = true;
        }

        /*if(mediaControlBinder!=null)
            mediaControlBinder.savemediaPlayerState();
*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {

        super.onRestoreInstanceState(savedInstanceState);

    }

    public void layoutLocate()
    {
        FrameLayout detailFrag = (FrameLayout) findViewById(R.id.bookHolder);
        if (detailFrag == null)
        {
            inFrame = false;
        }
        else
        {
            inFrame = true;
            listFrag = (BookListFragment) getSupportFragmentManager().findFragmentById(R.id.bookfrag);
            listFrag.setActivateOnItemClick(true);

            listFrag.my_work(arrList.allBooksArraylist);
        }
    }

    @Override
    public void onItemSelected(int position)
    {
        if (inFrame == false)
        {
            Intent transfer = new Intent(this, BookDetailsFragment.class);
            startActivity(transfer.putExtra("bookObject", arrList.allBooksArraylist.get(position)));

        } else if (inFrame == true)
        {

            BookDetailsFragment bookFrag = BookDetailsFragment.newInstance(arrList.allBooksArraylist.get(position));
            FragmentTransaction frag = getSupportFragmentManager().beginTransaction();
            frag.replace(R.id.bookHolder, bookFrag).commit();
        }

    }

    public void getBooks_request(String search)
    {

        if(!InternetConnection.checkConnection(this))
        {
            Toast.makeText(this, "Network not Available", Toast.LENGTH_SHORT).show();
            return;
        }

        String mJSONURLString=null;
        arrList.allBooksArraylist.clear();

        if(!TextUtils.isEmpty( search))
        {
            mJSONURLString ="https://kamorris.com/lab/audlib/booksearch.php?search="+search+"";
        }
        else
        {
            mJSONURLString ="https://kamorris.com/lab/audlib/booksearch.php";
        }

        Toast.makeText(MainActivity.this, "Loading Data...!", Toast.LENGTH_SHORT).show();


        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(mJSONURLString,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray)
                    {
                        Log.e("wah",jsonArray.toString());
                        load_data(jsonArray);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("wah",volleyError.getLocalizedMessage());
                        Toast.makeText(MainActivity.this, "Unable to fetch data: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
        // Add the request to the RequestQueue.
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);


    }

    private void load_data(JSONArray response)
    {

        try
        {
            //    JSONArray array = response.getJSONArray("array");
            for (int i=0;i<response.length();i++)
            {
                int id = response.getJSONObject(i).getInt("book_id");
                String title=response.getJSONObject(i).getString("title");
                String author=response.getJSONObject(i).getString("author");
                int published=response.getJSONObject(i).getInt("published");
                String cover_url=response.getJSONObject(i).getString("cover_url");
                int duration =response.getJSONObject(i).getInt("duration");

                Book book = new Book(id,title,author,published,cover_url,duration);
                arrList.allBooksArraylist.add(book);
            }



            if (table == null) {
                layoutLocate();


            }else {


                table.setAdapter(new customAdapter(arrList.allBooksArraylist, MainActivity.this, this,checkPermissions));
            }



        } catch (JSONException e)
        {
            Log.e("wahwah",e.getMessage());
            e.printStackTrace();
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudiobookService.class);
        //pass data through intent
        // intent.putExtra("ID",1);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        //  handler.post(r);
        if(prefes.getBool(Utils.SAVE_MEDIA_PLAYING,false)){
            Toast.makeText(this,"POsition"+prefes.getInt(Utils.SAVE_MEDIA_PLAYER_POSITION,0),Toast.LENGTH_SHORT).show();
        }

    }




    @Override
    protected void onStop() {
        super.onStop();

        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
            //  handler.removeCallbacks(r);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // mediaControlBinder.stop();
        mediaControlBinder.savemediaPlayerState();
        //  handler.removeCallbacks(r);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaControlBinder.setProgressHandler(null);
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaControlBinder = (AudiobookService.MediaControlBinder) service;
            audiobookService = mediaControlBinder.getService();
            mediaControlBinder.updateS(updateSeekBar);
            mediaControlBinder.setProgressHandler(handler);
            mServiceBound = true;



        }
    };


    @Override
    public void onClickButton(int number,int id,String file)
    {

        switch (number)
        {
            case 1:
                if(AudiobookService.mediaPlayer.isPlaying()){
                    mediaControlBinder.setProgressHandler(null);
                    mediaControlBinder.saveCurrentMP();
                    seekBar.setMax(0);
                    seekBar.setProgress(0);
                }
                int position= prefes.getInt(String.valueOf(id),0);
                prefes.setValue(Utils.BOOK_ID,String.valueOf(id));
                mediaControlBinder.play(id,position);
                Toast.makeText(this,"Playing: From Url",Toast.LENGTH_SHORT).show();
                mediaControlBinder.setProgressHandler(handler);
                // handler.post(r);
                break;
            case 2:
                mediaControlBinder.pause();
                mediaControlBinder.setProgressHandler(null);
                Toast.makeText(this,prefes.getBool(Utils.SAVE_MEDIA_PLAYING,false)+"MediaPosition"+prefes.getInt(Utils.SAVE_MEDIA_PLAYER_POSITION,0),Toast.LENGTH_SHORT).show();

                break;
            case 3:
                mediaControlBinder.stop();
                mediaControlBinder.setProgressHandler(null);
                seekBar.setMax(0);
                seekBar.setProgress(0);
                timeTV.setText("0:0");
                break;
            case 4:
                mediaControlBinder.seekTo(id);
                break;
            case 5  :
                if(AudiobookService.mediaPlayer.isPlaying()){
                    mediaControlBinder.setProgressHandler(null);
                    mediaControlBinder.saveCurrentMP();
                    seekBar.setMax(0);
                    seekBar.setProgress(0);
                }
                int positions= prefes.getInt(String.valueOf(id),0);
                prefes.setValue(Utils.BOOK_ID,String.valueOf(id));
                File soundFile = new  File(file);
                Toast.makeText(this,"Playing:"+soundFile,Toast.LENGTH_SHORT).show();
                mediaControlBinder.setProgressHandler(handler);
                mediaControlBinder.play(soundFile,positions);
                break;
        }


        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                //your method
            }
        }, 0, 1000);//put here time 1000 milliseconds=1 second

    }

}