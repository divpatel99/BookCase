package edu.temple.lab7;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


public class customAdapter extends PagerAdapter {

    ArrayList<Book> listing;
    ImageView imageView;
    TextView bookName,author_text,text_year;
    ViewGroup viewframe;
    Context context;

    Button play_btn,pasuse_btn,stop_btn;
    SeekBar seekBar;

    private Runnable mRunnable;

    public interface Callback_Adapter {
        public void onClickButton(int number,int id);
    }
    private  Callback_Adapter callback;

    customAdapter(ArrayList<Book> listing,Context context, Callback_Adapter callback)
    {

        this.listing = listing;
        this.context = context;
        this.callback = callback;

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
    public Object instantiateItem(ViewGroup container,final int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        viewframe = (ViewGroup) inflater.inflate(R.layout.bookdetail_frag, container,
                false);
        author_text = viewframe.findViewById(R.id.text_author);
        text_year = viewframe.findViewById(R.id.text_yearpublished);
        bookName = viewframe.findViewById(R.id.bookName);
        imageView =viewframe.findViewById(R.id.imageview);
        seekBar=viewframe.findViewById(R.id.seekbar_);

        play_btn =viewframe.findViewById(R.id.play_button);
        pasuse_btn =viewframe.findViewById(R.id.pause_button);
        stop_btn =viewframe.findViewById(R.id.stop_button);


        seekBar.setMax(listing.get(position).getDuration());

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onClickButton(1,listing.get(position).getId());
            }
        });
        pasuse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onClickButton(2,listing.get(position).getId());
            }
        });
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onClickButton(3,listing.get(position).getId());
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                callback.onClickButton(4,progress);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
