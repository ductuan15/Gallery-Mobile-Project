package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.selection.DefaultSelectionTracker;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.ui.DetailsLookup;
import com.google.android.material.appbar.MaterialToolbar;

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
    static final int REQUEST_IMAGE_CAPTURE = 2;


    TypedValue a = new TypedValue();
    int albumSelectedPos = 0;
    boolean isSelectionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        MaterialToolbar toolbar = findViewById(R.id.AlbumView_TopAppBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        Bundle data = getIntent().getExtras();

        //get all album data
//        int len = this.albumSelectedPos = data.getInt("albumLen");
//        for (int i = 0; i < len; i++)
//            defaultAlbumArrayList.add(data.getParcelable(String.valueOf(i)));
//
        this.albumSelectedPos = data.getInt("albumPos");


        // get all favorite media
        favoriteSharedPreferences = SharePreferenceHandler.getFavoriteSharePreferences(this);
        SharePreferenceHandler.getAllDataFromSharedPreference(favoriteSharedPreferences, favoriteMediaHashSet);

        // get data set
        Media.getAllMediaUri(this, mediaArrayList, defaultAlbumArrayList, favoriteMediaHashSet);

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
        this.thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.mediaArrayList, this, null, this);
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
        if (this.isSelectionMode) {
            isSelectionMode = false;
            selectionTracker.clearSelection();
            thumbnailPictureAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        thumbnailPictureAdapter.setMediaArrayList(defaultAlbumArrayList.get(albumSelectedPos).getMediaArrayList());

        thumbnailPictureAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getTag() instanceof ThumbnailPictureAdapter.ThumbnailPictureViewHolder) {
            ThumbnailPictureAdapter.ThumbnailPictureViewHolder viewHolder = (ThumbnailPictureAdapter.ThumbnailPictureViewHolder) v.getTag();
            int pos = viewHolder.getAdapterPosition();
            int albumPos = mediaArrayList.get(pos).getAlbumIn();
            Intent intent = new Intent(this, SlideMediaActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("view_mode", SlideMediaActivity.VIEW_MODE_ALBUM);
            bundle.putParcelable("curAlbum", this.defaultAlbumArrayList.get(albumPos));
            bundle.putInt("mediaPos", pos);
            intent.putExtras(bundle);
            startActivity(intent);


        } else if (v.getTag() instanceof ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder) {
            ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder viewHolder = (ThumbnailAlbumAdapter.ThumbnailAlbumViewHolder) v.getTag();
            int pos = viewHolder.getAdapterPosition();
            if (pos != this.albumSelectedPos) {
                selectionTracker.clearSelection();
                isSelectionMode = false;
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
            menu.findItem(R.id.change_layout_opt).setVisible(false);
            menu.findItem(R.id.go_to_secure_album).setVisible(false);

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

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // move to album mode
        if (item.getItemId() == R.id.move_to_album_opt) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            AlbumSelectDialogFragment newFragment = new AlbumSelectDialogFragment(this.defaultAlbumArrayList,
                    this.mediaArrayList,
                    this.selectionTracker.getSelection(),
                    AlbumSelectDialogFragment.MOVE_TO_ALBUM_MODE);
            newFragment.show(fragmentManager, "dialog");
        }
        // copy to album mode
        else if (item.getItemId() == R.id.copy_to_album_opt) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            AlbumSelectDialogFragment newFragment = new AlbumSelectDialogFragment(this.defaultAlbumArrayList,
                    this.mediaArrayList,
                    this.selectionTracker.getSelection(),
                    AlbumSelectDialogFragment.COPY_TO_ALBUM_MODE);
            newFragment.show(fragmentManager, "dialog");
        } else if (item.getItemId() == R.id.delete_opt) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.ask_for_delete_title)
                    .setMessage(R.string.ask_for_delete_message)
                    .setPositiveButton("OK", (dialog, which) -> {
                        for (Long l : selectionTracker.getSelection()) {
                            if (l < Integer.MAX_VALUE && l >= 0) {
                                int i = l.intValue();
                                mediaArrayList.get(i).deleteMedia(this);
                            }
                        }
                        this.selectionTracker.clearSelection();
                        mediaArrayList.clear();
                        defaultAlbumArrayList.clear();
                        Media.getAllMediaUri(this,mediaArrayList,defaultAlbumArrayList,favoriteMediaHashSet);
                        thumbnailPictureAdapter.setMediaArrayList(mediaArrayList);
                        thumbnailPictureAdapter.notifyDataSetChanged();

                        thumbnailAlbumAdapter.setDefaultAlbumArrayList(defaultAlbumArrayList);
                        thumbnailAlbumAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();

        } else if (item.getItemId() == R.id.gotocam) {
            dispatchTakePictureIntent();
            return true;
        } else if (item.getItemId() == R.id.slideshow_opt) {
            Intent intentSlideShow = new Intent(this, SlideMediaActivity.class);
            Bundle data = new Bundle();
            data.putInt("view_mode",SlideMediaActivity.VIEW_MODE_ALBUM);
            data.putInt("mediaPos", 0);
            data.putBoolean("isSlideShow", true);
            intentSlideShow.putExtras(data);
            startActivity(intentSlideShow);
            return true;
        }
        return false;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
            Toast.makeText(this, R.string.cannot_use_camera, Toast.LENGTH_SHORT).show();
        }
    }

}
