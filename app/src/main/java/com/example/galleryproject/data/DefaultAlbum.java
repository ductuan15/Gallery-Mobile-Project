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
public class DefaultAlbum implements Parcelable{
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    String albumName;

    protected DefaultAlbum(Parcel in) {
        mediaArrayList = in.createTypedArrayList(Media.CREATOR);
        albumName = in.readString();
    }

    public static final Creator<DefaultAlbum> CREATOR = new Creator<DefaultAlbum>() {
        @Override
        public DefaultAlbum createFromParcel(Parcel in) {
            return new DefaultAlbum(in);
        }

        @Override
        public DefaultAlbum[] newArray(int size) {
            return new DefaultAlbum[size];
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
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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

    public DefaultAlbum(String albumName) {
        this.albumName = albumName;
    }

    public ArrayList<Media> getMediaArrayList() {
        return mediaArrayList;
    }

    public String getAlbumName() {
        return albumName;
    }


    public Uri getUriThumbnail() {
        return this.mediaArrayList.get(0).getUri();
    }

}
