package com.example.a1project;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class FeedbackType extends AppCompatActivity {
    Button backBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_type);

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> finish());
    }


}