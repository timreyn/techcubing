package com.techcubing.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.techcubing.android.util.SharedPreferenceKeys;
import com.techcubing.proto.DeviceTypeProto.DeviceType;


public class AcquireScorecardActivity extends AppCompatActivity {
    private static final String TAG = "TCAcquireScorecard";
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.toUri(0));
        String scorecardId = uri.getPathSegments().get(0);
        Log.i(TAG, "Acquiring " + scorecardId);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(SharedPreferenceKeys.SCORECARD_ID, scorecardId).commit();

        DeviceType deviceType = DeviceType.forNumber(
                sharedPreferences.getInt(SharedPreferenceKeys.DEVICE_TYPE, 0));
        switch (deviceType) {
            case JUDGE:
                startActivity(new Intent(this, JudgeActivity.class));
            case SCRAMBLER:
                startActivity(new Intent(this, ScrambleActivity.class));
        }
    }
}
