package edu.temple.lab7;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class customAdapter extends PagerAdapter {

    ArrayList<Book> listing;
    ImageView imageView;
    TextView bookName,author_text,text_year;
    ViewGroup viewframe;
    Context context;

    customAdapter(ArrayList<Book> listing,Context context)
    {

        this.listing = listing;
        this.context = context;

    }

    @Override
    public int getCount() {
        return listing.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        viewframe = (ViewGroup) inflater.inflate(R.layout.bookdetail_frag, container,
                false);
        author_text = viewframe.findViewById(R.id.text_author);
        text_year = viewframe.findViewById(R.id.text_yearpublished);
        bookName = viewframe.findViewById(R.id.bookName);
        imageView =viewframe.findViewById(R.id.imageview);
        bookName.setText(listing.get(position).getTitle());
        bookName.setTextColor(0xFF00FF00);

        author_text.setText(listing.get(position).getAuthor());
        text_year.setText(listing.get(position).getPublished()+"");
        Glide.with(context).load(listing.get(position).getCoverURL()).into(imageView);

        container.addView(viewframe);
        return viewframe;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }



}
