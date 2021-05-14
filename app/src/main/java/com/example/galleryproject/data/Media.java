package com.example.galleryproject.data;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.galleryproject.FileHandler;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import wseemann.media.FFmpegMediaMetadataRetriever;

public abstract class Media implements Parcelable {
    final Uri uri;
    String size;
    String date;
    String resolution;
    final int MEDIA_TYPE;
    final String fileName;
    String location;
    final int orientation;
    boolean isFavorite = false;
    boolean isTrash = false;
    int albumIn;
    private static final String[] sizeNotation = {"B", "KB", "MB", "GB"};
    private static final java.text.SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);


    public Media(Uri uri, String size, String date, String resolution, int MEDIA_TYPE, String fileName, String location, int orientation, boolean isFavorite, boolean isTrash) {
        this.uri = uri;
        this.size = size;
        this.date = date;
        this.resolution = resolution;
        this.MEDIA_TYPE = MEDIA_TYPE;
        this.fileName = fileName;
        this.location = location;
        this.orientation = orientation;
        this.isFavorite = isFavorite;
        this.isTrash = isTrash;
    }

    protected Media(Parcel in) {
        uri = in.readParcelable(Uri.class.getClassLoader());
        size = in.readString();
        date = in.readString();
        resolution = in.readString();
        MEDIA_TYPE = in.readInt();
        fileName = in.readString();
        location = in.readString();
        orientation = in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isFavorite = in.readBoolean();
            isTrash = in.readBoolean();
        }
        albumIn = in.readInt();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isFavorite);
            dest.writeBoolean(isTrash);
        }
        dest.writeInt(albumIn);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getFileName() {
        return fileName;
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

    public String getDate() {
        return date;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public int getAlbumIn() {
        return this.albumIn;
    }

    public String getSize() {
        return size;
    }

    public String getTransferSize() {
        int notationPos = 0;
        float transferSize = (float) Long.parseLong(this.size);
        while (transferSize > 1024) {
            transferSize /= 1024;
            notationPos++;
        }
        transferSize = (float) (Math.round(transferSize * 100.00) / 100.00);
        return transferSize + sizeNotation[notationPos];
    }

    public String getResolution() {
        return resolution;
    }

    public String getLocation() {
        return location;
    }

    public void setAlbumIn(int albumIn) {
        this.albumIn = albumIn;
    }

    public void setSize(String size) {
        this.size = size;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void changeFavoriteState() {
        this.isFavorite = !this.isFavorite;
    }


    private static int findAlbumPos(ArrayList<DefaultAlbum> defaultAlbumArrayList, String newAlbumPath) {
        int len = defaultAlbumArrayList.size();
        for (int i = 0; i < len; i++) {
            try {
                if (defaultAlbumArrayList.get(i).getAlbumPath().compareTo(newAlbumPath) == 0) {
                    return i;
                }
            } catch (Exception e) {
                Log.e("", "findAlbumPos: ");
            }

        }
        return -1;
    }


    //TODO: FIX THIS
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void getAllMediaUriAbovePI29(Context context, ArrayList<Media> mediaArrayList, ArrayList<DefaultAlbum> defaultAlbumArrayList, HashSet<String> favoriteMediaHashSet) {
        // get all pic and vid
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.DURATION,
                MediaStore.MediaColumns.ORIENTATION,
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

        while (cursor.moveToNext()) {

            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));

            String relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH));
            // media type
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));

            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));

            String dataAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
            //directory of file
            String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME));

            int orientation = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.ORIENTATION));
            boolean isFavorite = false;
            boolean isTrash = false;

            Uri contentUri;
            Media nextMedia;
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                // get duration of video
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));

                // get thumbnail video
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                // check if media is favorite
                if (favoriteMediaHashSet != null && favoriteMediaHashSet.contains(contentUri.toString())) {
                    isFavorite = true;
                }
                nextMedia = new VideoInfo(contentUri, null, dataAdded, null, mediaType, fileName, null, duration, orientation, isFavorite, isTrash);
                mediaArrayList.add(nextMedia);
                int albumPos = addVideoToAlbumList(relativePath, bucketName, defaultAlbumArrayList, (VideoInfo) nextMedia);
                if (albumPos != -1)
                    nextMedia.setAlbumIn(albumPos);

            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                String location = null;

                if (favoriteMediaHashSet != null && favoriteMediaHashSet.contains(contentUri.toString())) {
                    isFavorite = true;
                }
                nextMedia = new ImageInfo(contentUri, null, dataAdded, null, mediaType, fileName, location, orientation, isFavorite, isTrash);
                mediaArrayList.add(nextMedia);
                int albumPos = addImageToAlbumList(relativePath, bucketName, defaultAlbumArrayList, (ImageInfo) nextMedia);
                if (albumPos != -1)
                    nextMedia.setAlbumIn(albumPos);
            }
        }
        cursor.close();
    }

    public static void getAllMediaUri(Context context, ArrayList<Media> mediaArrayList, ArrayList<DefaultAlbum> defaultAlbumArrayList, HashSet<String> favoriteMediaHashSet) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
            getAllMediaUriAbovePI29(context, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);
        else
            getAllMediaUriBelowAPI29(context, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);

    }

    private static void getAllMediaUriBelowAPI29(Context context, ArrayList<Media> mediaArrayList, ArrayList<DefaultAlbum> defaultAlbumArrayList, HashSet<String> favoriteMediaHashSet) {
        // get all pic and vid
        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.MediaColumns.DATE_ADDED,
                //MediaStore.MediaColumns.DURATION,
                //MediaStore.MediaColumns.ORIENTATION,
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

        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();

        while (cursor.moveToNext()) {


            String absolutePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));

            Uri uri = Uri.fromFile(new File(absolutePath));

            String[] detailPath = FileHandler.getPathDetail(absolutePath);

            String relativePath = detailPath[0];

            String bucketName = detailPath[1];

            String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
            // media type
            int mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE));

            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));

            String dataAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
            //directory of file

            int orientation = 0;
            boolean isFavorite = false;
            boolean isTrash = false;

            Uri contentUri;
            Media nextMedia;
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                // get duration of video
                int duration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));

                // get thumbnail video
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
                // check if media is favorite
                if (favoriteMediaHashSet != null && favoriteMediaHashSet.contains(contentUri.toString())) {
                    isFavorite = true;
                }
                nextMedia = new VideoInfo(contentUri, null, dataAdded, null, mediaType, fileName, null, duration, orientation, isFavorite, isTrash);
                mediaArrayList.add(nextMedia);
                int albumPos = addVideoToAlbumList(relativePath, bucketName, defaultAlbumArrayList, (VideoInfo) nextMedia);
                if (albumPos != -1)
                    nextMedia.setAlbumIn(albumPos);

            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                String location = null;

                if (favoriteMediaHashSet != null && favoriteMediaHashSet.contains(contentUri.toString())) {
                    isFavorite = true;
                }
                nextMedia = new ImageInfo(contentUri, null, dataAdded, null, mediaType, fileName, location, orientation, isFavorite, isTrash);
                mediaArrayList.add(nextMedia);
                int albumPos = addImageToAlbumList(relativePath, bucketName, defaultAlbumArrayList, (ImageInfo) nextMedia);
                if (albumPos != -1)
                    nextMedia.setAlbumIn(albumPos);
            }
        }
        cursor.close();
    }

    public void getMediaDetail(DefaultAlbum albumIn, ContentResolver contentResolver, Context context) {
        String[] projection = {
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.DATE_ADDED,
        };
        Cursor cursor = contentResolver.query(this.uri, projection, null, null, null);
        if (cursor == null) {
            return;
        }
        cursor.moveToNext();
        String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));
        String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED));
        String resolution = "";
        String filePath = FileHandler.EXTERNAL_STORAGE_DIR + albumIn.getAlbumPath() + this.fileName;
        if (this.getMEDIA_TYPE() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            resolution = options.outWidth + "x" + options.outHeight;
        } else {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(filePath);
            String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            resolution = width + "x" + height;
        }

        String location = "";
        float[] coordination = new float[2];
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(filePath);
            if (exifInterface.getLatLong(coordination))
                location = getAddress(coordination[0], coordination[1], context);
            else
                location = "unknown";
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.setSize(size);
        this.setDate(dateAdded);
        this.setResolution(resolution);
        this.setLocation(location);
        cursor.close();
    }



    public static String getAddress(double lat, double lng, Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() != 0) {
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
//                add = add + "\n" + obj.getCountryName();
//                add = add + "\n" + obj.getCountryCode();
//                add = add + "\n" + obj.getAdminArea();
////                add = add + "\n" + obj.getPostalCode();
////                add = add + "\n" + obj.getSubAdminArea();
////                add = add + "\n" + obj.getLocality();
////                add = add + "\n" + obj.getSubThoroughfare();
                return add;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static int addImageToAlbumList(String albumPath, String bucketName, ArrayList<DefaultAlbum> defaultAlbumArrayList, ImageInfo nextMedia) {

        // check if bucket name is existed
        if (defaultAlbumArrayList == null) {
            return -1;
        }
        if (bucketName == null) {
            bucketName = "0";
        }
        int pos = findAlbumPos(defaultAlbumArrayList, albumPath);
        if (pos == -1) {
            defaultAlbumArrayList.add(new DefaultAlbum(albumPath, bucketName));
            pos = defaultAlbumArrayList.size() - 1;
        }

        defaultAlbumArrayList.get(pos).addImageInfo(nextMedia);
        return pos;
    }

    private static int addVideoToAlbumList(String albumPath, String bucketName, ArrayList<DefaultAlbum> defaultAlbumArrayList, VideoInfo nextMedia) {

        // check if defaultAlbumArrayList = null
        if (defaultAlbumArrayList == null) {
            return -1;
        }
        // check if bucket name is null (root dir)
        if (bucketName == null) {
            bucketName = "0";
        }
        int pos = findAlbumPos(defaultAlbumArrayList, albumPath);
        if (pos == -1) {
            defaultAlbumArrayList.add(new DefaultAlbum(albumPath, bucketName));
            pos = defaultAlbumArrayList.size() - 1;
        }

        defaultAlbumArrayList.get(pos).addVideoInfo(nextMedia);
        return pos;
    }


    @Override
    public abstract String toString();

    public static Media parseString(String data) {
        String datas[] = data.split("\\|");
        if (Integer.parseInt(datas[4]) == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            return ImageInfo.parseString(datas);
        else if (Integer.parseInt(datas[4]) == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
            return VideoInfo.parseString(datas);
        return null;
    }

    public static Date getDate(long val) {
        return new Date(val * 1000L);
    }


    public static Uri getUriMediaCollection(int mediaType) {
        Uri mediaCollection = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                mediaCollection = MediaStore.Images.Media
                        .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                mediaCollection = MediaStore.Video.Media
                        .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
        } else {
            if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                mediaCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                mediaCollection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            }
        }
        return mediaCollection;
    }

    public void deleteMedia(Context context) {
        try {
            ContentResolver resolver = context.getContentResolver();            // Remove a specific media item.
            Uri imageUri = this.getUri();                                       // URI of the image to remove.
            // Perform the actual removal.
            int numImagesRemoved = resolver.delete(
                    imageUri,
                    null,
                    null);
            if (numImagesRemoved == 0) {
                Toast.makeText(context, "Delete unsuccessfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Delete successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public static void getFavorite(ArrayList<Media> allMediaArrayList, ArrayList<Media> mediaArrayList, HashSet<String> favoriteMediaHashSet) {
        mediaArrayList.clear();
        for (int i = 0; i < allMediaArrayList.size(); i++) {
            if (favoriteMediaHashSet.contains(allMediaArrayList.get(i).getUri().toString())) {
                mediaArrayList.add(allMediaArrayList.get(i));
            }
        }
    }

    public static void getVideo(ArrayList<Media> allMediaArrayList, ArrayList<Media> mediaArrayList) {
        mediaArrayList.clear();
        for (int i = 0; i < allMediaArrayList.size(); i++) {
            if (allMediaArrayList.get(i).getMEDIA_TYPE() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                mediaArrayList.add(allMediaArrayList.get(i));
            }
        }
    }

    public static void getImage(ArrayList<Media> allMediaArrayList, ArrayList<Media> mediaArrayList) {
        mediaArrayList.clear();
        for (int i = 0; i < allMediaArrayList.size(); i++) {
            if (allMediaArrayList.get(i).getMEDIA_TYPE() == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                mediaArrayList.add(allMediaArrayList.get(i));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isDiffDate(Media media1, Media media2, int mode) {
        try {
            Date date1 = Media.getDate(Long.parseLong(media1.getDate()));
            Date date2 = Media.getDate(Long.parseLong(media2.getDate()));
            Calendar c = Calendar.getInstance();
            c.setTime(date1);
            int a = c.get(Calendar.DAY_OF_MONTH);
            c.setTime(date2);
            int b = c.get(Calendar.DAY_OF_MONTH);
            if (a != b) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}