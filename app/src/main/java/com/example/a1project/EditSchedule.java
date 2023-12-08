package com.example.a1project;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditSchedule extends AppCompatActivity implements AdapterView.OnItemSelectedListener,DeleteDialogFragment.DeleteDialogListener {
    //      TODO: Fix the bug of the BackPressed, it should be redirected to SettingsFragment instead of Schedulefragment
    //      TODO: uses unchecked or unsafe operations.

    private Spinner garbageTypeSpinner;
    private Spinner repeatTimeSpinner;

    private DataSnapshot scheduleSnapshot;

    private TableLayout dataTableLayout;
    private LinearLayout linearLayoutInputs;

    private Button btnEditTimeFrom;
    private Button btnSelectTime_to;
    Button backBtn2;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        linearLayoutInputs = findViewById(R.id.linearLayout_inputs);

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> BackPressed());


        Button deleteButton = findViewById(R.id.btn_delete);

        initAndPopulateSpinners();

        garbageTypeSpinner = findViewById(R.id.spinner_typeofgarbage);
        repeatTimeSpinner = findViewById(R.id.spinner_doesNotRepeat);


        LinearLayout linearLayoutButtons = findViewById(R.id.linearLayout_buttons);
        Button updateButton = linearLayoutButtons.findViewById(R.id.btn_update);

        garbageTypeSpinner = findViewById(R.id.spinner_typeofgarbage);
        repeatTimeSpinner = findViewById(R.id.spinner_doesNotRepeat);

        TextInputLayout dateInputLayout = findViewById(R.id.layout_addSched_date);
        TextInputLayout addressInputLayout = findViewById(R.id.layout_addSched_address);

// Initialize views in the onCreate method
        AutoCompleteTextView dateAutoCompleteTextView = dateInputLayout.findViewById(R.id.addSched_date);
        AutoCompleteTextView addressAutoCompleteTextView = addressInputLayout.findViewById(R.id.addSched_address);

        garbageTypeSpinner = findViewById(R.id.spinner_typeofgarbage);
        repeatTimeSpinner = findViewById(R.id.spinner_doesNotRepeat);

        Button btnEditTimeFrom = findViewById(R.id.btnEdit_Time_from);
        Button btnEditTimeTo = findViewById(R.id.btnEdit_Time_to);

        btnEditTimeFrom.setOnClickListener(v -> showTimePickerDialog_from_To(btnEditTimeFrom));
        btnEditTimeTo.setOnClickListener(v -> showTimePickerDialog_from_To(btnEditTimeTo));




        // Set up the OnClickListener for the Update Button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated values from input fields and spinners
                String updatedDate = dateAutoCompleteTextView.getText().toString();
                String updatedAddress = addressAutoCompleteTextView.getText().toString();
                String updatedGarbageType = garbageTypeSpinner.getSelectedItem().toString();
                String updatedRepeatType = repeatTimeSpinner.getSelectedItem().toString();

                // Update the data in the Firebase database
                // Use the unique key of the clicked row to identify and update the data
                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("schedules")
                        .child(Objects.requireNonNull(scheduleSnapshot.getKey()));

                updateRef.child("date").setValue(updatedDate);
                updateRef.child("address").setValue(updatedAddress);
                updateRef.child("garbageType").setValue(updatedGarbageType);
                updateRef.child("repeatType").setValue(updatedRepeatType);

                // Display a toast message for successful update
                showToast("Row updated successfully");
                linearLayoutInputs.setVisibility(View.GONE);
            }
        });



        calendar = Calendar.getInstance();
        dateAutoCompleteTextView.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this, (view, year1, month1, day1) -> {
                // Display the selected date in the format mm/dd/yyyy
                String selectedDate = String.format(Locale.US, "%02d/%02d/%d", month1 + 1, day1, year1);
                dateAutoCompleteTextView.setText(selectedDate);
            }, year, month, day);

            datePicker.show();
            Log.d("DatePicker", "Date picker dialog opened.");
        });


//       spinner for the scheduled places
        Spinner dropDownSpinnerForLocation = findViewById(R.id.DropDown_spinner_for_location);
        dropDownSpinnerForLocation.setOnItemSelectedListener(this);
        fetchFirebaseDataAndPopulateSpinner(dropDownSpinnerForLocation);

        updateTableWithFilteredData((String) dropDownSpinnerForLocation.getSelectedItem());


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

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String garbageType = snapshot.child("garbageType").getValue(String.class);
                    String repeatType = snapshot.child("repeatType").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String startTime = snapshot.child("startTime").getValue(String.class);
                    String endTime = snapshot.child("endTime").getValue(String.class);

                    // Check if the schedule's address matches the selected address
                    if (selectedAddress != null && selectedAddress.equals(address)) {
                        scheduleSnapshot = snapshot;
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
                                if (linearLayoutInputs.getVisibility() == View.GONE) {
                                    linearLayoutInputs.setVisibility(View.VISIBLE);

                                    // Set values from the clicked row to input fields and spinners
                                    dateAutoCompleteTextView.setText(date);
                                    addressAutoCompleteTextView.setText(address);
                                    setSpinnerSelection(garbageTypeSpinner, garbageType);
                                    setSpinnerSelection(repeatTimeSpinner, repeatType);

                                    // Set text for btnSelectTime_from and btnSelectTime_to
                                    btnEditTimeFrom.setText(startTime);
                                    btnEditTimeTo.setText(endTime);

                                    Toast.makeText(EditSchedule.this, "Start Time: " + startTime, Toast.LENGTH_SHORT).show();
                                }
                            }


                            private void setSpinnerSelection(Spinner spinner, String value) {
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
                                if (adapter != null) {
                                    int position = adapter.getPosition(value);
                                    if (position != -1) {
                                        spinner.setSelection(position);
                                    }
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the DeleteDialogFragment when the delete button is clicked
                showDeleteDialog();
            }

            private void showDeleteDialog() {
                DeleteDialogFragment deleteDialog = new DeleteDialogFragment();
                deleteDialog.show(getSupportFragmentManager(), "delete_dialog");
            }
        });
    }

    //for selecting time "from" and "to"
    public void showTimePickerDialog_from_To(final Button button) {
        LocalTime currentTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentTime = LocalTime.now();
        }
        int hour = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            hour = currentTime.getHour();
        }
        int minute = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            minute = currentTime.getMinute();
        }

        // Determine whether it's AM or PM
        String amPm;
        if (hour >= 12) {
            amPm = "PM";
            if (hour > 12) {
                hour -= 12;
            }
        } else {
            amPm = "AM";
            if (hour == 0) {
                hour = 12;
            }
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update the text on the button with the selected time
                        String time = String.format("%02d:%02d %s", (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12, minute, (hourOfDay < 12) ? "AM" : "PM");
                        button.setText(time);
                    }
                },
                hour,
                minute,
                false  // Set this to false to use 12-hour format
        );

        timePickerDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initAndPopulateSpinners() {
        // Initialize spinners
        Spinner spinnerTypeOfGarbage = findViewById(R.id.spinner_typeofgarbage);
        ArrayAdapter<CharSequence> typeOfGarbageAdapter = ArrayAdapter.createFromResource(this, R.array.garbageTypes, android.R.layout.simple_spinner_item);
        typeOfGarbageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfGarbage.setAdapter(typeOfGarbageAdapter);

        Spinner spinnerDoesNotRepeat = findViewById(R.id.spinner_doesNotRepeat);
        ArrayAdapter<CharSequence> doesNotRepeatAdapter = ArrayAdapter.createFromResource(this, R.array.doesNotRepeat_array, android.R.layout.simple_spinner_item);
        doesNotRepeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoesNotRepeat.setAdapter(doesNotRepeatAdapter);

        // Set OnItemSelectedListener for the spinners
        spinnerTypeOfGarbage.setOnItemSelectedListener(this);
        spinnerDoesNotRepeat.setOnItemSelectedListener(this);
    }

    private void BackPressed() {
        //                working but redirected to the schedule fragment instead of setting fragment
        Intent intent = new Intent(this, Admin_Home_activity.class);
        startActivity(intent);
        finish();

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


    private void updateTableWithFilteredData(String selectedAddress) {
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

        TextInputLayout dateInputLayout = findViewById(R.id.layout_addSched_date);
        TextInputLayout addressInputLayout = findViewById(R.id.layout_addSched_address);

        Button btnEditTimeFrom = findViewById(R.id.btnEdit_Time_from);
        Button btnEditTimeTo = findViewById(R.id.btnEdit_Time_to);

// Initialize views in the onCreate method
        AutoCompleteTextView dateAutoCompleteTextView = dateInputLayout.findViewById(R.id.addSched_date);
        AutoCompleteTextView addressAutoCompleteTextView = addressInputLayout.findViewById(R.id.addSched_address);

        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing data rows
                dataTableLayout.removeAllViews();

                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                    String date = scheduleSnapshot.child("date").getValue(String.class);
                    String garbageType = scheduleSnapshot.child("garbageType").getValue(String.class);
                    String repeatType = scheduleSnapshot.child("repeatType").getValue(String.class);
                    String address = scheduleSnapshot.child("address").getValue(String.class);
                    String startTime = scheduleSnapshot.child("startTime").getValue(String.class);
                    String endTime = scheduleSnapshot.child("endTime").getValue(String.class);

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

                                    // Set values from the clicked row to input fields and spinners
                                    dateAutoCompleteTextView.setText(date);
                                    addressAutoCompleteTextView.setText(address);
                                    setSpinnerSelection(garbageTypeSpinner, garbageType);
                                    setSpinnerSelection(repeatTimeSpinner, repeatType);


                                    // Set text for btnSelectTime_from and btnSelectTime_to
                                    btnEditTimeFrom.setText(startTime);
                                    btnEditTimeTo.setText(endTime);

                                    Toast.makeText(EditSchedule.this, "Start Time: " + startTime, Toast.LENGTH_SHORT).show();
                                }
                            }

                            private void setSpinnerSelection(Spinner spinner, String value) {
                                ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
                                if (adapter != null) {
                                    int position = adapter.getPosition(value);
                                    if (position != -1) {
                                        spinner.setSelection(position);
                                    }
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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get the selected address from the spinner
        String selectedAddress = (String) parent.getSelectedItem();

        // call the method with the selected address from the spinner
        updateTableWithFilteredData(selectedAddress);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDeleteConfirmed(boolean deleteThisEvent, boolean deleteThisAndFollowingEvents) {
        TextInputLayout dateInputLayout = findViewById(R.id.layout_addSched_date);
        TextInputLayout addressInputLayout = findViewById(R.id.layout_addSched_address);
        AutoCompleteTextView dateAutoCompleteTextView = dateInputLayout.findViewById(R.id.addSched_date);
        AutoCompleteTextView addressAutoCompleteTextView = addressInputLayout.findViewById(R.id.addSched_address);


        // Handle the delete confirmation for "This Event" radio button Checked
        if (deleteThisEvent) {

            String selectedDate = dateAutoCompleteTextView.getText().toString();
            String selectedAddress = addressAutoCompleteTextView.getText().toString();
            String selectedGarbageType = garbageTypeSpinner.getSelectedItem().toString();
            String selectedRepeatType = repeatTimeSpinner.getSelectedItem().toString();

            DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

            Query deleteQuery = schedulesRef.orderByChild("date").equalTo(selectedDate);

            deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Iterate through the results and find the matching schedule
                    for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                        String address = scheduleSnapshot.child("address").getValue(String.class);
                        String garbageType = scheduleSnapshot.child("garbageType").getValue(String.class);
                        String repeatType = scheduleSnapshot.child("repeatType").getValue(String.class);

                        // Check if the current schedule matches the selected values
                        if (selectedAddress.equals(address)
                                && selectedGarbageType.equals(garbageType)
                                && selectedRepeatType.equals(repeatType)) {
                            // Delete the matching schedule
                            scheduleSnapshot.getRef().removeValue();
                            break; // Assuming there's only one matching schedule
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });

            // Handle the delete confirmation for "This and following Events" radio button Checked
        }else if (deleteThisAndFollowingEvents) {


                // Get the current values from your input fields and spinners
                String selectedDate = dateAutoCompleteTextView.getText().toString();
                String selectedAddress = addressAutoCompleteTextView.getText().toString();
                String selectedGarbageType = garbageTypeSpinner.getSelectedItem().toString();
                String selectedRepeatType = repeatTimeSpinner.getSelectedItem().toString();

                DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

                schedulesRef.orderByChild("address").equalTo(selectedAddress)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Iterate through the results and delete matching schedules
                                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                                    String scheduleDate = scheduleSnapshot.child("date").getValue(String.class);
                                    String garbageType = scheduleSnapshot.child("garbageType").getValue(String.class);
                                    String repeatType = scheduleSnapshot.child("repeatType").getValue(String.class);

                                    // Check if the schedule is on or after the selected date
                                    if (compareDates(scheduleDate, selectedDate) >= 0
                                            && selectedGarbageType.equals(garbageType)
                                            && selectedRepeatType.equals(repeatType)) {
                                        // Delete the matching schedule
                                        scheduleSnapshot.getRef().removeValue();
                                    }
                                }
                            }

                            private int compareDates(String date1, String date2) {
                                // Assuming date format is "MM/dd/yyyy"
                                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                                try {
                                    Date d1 = sdf.parse(date1);
                                    Date d2 = sdf.parse(date2);

                                    if (d1 != null && d2 != null) {
                                        return d1.compareTo(d2);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                return 0;
                            }


                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle errors
                            }
                        });
            }
        //end of Delete this and following events

        }
        //end of delete function

    @Override
    public void onDeleteCancelled() {
    }
}