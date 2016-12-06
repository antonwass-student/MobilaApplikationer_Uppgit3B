package com.anton.mobilaapplikationer_uppgit3b;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Anton on 2016-12-06.
 */

public class SettingsFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
