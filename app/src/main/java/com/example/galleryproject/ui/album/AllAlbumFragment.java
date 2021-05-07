package com.example.galleryproject.ui.album;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.AlbumViewActivity;
import com.example.galleryproject.MainActivity;
import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailAlbumAdapter;
import com.example.galleryproject.data.CreatedAlbum;
import com.example.galleryproject.data.DefaultAlbum;

import java.util.ArrayList;
import java.util.List;

public class AllAlbumFragment extends Fragment implements View.OnClickListener {
    private RecyclerView thumbnailAlbum_GridView;
    private RecyclerView thumbnailPeopleLocation_GridView;
    ArrayList<DefaultAlbum> defaultAlbumArrayList;

    @RequiresApi(api = Build.VERSION_CODES.R)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_allalbum, container, false);
        int colNum = 3;
        // TODO: replace by img and album name
        this.defaultAlbumArrayList = ((MainActivity) requireActivity()).defaultAlbumArrayList;

//        ((MainActivity) requireActivity()).createdAlbumViewModel.getAllCreatedAlbum().observe(((MainActivity) requireActivity()), createdAlbums -> {
//            Log.e("TAG", "onCreate: ");
//            ((MainActivity) requireActivity()).createdAlbumArrayList.clear();
//            ((MainActivity) requireActivity()).createdAlbumArrayList.addAll(createdAlbums);
//            ((MainActivity) requireActivity()).getAllDataSet();
//        });


        ThumbnailAlbumAdapter thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(defaultAlbumArrayList, this, this.getContext());
        this.thumbnailAlbum_GridView = root.findViewById(R.id.grid_view_thumbnail_album);
        this.thumbnailAlbum_GridView.setLayoutManager(new GridLayoutManager(getActivity(), colNum, RecyclerView.HORIZONTAL, false));
        this.thumbnailAlbum_GridView.setAdapter(thumbnailAlbumAdapter);
        return root;
    }


    @Override
    public void onClick(View v) {
        ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder viewHolder = (ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        Intent intent = new Intent(this.getActivity(), AlbumViewActivity.class);
        Bundle data = new Bundle();
        for (int i = 0; i < this.defaultAlbumArrayList.size(); i++) {
            data.putParcelable(Integer.toString(i), this.defaultAlbumArrayList.get(i));
        }
        data.putInt("albumLen", this.defaultAlbumArrayList.size());
        data.putInt("albumPos", pos);
        intent.putExtras(data);
        startActivity(intent);
    }

    public boolean onBackPressed() {
        return false;
    }
}