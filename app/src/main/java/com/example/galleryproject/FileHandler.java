package com.example.galleryproject;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import static android.os.Environment.getExternalStorageDirectory;

public class FileHandler {

    public static String DEFAULT_RELATIVE_ALBUM_PATH = Environment.DIRECTORY_DCIM + File.separator;
    public static String DEFAULT_ABSOLUTE_ALBUM_PATH = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator;
    public static String EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory() + File.separator;
    public static final String SECURE_ALBUM_INTERNAL_DIR_NAME = "secure_album";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int copyFile(Media media, String folderPath, Context context) {
        InputStream a;
        try {
            a = context.getContentResolver().openInputStream(media.getUri());
            Path path = FileSystems.getDefault().getPath(String.valueOf(getExternalStorageDirectory()), folderPath, media.getFileName());
            if (Files.exists(path)) {
                return 2;
            }
            Files.copy(a, path);
            String absolutePath = getExternalStorageDirectory() + File.separator + folderPath + File.separator + media.getFileName();
            MediaScannerConnection.scanFile(context, new String[]{absolutePath}, null, null);
            return 1;
        } catch (IOException ioException) {
            Log.e("TAG", ioException.getMessage());
            ;
            return 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int moveFile(Media media, DefaultAlbum des, DefaultAlbum src, Context content) {
        Path desPath = FileSystems.getDefault().getPath(String.valueOf(getExternalStorageDirectory()), des.getAlbumPath(), media.getFileName());
        Path srcPath = FileSystems.getDefault().getPath(String.valueOf(getExternalStorageDirectory()), src.getAlbumPath(), media.getFileName());
        try {
            Files.move(srcPath, desPath);
            String filePath = getExternalStorageDirectory() + File.separator + des.getAlbumPath() + File.separator + media.getFileName();
            MediaScannerConnection.scanFile(content, new String[]{filePath}, null, null);
            content.getContentResolver().delete(
                    media.getUri(),
                    null,
                    null);
        } catch (FileAlreadyExistsException e) {
            Log.e("", e.toString());
            return 2;
        } catch (IOException e) {
            Log.e("", e.toString());
            return 0;
        }
        return 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int moveFromSecureAlbum(Media srcMedia, DefaultAlbum desAlbum, Context context) {
        String srcPathStr = context.getFilesDir() + File.separator + SECURE_ALBUM_INTERNAL_DIR_NAME + File.separator + srcMedia.getFileName();
        String desPathStr = FileHandler.EXTERNAL_STORAGE_DIR + File.separator + desAlbum.getAlbumPath() + File.separator + srcMedia.getFileName();
        Path srcPath = FileSystems.getDefault().getPath(srcPathStr);
        Path desPath = FileSystems.getDefault().getPath(desPathStr);
        try {
            Files.move(srcPath, desPath);
            MediaScannerConnection.scanFile(context, new String[]{desPathStr}, null, null);

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int moveToSecureAlbum(Media srcMedia, Context context) {
        String srcPathStr = getFilePath(srcMedia.getUri(), context.getContentResolver());
        Path srcPath = FileSystems.getDefault().getPath(srcPathStr);
        Path desPath = FileSystems.getDefault().getPath(String.valueOf(context.getFilesDir()), SECURE_ALBUM_INTERNAL_DIR_NAME, srcMedia.getFileName());
        String folderPath = context.getFilesDir() + File.separator + SECURE_ALBUM_INTERNAL_DIR_NAME;
        File folder = new File(folderPath);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            try {
                Files.move(srcPath, desPath);
                srcMedia.deleteMedia(context);
            } catch (FileAlreadyExistsException ignored) {
                return 2;
            } catch (IOException e) {
                Log.e("TAG", e.getMessage());
                return 0;
            }
        }
        return 1;
    }

    public static String getFilePath(Uri mediaUri, ContentResolver contentResolver) {
        String result;
        Cursor cursor = contentResolver.query(mediaUri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return null;
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static String getSecureAlbumPath(Context context) {
        return context.getFilesDir() + File.separator + SECURE_ALBUM_INTERNAL_DIR_NAME + File.separator;
    }

    public static ArrayList<File> getAllFileFromPath(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files != null)
            return new ArrayList<>(Arrays.asList(files));
        else
            return new ArrayList<>();
    }

    public static String getFileName(File file) {
        String[] s = file.getAbsolutePath().split("/");
        return s[s.length - 1];

    }

    public static boolean deleteFile(Uri uri, Context context) {
        File file = new File(uri.getPath());
        if (file.exists()) {
            return file.delete();
        } else {
            return false;
        }
    }
    public static String[] getPathDetail(String absolutePath){
        String[] detailPath = new String[2];
        String[] splitPath =  absolutePath.split("/");
        detailPath[1] = splitPath[splitPath.length - 2];
        StringBuilder relativePath = new StringBuilder();
        for(int i = 0;i<detailPath.length;i++){
            if(splitPath[i].compareTo("0") == 0){
                for(int j = i + 1;j<detailPath.length;j++){
                    relativePath.append(splitPath[j]).append(File.separator);
                }
            }
        }
        if(relativePath.toString().length() == 0){
            detailPath[0] = null;
        }else{
            detailPath[1] = relativePath.toString();
        }
        return detailPath;

    }
}
