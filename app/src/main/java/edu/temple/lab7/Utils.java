package edu.temple.lab7;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class Utils {
    public static String BOOK_DOWNLOAD_URL = "https://kamorris.com/lab/audlib/download.php?id=";
    public static String Directory_uri= Environment.getExternalStorageDirectory() + "/DownloadManager";
    public static ArrayList<Book> insertBDBook=new ArrayList<>();

    public static String SAVE_MEDIA_PLAYER_POSITION="CURRENT_POSITION_MP";
    public static String SAVE_MEDIA_PLAYING="PLAYING_MP";
    public static String SAVE_MEDIA_TN="TITLE";
    public static String BOOK_ID="BOOK_ID";
    public static String SAVE_MEDIA_ND="DESCRIPTION";
    public static String SAVE_MEDIA_PLAYER_PREF="MP_PREFS";
    public static String SEARCH_API="SEARCH";
    public static ArrayList<Book> arraylistBookDB=new ArrayList<>();
    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(int i=0; i<files.length; i++) {
                if(files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return( path.delete() );
    }
}
