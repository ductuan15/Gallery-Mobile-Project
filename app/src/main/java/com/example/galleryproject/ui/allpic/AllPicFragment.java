package com.example.galleryproject.ui.allpic;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.AlbumSelectDialogFragment;
import com.example.galleryproject.MainActivity;
import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailPictureAdapter;
import com.example.galleryproject.SlideMediaActivity;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.ui.DetailsLookup;

import java.util.ArrayList;

//public class AllPicFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

public class AllPicFragment extends Fragment implements View.OnClickListener, OnItemActivatedListener<Long> {
    private AllPicViewModel allPicViewModel;
    private RecyclerView thumbnailPicGridView;
    private ThumbnailPictureAdapter thumbnailPictureAdapter;
    ArrayList<Media> mediaArrayList;
    SelectionTracker<Long> selectionTracker;
    boolean isSelectionMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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


        this.mediaArrayList = ((MainActivity) requireActivity()).mediaArrayList;
        // set this fragment as a listener
        this.thumbnailPicGridView = root.findViewById(R.id.grid_view_thumbnail_pic);
        StableIdKeyProvider keyProvider = new StableIdKeyProvider(this.thumbnailPicGridView);
        this.thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.mediaArrayList, this.getContext(), this.selectionTracker, this);

        this.thumbnailPicGridView.setHasFixedSize(true);
        this.thumbnailPicGridView.setLayoutManager(new GridLayoutManager(getActivity(), colNum));
        this.thumbnailPicGridView.setAdapter(this.thumbnailPictureAdapter);
        selectionTracker = new SelectionTracker.Builder<>(
                "media_select",
                this.thumbnailPicGridView,
                keyProvider,
                new DetailsLookup(this.thumbnailPicGridView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(this)
                .build();
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
            @Override
            public void onItemStateChanged(@NonNull Long key, boolean selected) {
                super.onItemStateChanged(key, selected);
                int size = selectionTracker.getSelection().size();
                if (size != 0) {
                    ((MainActivity) requireActivity()).getSupportActionBar().setTitle(String.valueOf(selectionTracker.getSelection().size()));
                    if (!isSelectionMode) {
                        isSelectionMode = true;
                        requireActivity().invalidateOptionsMenu();
                    }
                } else {
                    ((MainActivity) requireActivity()).getSupportActionBar().setTitle(R.string.title_allpic);
                    isSelectionMode = false;
                    requireActivity().invalidateOptionsMenu();
                }
            }
        });

        this.thumbnailPictureAdapter.setSelectionTracker(this.selectionTracker);
        return root;

    }


    @Override
    public void onResume() {
        super.onResume();
        thumbnailPictureAdapter.setMediaArrayList(this.mediaArrayList);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        if (this.isSelectionMode) {
            inflater.inflate(R.menu.multiselect_menu, menu);
        } else {
            inflater.inflate(R.menu.top_menu, menu);
        }

    }

    //TODO:move to new Album
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.move_to_album_opt) {
//            Selection<Long> selectedItem = this.selectionTracker.getSelection();
//            InputStream a;
//            for (Long l : selectedItem) {
//                if (l < Integer.MAX_VALUE && l >= 0) {
//                    int i = l.intValue();
//                    try {
//                        InputStream a = requireActivity().getContentResolver().openInputStream(this.mediaArrayList.get(i).getUri());
//                        Path path = FileSystems.getDefault().getPath("/storage/emulated/0/DCIM/Camera/temp_224.jpg");
//
//                        // Add a specific media item.
//                        ContentResolver resolver = requireActivity().getApplicationContext()
//                                .getContentResolver();
//                        Uri audioCollection;
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                            audioCollection = MediaStore.Images.Media
//                                    .getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
//                        } else {
//                            audioCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                        }
//                        Media m = this.mediaArrayList.get(i);
//                        ContentValues newSongDetails = new ContentValues();
//                        newSongDetails.put(MediaStore.MediaColumns.RELATIVE_PATH,
//                                Environment.DIRECTORY_DCIM + "/Camera/");
//                        newSongDetails.put(MediaStore.MediaColumns.DISPLAY_NAME, "temp_224.jpg");
//
//                        newSongDetails.put(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME, "Camera");
//
//                        newSongDetails.put(MediaStore.MediaColumns.ORIENTATION, this.mediaArrayList.get(i).getOrientation());
//
//                        newSongDetails.put(MediaStore.MediaColumns.DATE_ADDED,m.getDate());
//
//                        Uri myFavoriteSongUri = resolver.insert(audioCollection, newSongDetails);      //important!
//
//                        Files.copy(a,path);
//                        Log.e("Tag", myFavoriteSongUri.getPath());
//
//                    } catch (IOException e) {
//                        Log.e("TAG", e.getMessage());
//                    }
//                }
//            }
//        }
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            AlbumSelectDialogFragment newFragment = new AlbumSelectDialogFragment(((MainActivity) requireActivity()).defaultAlbumArrayList,
                    ((MainActivity) requireActivity()).mediaArrayList,
                    this.selectionTracker.getSelection(),
                    (String) item.getTitle());
            newFragment.show(fragmentManager, "dialog");
        }
        return false;
    }

    public boolean onBackPressed() {
        this.selectionTracker.clearSelection();
        this.thumbnailPictureAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onClick(View v) {
        ThumbnailPictureAdapter.ThumbnailPictureViewHolder viewHolder = (ThumbnailPictureAdapter.ThumbnailPictureViewHolder) v.getTag();
        int pos = viewHolder.getAdapterPosition();
        Intent intent = new Intent(this.getActivity(), SlideMediaActivity.class);
        Bundle data = new Bundle();
        data.putInt("imgPos", pos);
        intent.putExtras(data);
        startActivity(intent);
    }


    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull MotionEvent e) {
        return false;
    }
}


