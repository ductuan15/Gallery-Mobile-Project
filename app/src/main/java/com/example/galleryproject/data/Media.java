package com.example.galleryproject.data;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;


public class Media {
    final Uri mUri;
    final String mSize;
    final String mDate;

    public Media(Uri mUri, String mSize, String mDate) {
        this.mUri = mUri;
        this.mSize = mSize;
        this.mDate = mDate;
    }

    private static int findAlbumPos(ArrayList<Album> albumArrayList, String newAlbumName){
        int len = albumArrayList.size();
        for(int i = 0;i< len;i++){
            try{
                if(albumArrayList.get(i).getAlbumName().compareTo(newAlbumName) == 0){
                    return i;
                }
            }catch (Exception e){
                Log.e("", "findAlbumPos: ");
            }

        }
        return -1;
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void getAllMedia(Context context,ArrayList<Uri> uriArrayList, ArrayList<Album> albumArrayList) {
        // get all pic and vid
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        int column_index_data;
        @SuppressLint("Recycle") Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(column_index_data);
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            String buckerName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME));
            Uri contentUri = null;


            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                // get duration of video
                //String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                // get thumbnail video
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                uriArrayList.add(contentUri);

            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                // get thumbnail img

                contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                uriArrayList.add(contentUri);
            }

            // check if bucket name is existed
            if(buckerName == null){
                buckerName = "0";
            }
            int pos = findAlbumPos(albumArrayList,buckerName);
            if(pos == -1) {
                albumArrayList.add(new Album(buckerName, false));
                pos = albumArrayList.size() - 1;
            }
            albumArrayList.get(pos).addMedia(contentUri);

        }
    }
}
