package com.techcubing.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.proto.ScorecardProto;

public class ScrambleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scramble);

        ScorecardProto.Scorecard scorecard =
                ActiveState.getActive(ActiveState.SCORECARD, this);
        TextView textView = findViewById(R.id.scramble_text_view);
        textView.setText("Scramble " + scorecard.getId());

        Button button = findViewById(R.id.scramble_check_button);
        button.setOnClickListener(view -> {
          startActivity(new Intent(this, ReleaseScorecardActivity.class));
        });
    }

}
