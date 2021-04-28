package com.example.galleryproject;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.galleryproject.data.Album;
import com.example.galleryproject.data.Media;
import com.example.galleryproject.ui.allpic.AllPicFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_READ_EXTERNAL_STORAGE_CODE = 1;
    static final int REQUEST_IMAGE_CAPTURE = 2;
    String currentLanguage = "en";       //value
    String currentTheme = "Light";
    Locale myLocale;
    String currentLang;                 //key intent
    SharedPreferences preferences;
    boolean gettedData = false;


    public ArrayList<Media> mediaArrayList = new ArrayList<>();
    public ArrayList<Album> albumArrayList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLanguage = preferences.getString(getString(R.string.language_key), "en");               // get selected option from preference language_key
        setLocale(currentLanguage);

        ActionBar actionBar = getSupportActionBar();                                                            //change color for actionbar
        ColorDrawable colorDrawable;

        currentTheme = preferences.getString(getString(R.string.theme_key), "Light");               // get selected option from preference theme
        switch (currentTheme) {
            case "Light": {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                break;
            }
            case "Dark": {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                colorDrawable = new ColorDrawable(Color.parseColor("#0F9D58"));
                actionBar.setBackgroundDrawable(colorDrawable);
                break;
            }
        }

        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_allpic, R.id.navigation_allalbum, R.id.navigation_setting)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //request for all permission
        askingForPermission();


    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onResume() {
        super.onResume();
        // get all data need to run app
        getAllDataSet();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                getAllDataSet();
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        new Thread(runnable).start();
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

                return true;
            case R.id.selectall:

                return true;
            case R.id.gotocam:
                dispatchTakePictureIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocale(String localeName) {                                                      //set locale of current locale
//        if (!localeName.equals(currentLanguage)) {
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


    @RequiresApi(api = Build.VERSION_CODES.R)
    private void getAllDataSet() {
        this.mediaArrayList.clear();
        this.albumArrayList.clear();
        Media.getAllMediaUri(this, this.mediaArrayList, this.albumArrayList);
        Log.e("", "getAllDataSet: ");
    }
}