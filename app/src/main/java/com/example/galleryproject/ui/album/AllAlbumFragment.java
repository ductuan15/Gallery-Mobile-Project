package com.example.galleryproject.ui.album;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.galleryproject.AlbumViewActivity;
import com.example.galleryproject.MainActivity;
import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailAlbumAdapter;
import com.example.galleryproject.ThumbnailPictureAdapter;
import com.example.galleryproject.data.Album;

import java.util.ArrayList;
import java.util.Objects;

public class AllAlbumFragment extends Fragment implements View.OnClickListener {

    private RecyclerView thumbnailAlbum_GridView;
    private RecyclerView thumbnailPeopleLocation_GridView;
    ArrayList<Album> albumArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_allalbum, container, false);
        int colNum = 3;
        // TODO: replace by img and album name
        this.albumArrayList = ((MainActivity) requireActivity()).albumArrayList;


        ThumbnailAlbumAdapter thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(albumArrayList,this,this.getContext());
        this.thumbnailAlbum_GridView = root.findViewById(R.id.grid_view_thumbnail_album);
        this.thumbnailAlbum_GridView.setLayoutManager(new GridLayoutManager(getActivity(),colNum,RecyclerView.HORIZONTAL,false));
        this.thumbnailAlbum_GridView.setAdapter(thumbnailAlbumAdapter);
        return root;
    }



    @Override
    public void onClick(View v) {
        ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder viewHolder = (ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        //Album a = ((MainActivity) getActivity()).albumArrayList.get(pos);
        Intent intent = new Intent(this.getActivity(), AlbumViewActivity.class);
        Bundle data = new Bundle();
        for(int i= 0;i<this.albumArrayList.size();i++){
            data.putParcelable(Integer.toString(i),this.albumArrayList.get(i));
        }
        data.putInt("albumLen",this.albumArrayList.size());
        data.putInt("albumPos",pos);
        intent.putExtras(data);
        startActivity(intent);
    }
}