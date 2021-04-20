package com.example.galleryproject.data;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Album implements Parcelable{
    ArrayList<Uri> mediaUriArrayList = new ArrayList<>();
    String albumName;
    boolean isLock;

    protected Album(Parcel in) {
        mediaUriArrayList = in.createTypedArrayList(Uri.CREATOR);
        albumName = in.readString();
        isLock = in.readByte() != 0;
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mediaUriArrayList);
        dest.writeString(this.albumName);
        dest.writeBoolean(this.isLock);
    }
}
