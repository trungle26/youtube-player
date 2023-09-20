package com.trungcoder.youtubeforcar;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

public class VideoService extends Service {
    public VideoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            String action = intent.getAction();
            if(Actions.START.toString().equals(action)){
                start();
            }else if(Actions.PAUSE.toString().equals(action)){
                MainActivity.youTubePlayer.pause();
            }else if(Actions.NEXT.toString().equals(action)){
                MainActivity.youTubePlayer.nextVideo();
            }else if(Actions.PREV.toString().equals(action)){
                MainActivity.youTubePlayer.previousVideo();
            }else if(Actions.STOP.toString().equals(action)){
                stopSelf();
                sendBroadcast(new Intent("STOP"));
            }else if(Actions.PLAY.toString().equals(action)){
                MainActivity.youTubePlayer.play();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public enum Actions{
        START,PAUSE,NEXT,PREV,STOP,PLAY
    }

    private void start(){
        MainActivity.youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                MainActivity.youTubePlayer = youTubePlayer;
                super.onReady(youTubePlayer);
            }
        }, MainActivity.iFramePlayerOptions);
        MainActivity.youTubePlayerView.enableBackgroundPlayback(true);

        // Create an Intent for each action
        Intent prevIntent = new Intent(this, VideoService.class);
        prevIntent.setAction(Actions.PREV.toString());

        Intent pauseIntent = new Intent(this, VideoService.class);
        pauseIntent.setAction(Actions.PAUSE.toString());

        Intent nextIntent = new Intent(this, VideoService.class);
        nextIntent.setAction(Actions.NEXT.toString());

        Intent playIntent = new Intent(this, VideoService.class);
        playIntent.setAction(Actions.PLAY.toString());
        Intent exitIntent = new Intent(this, VideoService.class);
        exitIntent.setAction(Actions.STOP.toString());

        // Create PendingIntent objects
        PendingIntent prevPendingIntent = PendingIntent.getService(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent exitPendingIntent = PendingIntent.getService(this, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this,"running_channel")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(R.drawable.ic_prev,"Previous",prevPendingIntent)
                .addAction(R.drawable.ic_pause,"Pause",pausePendingIntent)
                .addAction(R.drawable.ic_next,"Next",nextPendingIntent)
                .addAction(R.drawable.ic_play,"Play",playPendingIntent)
                .addAction(R.drawable.ic_exit,"Exit",exitPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1))
                .setContentTitle("Background Youtube")
                .build();
        startForeground(1,notification);
    }
}