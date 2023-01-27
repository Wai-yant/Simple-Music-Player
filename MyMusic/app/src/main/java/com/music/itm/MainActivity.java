package com.music.itm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity{


	TextView title;
	ImageView playPause;
	MainViewModel mainViewModel;
	SeekBar progressBar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		
		RecyclerView rv = findViewById(R.id.main_rv);
		ImageView prev = findViewById(R.id.main_previous);
		playPause = findViewById(R.id.main_playPause);
		ImageView next = findViewById(R.id.main_next);
	    title = findViewById(R.id.main_title);
		progressBar = findViewById(R.id.main_progress);
		
		
		if(mainViewModel==null){
			mainViewModel = new MainViewModel(this);
		}
		
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				if(MusicPlayer.instance!=null){
					if(MusicPlayer.instance.isPlaying()){
						playPause.setImageResource(R.drawable.ic_pause);
						
						progressBar.setProgress(MusicPlayer.instance.getCurrentPosition());
						progressBar.setMax(MusicPlayer.instance.getDuration());
						
					}else{
						playPause.setImageResource(R.drawable.ic_play);
					}
					
				}else{
					playPause.setImageResource(R.drawable.ic_play);
				}
				if(SingleSongList.songList.size()!=0){
					if(SingleSongList.currentIndex!=-1){
						if(!SingleSongList.songList.get(SingleSongList.currentIndex).getTitle().equals(title.getText().toString())){
							title.setText(SingleSongList.songList.get(SingleSongList.currentIndex).getTitle());
						}
					}else{
						if(!SingleSongList.songList.get(0).getTitle().equals(title.getText().toString())){
							title.setText(SingleSongList.songList.get(0).getTitle());
						}
					}
				}
				
				new Handler().postDelayed(this,100);
			}
		});
		
		
		final Intent i = new Intent(this,MusicPlayer.class);
		
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
        
	
		rv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
		rv.setAdapter(new MainAdapter(SingleSongList.songList));
		
		title.setSelected(true);
		
		if(MusicPlayer.instance==null){
			playPause.setImageResource(R.drawable.ic_play);
		}else if(MusicPlayer.instance.isPlaying()){
			playPause.setImageResource(R.drawable.ic_pause);
		}else{
			playPause.setImageResource(R.drawable.ic_play);
		}
		
		prev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				i.setAction(MusicPlayer.ACTION_PREVIOUS);
				startService(i);
			}
		});
			
		playPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				i.setAction(MusicPlayer.ACTION_PLAYPAUSE);
				startService(i);
			}
		});
			
		next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				i.setAction(MusicPlayer.ACTION_NEXT);
				startService(i);
			}
		});
		
		progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar p1, int p2, boolean p3) {
				if(MusicPlayer.instance!=null){
					if(p3){
						if(MusicPlayer.instance.isPlaying()){
							MusicPlayer.instance.seekTo(p2);
						}
					}
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar p1) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar p1) {
			}	
		});
		
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
    
}
