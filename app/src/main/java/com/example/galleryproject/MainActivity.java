package com.example.galleryproject;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.galleryproject.data.DefaultAlbum;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.ui.allpic.AllPicFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    private final int REQUEST_MANAGE_EXTERNAL_STORAGE_CODE = 3;
    String currentLanguage = "en";       //value
    String currentTheme = "Light";
    public MaterialToolbar toolbar;

    SharedPreferences defaultSharedPreferences;
    SharedPreferences favoriteSharedPreferences;

    public int viewMode = AllPicFragment.VIEW_MODE_ALL;
    public ArrayList<Media> mediaArrayList = new ArrayList<>();
    public ArrayList<Media> allMediaArrayList = new ArrayList<>();

    public ArrayList<DefaultAlbum> defaultAlbumArrayList = new ArrayList<>();
    public HashSet<String> favoriteMediaHashSet = new HashSet<>();
    public String curPassword;
    ActionBarDrawerToggle toggle;

    BottomNavigationView navView;
    NavHostFragment navHostFragment;

    @Override
    protected void onPostCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.toggle.onConfigurationChanged(newConfig);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.mainTopAppBar);
        toolbar.setNavigationOnClickListener(this);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.container_drawer);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        this.toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLanguage = defaultSharedPreferences.getString(getString(R.string.language_key), "en");               // get selected option from preference language_key
        setLocale(currentLanguage);

        // get all favorite media

        favoriteSharedPreferences = SharePreferenceHandler.getFavoriteSharePreferences(this);

        currentTheme = defaultSharedPreferences.getString(getString(R.string.theme_key), "Light");               // get selected option from preference theme
        switch (currentTheme) {
            case "Light": {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            }
            case "Dark": {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            }
        }


        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_allpic, R.id.navigation_allalbum, R.id.navigation_setting)
                .build();

        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        //request for all permission

        askingForPermission();


    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onResume() {
        super.onResume();
        //reload password
        curPassword = defaultSharedPreferences.getString(getString(R.string.pin_key), getString(R.string.default_pin_key));
        // get all favorite media
        SharePreferenceHandler.getAllDataFromSharedPreference(favoriteSharedPreferences, favoriteMediaHashSet);
        // get all data need to run app
        getAllDataSet();
    }


    @Override
    public void onBackPressed() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        List<Fragment> fragmentList = null;
        if (navHostFragment != null) {
            fragmentList = navHostFragment.getChildFragmentManager().getFragments();
            boolean handled;
            for (Fragment f : fragmentList) {
                if (f instanceof AllPicFragment) {
                    handled = ((AllPicFragment) f).onBackPressed();
                    if (handled) {
                        return;
                    }
                }
            }
        }
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void askingForPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent askPermission = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                try {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("The app need manage all file permission to run perfectly")
                            .setPositiveButton("OK", (dialog, which) -> startActivityForResult(askPermission, REQUEST_IMAGE_CAPTURE))
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
                } catch (IllegalAccessError e) {
                    Log.e("", "askingForPermission: ");
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("This permission must have to run app")
                            .setPositiveButton("OK", (dialog, which) -> requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_CODE))
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create().show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION}, REQUEST_READ_EXTERNAL_STORAGE_CODE);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_MANAGE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.slideshow_opt:
                Intent intentSlideShow = new Intent(this, SlideMediaActivity.class);
                Bundle data = new Bundle();
                data.putInt("mediaPos", 0);
                data.putBoolean("isSlideShow", true);
                intentSlideShow.putExtras(data);
                startActivity(intentSlideShow);

            case R.id.gotocam:
                dispatchTakePictureIntent();
                return true;
            case R.id.go_to_secure_album:
                View dialogView = LayoutInflater.from(this).inflate(R.layout.diglog_input, null);
                EditText passText = dialogView.findViewById(R.id.input_text);
                passText.setHint("Enter the secret (～o￣3￣)～");
                passText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                new MaterialAlertDialogBuilder(this)
                        .setTitle("Enter password")
                        .setIcon(R.drawable.ic_baseline_lock_24)
                        .setMessage("Password is required to enter this secure album =￣ω￣=")
                        .setView(dialogView)
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setPositiveButton("OK", (dialog, which) -> {
                            String inputPassword = passText.getText().toString();
                            if (inputPassword.compareTo(curPassword) == 0) {
                                Intent intent = new Intent(this, SecureAlbumActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getBaseContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                            }
                        }).create().show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocale(String localeName) {                                                      //set locale of current locale
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();                                                         //get resource of app
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
            Toast.makeText(MainActivity.this, "Can't use camera", Toast.LENGTH_SHORT).show();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void getAllDataSet() {
        this.allMediaArrayList.clear();
        this.defaultAlbumArrayList.clear();
        Media.getAllMediaUri(this, this.allMediaArrayList, this.defaultAlbumArrayList, this.favoriteMediaHashSet);

        this.mediaArrayList.clear();
        this.mediaArrayList.addAll(this.allMediaArrayList);
        Log.e("", "getAllDataSet: ");
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (item.getItemId() == R.id.drawer_favorite) {
            navView.setVisibility(View.GONE);
            if (navHostFragment != null) {
                Media.getFavorite(allMediaArrayList,mediaArrayList,favoriteMediaHashSet);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_allpic);
                viewMode = AllPicFragment.VIEW_MODE_FAV;
            }
        }
        else if (item.getItemId() == R.id.drawer_video) {
            navView.setVisibility(View.GONE);
            if (navHostFragment != null) {
                Media.getVideo(allMediaArrayList,mediaArrayList);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_allpic);
                viewMode = AllPicFragment.VIEW_MODE_VID;

            }
        }
        else if (item.getItemId() == R.id.drawer_image) {
            navView.setVisibility(View.GONE);
            if (navHostFragment != null) {
                Media.getImage(allMediaArrayList,mediaArrayList);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_allpic);
                viewMode = AllPicFragment.VIEW_MODE_IMG;
            }
        }
        else if (item.getItemId() == R.id.drawer_home) {
            navView.setVisibility(View.VISIBLE);
            if (navHostFragment != null) {
                mediaArrayList.clear();
                mediaArrayList.addAll(allMediaArrayList);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.navigation_allpic);
                viewMode = AllPicFragment.VIEW_MODE_ALL;
            }
        }
        return false;
    }
}