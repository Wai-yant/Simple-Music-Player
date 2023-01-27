package com.music.itm;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.IOException;
import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
	private ArrayList<SongModel> songList;
	public MainAdapter(ArrayList<SongModel> songList){
		this.songList = songList;
	}

	@Override
	public MainAdapter.MainViewHolder onCreateViewHolder(ViewGroup p1,int p2) {
		View itemView = LayoutInflater.from(p1.getContext()).inflate(R.layout.item_main,p1,false);
		final MainAdapter.MainViewHolder holder = new MainAdapter.MainViewHolder(itemView);
		
		itemView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View p1) {
				
				SingleSongList.currentIndex = holder.getAdapterPosition();
				Intent i = new Intent(p1.getContext(),MusicPlayer.class);
				i.setAction(MusicPlayer.ACTION_START);
				p1.getContext().startService(i);
				
			}
				
				
		});
		
		return holder;
	}

	@Override
	public void onBindViewHolder(MainAdapter.MainViewHolder p1, int p2) {
		p1.tv.setText(songList.get(p2).getTitle());
	}

	@Override
	public int getItemCount() {
		return songList.size();
	}

    
	public class MainViewHolder extends RecyclerView.ViewHolder{
		TextView tv;
		
		public MainViewHolder(View itemView){
			super(itemView);
			tv = itemView.findViewById(R.id.item_main_tv);
		}
	} 
    
}
