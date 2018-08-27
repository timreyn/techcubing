package com.techcubing.android.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import com.techcubing.android.R;

import com.techcubing.android.util.SharedPreferenceKeys;

public class JudgeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge);

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        TextView textView = (TextView) findViewById(R.id.judge_text_view);
        textView.setText("Judge " + sharedPreferences.getString(SharedPreferenceKeys.SCORECARD_ID, ""));

        Button button = (Button) findViewById(R.id.judge_release_button);
        button.setOnClickListener(view -> {
          startActivity(new Intent(this, ReleaseScorecardActivity.class));
        });
    }

}
