package com.music.itm;

import android.content.Context;
import android.widget.Toast;

public class MyToast {
    
    private Context context;
	private String message;

	public MyToast(Context context, String message) {
		this.context = context;
		this.message = message;
		
		Toast.makeText(this.context,this.message,Toast.LENGTH_SHORT).show();
	}
    
}
