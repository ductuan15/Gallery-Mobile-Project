package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.DefaultSelectionTracker;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.ui.DetailsLookup;

import java.util.ArrayList;
import java.util.HashSet;


public class AlbumViewActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnLongClickListener {
    ArrayList<DefaultAlbum> defaultAlbumArrayList = new ArrayList<>();
    ArrayList<Media> mediaArrayList = new ArrayList<>();
    SharedPreferences favoriteSharedPreferences;
    public HashSet<String> favoriteMediaHashSet = new HashSet<>();
    RecyclerView listViewAllAlbum;
    RecyclerView gridViewAllMedia;
    ThumbnailPictureAdapter thumbnailPictureAdapter;
    ThumbnailAlbumAdapter thumbnailAlbumAdapter;
    SelectionTracker<Long> selectionTracker;
    int albumSelectedPos = 0;
    boolean isSelectionMode = false;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        Bundle data = getIntent().getExtras();

        //get all album data
//        int len = this.albumSelectedPos = data.getInt("albumLen");
//        for (int i = 0; i < len; i++)
//            defaultAlbumArrayList.add(data.getParcelable(String.valueOf(i)));
//
        this.albumSelectedPos = data.getInt("albumPos");

        // get all favorite media
        favoriteSharedPreferences = SharePreferenceHandler.getFavoriteSharePreferences(this);
        SharePreferenceHandler.getAllDataFromSharedPreference(favoriteSharedPreferences,favoriteMediaHashSet);

        // get data set
        Media.getAllMediaUri(this,mediaArrayList,defaultAlbumArrayList,favoriteMediaHashSet);

        // set up album recycleView
        thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(this.defaultAlbumArrayList, this, this);

        this.listViewAllAlbum = findViewById(R.id.list_view_thumbnail_album);
        this.listViewAllAlbum.setAdapter(thumbnailAlbumAdapter);
        this.listViewAllAlbum.setLayoutManager(new LinearLayoutManager(this));

        //scrolling to current album
        listViewAllAlbum.scrollToPosition(this.albumSelectedPos);


        // get all media of album
        this.mediaArrayList = this.defaultAlbumArrayList.get(this.albumSelectedPos).getMediaArrayList();

        //set up  recycleView
        this.gridViewAllMedia = findViewById(R.id.grid_view_thumbnail_media);

        StableIdKeyProvider keyProvider = new StableIdKeyProvider(this.gridViewAllMedia);
        this.thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.mediaArrayList,this, null, this);
        this.gridViewAllMedia.setAdapter(thumbnailPictureAdapter);
        this.gridViewAllMedia.setLayoutManager(new GridLayoutManager(this, 3));

        selectionTracker = new SelectionTracker.Builder<>(
                "media_select",
                this.gridViewAllMedia,
                keyProvider,
                new DetailsLookup(this.gridViewAllMedia),
                StorageStrategy.createLongStorage()).build();

        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onItemStateChanged(@NonNull Long key, boolean selected) {
                super.onItemStateChanged(key, selected);
                int size = selectionTracker.getSelection().size();
                if (size != 0) {
                    getSupportActionBar().setTitle(String.valueOf(selectionTracker.getSelection().size()));
                    if (!isSelectionMode) {
                        isSelectionMode = true;
                        invalidateOptionsMenu();
                    }
                } else {
                    getSupportActionBar().setTitle(R.string.title_allpic);
                    isSelectionMode = false;
                    invalidateOptionsMenu();
                }
            }
        });

        thumbnailPictureAdapter.setSelectionTracker(this.selectionTracker);

    }

    @Override
    public void onBackPressed() {
        if(this.isSelectionMode){
            isSelectionMode =false;
            selectionTracker.clearSelection();
            thumbnailPictureAdapter.notifyDataSetChanged();
        }else{
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onResume() {
        super.onResume();


        thumbnailPictureAdapter.setMediaArrayList(defaultAlbumArrayList.get(albumSelectedPos).getMediaArrayList());

        thumbnailPictureAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(v.getTag() instanceof  ThumbnailPictureAdapter.ThumbnailPictureViewHolder){
            ThumbnailPictureAdapter.ThumbnailPictureViewHolder viewHolder = (ThumbnailPictureAdapter.ThumbnailPictureViewHolder) v.getTag();
            int pos = viewHolder.getAdapterPosition();
            int albumPos = mediaArrayList.get(pos).getAlbumIn();
            Intent intent = new Intent(this,SlideMediaActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("curAlbum",this.defaultAlbumArrayList.get(albumPos));
            bundle.putInt("mediaPos",pos);
            intent.putExtras(bundle);
            startActivity(intent);


        }else if (v.getTag() instanceof ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder){
            ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder viewHolder = (ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder) v.getTag();
            int pos = viewHolder.getAdapterPosition();
            if (pos != this.albumSelectedPos) {
                selectionTracker.clearSelection();
                isSelectionMode =false;
                invalidateOptionsMenu();
                this.mediaArrayList = this.defaultAlbumArrayList.get(pos).getMediaArrayList();
                this.thumbnailPictureAdapter.setMediaArrayList(this.mediaArrayList);
                this.thumbnailPictureAdapter.notifyDataChange();
                this.albumSelectedPos = pos;
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        menu.clear();
        if (this.isSelectionMode) {
            inflater.inflate(R.menu.multiselect_menu, menu);
        } else {
            inflater.inflate(R.menu.top_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}
