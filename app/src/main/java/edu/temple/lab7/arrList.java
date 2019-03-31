package edu.temple.lab7;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class arrList implements Serializable {


    public static boolean isLan = false;
    public static boolean isVer = false;
    Application application;


    public arrList(Application application) {

        this.application =application;
    }

    public static  ArrayList<Book>  allBooksArraylist =new ArrayList<>();
    public static ArrayList<String > displayBooksTitle(ArrayList<Book> arrayList) {
        ArrayList<String> books = new ArrayList<>();
        for (int i=0; i< arrayList.size();i++)
        {
            books.add(arrayList.get(i).title);
        }
        return books;
    }



}