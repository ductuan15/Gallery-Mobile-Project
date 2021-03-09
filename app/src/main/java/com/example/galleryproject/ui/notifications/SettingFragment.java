package com.example.galleryproject.ui.notifications;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.example.galleryproject.R;

public class SettingFragment extends PreferenceFragmentCompat {

    private SettingViewModel settingViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_setting,rootKey);
    }

}