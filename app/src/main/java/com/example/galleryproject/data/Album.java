package com.example.galleryproject.data;

import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class Album {
    ArrayList<Uri> mediaUriArrayList = new ArrayList<>();
    String albumName;
    boolean isLock;

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setLock(boolean lock) {
        this.isLock = lock;
    }

    public boolean addMedia(Uri uri){
        try{
            this.mediaUriArrayList.add(uri);
        }catch (Exception e){
            Log.e("", e.toString());
            return false;
        }
        return true;
    }


    public Album( String albumName, boolean isLock) {
        this.albumName = albumName;
        this.isLock = isLock;
    }

    public ArrayList<Uri> getMediaUriArrayList() {
        return mediaUriArrayList;
    }

    public String getAlbumName() {
        return albumName;
    }

    public boolean isLock() {
        return isLock;
    }
    public Uri getUriThumbnail(){
        return this.mediaUriArrayList.get(0);
    }
}
