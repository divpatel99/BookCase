package edu.temple.lab7;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class DirectoryHelper extends ContextWrapper {

    private static final String TAG = "DirectoryHelper";
    public static final String ROOT_DIRECTORY_NAME = "DownloadManager";

    private DirectoryHelper(Context context) {
        super(context);
        createFolderDirectories();
    }

    public static void createDirectory(Context context) {
        new DirectoryHelper(context);
    }

    private boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    private void createFolderDirectories() {
        if (isExternalStorageAvailable())
            createDirectory(ROOT_DIRECTORY_NAME);
    }

    private void createDirectory(String directoryName) {
        if (!isDirectoryExists(directoryName)) {
            File file = new File(Environment.getExternalStorageDirectory(), directoryName);
            file.mkdir();
            Log.d(TAG, "createDirectory: ");
        }
    }

    private boolean isDirectoryExists(String directoryName) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + directoryName);
        return file.isDirectory() && file.exists();
    }
}