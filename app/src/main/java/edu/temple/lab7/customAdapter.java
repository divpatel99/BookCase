package edu.temple.lab7;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;


public class customAdapter extends PagerAdapter {
    List<arrList> listing;
    TextView bookName;
    ViewGroup viewframe;
    Context context;


    customAdapter(List<arrList> listing,Context context) {

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
        bookName = viewframe.findViewById(R.id.bookName);
        bookName.setText(listing.get(position).toString());
        bookName.setTextColor(0xFF00FF00);

        container.addView(viewframe);
        return viewframe;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }



}
