package com.example.vi.gomokuplus;

import android.media.MediaPlayer;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import group7.gomoku.R;


/*
* The service for playing sound
* @author Lai Xu(Tony)
* @date 01/22/2015
* */
public class ServiceAudioPlay extends Service {
	MediaPlayer audioPlayer;

	public void onCreate(){
	    super.onCreate();
	    audioPlayer = MediaPlayer.create(this, R.raw.blobbitfinal);
        //audioPlayer.setLooping(true);

	}

    @Override
    public IBinder onBind(Intent objIndent) {
        return null;
    }

    //play sound when service starts
	public int onStartCommand(Intent intent, int flags, int startId){
        audioPlayer.start();
        return 1;
	}

    //stop sound when service stops
	public void onDestroy(){
        audioPlayer.stop();
        audioPlayer.release();
        super.onDestroy();
	}
}
