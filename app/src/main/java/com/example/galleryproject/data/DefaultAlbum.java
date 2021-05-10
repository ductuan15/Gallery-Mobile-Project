package com.example.galleryproject.data;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.room.Entity;

import java.io.File;
import java.util.ArrayList;

@Entity
public class DefaultAlbum implements Parcelable{
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    String albumPath;
    String albumName;
    public static String DEFAULT_RELATIVE_ALBUM_PATH = Environment.DIRECTORY_DCIM + File.separator;
    public static String DEFAULT_ABSOLUTE_ALBUM_PATH = Environment.getExternalStorageDirectory() + File.separator  + Environment.DIRECTORY_DCIM + File.separator ;
    public static String EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory() + File.separator;

    protected DefaultAlbum(Parcel in) {
        mediaArrayList = in.createTypedArrayList(Media.CREATOR);
        albumPath = in.readString();
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
        dest.writeString(this.albumPath);
        dest.writeString(this.albumName);
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

    public DefaultAlbum(String albumPath, String albumName) {
        this.albumPath = albumPath;
        this.albumName = albumName;
    }


    public ArrayList<Media> getMediaArrayList() {
        return mediaArrayList;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumPath() {return albumPath;}

    public Uri getUriThumbnail() {
        if(this.mediaArrayList.size() < 1){
            return null;
        }
        return this.mediaArrayList.get(0).getUri();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
