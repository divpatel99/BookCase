package edu.temple.lab7;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BookDetailsFragment extends Fragment{
    TextView bookName;
    arrList book;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.bookdetail_frag, container, false);

        bookName = (TextView) view.findViewById(R.id.bookName);

        if (arrList.isVer == false) {
            //do work for landscape
        }
        bookName.setText(book.toString());
        bookName.setTextColor(0xFF00FF00);

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
            arrList book = (arrList) getIntent().getSerializableExtra("book");

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
        book = (arrList) getArguments().getSerializable("book");

    }



    public static BookDetailsFragment newInstance (arrList book){
        BookDetailsFragment frag1 = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("book", book);

        frag1.setArguments(bundle);
        return frag1;
    }
}

