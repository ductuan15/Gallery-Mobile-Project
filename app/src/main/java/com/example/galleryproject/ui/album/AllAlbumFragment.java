package com.example.galleryproject.ui.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailAlbumAdapter;
import com.example.galleryproject.ThumbnailPictureAdapter;

public class AllAlbumFragment extends Fragment {


    private AllAlbumViewModel allAlbumViewModel;

    private RecyclerView thumbnailAlbum_GridView;
    private RecyclerView thumbnailPeopleLocation_GridView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allAlbumViewModel =
                new ViewModelProvider(this).get(AllAlbumViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allalbum, container, false);
        int colNum = 3;
        // TODO: replace by img and album name
        String [] data1= {"1","2","","","","","","","","",""};
        String [] data2= {"1","2","","","","","","","","",""};
        ThumbnailAlbumAdapter thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(data1,data2);
        this.thumbnailAlbum_GridView = root.findViewById(R.id.grid_view_thumbnail_album);
        this.thumbnailAlbum_GridView.setLayoutManager(new GridLayoutManager(getActivity(),colNum,RecyclerView.HORIZONTAL,false));
        this.thumbnailAlbum_GridView.setAdapter(thumbnailAlbumAdapter);

        thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(data1,data2);
        this.thumbnailPeopleLocation_GridView = root.findViewById(R.id.grid_view_thumbnail_people_location);
        this.thumbnailPeopleLocation_GridView.setLayoutManager(new GridLayoutManager(getActivity(),colNum,RecyclerView.HORIZONTAL,false));
        this.thumbnailPeopleLocation_GridView.setAdapter(thumbnailAlbumAdapter);

        return root;
    }
}