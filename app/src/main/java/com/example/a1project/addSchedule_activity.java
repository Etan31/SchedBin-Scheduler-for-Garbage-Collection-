package com.example.a1project;
import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a1project.adapter.PlaceAutoSuggestAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class addSchedule_activity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener, DeleteDialogFragment.DeleteDialogListener {
    // TODO: will add first the data to the database before showing the Toast message

    Button backBtn2;
    private Calendar calendar;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        //for accessing the Address with PlaceAPI autocomplete
        TextInputLayout addressTextInputLayout = findViewById(R.id.layout_addSched_address);
        AutoCompleteTextView autoCompleteTextView = addressTextInputLayout.findViewById(R.id.addSched_address);

        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1));



        //for storing schedules to firebase
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("schedules");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("schedules");


        TextInputLayout dateInputLayout = findViewById(R.id.layout_addSched_date);
        AutoCompleteTextView dateAutoCompleteTextView = findViewById(R.id.addSched_date);
        AutoCompleteTextView addressAutoCompleteTextView = findViewById(R.id.addSched_address);
        Button addScheduleButton = findViewById(R.id.add_schedule_Btn);


        //end of line for storing schedules to firebase


        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> onBackPressed());

        //for displaying spinners
        Spinner spinner_type_of_garbage = findViewById(R.id.spinner_typeofgarbage);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.garbageTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type_of_garbage.setAdapter(adapter);
        spinner_type_of_garbage.setOnItemSelectedListener(this);


        Spinner spinnerDoesNotRepeat = findViewById(R.id.spinner_doesNotRepeat);
        ArrayAdapter<CharSequence> doesNotRepeatAdapter = ArrayAdapter.createFromResource(this, R.array.doesNotRepeat_array, android.R.layout.simple_spinner_item);
        doesNotRepeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoesNotRepeat.setAdapter(doesNotRepeatAdapter);
        spinnerDoesNotRepeat.setOnItemSelectedListener(this);
        //end of //for displaying spinners

        //for adding data to firebase
        //for the repeating schedule with the spinner

        //adding schedule with the repeat event
        addScheduleButton.setOnClickListener(view -> {
            // Get values from input fields
            String date = dateAutoCompleteTextView.getText().toString();
            String address = addressAutoCompleteTextView.getText().toString();
            String garbageType = spinner_type_of_garbage.getSelectedItem().toString();
            String repeatType = spinnerDoesNotRepeat.getSelectedItem().toString();

            // Create a unique key for the schedule
            String scheduleKey = databaseReference.push().getKey();

            // Add conditional checks for repeat type
            if (repeatType.equals("Does not Repeat")) {
                // Schedule does not repeat
                Schedule schedule = new Schedule(date, address, garbageType, repeatType);
                // Save the schedule to Firebase
                databaseReference.child(scheduleKey).setValue(schedule);
            } else {
                // Schedule repeats, handle different repeat types based on the selected date

                // Parse the selected date
                DateTimeFormatter formatter = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                }
                LocalDate selectedDate = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    selectedDate = LocalDate.parse(date, formatter);
                }

                // Create a base schedule without additional properties
                Schedule baseSchedule = new Schedule(date, address, garbageType, repeatType);

                // Add additional schedules based on repeatType
                if (repeatType.equals("Everyday")) {
                    // Generate schedules for every day
                    for (int i = 0; i < 365; i++) {
                        // Increment the date for each day
                        LocalDate repeatedDate = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            repeatedDate = selectedDate.plusDays(i);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            addScheduleToFirebase(repeatedDate.format(formatter), address, garbageType, repeatType);
                        }
                    }
                } else if (repeatType.equals("Every week")) {
                    // Generate schedules for every week
                    for (int i = 0; i < 52; i++) {
                        // Increment the date for each week (7 days)
                        LocalDate repeatedDate = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            repeatedDate = selectedDate.plusWeeks(i);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            addScheduleToFirebase(repeatedDate.format(formatter), address, garbageType, repeatType);
                        }
                    }
                } else if (repeatType.equals("Every month")) {
                    // Generate schedules for every month
                    for (int i = 0; i < 12; i++) {
                        // Increment the month for each month
                        LocalDate repeatedDate = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            repeatedDate = selectedDate.plusMonths(i);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            addScheduleToFirebase(repeatedDate.format(formatter), address, garbageType, repeatType);
                        }
                    }
                } else if (repeatType.equals("Every Year")) {
                    // Generate schedules for every year
                    for (int i = 0; i < 5; i++) {
                        // Increment the year for each year
                        LocalDate repeatedDate = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            repeatedDate = selectedDate.plusYears(i);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            addScheduleToFirebase(repeatedDate.format(formatter), address, garbageType, repeatType);
                        }
                    }
                }
            }

            deletePastSchedules();
            Toast.makeText(this, "Schedules added to Firebase", Toast.LENGTH_SHORT).show();
        });




        //for clicking the date there will be a calendar to choose date
        calendar = Calendar.getInstance();
        dateAutoCompleteTextView.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(addSchedule_activity.this, (view, year1, month1, day1) -> {
                // Display the selected date in the format mm/dd/yyyy
                String selectedDate = String.format(Locale.US, "%02d/%02d/%d", month1 + 1, day1, year1);
                dateAutoCompleteTextView.setText(selectedDate);
            }, year, month, day);

            datePicker.show();
            Log.d("DatePicker", "Date picker dialog opened.");
        });

    }

    // Helper method to add a schedule to Firebase
    private void addScheduleToFirebase(String date, String address, String garbageType, String repeatType) {
        Schedule repeatedSchedule = new Schedule(date, address, garbageType, repeatType);
        databaseReference.child(Objects.requireNonNull(databaseReference.push().getKey())).setValue(repeatedSchedule);
    }

    private void deletePastSchedules() {
        Calendar currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Schedule schedule = snapshot.getValue(Schedule.class);

                    // Parse the schedule date and compare with the current date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    try {
                        Date scheduleDate = dateFormat.parse(schedule.getDate());
                        if (scheduleDate != null && scheduleDate.before(currentDate)) {
                            // Schedule is in the past, delete it
                            snapshot.getRef().removeValue();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error deleting past schedules: " + databaseError.getMessage());
            }
        });
    }





    //for displaying spinners
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        String text = parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDeleteConfirmed(boolean deleteThisEvent, boolean deleteThisAndFollowingEvents) {
        // Handle the delete confirmation
        if (deleteThisEvent) {
            // Delete only this event
        } else if (deleteThisAndFollowingEvents) {
            // Delete this and following events
        }
        // Add your logic to perform the delete operation
    }

    @Override
    public void onDeleteCancelled() {
        // Handle the cancel action
        // This can be empty or include any specific behavior you want
        onBackPressed();
    }
}