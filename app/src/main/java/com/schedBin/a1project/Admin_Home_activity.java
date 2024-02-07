package com.schedBin.a1project;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.schedBin.a1project.databinding.ActivityAdminHomeBinding;

public class Admin_Home_activity extends AppCompatActivity implements ScheduleFragment.OnFabClickListener  {

    ActivityAdminHomeBinding binding;
    Button activity_add_schedule;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new ScheduleFragment());


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
                .addToBackStack(null) // Add to the back stack
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