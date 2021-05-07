package com.example.galleryproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;

public class AlbumSelectDialogFragment extends DialogFragment implements Toolbar.OnMenuItemClickListener {
    ArrayList<DefaultAlbum> defaultAlbumArrayList;
    ArrayList<Media> mediaArrayList;
    Selection<Long> selectedItem;
    private Toolbar toolbar;
    private String action;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    public AlbumSelectDialogFragment(ArrayList<DefaultAlbum> defaultAlbumArrayList, ArrayList<Media> mediaArrayList, Selection<Long> selectedItem, String action) {
        super();
        this.selectedItem = selectedItem;
        this.defaultAlbumArrayList = defaultAlbumArrayList;
        this.mediaArrayList = mediaArrayList;
        this.action = action;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_select_album, container, false);
        toolbar = root.findViewById(R.id.toolbar);
        ThumbnailAlbumAdapter thumbnailAlbumAdapter = new ThumbnailAlbumAdapter(this.defaultAlbumArrayList, null, this.requireActivity());
        RecyclerView recyclerView = root.findViewById(R.id.select_album_recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(thumbnailAlbumAdapter);
        return root;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setWindowAnimations(R.style.Widget_AppCompat_ActionBar);
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle(action);
        toolbar.inflateMenu(R.menu.album_selection_menu);
        toolbar.setOnMenuItemClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.cancel_action_opt) {
            dismiss();
        } else if (item.getItemId() == R.id.create_album_opt) {
            View dialogView = LayoutInflater.from(requireActivity()).inflate(R.layout.diglog_input, null);
            EditText albumText = dialogView.findViewById(R.id.album_name_text);
            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle("Create album")
                    .setView(dialogView)
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Create", (dialog, which) -> {
                        String albumName = albumText.getText().toString();
                        String folderName = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + albumName;
                        File folder = new File(folderName);
                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            //((MainActivity) requireActivity()).defaultAlbumArrayList.add(new DefaultAlbum(albumText.getText().toString(), folderName));
                            for (Long l : selectedItem) {
                                if (l < Integer.MAX_VALUE && l >= 0) {
                                    int i = l.intValue();
                                    Media media = this.mediaArrayList.get(i);
                                    Media.copyFile(media.getUri(),media.getFileName(),albumName,requireActivity().getContentResolver());
                                }
                            }
                            Toast.makeText(requireActivity(), "Created", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireActivity(), "Create Fail", Toast.LENGTH_LONG).show();
                        }
                    })
                    .show();
        }
        return false;
    }
}
