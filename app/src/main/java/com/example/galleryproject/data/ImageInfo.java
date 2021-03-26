package com.example.galleryproject.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

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

    public String getmSize() {
        return mSize;
    }

    public String getmResolution() {
        return mResolution;
    }

    public String getmDateAdded() {
        return mDateAdded;
    }

    public Uri getmUri() {
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
}
