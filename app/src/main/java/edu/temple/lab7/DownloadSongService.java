package edu.temple.lab7;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import edu.temple.lab7.dbHelper.DatabaseHelper;

public class DownloadSongService extends IntentService {
    private static final String DOWNLOAD_PATH = "edu.temple.lab7.androiddownloadmanager_DownloadSongService_Download_path";
    private static final String DESTINATION_PATH = "edu.temple.lab7.androiddownloadmanager_DownloadSongService_Destination_path";
    private static final String SAVE_DB= "SAVE_DB";
    private static DatabaseHelper db;
    private String fileImageName;
    public DownloadSongService() {
        super("DownloadSongService");
    }
    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath,String saveDB) {
        db = new DatabaseHelper(callingClassContext);
        return new Intent(callingClassContext, DownloadSongService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath)
                .putExtra(SAVE_DB,saveDB);

    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        String checkSAVEDB= intent.getStringExtra(SAVE_DB);
        startDownload(downloadPath, destinationPath,checkSAVEDB);
    }
   
}
