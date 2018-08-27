package com.techcubing.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.proto.ScorecardProto;

public class JudgeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge);

        TextView textView = findViewById(R.id.judge_text_view);

        ScorecardProto.Scorecard scorecard =
                ActiveState.getActive(ActiveState.SCORECARD, this);
        textView.setText("Judge " + scorecard.getId());

        Button button = findViewById(R.id.judge_release_button);
        button.setOnClickListener(view -> {
          startActivity(new Intent(this, ReleaseScorecardActivity.class));
        });
    }

}
