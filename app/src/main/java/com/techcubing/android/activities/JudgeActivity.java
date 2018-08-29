package com.techcubing.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.techcubing.android.R;
import com.techcubing.android.util.ActiveState;
import com.techcubing.android.util.TimerListener;
import com.techcubing.proto.ScorecardProto;

public class JudgeActivity extends AppCompatActivity {
    private static final String TAG = "TCJudgeActivity";

    private TimerListener timerListener;
    private TextView timeTextView;
    private TextView statusCharacterTextView;

    private TimerListener.TimerCallback callback = new TimerListener.TimerCallback() {
        @Override
        public void onTimerUpdate(TimerListener.TimerState state) {
            timeTextView.setText(state.getTime());
            statusCharacterTextView.setText(String.valueOf(state.getStatusCharacter()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge);

        ScorecardProto.Scorecard scorecard =
                ActiveState.getActive(ActiveState.SCORECARD, this);
        timeTextView = findViewById(R.id.judge_time_text_view);
        statusCharacterTextView = findViewById(R.id.judge_status_character_text_view);

        Button button = findViewById(R.id.judge_release_button);
        button.setOnClickListener(view -> {
          startActivity(new Intent(this, ReleaseScorecardActivity.class));
        });

        timerListener = new TimerListener();
        timerListener.registerCallback(callback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        timerListener.start();
    }

    @Override
    protected void onStop() {
        timerListener.stop();
        super.onStop();
    }

}
