package edu.temple.lab7;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements BookListFragment.OnItemSelectedListener, BookDetailsFragment.Callback, customAdapter.Callback_Adapter {

    boolean inFrame = false;
    ViewPager table;
    EditText editText;
    Button button_Search;
    BookListFragment listFrag;

    AudiobookService audiobookService;
    boolean mServiceBound = false;
    AudiobookService.MediaControlBinder mediaControlBinder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////////////
        editText =findViewById(R.id.edit_text);
        button_Search=findViewById(R.id.button_search);
        //////////////
        table = findViewById(R.id.page);


        {
            getBooks_request("");
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
                    getBooks_request(editText.getText().toString());
                }

            }
        });
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            arrList.isLan = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            arrList.isVer = true;
        }


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


            }else
                table.setAdapter(new customAdapter( arrList.allBooksArraylist,MainActivity.this,this));




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



    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mediaControlBinder.stop();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mediaControlBinder = (AudiobookService.MediaControlBinder) service;
            audiobookService = mediaControlBinder.getService();
            mServiceBound = true;



        }
    };


    @Override
    public void onClickButton(int number,int id)
    {
        switch (number)
        {
            case 1:
                mediaControlBinder.play(id);
                break;
            case 2:
                mediaControlBinder.pause();
                break;
            case 3:
                mediaControlBinder.stop();
                break;
            case 4:
                mediaControlBinder.seekTo(id);


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