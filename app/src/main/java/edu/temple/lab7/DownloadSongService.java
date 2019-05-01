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

    private void startDownload(String downloadPath, String destinationPath,String saveDB) {
        Uri uri = Uri.parse(downloadPath); // Path where you want to download file.

        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle("Downloading a file"); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(destinationPath, uri.getLastPathSegment());  // Storage directory path
        if(saveDB.equals("No")) {
            fileImageName = uri.getLastPathSegment();
        }
        ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading
        if(saveDB.equals("Yes")){
            String saveImageMP3=destinationPath+":"+fileImageName+":"+uri.getLastPathSegment();
            db.insertNote(Utils.insertBDBook.get(0).getId(),Utils.insertBDBook.get(0).getAuthor(),String.valueOf(Utils.insertBDBook.get(0).getDuration()),String.valueOf(Utils.insertBDBook.get(0).getPublished()),saveImageMP3,Utils.insertBDBook.get(0).getTitle());
            Utils.arraylistBookDB.clear();
            Utils.arraylistBookDB.addAll(db.getAllNotes());
        }
    }

}
