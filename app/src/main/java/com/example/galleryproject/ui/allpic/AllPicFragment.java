package com.example.galleryproject.ui.allpic;

import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailPictureAdapter;
import com.example.galleryproject.SlideMediaActivity;
import com.example.galleryproject.data.ImageInfo;

import java.util.ArrayList;

public class AllPicFragment extends Fragment implements AdapterView.OnItemClickListener {

    private AllPicViewModel allPicViewModel;
    private RecyclerView thumbnailPic_GridView;
    private ThumbnailPictureAdapter mThumbnailPictureAdapter;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allPicViewModel = new ViewModelProvider(this).get(AllPicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allpic, container, false);
        int colNum = 3;
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            colNum = 8;
        }


        ArrayList<Uri> uriArrayList = ImageInfo.getAllPic(this.getActivity());


        // set this fragment as a listener
        ThumbnailPictureAdapter thumbnailPictureAdapter = new ThumbnailPictureAdapter(uriArrayList, this.getContext(),this);
        this.mThumbnailPictureAdapter = thumbnailPictureAdapter;
        this.thumbnailPic_GridView = root.findViewById(R.id.grid_view_thumbnail_pic);
        this.thumbnailPic_GridView.setHasFixedSize(true);
        this.thumbnailPic_GridView.setLayoutManager(new GridLayoutManager(getActivity(), colNum));
        this.thumbnailPic_GridView.setAdapter(this.mThumbnailPictureAdapter);


        return root;

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent imgView = new Intent(this.getContext(), SlideMediaActivity.class);
        Bundle data = new Bundle();
        data.putInt("imgPos",position);
        imgView.putExtras(data);
        startActivity(imgView);
    }
}


