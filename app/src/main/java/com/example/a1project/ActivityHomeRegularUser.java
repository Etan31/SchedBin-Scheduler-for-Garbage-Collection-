package com.example.a1project;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ActivityHomeRegularUser extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private CalendarView calendarView;
    private Map<String, String> dateGarbageTypeMap;
    private GridView calendarGridView;

    Button backBtn2;

    private Context mContext;
    private Spinner dropDownSpinnerForLocation;
    private TableLayout dataTableLayout;
    private MaterialCalendarView materialCalendarView;

    private HashSet<CalendarDay> scheduledDates_CalendarDay = new HashSet<>();
    private HashSet<String> scheduledDates = new HashSet<>();

    // For displaying schedule to the tablelayout
    private TableLayout tableLayout;

    private String selectedAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_regular_user); // Replace with your activity layout

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mContext = this;
        Context context = this;

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> finish());


        // Spinner
        dropDownSpinnerForLocation = findViewById(R.id.DropDown_spinner_for_location);

        // Set the listener
        dropDownSpinnerForLocation.setOnItemSelectedListener(this);

        // Fetch data from Firebase
        fetchFirebaseDataAndPopulateSpinner(dropDownSpinnerForLocation);

        // TableLayout
        dataTableLayout = findViewById(R.id.data_table_layout);

        // CalendarView
        materialCalendarView = findViewById(R.id.Admin_calendarView);

        // Fetch and decorate calendar
        fetchAndDecorateCalendar();
    }

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
                        ActivityHomeRegularUser.this, // Use 'this' or 'requireActivity()' depending on the context
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

    private void fetchAndDecorateCalendar() {
        DatabaseReference schedulesRefs = FirebaseDatabase.getInstance().getReference("schedules");

        schedulesRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scheduledDates_CalendarDay.clear(); // Clear the existing data

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extract date from schedule
                    String date = snapshot.child("date").getValue(String.class);

                    // Convert date string to CalendarDay object
                    CalendarDay calendarDay = convertStringToCalendarDay(date);
                    scheduledDates_CalendarDay.add(calendarDay);
                }

                // Apply Decorator to MaterialCalendarView
                applyDecorator();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });
    }



    private void updateTableWithFilteredData() {
        if ( mContext == null) {
            return; // Fragment is not attached, do nothing
        }
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the fragment is still attached to a context
                if (mContext == null) {
                    return;
                }

//                TableLayout tableLayout = requireView().findViewById(R.id.schedule_tableLayout);
                TableLayout dataTableLayout = findViewById(R.id.data_table_layout);

                // Clear existing data rows
                dataTableLayout.removeAllViews();


                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                    String date = scheduleSnapshot.child("date").getValue(String.class);
                    String garbageType = scheduleSnapshot.child("garbageType").getValue(String.class);
                    String address = scheduleSnapshot.child("address").getValue(String.class);

                    // Check if the schedule's address matches the selected address
                    if (selectedAddress != null && selectedAddress.equals(address)) {
                        // Create a new TableRow for the data entry
                        TableRow dataRow = new TableRow(mContext);

                        // Create TextViews for the data
                        TextView dateTextView = new TextView(mContext); // Use stored context
                        dateTextView.setText(date);
                        dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        dateTextView.setGravity(Gravity.START);
                        dateTextView.setPadding(10, 10, 5, 5);

                        TextView garbageTypeTextView = new TextView(mContext);
                        garbageTypeTextView.setText(garbageType);
                        garbageTypeTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        garbageTypeTextView.setGravity(Gravity.START);
                        garbageTypeTextView.setPadding(10, 10, 5, 5);

                        // Add the TextViews to the dataRow
                        dataRow.addView(dateTextView);
                        dataRow.addView(garbageTypeTextView);

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
        selectedAddress = parent.getItemAtPosition(position).toString();

        // Update the table with the filtered data
        updateTableWithFilteredData();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle case where nothing is selected in the spinner
    }

    private CalendarDay convertStringToCalendarDay(String date) {
        if (TextUtils.isEmpty(date)) {
            // Handle the case where the date string is empty
            return null;
        }

        // Convert date string to Calendar object
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        // Extract day, month, and year
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return CalendarDay.from(year, month + 1, day);
    }


    // Method to apply decorator to MaterialCalendarView
    private void applyDecorator() {
        // Create a decorator and add it to the MaterialCalendarView
        materialCalendarView.addDecorator(new ScheduleDecorator(this, scheduledDates_CalendarDay));
    }

}