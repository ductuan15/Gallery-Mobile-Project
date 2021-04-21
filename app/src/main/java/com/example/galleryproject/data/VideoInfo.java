package com.example.galleryproject.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class VideoInfo extends Media implements Parcelable {


    final int duration;

    public VideoInfo(Uri uri, String size, String date, String resolution, int media_type, String fileName, String location,int duration) {
        super(uri, size, date, resolution, media_type, fileName, location);
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


}
