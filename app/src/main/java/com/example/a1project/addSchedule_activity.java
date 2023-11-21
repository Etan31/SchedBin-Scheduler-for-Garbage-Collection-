package com.example.a1project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a1project.adapter.PlaceAutoSuggestAdapter;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class addSchedule_activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Button backBtn2;
    private Calendar calendar;


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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("schedules");


        TextInputLayout dateInputLayout = findViewById(R.id.layout_addSched_date);
        AutoCompleteTextView dateAutoCompleteTextView = findViewById(R.id.addSched_date);
//        TextInputLayout timeTextInputLayout = findViewById(R.id.layout_addSched_time);
//        AutoCompleteTextView timeAutoCompleteTextView = findViewById(R.id.addSched_time);
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
        addScheduleButton.setOnClickListener(view -> {
            // Get values from input fields
            String date = dateAutoCompleteTextView.getText().toString();
//            String time = timeAutoCompleteTextView.getText().toString();
            String address = addressAutoCompleteTextView.getText().toString();
            String garbageType = spinner_type_of_garbage.getSelectedItem().toString();
            String repeatType = spinnerDoesNotRepeat.getSelectedItem().toString();

            // Create a unique key for the schedule
            String scheduleKey = databaseReference.push().getKey();

            // Create a Schedule object
            // Create Schedule without time
            Schedule schedule = new Schedule(date, address, garbageType, repeatType);


            // Save the schedule to Firebase
            databaseReference.child(scheduleKey).setValue(schedule);

             Toast.makeText(this, "Schedule added to Firebase", Toast.LENGTH_SHORT).show();
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

//        timeAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int hour = calendar.get(Calendar.HOUR);
//                int minute = calendar.get(Calendar.MINUTE);
//
//                TimePickerDialog timePicker = new TimePickerDialog(addSchedule_activity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        Calendar selectedTime = Calendar.getInstance();
//                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                        selectedTime.set(Calendar.MINUTE, minute);
//
//                        // Display the selected time in the 12-hour format with AM/PM
//                        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
//                        String selectedTimeStr = timeFormat.format(selectedTime.getTime());
//                        timeAutoCompleteTextView.setText(selectedTimeStr);
//                    }
//                }, hour, minute, false);
//
//                timePicker.show();
//            }
//        });
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
}