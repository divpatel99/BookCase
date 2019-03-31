package edu.temple.lab7;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookDetailsFragment extends Fragment{
    TextView bookName,text_author,text_yearpublish;
    ImageView imageView;

    Book book;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.bookdetail_frag, container, false);

        imageView=(ImageView) view.findViewById(R.id.imageview);
        bookName = (TextView) view.findViewById(R.id.bookName);
        text_author = (TextView) view.findViewById(R.id.text_author);
        text_yearpublish = (TextView) view.findViewById(R.id.text_yearpublished);

        if (arrList.isVer == false) {
            //do work for landscape
        }
        bookName.setText(book.getTitle());
        bookName.setTextColor(0xFF00FF00);

        Glide.with(this).load(book.getCoverURL()).into(imageView);

        text_author.setText(book.getAuthor());
        text_yearpublish.setText(book.getPublished()+"");

        return view;
    }


    public static class bookActivity extends FragmentActivity
    {
        BookDetailsFragment detailView;

        @Override
        public boolean onCreateOptionsMenu(Menu list) {
            getMenuInflater().inflate(R.menu.bookmenu, list);
            return true;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_frag);
            Book book = (Book) getIntent().getSerializableExtra("bookObject");

            if (savedInstanceState == null) {
                detailView = BookDetailsFragment.newInstance(book);
                FragmentTransaction transactionFrag = getSupportFragmentManager().beginTransaction();
                transactionFrag.replace(R.id.bookHolder, detailView).commit();
            }
        }

    }

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        book = (Book) getArguments().getSerializable("bookObject");

    }



    public static BookDetailsFragment newInstance (Book book){
        BookDetailsFragment frag1 = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bookObject", book);

        frag1.setArguments(bundle);
        return frag1;
    }
}

