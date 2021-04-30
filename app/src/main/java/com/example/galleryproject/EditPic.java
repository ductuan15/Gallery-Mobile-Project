package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.galleryproject.edit.FileSaveHelper;
import com.example.galleryproject.edit.PropertiesBSFragment;
import com.example.galleryproject.edit.TextEditorDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditPic extends AppCompatActivity implements PropertiesBSFragment.Properties {

    ImageButton cropBtn, filterBtn, toneBtn, brushBtn, emojiBtn, textBtn, brightBtn, colorBtn, rotateLeftBtn, rotateRightBtn;
    PhotoEditor mPhotoEditor;
    PropertiesBSFragment mPropertiesBSFragment;
    private PhotoEditorView mPhotoEditorView;
    private FileSaveHelper mSaveFileHelper;
    ConstraintSet mConstraintSet = new ConstraintSet();
    boolean mIsFilterVisible;
    RecyclerView mRvFilters;
    ConstraintLayout mRootView;
    Uri mSaveImageUri;
    public static final String ACTION_NEXTGEN_EDIT = "action_nextgen_edit";

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

        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.undo_opt:

                return true;
            case R.id.redo_opt:

                return true;
            case R.id.done_opt:
                saveImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupPhotoEditor() {
        brushBtn.setOnClickListener(v -> {
            mPhotoEditor.setBrushDrawingMode(true);
            showBottomSheetDialogFragment(mPropertiesBSFragment);                                   //show fragment custom brush
        });
        textBtn.setOnClickListener(v -> {
            TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
            textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
                @Override
                public void onDone(String inputText, int colorCode) {
                    final TextStyleBuilder styleBuilder = new TextStyleBuilder();
                    styleBuilder.withTextColor(colorCode);
                    mPhotoEditor.addText(inputText, styleBuilder);
                }
            });
        });


    }

    public static boolean isSdkHigherThan28() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q);
    }

    private void saveImage() {
        final String fileName = System.currentTimeMillis() + ".png";
        final boolean hasStoragePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED;
        if(hasStoragePermission || isSdkHigherThan28()) {
            mSaveFileHelper.createFile(fileName, (fileCreated, filePath, error, uri) -> {
                if (fileCreated) {
                    SaveSettings saveSettings = new SaveSettings.Builder()
                            .setClearViewsEnabled(true)
                            .setTransparencyEnabled(true)
                            .build();

                    mPhotoEditor.saveAsFile(filePath, saveSettings, new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath) {
                            mSaveFileHelper.notifyThatFileIsNowPubliclyAvailable(getContentResolver());
                            Toast.makeText(EditPic.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
                            mSaveImageUri = uri;
                            mPhotoEditorView.getSource().setImageURI(mSaveImageUri);
                        }
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(EditPic.this, "Failed to save Image", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(EditPic.this, error, Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    @Override
    public void onColorChanged(int colorCode) {
        mPhotoEditor.setBrushColor(colorCode);
    }

    @Override
    public void onOpacityChanged(int opacity) {
        mPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushSizeChanged(int brushSize) {
        mPhotoEditor.setBrushSize(brushSize);
    }
}


