package edu.temple.lab7;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class AudiobookService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private final MediaControlBinder binder = new MediaControlBinder();
    private static final String TAG = "Audiobook Service";
    MediaPlayer mediaPlayer = new MediaPlayer();
    Notification notification;
    Handler progressHandler;
    Thread progressThread;
    int playingState; // 0 - stopped, 1 - playing, 2 - paused
    int startPosition;

    private final String NOTIFICATION_CHANNEL_ID = "media_player_control";

    public AudiobookService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();

        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).build());
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        String NOTIFICATION_PLAYING_TITLE = "Title";
        String NOTIFICATION_PLAYING_DESCRIPTION = "Description";

        notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(NOTIFICATION_PLAYING_TITLE)
                .setContentText(NOTIFICATION_PLAYING_DESCRIPTION)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();


    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "Player bound");
        //receive data here


     /*  int id= intent.getIntExtra("ID",1);

        binder.play(id);*/
        return binder;
    }

    private void setHandler (Handler handler) {}

    private void play(int id) {
        try {
            mediaPlayer.reset();
            String BOOK_DOWNLOAD_URL = "https://kamorris.com/lab/audlib/download.php?id=";
            mediaPlayer.setDataSource(BOOK_DOWNLOAD_URL + id);
            playingState = 0;
            mediaPlayer.prepareAsync();
            Log.i(TAG, "Audiobook preparing");
            int FOREGROUND_CODE = 1;
            startForeground(FOREGROUND_CODE, notification);
            Log.i(TAG, "Foreground notification started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play(int id, int position) {
        startPosition = position;
        play(id);
    }

    private void play(File file) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource((new FileInputStream(file)).getFD());
            playingState = 0;
            mediaPlayer.prepareAsync();
            Log.i(TAG, "Audiobook preparing");
            int FOREGROUND_CODE = 1;
            startForeground(FOREGROUND_CODE, notification);
            Log.i(TAG, "Foreground notification started");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void play(File file, int position) {
        startPosition = position;
        play(file);
    }

    private void pause () {
        if (playingState == 1) {
            playingState = 2;
            mediaPlayer.pause();
            if (progressThread != null) {
                progressThread.interrupt();
                progressThread = null;
            }
            Log.i(TAG, "Player paused");
        } else if (playingState == 2) {
            playingState = 1;
            mediaPlayer.start();
            progressThread = new Thread(new NotifyProgress());
            progressThread.start();
            Log.i(TAG, "Player started");
        }
    }

    private void stop() {
        mediaPlayer.stop();
        playingState = 0;
        stopForeground(true);
        if (progressThread != null) {
            progressThread.interrupt();
            progressThread = null;
        }
        Log.i(TAG, "Player stopped");
    }

    private void seekTo(int position) {
        position = position * 1000;
        if (position <= mediaPlayer.getDuration()) {
            mediaPlayer.seekTo((position));
            Log.i(TAG, "Audiobook position changed");
        }
    }

    public class MediaControlBinder extends Binder {
        public void play(int id) {
            AudiobookService.this.play(id);
        }

        public void play(int id, int startPosition) {
            AudiobookService.this.play(id, startPosition);
        }

        public void play(File file) {
            AudiobookService.this.play(file);
        }

        public void play(File file, int startPosition) {
            AudiobookService.this.play(file, startPosition);
        }

        public void pause() {
            AudiobookService.this.pause();
        }

        public void stop() {
            AudiobookService.this.stop();
        }

        public void setProgressHandler(Handler handler) {
            AudiobookService.this.setHandler(handler);
        }

        public void seekTo(int position) {
            AudiobookService.this.seekTo(position);
        }

        public AudiobookService getService() {
            return AudiobookService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {}

    @Override
    public void onDestroy() {}

    private void createNotificationChannel(){}

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {}

    @Override
    public void onCompletion(MediaPlayer mp) {}

    class NotifyProgress implements Runnable {

        @Override
        public void run() {
        }


    }




}
