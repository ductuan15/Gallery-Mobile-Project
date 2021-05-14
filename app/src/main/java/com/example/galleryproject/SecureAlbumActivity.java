package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.ImageInfo;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.data.VideoInfo;
import com.example.galleryproject.ui.DetailsLookup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import java.util.ArrayList;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class SecureAlbumActivity extends AppCompatActivity implements View.OnClickListener, OnItemActivatedListener<Long> {
    public ArrayList<Media> secureMediaArrayList = new ArrayList<>();
    public RecyclerView thumbnailMediaRecyclerView;
    SharedPreferences defaultSharedPreferences;
    SelectionTracker<Long> selectionTracker;
    boolean isSelectionMode = false;
    ThumbnailPictureAdapter thumbnailPictureAdapter;

    String curPass;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get current password
        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        curPass = defaultSharedPreferences.getString(getString(R.string.pin_key), getString(R.string.default_pin_key));
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) toolbar.setTitle(R.string.secret_album);
        setContentView(R.layout.activity_secure_album);
        getAllSecureMedia();
        thumbnailMediaRecyclerView = findViewById(R.id.secure_recycleView);
        StableIdKeyProvider keyProvider = new StableIdKeyProvider(this.thumbnailMediaRecyclerView);
        thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.secureMediaArrayList, this, null, this);


        this.thumbnailMediaRecyclerView.setHasFixedSize(true);
        this.thumbnailMediaRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        this.thumbnailMediaRecyclerView.setAdapter(thumbnailPictureAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "media_select",
                this.thumbnailMediaRecyclerView,
                keyProvider,
                new DetailsLookup(this.thumbnailMediaRecyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(this)
                .build();
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

        this.thumbnailPictureAdapter.setSelectionTracker(this.selectionTracker);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAllSecureMedia() {
        this.secureMediaArrayList.clear();
        ArrayList<File> secureMediaFileArrayList = FileHandler.getAllFileFromPath(this.getFilesDir() + File.separator + FileHandler.SECURE_ALBUM_INTERNAL_DIR_NAME);
        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        for (int i = 0; i < secureMediaFileArrayList.size(); i++) {
            try {
                Uri uri = Uri.fromFile(secureMediaFileArrayList.get(i));
                mmr.setDataSource(secureMediaFileArrayList.get(i).getAbsolutePath());
                String size = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE);
                String resolution = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) + "x" + mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                int duration = Integer.parseInt(mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION));
                String fileName = FileHandler.getFileName(secureMediaFileArrayList.get(i));
                if (duration > 0) {
                    VideoInfo newVideo = new VideoInfo(uri, size, null, resolution, MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO, fileName, null, duration, 0, false, false);
                    this.secureMediaArrayList.add(newVideo);
                } else {
                    ImageInfo newImage = new ImageInfo(uri, size, null, resolution, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE, fileName, null, 0, false, false);
                    this.secureMediaArrayList.add(newImage);
                }
            } catch (Exception e) {
                Log.e("TAG", "getAllSecureMedia: ");
            }
        }
        mmr.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (!isSelectionMode)
            inflater.inflate(R.menu.secure_album_menu, menu);
        else
            inflater.inflate(R.menu.secure_multi_select_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isSelectionMode) {
                selectionTracker.clearSelection();
                thumbnailPictureAdapter.notifyDataSetChanged();
            } else {
                onBackPressed();
            }
        } else if (item.getItemId() == R.id.unlock_multi_opt) {
            ArrayList<Media> mediaArrayList = new ArrayList<>();
            ArrayList<DefaultAlbum> defaultAlbumArrayList = new ArrayList<>();
            Media.getAllMediaUri(this, mediaArrayList, defaultAlbumArrayList, null);
            FragmentManager fragmentManager = getSupportFragmentManager();
            AlbumSelectDialogFragment newFragment = new AlbumSelectDialogFragment(defaultAlbumArrayList,
                    mediaArrayList,
                    selectionTracker.getSelection(),
                    AlbumSelectDialogFragment.MOVE_FROM_SECURE);
            newFragment.show(fragmentManager, "dialog");
        } else if (item.getItemId() == R.id.delete_opt) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.ask_for_delete_title)
                    .setMessage(R.string.ask_for_delete_message)
                    .setPositiveButton("OK", (dialog, which) -> {
                        for (Long l : selectionTracker.getSelection()) {
                            if (l < Integer.MAX_VALUE && l >= 0) {
                                int i = l.intValue();
                                if (FileHandler.deleteFile(secureMediaArrayList.get(i).getUri(), this)) {
                                    Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, R.string.Delete_fail, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        onBackPressed();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
        } else if (item.getItemId() == R.id.change_password) {
            View enterCurDialogView = LayoutInflater.from(this).inflate(R.layout.diglog_input, null);
            EditText curPassText = enterCurDialogView.findViewById(R.id.input_text);

            new MaterialAlertDialogBuilder(this)
                    .setTitle("Enter current password")
                    .setView(enterCurDialogView)
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("OK", (dialog, which) -> {
                        String inputPassword = curPassText.getText().toString();
                        if (curPassText.getText().toString().compareTo(curPass) == 0) {
                            View dialogView = LayoutInflater.from(this).inflate(R.layout.diglog_input, null);
                            EditText newPassText = dialogView.findViewById(R.id.input_text);
                            new MaterialAlertDialogBuilder(this)
                                    .setTitle("Enter new password")
                                    .setView(dialogView)
                                    .setNegativeButton("Cancel", (dialog1, which1) -> {
                                        dialog.dismiss();
                                    })
                                    .setPositiveButton("OK", (dialog1, which1) -> {
                                        SharedPreferences.Editor editor = defaultSharedPreferences.edit();
                                        editor.apply();
                                        editor.putString(getString(R.string.pin_key), newPassText.getText().toString());
                                        editor.apply();
                                        finish();
                                    }).create().show();
                        } else {
                            Toast.makeText(getBaseContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                        }
                    }).create().show();
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        ThumbnailPictureAdapter.ThumbnailPictureViewHolder viewHolder = (ThumbnailPictureAdapter.ThumbnailPictureViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        Intent intent = new Intent(this, SlideMediaActivity.class);
        Bundle bundle = new Bundle();
        DefaultAlbum secureAlbum = new DefaultAlbum("", "");
        secureAlbum.setMediaArrayList(this.secureMediaArrayList);
        bundle.putBoolean("isSecureMode", true);
        bundle.putParcelable("curAlbum", secureAlbum);
        bundle.putInt("mediaPos", pos);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public boolean onItemActivated(@NonNull @NotNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull @NotNull MotionEvent e) {
        return false;
    }
}