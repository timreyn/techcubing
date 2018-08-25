package com.techcubing.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.techcubing.android.util.SharedPreferenceKeys;


public class ReleaseScorecardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().remove(SharedPreferenceKeys.SCORECARD_ID).commit();
        startActivity(new Intent(this, LobbyActivity.class));
    }
}
