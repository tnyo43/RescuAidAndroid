package com.example.haruka.rescue_aid.activities;

import android.os.Bundle;

import com.example.haruka.rescue_aid.R;

public class MyPreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

}
