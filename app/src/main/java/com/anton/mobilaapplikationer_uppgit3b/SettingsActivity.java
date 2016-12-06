package com.anton.mobilaapplikationer_uppgit3b;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Anton on 2016-12-06.
 */

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
