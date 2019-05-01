package edu.temple.lab7;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookDetailsFragment extends Fragment implements View.OnClickListener {
    TextView bookName,text_author,text_yearpublish;
    ImageView imageView;
    Book book;
    Button play_btn,pasuse_btn,stop_btn;


    public interface Callback {
        public void onClickButton(int number,int id,String file);
    }

    private Callback callback;

    @Override
    public void onAttach(Activity ac) {
        super.onAttach(ac);
        callback = (Callback) ac;
    }

    @Override
    public void onDetach() {
        callback = null;
        super.onDetach();
    }



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.bookdetail_frag, container, false);

        imageView=(ImageView) view.findViewById(R.id.imageview);
        bookName = (TextView) view.findViewById(R.id.bookName);
        text_author = (TextView) view.findViewById(R.id.text_author);
        text_yearpublish = (TextView) view.findViewById(R.id.text_yearpublished);
        play_btn =view.findViewById(R.id.play_button);
        pasuse_btn =view.findViewById(R.id.pause_button);
        stop_btn =view.findViewById(R.id.stop_button);


        play_btn.setOnClickListener(this);
        pasuse_btn.setOnClickListener(this);
        stop_btn.setOnClickListener(this);



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

    @Override
    public void onClick(View v)
    {
        if (v.getId() == pasuse_btn.getId())
        {
            callback.onClickButton(2,book.getId(),"");
        }
        else if (v.getId() == stop_btn.getId())
        {
            callback.onClickButton(3,book.getId(),"");
        }
        else if (v.getId() == play_btn.getId())
        {
            callback.onClickButton(1,book.getId(),"");
        }
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

