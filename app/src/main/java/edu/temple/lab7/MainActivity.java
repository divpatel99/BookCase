package edu.temple.lab7;

import android.content.Intent;
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


public class MainActivity extends AppCompatActivity implements BookListFragment.OnItemSelectedListener {

    boolean inFrame = false;
    ViewPager table;
    EditText editText;
    Button button_Search;
    BookListFragment listFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////////////
        editText =findViewById(R.id.edit_text);
        button_Search=findViewById(R.id.button_search);
        //////////////
        table = findViewById(R.id.page);

       /* if (savedInstanceState!=null)
        {
            arrList.allBooksArraylist = (ArrayList<Book>) savedInstanceState.get("array");
        }
        else*/
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

   /* @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putSerializable("array",arrList.allBooksArraylist);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        arrList.allBooksArraylist = (ArrayList<Book>) savedInstanceState.get("array");
    }
    */


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

                Book book = new Book(id,title,author,published,cover_url);
                arrList.allBooksArraylist.add(book);
            }



            if (table == null) {
                layoutLocate();


            }else
                table.setAdapter(new customAdapter( arrList.allBooksArraylist,MainActivity.this));




        } catch (JSONException e)
        {
            Log.e("wahwah",e.getMessage());
            e.printStackTrace();
        }


    }

}