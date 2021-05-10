package com.example.galleryproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import com.example.galleryproject.edit.EmojiBSFragment;
import com.example.galleryproject.edit.FileSaveHelper;
import com.example.galleryproject.edit.PropertiesBSFragment;
import com.example.galleryproject.edit.TextEditorDialogFragment;
import com.example.galleryproject.edit.filters.FilterListener;
import com.example.galleryproject.edit.filters.FilterViewAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.TextStyleBuilder;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class EditPic extends AppCompatActivity implements PropertiesBSFragment.Properties,
        EmojiBSFragment.EmojiListener,
        FilterListener {

    ImageButton cropBtn, filterBtn, toneBtn, brushBtn, eraserBtn, emojiBtn, textBtn, colorBtn, rotateLeftBtn, rotateRightBtn;
    PhotoEditor mPhotoEditor;
    private EmojiBSFragment mEmojiBSFragment;
    PropertiesBSFragment mPropertiesBSFragment;
    private PhotoEditorView mPhotoEditorView;
    private FileSaveHelper mSaveFileHelper;
    ConstraintSet mConstraintSet = new ConstraintSet();
    private RecyclerView mRvFilters;
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private boolean mIsFilterVisible;
    ConstraintLayout mRootView;
    Uri mSaveImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);


        Bundle data = getIntent().getExtras();
        Uri imageUri = data.getParcelable("imageUri");
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditorView.getSource().setImageURI(imageUri);

        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        mPropertiesBSFragment = new PropertiesBSFragment();
        mPropertiesBSFragment.setPropertiesChangeListener(this);

        mEmojiBSFragment = new EmojiBSFragment();
        mEmojiBSFragment.setEmojiListener(this);

        mRvFilters = findViewById(R.id.rvFilterView);
        LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRvFilters.setLayoutManager(llmFilters);
        mRvFilters.setAdapter(mFilterViewAdapter);
        mRootView = findViewById(R.id.rootView);

        //set up button
        cropBtn = findViewById(R.id.crop_button);
        filterBtn = findViewById(R.id.filter_button);
        toneBtn = findViewById(R.id.tonality_button);
        brushBtn = findViewById(R.id.brush_button);
        eraserBtn = findViewById(R.id.eraser_button);
        emojiBtn = findViewById(R.id.emoji_button);
        textBtn = findViewById(R.id.addtext_button);
        colorBtn = findViewById(R.id.color_button);
        rotateLeftBtn = findViewById(R.id.rotate_left_button);
        rotateRightBtn = findViewById(R.id.rotate_right_button);


        setupPhotoEditor();
        mSaveFileHelper = new FileSaveHelper(this);

        cropBtn.setOnClickListener(v -> {
            CropImage.activity(imageUri)
                    .start(this);
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.undo_opt:
                mPhotoEditor.undo();
                return true;
            case R.id.redo_opt:
                mPhotoEditor.redo();
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

        eraserBtn.setOnClickListener(v -> {
            mPhotoEditor.brushEraser();
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

        emojiBtn.setOnClickListener(v -> {
            showBottomSheetDialogFragment(mEmojiBSFragment);
        });

        filterBtn.setOnClickListener(v -> {
            showFilter(true);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of CropImageActivity
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Intent editIntent = new Intent(this, EditPic.class);
                Bundle cropData = new Bundle();
                cropData.putParcelable("imageUri", result.getUri());
                editIntent.putExtras(cropData);
                startActivity(editIntent);
                Toast.makeText(this, "Cropping successful", Toast.LENGTH_SHORT).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
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
                    SaveSettings saveSettings = new SaveSettings.Builder() //gì v?
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
        onBackPressed();
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

    @Override
    public void onEmojiClick(String emojiUnicode) {
        mPhotoEditor.addEmoji(emojiUnicode);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
        mPhotoEditor.setFilterEffect(photoFilter);
    }

    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
        }
        else {
            super.onBackPressed();
        }
    }
}