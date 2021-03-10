package com.example.galleryproject.ui.allpic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailPictureAdapter;

public class AllPicFragment extends Fragment {

    private AllPicViewModel allPicViewModel;
    private RecyclerView thumbnailPic_GridView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allPicViewModel =
                new ViewModelProvider(this).get(AllPicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allpic, container, false);
        int colNum = 4;
        String [] data= {"1","","","","","","","","","","","","","","","","","","","","","","","","",""};
        ThumbnailPictureAdapter thumbnailPictureAdapter = new ThumbnailPictureAdapter(data);
        this.thumbnailPic_GridView =  root.findViewById(R.id.grid_view_thumbnail_pic);
        this.thumbnailPic_GridView.setLayoutManager(new GridLayoutManager(getActivity(),colNum));
        this.thumbnailPic_GridView.setAdapter(thumbnailPictureAdapter);
        return root;
    }
}