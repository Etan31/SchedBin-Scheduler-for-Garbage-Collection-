package com.example.a1project;

import static com.example.a1project.R.id.nav_calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.a1project.databinding.ActivityAdminHomeBinding;

public class Admin_Home_activity extends AppCompatActivity {

    ActivityAdminHomeBinding binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

//            switch (item.getItemId()) {
//
//                case nav_calendar:
//                    replaceFragment(new ScheduleFragment());
//                    break;
//                case R.id.nav_settings:
//                    replaceFragment(new SettingsFragment());
//                    break;
//            }

            return true;
        });
    }


    private void replaceFragment(androidx.fragment.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}