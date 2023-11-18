package com.example.a1project;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.a1project.databinding.ActivityAdminHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditSchedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TableLayout dataTableLayout;
    private LinearLayout linearLayoutInputs;
    Button backBtn2;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        mAuth = FirebaseAuth.getInstance();
        linearLayoutInputs = findViewById(R.id.linearLayout_inputs);

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> BackPressed());


//       spinner for the scheduled places
        Spinner dropDownSpinnerForLocation = findViewById(R.id.DropDown_spinner_for_location);
        dropDownSpinnerForLocation.setOnItemSelectedListener(this);
        fetchFirebaseDataAndPopulateSpinner(dropDownSpinnerForLocation);


//        for the displaying of the schedule to the table
        dataTableLayout = findViewById(R.id.data_table_layout);
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing data rows
                dataTableLayout.removeAllViews();

                // Get the selected address from the spinner
                String selectedAddress = (String) dropDownSpinnerForLocation.getSelectedItem();

                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                    String date = scheduleSnapshot.child("date").getValue(String.class);
                    String garbageType = scheduleSnapshot.child("garbageType").getValue(String.class);
                    String address = scheduleSnapshot.child("address").getValue(String.class);

                    // Check if the schedule's address matches the selected address
                    if (selectedAddress != null && selectedAddress.equals(address)) {
                        // Create a new TableRow for the data entry
                        TableRow dataRow = new TableRow(EditSchedule.this); // Use the activity context

                        // Create TextViews for the data
                        TextView dateTextView = new TextView(EditSchedule.this); // Use the activity context
                        dateTextView.setText(date);
                        dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        dateTextView.setGravity(Gravity.START);
                        dateTextView.setPadding(10, 10, 5, 10);

                        TextView garbageTypeTextView = new TextView(EditSchedule.this); // Use the activity context
                        garbageTypeTextView.setText(garbageType);
                        garbageTypeTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        garbageTypeTextView.setGravity(Gravity.START);
                        garbageTypeTextView.setPadding(10, 10, 5, 10);

                        // Add the TextViews to the dataRow
                        dataRow.addView(dateTextView);
                        dataRow.addView(garbageTypeTextView);

                // Add an OnClickListener to the dataRow
                dataRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toggle the visibility of linearLayout_inputs
                        if (linearLayoutInputs.getVisibility() == View.VISIBLE) {
                            linearLayoutInputs.setVisibility(View.GONE);
                        } else {
                            linearLayoutInputs.setVisibility(View.VISIBLE);
                        }
                    }
                });

                // Add the dataRow to the dataTableLayout (inside the ScrollView)
                dataTableLayout.addView(dataRow);
            }
        }
    }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void BackPressed() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));

        } else {
            // User is signed in
            ActivityAdminHomeBinding binding;
            binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            replaceFragment(new SettingsFragment());
        }
    }

    private void replaceFragment(androidx.fragment.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.BaseFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    //    displaying List of Address on the firebase realtime database to the spinner
    private void fetchFirebaseDataAndPopulateSpinner(Spinner spinner) {
        // Assuming you have a reference to your Firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("schedules");

        // Listen for changes in the data
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> uniqueAddresses = new ArrayList<>();

                // Iterate through the data and extract unique addresses
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String address = snapshot.child("address").getValue(String.class);

                    // Check if the address is not already in the list
                    if (address != null && !uniqueAddresses.contains(address)) {
                        uniqueAddresses.add(address);
                    }
                }
                if (uniqueAddresses.isEmpty()) {
                    uniqueAddresses.add("No Schedule");
                }

                // Create an ArrayAdapter with the unique addresses
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        EditSchedule.this,
                        android.R.layout.simple_spinner_item,
                        uniqueAddresses
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}