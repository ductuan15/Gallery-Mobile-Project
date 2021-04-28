package com.example.galleryproject.data;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.exifinterface.media.ExifInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Media implements Parcelable {
    final Uri uri;
    final String size;
    final String date;
    final String resolution;
    final int MEDIA_TYPE;
    final String fileName;
    final String location;
    final int orientation;
    boolean isInLockedAlbum = false;

    protected Media(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        size = in.readString();
        date = in.readString();
        resolution = in.readString();
        MEDIA_TYPE = in.readInt();
        fileName = in.readString();
        location = in.readString();
        orientation = in.readInt();
    }


    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {

            int type = in.readInt();
            if (type == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                return new ImageInfo(in);
            } else {
                return new VideoInfo(in);
            }
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(MEDIA_TYPE);
        dest.writeParcelable(uri, flags);
        dest.writeString(size);
        dest.writeString(date);
        dest.writeString(resolution);
        dest.writeInt(MEDIA_TYPE);
        dest.writeString(fileName);
        dest.writeString(location);
        dest.writeInt(orientation);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public Uri getUri() {
        return uri;
    }

    public int getMEDIA_TYPE() {
        return MEDIA_TYPE;
    }

    public int getOrientation() {
        return orientation;
    }


    public Media(Uri mUri, String mSize, String mDate, String resolution, int media_type, String fileName, String location, int orientation) {
        this.uri = mUri;
        this.size = mSize;
        this.date = mDate;
        this.resolution = resolution;
        MEDIA_TYPE = media_type;
        this.fileName = fileName;
        this.location = location;
        this.orientation = orientation;
    }


    private static int findAlbumPos(ArrayList<Album> albumArrayList, String newAlbumName) {
        int len = albumArrayList.size();
        for (int i = 0; i < len; i++) {
            try {
                if (albumArrayList.get(i).getAlbumName().compareTo(newAlbumName) == 0) {
                    return i;
                }
            } catch (Exception e) {
                Log.e("", "findAlbumPos: ");
            }

        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void getAllMedia(Context context, ArrayList<Media> mediaArrayList, ArrayList<Album> albumArrayList) {
        // get all pic and vid
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.RESOLUTION,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DURATION,
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

        float[] coordinate;
        String location = null;
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));

            // get location of media
            try {
                ExifInterface exifInterface = new ExifInterface(absolutePathOfImage);
                coordinate = new float[2];
                exifInterface.getLatLong(coordinate);
                location = getAddress(coordinate[0], coordinate[1], context);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // file name
            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
            // media type
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));
            // size of media
            String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
            // data added
            String date = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
            // resolution
            String resolution = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RESOLUTION));

            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
            //directory of file
            String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME));

            int orientation = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION)));


            Uri contentUri;
            Media nextMedia = null;
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                // get duration of video
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                // get thumbnail video
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                nextMedia = new VideoInfo(contentUri, size, date, resolution, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, fileName, location, duration, orientation);
                mediaArrayList.add(nextMedia);

            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                // get thumbnail img

                contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                nextMedia = new ImageInfo(contentUri, size, date, resolution, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, fileName, location, orientation);
                mediaArrayList.add(nextMedia);
            }

            // check if bucket name is existed
            if (bucketName == null) {
                bucketName = "0";
            }
            int pos = findAlbumPos(albumArrayList, bucketName);
            if (pos == -1) {
                albumArrayList.add(new Album(bucketName, false));
                pos = albumArrayList.size() - 1;
            }
            if (albumArrayList != null)
                albumArrayList.get(pos).addMedia(nextMedia);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void getAllMediaUri(Context context, ArrayList<Media> mediaArrayList, ArrayList<Album> albumArrayList) {
        // get all pic and vid
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ORIENTATION

//                MediaStore.Images.ImageColumns.LATITUDE,
//                MediaStore.Images.ImageColumns.LONGITUDE
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
//        Uri geoUri;
//        InputStream stream;
//        ExifInterface exifInterface;
        while (cursor.moveToNext()) {

            //TODO: delete this block test
//            double[] coordinate;
//            String location = null;
//            String absolutePathOfImage = "/storage/self/primary/DCIM/Camera/20210423_000549.jpg";
//
//            // get location of media
//            try {
//                ExifInterface exifInterface = new ExifInterface(absolutePathOfImage);
//                String lat = ExifInterface.TAG_GPS_LATITUDE;
//                String lon = ExifInterface.TAG_GPS_LONGITUDE;
//                lat = exifInterface.getAttribute(lat);
//                lon = exifInterface.getAttribute(lon);
//                coordinate = exifInterface.getLatLong();
//                if(coordinate!=null){
//                    Log.e("TAG", "getAllMediaUri: ");
//                }
////
//                location = getAddress(coordinate[0], coordinate[1], context);
//                if(location!=null){
//                    Log.e("", "OH YEAH" );
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            // media type
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));

            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));

            //directory of file
            String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME));

            int orientation = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION));
            Uri contentUri;
            Media nextMedia;
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                // get duration of video
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                // get thumbnail video
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                nextMedia = new VideoInfo(contentUri, null, null, null, mediaType, null, null, duration, orientation);
                mediaArrayList.add(nextMedia);
                addVideoToAlbumList(bucketName, albumArrayList, (VideoInfo) nextMedia);

            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                // get thumbnail img

                contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                String location = null;
//                double[] coordinate;
//                try {
//                    geoUri = MediaStore.setRequireOriginal(contentUri);
//                    stream = context.getContentResolver().openInputStream(geoUri);
//                    if (stream != null) {
//                        exifInterface = new ExifInterface(stream);
//                        coordinate = exifInterface.getLatLong();
//                        if (coordinate != null) {
//                            location = getAddress(coordinate[0], coordinate[1], context);
//                        }
//                        // Don't reuse the stream associated with the instance of "ExifInterface".
//                        stream.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                nextMedia = new ImageInfo(contentUri, null, null, null, mediaType, null, location, orientation);
                mediaArrayList.add(nextMedia);
                addImageToAlbumList(bucketName, albumArrayList, (ImageInfo) nextMedia);
            }
        }

    }


    public static String getAddress(double lat, double lng, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
//                add = add + "\n" + obj.getPostalCode();
//                add = add + "\n" + obj.getSubAdminArea();
//                add = add + "\n" + obj.getLocality();
//                add = add + "\n" + obj.getSubThoroughfare();
                return add;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static void addImageToAlbumList(String bucketName, ArrayList<Album> albumArrayList, ImageInfo nextMedia) {

        // check if bucket name is existed
        if (albumArrayList == null) {
            return;
        }
        if (bucketName == null) {
            bucketName = "0";
        }
        int pos = findAlbumPos(albumArrayList, bucketName);
        if (pos == -1) {
            albumArrayList.add(new Album(bucketName, false));
            pos = albumArrayList.size() - 1;
        }

        albumArrayList.get(pos).addImageInfo(nextMedia);
    }

    private static void addVideoToAlbumList(String bucketName, ArrayList<Album> albumArrayList, VideoInfo nextMedia) {

        // check if bucket name is existed
        if (albumArrayList == null) {
            return;
        }
        if (bucketName == null) {
            bucketName = "0";
        }
        int pos = findAlbumPos(albumArrayList, bucketName);
        if (pos == -1) {
            albumArrayList.add(new Album(bucketName, false));
            pos = albumArrayList.size() - 1;
        }

        albumArrayList.get(pos).addVideoInfo(nextMedia);
    }

    @NonNull
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
                this.getMEDIA_TYPE() +
                "|" +
                this.fileName +
                "|" +
                this.location +
                "|" +
                this.orientation;
    }
}
