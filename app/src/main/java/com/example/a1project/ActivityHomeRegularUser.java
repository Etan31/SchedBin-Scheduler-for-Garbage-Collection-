package com.example.a1project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

public class ActivityHomeRegularUser extends AppCompatActivity {

  ActivityHomeRegularUser binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_regular_user);
    }
}