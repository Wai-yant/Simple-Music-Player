package com.music.itm;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import android.net.Uri;
import android.content.ContentUris;

public class MainViewModel {
    
    private Context context;

	public MainViewModel(Context context) {
		this.context = context;
		
		String[] projection = {
			MediaStore.Audio.Media.TITLE,
			MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.DURATION,
			
		};
		String selection = MediaStore.Audio.Media.IS_MUSIC+"!=0";
		Cursor cursor = context.getContentResolver().query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			projection,
			selection,
			null,
			null
		);
		while(cursor.moveToNext()){
			
			SongModel songData = new SongModel(
			    cursor.getString(0),
			    cursor.getString(1),
			    cursor.getString(2)
			);

			if(new File(songData.getPath()).exists()){
				SingleSongList.songList.add(songData);
			}


		}
		
	}
	
    
}
