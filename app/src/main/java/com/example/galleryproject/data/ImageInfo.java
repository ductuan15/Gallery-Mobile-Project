package com.example.galleryproject.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class ImageInfo  extends Media implements Parcelable {
    public ImageInfo(Uri mUri, String size, String date, String resolution, int media_type, String fileName, String location, int orientation, boolean isFavorite, boolean isTrash) {
        super(mUri, size, date, resolution, media_type, fileName, location, orientation, isFavorite, isTrash);
    }

    protected ImageInfo(Parcel in) {
        super(in);
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel in) {
            return new ImageInfo(in);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @NotNull
    @Override
    public String toString() {
        return this.uri.toString() +
                "|" +
                this.size +
                "|" +
                this.date +
                "|" +
                this.resolution +
                "|" +
                this.MEDIA_TYPE +
                "|" +
                this.fileName +
                "|" +
                this.location +
                "|" +
                this.orientation +
                "|" +
                this.isFavorite +
                "|" +
                this.isTrash +
                "|" +
                this.albumIn;
    }

    public static ImageInfo parseString(String[] datas) {
        Uri uri = Uri.parse(datas[0]);
        String size = datas[1];
        String date = datas[2];
        String resolution = datas[3];
        int mediaType = Integer.parseInt(datas[4]);
        String fileName = datas[5];
        String location = datas[6];
        int orientation = Integer.parseInt(datas[7]);
        boolean isFavorite = Boolean.parseBoolean(datas[8]);
        boolean isTrash = Boolean.parseBoolean(datas[9]);
        int inAlbum = Integer.parseInt(datas[10]);

        return new ImageInfo(uri, size, date, resolution, mediaType, fileName, location, orientation, isFavorite, isTrash);
    }
}