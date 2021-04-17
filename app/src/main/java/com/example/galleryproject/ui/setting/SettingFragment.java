package com.example.galleryproject.ui.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Toast;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.galleryproject.MainActivity;
import com.example.galleryproject.R;

import java.util.Locale;

public class SettingFragment extends PreferenceFragmentCompat {

    private SettingViewModel settingViewModel;
    private ListPreference list_theme;
    private ListPreference list_lang;

    SharedPreferences preferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_setting, rootKey);

        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());              //get preference

        list_theme = (ListPreference) findPreference("theme_selection");
        list_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Intent refresh = new Intent(requireContext(), MainActivity.class);                  //use intent to restart activity
                startActivity(refresh);
                requireActivity().finish();
                return true;
            }
        });

        list_lang = (ListPreference) findPreference("language_selection");                     //get ListPreference

        list_lang.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {       //handle listener
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Intent refresh = new Intent(requireContext(), MainActivity.class);                  //use intent to restart activity
                startActivity(refresh);
                requireActivity().finish();
                return true;
            }
        });
    }


}