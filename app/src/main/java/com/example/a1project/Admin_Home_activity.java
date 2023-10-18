package com.example.a1project;

import static com.example.a1project.R.id.nav_calendar;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.a1project.databinding.ActivityAdminHomeBinding;

public class Admin_Home_activity extends AppCompatActivity {

    ActivityAdminHomeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ScheduleFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_calendar) {
                replaceFragment(new ScheduleFragment());
            } else if (item.getItemId() == R.id.nav_settings) {
                replaceFragment(new SettingsFragment());
            }

            return true;
        });

    }


    private void replaceFragment(androidx.fragment.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.BaseFrameLayout, fragment);
        fragmentTransaction.commit();
    }

}