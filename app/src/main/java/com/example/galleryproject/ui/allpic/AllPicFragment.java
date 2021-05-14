package com.example.galleryproject.ui.allpic;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StableIdKeyProvider;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.galleryproject.AlbumSelectDialogFragment;
import com.example.galleryproject.MainActivity;
import com.example.galleryproject.R;
import com.example.galleryproject.ThumbnailMediaDecoration;
import com.example.galleryproject.ThumbnailPictureAdapter;
import com.example.galleryproject.SlideMediaActivity;
import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.ui.DetailsLookup;

import java.util.ArrayList;

//public class AllPicFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {

public class AllPicFragment extends Fragment implements View.OnClickListener, OnItemActivatedListener<Long> {
    private AllPicViewModel allPicViewModel;
    private RecyclerView thumbnailPicGridView;
    private ThumbnailPictureAdapter thumbnailPictureAdapter;
    ThumbnailMediaDecoration thumbnailMediaDecoration;
    ArrayList<Media> mediaArrayList;
    SelectionTracker<Long> selectionTracker;

    SwipeRefreshLayout swipeRefreshLayout;
    boolean isSelectionMode = false;
    static int[] numsCol = {1, 3, 7};
    int curNumColPos = 1;
    public final static int VIEW_MODE_ALL = 0;
    public final static int VIEW_MODE_FAV = 1;
    public final static int VIEW_MODE_VID = 2;
    public final static int VIEW_MODE_IMG = 3;

    //support spanning
    int spannedCol = 0;

    int viewMode = VIEW_MODE_ALL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allPicViewModel = new ViewModelProvider(this).get(AllPicViewModel.class);
        viewMode = ((MainActivity) requireActivity()).viewMode;
        // set title for top app bar
        setTopBarTitle();
        View root = inflater.inflate(R.layout.fragment_allpic, container, false);
        int orientation = requireActivity().getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            curNumColPos = 2;
        }


        this.mediaArrayList = ((MainActivity) requireActivity()).mediaArrayList;
        // set this fragment as a listener
        this.thumbnailPicGridView = root.findViewById(R.id.grid_view_thumbnail_pic);
        StableIdKeyProvider keyProvider = new StableIdKeyProvider(this.thumbnailPicGridView);
        this.thumbnailPictureAdapter = new ThumbnailPictureAdapter(this.mediaArrayList, this.getContext(), this.selectionTracker, this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), numsCol[curNumColPos]);
//        thumbnailMediaDecoration = new ThumbnailMediaDecoration(numsCol[curNumColPos]);
        this.thumbnailPicGridView.setHasFixedSize(true);
        this.thumbnailPicGridView.setLayoutManager(gridLayoutManager);
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public void setSpanIndexCacheEnabled(boolean cacheSpanIndices) {
//                super.setSpanIndexCacheEnabled(cacheSpanIndices);
//            }
//
//            @Override
//            public int getSpanSize(int position) {
//                if (position < mediaArrayList.size() - 2 && Media.isDiffDate(mediaArrayList.get(position), mediaArrayList.get(position + 1), 0)) {
//                    //thumbnailPictureAdapter.setShowDatePos(position + 1);
//                    View view = gridLayoutManager.findViewByPosition(position - 1);
//                    if(view != null){
//                        return 2;
//                    }
//                }
//                return 1;
//            }
//        });
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
        this.swipeRefreshLayout = root.findViewById(R.id.refresh_swipe_layout);
        setupSwipeRefreshLayout();
        return root;

    }

    private void setTopBarTitle() {
        if (viewMode == VIEW_MODE_ALL) {
            ((MainActivity) requireActivity()).toolbar.setTitle(R.string.title_allpic);
        } else if (viewMode == VIEW_MODE_FAV) {
            ((MainActivity) requireActivity()).toolbar.setTitle(R.string.favorite);
        } else if (viewMode == VIEW_MODE_VID) {
            ((MainActivity) requireActivity()).toolbar.setTitle(R.string.video);
        } else if (viewMode == VIEW_MODE_IMG) {
            ((MainActivity) requireActivity()).toolbar.setTitle(R.string.image);
        }
    }

    private void setupSwipeRefreshLayout() {
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onRefresh() {
                ((MainActivity) requireActivity()).getAllDataSet();
                thumbnailPictureAdapter.setMediaArrayList(mediaArrayList);
                thumbnailPictureAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        if (item.getItemId() == R.id.change_layout_opt) {
            curNumColPos = (curNumColPos + 1) % numsCol.length;
            if (numsCol[curNumColPos] > 5) {
                thumbnailPictureAdapter.setIsSmall(true);
            } else {
                thumbnailPictureAdapter.setIsSmall(false);
            }
            this.thumbnailPicGridView.setLayoutManager(new GridLayoutManager(getActivity(), numsCol[curNumColPos]));
        }
        // move to album mode
        else if (item.getItemId() == R.id.move_to_album_opt) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            AlbumSelectDialogFragment newFragment = new AlbumSelectDialogFragment(((MainActivity) requireActivity()).defaultAlbumArrayList,
                    ((MainActivity) requireActivity()).mediaArrayList,
                    this.selectionTracker.getSelection(),
                    AlbumSelectDialogFragment.MOVE_TO_ALBUM_MODE);
            newFragment.show(fragmentManager, "dialog");
        }
        // copy to album mode
        else if (item.getItemId() == R.id.copy_to_album_opt) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            AlbumSelectDialogFragment newFragment = new AlbumSelectDialogFragment(((MainActivity) requireActivity()).defaultAlbumArrayList,
                    ((MainActivity) requireActivity()).mediaArrayList,
                    this.selectionTracker.getSelection(),
                    AlbumSelectDialogFragment.COPY_TO_ALBUM_MODE);
            newFragment.show(fragmentManager, "dialog");
        } else if (item.getItemId() == R.id.delete_opt) {

            new AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.ask_for_delete_title)
                    .setMessage(R.string.ask_for_delete_message)
                    .setPositiveButton("OK", (dialog, which) -> {
                        for (Long l : selectionTracker.getSelection()) {
                            if (l < Integer.MAX_VALUE && l >= 0) {
                                int i = l.intValue();
                                mediaArrayList.get(i).deleteMedia(requireActivity());
                            }
                        }
                        this.selectionTracker.clearSelection();
                        ((MainActivity) requireActivity()).getAllDataSet();
                        thumbnailPictureAdapter.setMediaArrayList(mediaArrayList);
                        thumbnailPictureAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
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
        if (((MainActivity) requireActivity()).viewMode != VIEW_MODE_ALL) {
            DefaultAlbum album = new DefaultAlbum("", "");
            album.setMediaArrayList(this.mediaArrayList);
            data.putParcelable("curAlbum", album);
            data.putInt("view_mode", SlideMediaActivity.VIEW_MODE_TYPE_MEDIA);
        } else {
            data.putInt("view_mode", SlideMediaActivity.VIEW_MODE_ALL);
        }
        data.putInt("mediaPos", pos);
        data.putBoolean("isSlideShow", false);
        intent.putExtras(data);
        startActivity(intent);
    }


    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull MotionEvent e) {
        return false;
    }
}


