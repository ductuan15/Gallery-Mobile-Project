package com.example.galleryproject;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditPic extends AppCompatActivity {

    ImageButton cropBtn, filterBtn, toneBtn, brushBtn, emojiBtn, textBtn, brightBtn, colorBtn, rotateLeftBtn, rotateRightBtn;
    PhotoEditor mPhotoEditor;
    PropertiesBSFragment mPropertiesBSFragment;
    ConstraintSet mConstraintSet = new ConstraintSet();
    boolean mIsFilterVisible;
    RecyclerView mRvFilters;
    ConstraintLayout mRootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);

        Bundle data = getIntent().getExtras();
        Uri imageUri = data.getParcelable("imageUri");
        PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditorView.getSource().setImageURI(imageUri);
        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);

        //loading font from assest
        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(mEmojiTypeFace)
                .build();

        //set up button
        cropBtn = findViewById(R.id.crop_button);
        filterBtn = findViewById(R.id.filter_button);
        toneBtn = findViewById(R.id.tonality_button);
        brushBtn = findViewById(R.id.brush_button);
        emojiBtn = findViewById(R.id.emoji_button);
        textBtn = findViewById(R.id.addtext_button);
        brightBtn = findViewById(R.id.brightness_button);
        colorBtn = findViewById(R.id.color_button);
        rotateLeftBtn = findViewById(R.id.rotate_left_button);
        rotateRightBtn = findViewById(R.id.rotate_right_button);

        setupPhotoEditor();

    }

    private void setupPhotoEditor() {
        brushBtn.setOnClickListener(v -> {
            mPhotoEditor.setBrushDrawingMode(true);
            // showBottomSheetDialogFragment(mPropertiesBSFragment);
            Toast.makeText(this, "Brush", Toast.LENGTH_SHORT).show();
            Log.d("Editor", "setupPhotoEditor: Brush");
        });
        filterBtn.setOnClickListener(v -> {
            showFilter(true);
            Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show();
            Log.d("Editor", "setupPhotoEditor: Bright");

        });




    }

    void showFilter(boolean isVisible) {
        mIsFilterVisible = isVisible;
        mConstraintSet.clone(mRootView);

        if (isVisible) {
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START);
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
        } else {
            mConstraintSet.connect(mRvFilters.getId(), ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.END);
            mConstraintSet.clear(mRvFilters.getId(), ConstraintSet.END);
        }

        ChangeBounds changeBounds = new ChangeBounds();
        changeBounds.setDuration(350);
        changeBounds.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        TransitionManager.beginDelayedTransition(mRootView, changeBounds);

        mConstraintSet.applyTo(mRootView);
    }

    private void showBottomSheetDialogFragment(BottomSheetDialogFragment fragment) {
        if (fragment == null || fragment.isAdded()) {
            return;
        }
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.editpicture_menu,menu);
        return true;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
}


