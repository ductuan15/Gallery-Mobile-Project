package com.example.galleryproject.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class ImageInfo {
    private final Uri mUri;
    private final String mDateAdded;
    private final String mResolution;
    private final String mSize;

    public ImageInfo(Uri mUri, String mDateAdded, String mResolution, String mSize) {
        this.mUri = mUri;
        this.mDateAdded = mDateAdded;
        this.mResolution = mResolution;
        this.mSize = mSize;
    }

    public String getSize() {
        return mSize;
    }

    public String getResolution() {
        return mResolution;
    }

    public String getDateAdded() {
        return mDateAdded;
    }

    public Uri getUri() {
        return mUri;
    }

    public static ImageInfo getImageInfo(Uri uri, Context context) {
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.RESOLUTION,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Video.Media.DURATION,
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null, // Selection args (none).
                null
        );
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
        Uri contentUri = ContentUris.withAppendedId(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
        String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RESOLUTION));
        String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));

        return new ImageInfo(contentUri,date,resolution,size);
    }

    //TODO: fix this
    public static ArrayList<Uri> getAllPic(Context context) {
        // get all pic and vid
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Video.Media.DURATION
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        int column_index_data;
        Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );


        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        ArrayList<Uri> uriArrayList = new ArrayList<Uri>();

        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(column_index_data);
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));

            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                // get duration of video
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                System.out.println(duration);

                // get thumbnail video
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                uriArrayList.add(contentUri);

            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                // get thumbnail img

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                uriArrayList.add(contentUri);
            }
        }
        return uriArrayList;

    }

}
