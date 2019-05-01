package edu.temple.lab7;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

import java.io.File;
import java.util.ArrayList;

import edu.temple.lab7.dbHelper.DatabaseHelper;
import edu.temple.lab7.listner.checkPermission;


public class customAdapter extends PagerAdapter  {

    ArrayList<Book> listing;
    ImageView imageView;
    TextView bookName,author_text,text_year;
    ViewGroup viewframe;
    Context context;
    private DatabaseHelper db;
    Button play_btn,pasuse_btn,stop_btn,download_btn,deleted_btn;
    SeekBar seekBar;

    private Runnable mRunnable;

    public interface Callback_Adapter {
        public void onClickButton(int number,int id,String file);
    }
    private  Callback_Adapter callback;
    private Prefes prefes;
    private edu.temple.lab7.listner.checkPermission checkPermissions;

    customAdapter(ArrayList<Book> listing, Context context, Callback_Adapter callback,checkPermission checkPermissions)
    {

        this.listing = listing;
        this.context = context;
        this.callback =  callback;
        this.checkPermissions=checkPermissions;
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
    public Object instantiateItem(ViewGroup container, final int position)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        db = new DatabaseHelper(context);
        prefes=new Prefes(context,Utils.SAVE_MEDIA_PLAYER_PREF);
        viewframe = (ViewGroup) inflater.inflate(R.layout.bookdetail_frag, container, false);
        author_text = viewframe.findViewById(R.id.text_author);
        text_year = viewframe.findViewById(R.id.text_yearpublished);
        bookName = viewframe.findViewById(R.id.bookName);
        imageView =viewframe.findViewById(R.id.imageview);
        seekBar=viewframe.findViewById(R.id.seekbar_);

        play_btn =viewframe.findViewById(R.id.play_button);
        pasuse_btn =viewframe.findViewById(R.id.pause_button);
        stop_btn =viewframe.findViewById(R.id.stop_button);
        download_btn=viewframe.findViewById(R.id.download_btn);
        deleted_btn=viewframe.findViewById(R.id.deleted_btn);
        download_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions.checkRW(true);
                }else{
                    File file = new File(Utils.Directory_uri + "/"+listing.get(position).getId());
                    Utils.insertBDBook.clear();
                    Utils.insertBDBook.add(listing.get(position));
                    if(!(file.isDirectory() && file.exists())) {
                        context.startService(DownloadSongService.getDownloadService(context.getApplicationContext(), listing.get(position).getCoverURL(), DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/" + listing.get(position).getId() + "/"),"No"));
                        context.startService(DownloadSongService.getDownloadService(context.getApplicationContext(), Utils.BOOK_DOWNLOAD_URL + listing.get(position).getId(), DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/" + listing.get(position).getId() + "/"),"Yes"));
                        //   db.insertNote(listing.get(position).getId(),listing.get(position).getAuthor(),String.valueOf(listing.get(position).getDuration()),String.valueOf(listing.get(position).getPublished()), DirectoryHelper.ROOT_DIRECTORY_NAME.concat("/" + listing.get(position).getId() + "/"),listing.get(position).getTitle());
                        Toast.makeText(context,"Downloading...!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"Already Downloaded!",Toast.LENGTH_SHORT).show();
                    }

                }}
        });

        deleted_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    checkPermissions.checkRW(true);
                }else{
                    File file = new File(Utils.Directory_uri + "/"+listing.get(position).getId());
                    if(file.isDirectory() && file.exists()) {
                        Utils.deleteDirectory(new File(Utils.Directory_uri + "/"+listing.get(position).getId()));
                        db.deleteNote(listing.get(position).getId());
                        Utils.arraylistBookDB.clear();
                        Utils.arraylistBookDB.addAll(db.getAllNotes());
                        Toast.makeText(context,"Successfully Deleted!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"Already Deleted!",Toast.LENGTH_SHORT).show();
                    }
                }}
        });
        seekBar.setMax(listing.get(position).getDuration());

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Book  book=checkDBList(listing.get(position).getId());
                prefes.setValue(Utils.SAVE_MEDIA_TN,listing.get(position).getTitle());
                prefes.setValue(Utils.SAVE_MEDIA_ND,listing.get(position).getAuthor());
                if(book==null){
                    callback.onClickButton(1,listing.get(position).getId(),"");
                }else{
                    String currentString =book.getCoverURL();
                    String[] separated = currentString.split(":");
                    String  musicPathDirectory= Environment.getExternalStorageDirectory()+"/"+separated[0]+ separated[2];
                    callback.onClickButton(5,book.getId(),musicPathDirectory);
                }

            }
        });
        pasuse_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onClickButton(2,listing.get(position).getId(),"");
            }
        });
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onClickButton(3,listing.get(position).getId(),"");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                callback.onClickButton(4,progress,"");



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Book book=checkDBList(listing.get(position).getId());
        //check list of database if it contain the book or not
        if(book==null) {
            bookName.setText(listing.get(position).getTitle());
            bookName.setTextColor(0xFF00FF00);

            author_text.setText(listing.get(position).getAuthor());
            text_year.setText(listing.get(position).getPublished() + "");
            Glide.with(context).load(listing.get(position).getCoverURL()).into(imageView);
        }else{
            bookName.setText(book.getTitle());
            bookName.setTextColor(0xFF00FF00);
            author_text.setText(book.getAuthor());
            text_year.setText(book.getPublished() + "");
            String currentString =book.getCoverURL();
            String[] separated = currentString.split(":");
            String imagePathDirectory= Environment.getExternalStorageDirectory()+"/"+separated[0]+separated[1];
            File imgFile = new  File(imagePathDirectory);

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                imageView.setImageBitmap(myBitmap);

            }
        }
        container.addView(viewframe);
        return viewframe;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private Book checkDBList(int bookid) {
        for(int i=0;i<Utils.arraylistBookDB.size();i++){
            if(Utils.arraylistBookDB.get(i).getId()==bookid){
                return  Utils.arraylistBookDB.get(i);
            }
        }
        return null;
    }




}
