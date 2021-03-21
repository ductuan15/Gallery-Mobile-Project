package com.example.galleryproject.ui.allpic;

import android.content.ContentUris;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailPictureAdapter;

import java.util.ArrayList;

public class AllPicFragment extends Fragment {

    private AllPicViewModel allPicViewModel;
    private RecyclerView thumbnailPic_GridView;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allPicViewModel = new ViewModelProvider(this).get(AllPicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allpic, container, false);
        int colNum = 4;
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            colNum = 8;
        }


        ArrayList<Uri> uriArrayList = this.getAllPic();


        ThumbnailPictureAdapter thumbnailPictureAdapter = new ThumbnailPictureAdapter(uriArrayList, this);
        this.thumbnailPic_GridView = root.findViewById(R.id.grid_view_thumbnail_pic);
        this.thumbnailPic_GridView.setHasFixedSize(true);
        this.thumbnailPic_GridView.setLayoutManager(new GridLayoutManager(getActivity(), colNum));
        this.thumbnailPic_GridView.setAdapter(thumbnailPictureAdapter);
        return root;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ArrayList<Uri> getAllPic() {
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
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
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


