package com.example.galleryproject.data;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.room.Entity;

import java.util.ArrayList;

@Entity
public class Album implements Parcelable{
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    String albumName;
    boolean isLock;

    protected Album(Parcel in) {
        mediaArrayList = in.createTypedArrayList(Media.CREATOR);
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


    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mediaArrayList);
        dest.writeString(this.albumName);
        dest.writeBoolean(this.isLock);
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setLock(boolean lock) {
        this.isLock = lock;
    }

    public void addMedia(Media media) {
        try {
            this.mediaArrayList.add(media);
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    public void addImageInfo(ImageInfo media) {
        try {
            this.mediaArrayList.add(media);
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    public void addVideoInfo(VideoInfo media) {
        try {
            this.mediaArrayList.add(media);
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    public Album(String albumName, boolean isLock) {
        this.albumName = albumName;
        this.isLock = isLock;
    }

    public ArrayList<Media> getMediaArrayList() {
        return mediaArrayList;
    }

    public String getAlbumName() {
        return albumName;
    }

    public boolean isLock() {
        return isLock;
    }

    public Uri getUriThumbnail() {
        return this.mediaArrayList.get(0).getUri();
    }

}
