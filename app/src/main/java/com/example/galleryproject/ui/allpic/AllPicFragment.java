package com.example.galleryproject.ui.allpic;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.MainActivity;
import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailPictureAdapter;
import com.example.galleryproject.SlideMediaActivity;
import com.example.galleryproject.data.Media;

import java.util.ArrayList;

public class AllPicFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    private AllPicViewModel allPicViewModel;
    private RecyclerView thumbnailPic_GridView;
    private ThumbnailPictureAdapter thumbnailPictureAdapter;
    ArrayList<Media> mediaArrayList;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allPicViewModel = new ViewModelProvider(this).get(AllPicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allpic, container, false);
        int colNum = 3;
        int orientation = requireActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            colNum = 8;
        }



        this.mediaArrayList = ((MainActivity)requireActivity()).mediaArrayList;
        // set this fragment as a listener
        this.thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.mediaArrayList,this.getContext(),this, this);
        this.thumbnailPictureAdapter.setMultiSelectMode(false);
        this.thumbnailPic_GridView = root.findViewById(R.id.grid_view_thumbnail_pic);
        this.thumbnailPic_GridView.setHasFixedSize(true);
        this.thumbnailPic_GridView.setLayoutManager(new GridLayoutManager(getActivity(), colNum));
        this.thumbnailPic_GridView.setAdapter(this.thumbnailPictureAdapter);
        return root;

    }


    @Override
    public void onResume() {
        super.onResume();
        thumbnailPictureAdapter.setMediaArrayList(this.mediaArrayList);
    }

    public boolean onBackPressed(){
        if(this.thumbnailPictureAdapter.isMultiSelectMode()){
            this.thumbnailPictureAdapter.setMultiSelectMode(false);
            this.thumbnailPictureAdapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        ThumbnailPictureAdapter.ThumbnailPictureViewHolder viewHolder = (ThumbnailPictureAdapter.ThumbnailPictureViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        if(this.thumbnailPictureAdapter.isMultiSelectMode()){
            viewHolder.changeSelectState();
        }else{
            Intent intent = new Intent(this.getActivity(), SlideMediaActivity.class);
            Bundle data = new Bundle();
            data.putInt("imgPos",pos);
            intent.putExtras(data);
            startActivity(intent);
        }

    }

    @Override
    public boolean onLongClick(View v) {
        if(this.thumbnailPictureAdapter.isMultiSelectMode()){
            return true;
        }
        ThumbnailPictureAdapter.ThumbnailPictureViewHolder viewHolder = (ThumbnailPictureAdapter.ThumbnailPictureViewHolder) v.getTag();
        viewHolder.changeSelectState();
        this.thumbnailPictureAdapter.setMultiSelectMode(true);
        this.thumbnailPictureAdapter.notifyDataSetChanged();
        return true;
    }
}


