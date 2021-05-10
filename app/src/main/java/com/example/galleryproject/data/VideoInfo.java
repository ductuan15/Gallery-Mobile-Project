package com.example.galleryproject.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class VideoInfo extends Media implements Parcelable {


    final int duration;

    public VideoInfo(Uri uri, String size, String date, String resolution, int media_type, String fileName, String location,int duration,int orientation,boolean isFavorite, boolean isTrash) {
        super(uri, size, date, resolution, media_type, fileName, location, orientation,isFavorite,isTrash);
        this.duration = duration;
    }

    public VideoInfo(Parcel in) {
        super(in);
        this.duration =  in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    public String getDuration() {
        int second = this.duration / 1000;
        int minute = second /60;
        second = second % 60;
        if(second  < 10)
            return minute + ":" + "0" + second;
        return minute + ":" + second;
    }

    protected VideoInfo(Parcel in, int duration) {
        super(in);
        this.duration = duration;
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
                this.duration +
                "|" +
                this.orientation +
                "|" +
                this.isFavorite +
                "|" +
                this.isTrash +
                "|" +
                this.albumIn;
    }

    public static VideoInfo parseString(String[] datas){
        Uri uri = Uri.parse(datas[0]);
        String size = datas[1];
        String date = datas[2];
        String resolution = datas[3];
        int mediaType = Integer.parseInt(datas[4]);
        String fileName = datas[5];
        String location = datas[6];
        int duration = Integer.parseInt(datas[8]);
        int orientation = Integer.parseInt(datas[8]);

        boolean isFavorite = Boolean.parseBoolean(datas[8]);
        boolean isTrash = Boolean.parseBoolean(datas[9]);
        int inAlbum = Integer.parseInt(datas[10]);

        return new VideoInfo(uri,size,date,resolution,mediaType,fileName,location,duration,orientation,isFavorite,isTrash);
    }


}
