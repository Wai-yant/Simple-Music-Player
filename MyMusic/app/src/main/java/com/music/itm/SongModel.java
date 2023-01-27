package com.music.itm;

import java.io.Serializable;

public class SongModel implements Serializable {
    private String title;
	private String path;
	private String duration;
	
	public SongModel(String title, String path, String duration) {
		this.title = title;
		this.path = path;
		this.duration = duration;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
	}
    

}
