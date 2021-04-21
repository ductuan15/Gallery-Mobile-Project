package com.example.galleryproject.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageInfo  extends Media implements Parcelable {
    public ImageInfo(Uri mUri, String size, String date, String resolution, int media_type,String fileName,String location) {
        super(mUri, size, date, resolution, media_type, fileName, location);
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

    //
//    public static ImageInfo getImageInfo(Uri uri, Context context) {
//        String[] projection = {
//                MediaStore.MediaColumns.DATA,
//                MediaStore.MediaColumns._ID,
//                MediaStore.Files.FileColumns.DATE_ADDED,
//                MediaStore.Files.FileColumns.RESOLUTION,
//                MediaStore.Files.FileColumns.SIZE,
//                MediaStore.Files.FileColumns.MEDIA_TYPE,
//                MediaStore.Video.Media.DURATION,
//        };
//        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Files.getContentUri("external"),
//                projection,
//                selection,
//                null, // Selection args (none).
//                null
//        );
//        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
//        Uri contentUri = ContentUris.withAppendedId(
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
//        String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
//        String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RESOLUTION));
//        String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
//
//        return new ImageInfo(contentUri,date,resolution,size);
//    }
//
//    //TODO: fix this
//    public static ArrayList<Uri> getAllPic(Context context) {
//        // get all pic and vid
//        String[] projection = {
//                MediaStore.MediaColumns.DATA,
//                MediaStore.MediaColumns._ID,
//                MediaStore.Files.FileColumns.MEDIA_TYPE,
//        };
//        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
//                + " OR "
//                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
//                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
//        int column_index_data;
//        @SuppressLint("Recycle") Cursor cursor = context.getApplicationContext().getContentResolver().query(
//                MediaStore.Files.getContentUri("external"),
//                projection,
//                selection,
//                null, // Selection args (none).
//                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
//        );
//
//        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//
//        ArrayList<Uri> uriArrayList = new ArrayList<Uri>();
//
//        while (cursor.moveToNext()) {
//            String absolutePathOfImage = cursor.getString(column_index_data);
//            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
//            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
//            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
//                // get duration of video
//                //String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
//
//                // get thumbnail video
//                Uri contentUri = ContentUris.withAppendedId(
//                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
//                uriArrayList.add(contentUri);
//
//            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
//                // get thumbnail img
//
//                Uri contentUri = ContentUris.withAppendedId(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
//                uriArrayList.add(contentUri);
//            }
//        }
//        return uriArrayList;
//
//    }

}
