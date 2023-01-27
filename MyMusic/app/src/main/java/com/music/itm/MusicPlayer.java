package com.music.itm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import java.io.IOException;

public class MusicPlayer extends Service implements MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener {

	@Override
	public void onAudioFocusChange(int p1) {
		if(focusIntent==null){
			focusIntent = new Intent(this,MusicPlayer.class);
		}
		
		switch(p1){
			case AudioManager.AUDIOFOCUS_GAIN:
				focusIntent.setAction(ACTION_PLAYPAUSE);
				startService(focusIntent);
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				focusIntent.setAction(ACTION_PLAYPAUSE);
				startService(focusIntent);
				break;
			case AudioManager.AUDIOFOCUS_LOSS:
				stopSelf();
				break;
				
		}
		
	}

	
	public static MediaPlayer instance;
	
	@Override
	public void onCompletion(MediaPlayer p1) {
		next();
	}
 
	public static String ACTION_NEXT = "action_next";
	public static String ACTION_PREVIOUS = "action_previous";
	public static String ACTION_PLAYPAUSE = "action_playpause";
	public static String ACTION_START = "action_start";
	public static String ACTION_STOP = "action_stop";
	
	AudioFocusRequest.Builder audioForcus;
	AudioManager audioManager;
	Intent focusIntent;

	@Override
	public IBinder onBind(Intent p1) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		if(instance==null){
			instance = new MediaPlayer();
		}
		
		
		AudioAttributes attrs = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_MEDIA).build();
		
		audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
		
	    audioForcus = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN);
		audioForcus.setAudioAttributes(attrs);
		audioForcus.setAcceptsDelayedFocusGain(true);
		audioForcus.setOnAudioFocusChangeListener(this);
		
		
		audioManager.requestAudioFocus(audioForcus.build());
		/*
		

		 TelephonyManager telephonyManager = (TelephonyManager) getSystemService(getBaseContext().TELEPHONY_SERVICE);
		 PhoneStateListener callStateListener = new PhoneStateListener(){
		 public void onCallStateChanged(int state,String number){
		 if(state==TelephonyManager.CALL_STATE_RINGING){
		 instance.pause();
		 }
		 if(state==TelephonyManager.CALL_STATE_OFFHOOK){
		 instance.pause();
		 }
		 if(state==TelephonyManager.CALL_STATE_IDLE){
		 instance.start();
		 }
		 }
		 };
		 telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
		 
		*/
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		
		if(intent!=null){
			String action = intent.getAction();

			if(action.equals(ACTION_NEXT)){
				next();

			}else if(action.equals(ACTION_PREVIOUS)){
				previous();

			}else if(action.equals(ACTION_PLAYPAUSE)){
				if(SingleSongList.currentIndex!=-1){
					if(instance.isPlaying()){
						instance.pause();
						showNotification();

					}else{
						instance.start();
						showNotification();

					}
				}else{
					SingleSongList.currentIndex = 0;
					preparePlayer();
				}
			}else if(action.equals(ACTION_START)){
				preparePlayer();

			}else if(action.equals(ACTION_STOP)){
				stopSelf();
			}
		}
		
		
		return START_STICKY;
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	
		if(instance!=null){
			if(instance.isPlaying()){
				instance.stop();
			}
			instance.release();
			instance = null;
		}
	}
	
	private void preparePlayer(){
		try {
			instance.reset();
			instance.setDataSource(SingleSongList.songList.get(SingleSongList.currentIndex).getPath());
			instance.prepare();
			instance.setOnCompletionListener(this);
		    instance.start();
		} catch (IOException e){
			e.printStackTrace();
		}
		showNotification();
	}
	private void next(){
		if(SingleSongList.currentIndex==SingleSongList.songList.size()-1){
			SingleSongList.currentIndex = 0;
		}else{
			SingleSongList.currentIndex += 1;
		}
		preparePlayer();
		showNotification();
		
	}
	private void previous(){
		if(SingleSongList.currentIndex==0){
			SingleSongList.currentIndex = SingleSongList.songList.size()-1;
		}else{
			SingleSongList.currentIndex -= 1;
		}
		preparePlayer();
		showNotification();
	}
	private void showNotification(){

		String CHANNEL_ID = "channel-01";

		PendingIntent pi = PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);

		Intent prevIntent = new Intent(this,MusicPlayer.class);
		prevIntent.setAction(ACTION_PREVIOUS);
		PendingIntent pprevIntent = PendingIntent.getService(this,0,prevIntent,0);
		
		Intent playPauseIntent = new Intent(this,MusicPlayer.class);
		playPauseIntent.setAction(ACTION_PLAYPAUSE);
		PendingIntent pplayPauseIntent = PendingIntent.getService(this,0,playPauseIntent,0);
		
		Intent nextIntent = new Intent(this,MusicPlayer.class);
		nextIntent.setAction(ACTION_NEXT);
		PendingIntent pnextIntent = PendingIntent.getService(this,0,nextIntent,0);
		
		Intent closeIntent = new Intent(this,MusicPlayer.class);
		closeIntent.setAction(ACTION_STOP);
		PendingIntent pcloseIntent = PendingIntent.getService(this,0,closeIntent,0);
		
		Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.default_audio_art);
			
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
		builder.setSmallIcon(R.drawable.icon_notification);
		builder.setContentTitle(SingleSongList.songList.get(SingleSongList.currentIndex).getTitle());
		builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		builder.setContentIntent(pi);
		builder.setOngoing(true);
		builder.setLargeIcon(Bitmap.createScaledBitmap(icon,128,128,false));
		builder.addAction(R.drawable.ic_skip_previous,"Previous",pprevIntent);
		builder.setOnlyAlertOnce(true);
		
		if(instance.isPlaying()){
		    builder.addAction(R.drawable.ic_pause,"Play",pplayPauseIntent);
		}else{
			builder.addAction(R.drawable.ic_play,"Pause",pplayPauseIntent);
		}
		
		builder.addAction(R.drawable.ic_skip_next,"Next",pnextIntent);
		builder.addAction(R.drawable.ic_close,"Close",pcloseIntent);
		builder.setStyle(new MediaStyle());
		
		
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"My Music Player",importance);
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}

//		NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//		notificationManagerCompat.notify(1,builder.build());
		
		startForeground(1, builder.build());
		
	}
	
}
