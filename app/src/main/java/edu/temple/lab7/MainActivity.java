package edu.temple.lab7;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.content.res.Configuration;



public class MainActivity extends AppCompatActivity implements BookListFragment.OnItemSelectedListener {


    boolean inFrame = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager table = findViewById(R.id.page);
        if (table == null)
            layoutLocate();
        else
            table.setAdapter(new customAdapter( arrList.displayBooks(),MainActivity.this));
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



    public void layoutLocate() {
        FrameLayout detailFrag = (FrameLayout) findViewById(R.id.bookHolder);
        if (detailFrag == null) {
            inFrame = false;

        }
        else{
            inFrame = true;
            BookListFragment listFrag =
                    (BookListFragment) getSupportFragmentManager().findFragmentById(R.id.bookfrag);
            listFrag.setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(arrList book) {
        if (inFrame == false) {
            Intent transfer = new Intent(this, BookDetailsFragment.class);
            startActivity(transfer.putExtra("book", book));

        } else if (inFrame == true){


            BookDetailsFragment bookFrag = BookDetailsFragment.newInstance(book);
            FragmentTransaction frag = getSupportFragmentManager().beginTransaction();
            frag.replace(R.id.bookHolder, bookFrag).commit();
        }
    }

}