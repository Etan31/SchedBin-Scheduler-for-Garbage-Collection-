package com.example.a1project;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackType extends AppCompatActivity {
    Button backBtn2;
    Button suggestionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_type);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> finish());

        suggestionBtn = findViewById(R.id.suggestionBtn);
        suggestionBtn.setOnClickListener(v -> viewSuggestion());
    }

    private void viewSuggestion() {
        Intent intent = new Intent(this, suggestion_feedback.class);
        startActivity(intent);
    }


}