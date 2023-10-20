package com.example.a1project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

//import com.example.a1project.databinding.ActivityAdminHomeBinding;
import com.example.a1project.databinding.ActivityAdminHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.constraintlayout.motion.widget.MotionLayout;

public class Admin_Home_activity extends AppCompatActivity implements ScheduleFragment.OnFabClickListener  {

    ActivityAdminHomeBinding binding;
    Button activity_add_schedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ScheduleFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_calendar) {
                replaceFragment(new ScheduleFragment());
            }else if (item.getItemId() == R.id.nav_settings) {
                replaceFragment(new SettingsFragment());
            }
            return true;
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        ScheduleFragment scheduleFragment = new ScheduleFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.add_schedule_id, scheduleFragment, "addSchedule_tag")
                .commit();

        Button addScheduleBtn = findViewById(R.id.add_schedule_Btn);
        addScheduleBtn.setOnClickListener(v -> viewSchedule_Activity());

    }

    private void viewSchedule_Activity() {
        Intent intent = new Intent(this, addSchedule_activity.class);
        startActivity(intent);
    }

    private void replaceFragment(androidx.fragment.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.BaseFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFabClick() {
        ScheduleFragment scheduleFragment = (ScheduleFragment) getSupportFragmentManager().findFragmentByTag("addSchedule_tag");
        if (scheduleFragment != null) {
            scheduleFragment.startMotionLayoutTransition();
        }
    }

    public void AddSchedule_Activity(){
        Intent intent = new Intent(this, addSchedule_activity.class);
        startActivity(intent);
    }
}