package com.example.a1project;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;

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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ScheduleFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    Button backBtn2;

    private CalendarView calendarView;
    private Map<String, String> dateGarbageTypeMap;
    private GridView calendarGridView;

    private MaterialCalendarView materialCalendarView;
    private HashSet<CalendarDay> scheduledDates_CalendarDay= new HashSet<>();
    private HashSet<String> scheduledDates = new HashSet<>();


    //for displaying schedule to the tablelayout
    private TableLayout tableLayout;

    private String selectedAddress;



    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public void startMotionLayoutTransition() {

    }

    private Context mContext; // Store the context


    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null; // Release the context when the fragment is detached
    }


    public interface OnFabClickListener {
        void onFabClick();
    }
    private OnFabClickListener fabClickListener;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScheduleFragment.
     */

    public static ScheduleFragment newInstance(String param1, String param2) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

        // Check if the parent activity implements the callback interface
        if (context instanceof OnFabClickListener) {
            fabClickListener = (OnFabClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFabClickListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);



        // Spinner
        Spinner dropDownSpinnerForLocation = view.findViewById(R.id.DropDown_spinner_for_location);

        // Set the listener
        dropDownSpinnerForLocation.setOnItemSelectedListener(this);

        // Fetch data from Firebase
        fetchFirebaseDataAndPopulateSpinner(dropDownSpinnerForLocation);

        // Set the listener
        dropDownSpinnerForLocation.setOnItemSelectedListener(this);
//        updateTableWithFilteredData();


        ////////////////////////////////
        TableLayout tableLayout = view.findViewById(R.id.schedule_tableLayout);
        TableLayout dataTableLayout = view.findViewById(R.id.data_table_layout);
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the fragment is still attached to a context
                if (!isAdded()) {
                    return;
                }
                if (mContext == null) {
                    return;
                }

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
                    TableRow dataRow = new TableRow(requireContext());



                    // Create TextViews for the data
                    TextView dateTextView = new TextView(mContext); // Use stored context
                    dateTextView.setText(date);
                    dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // Use layout weight to fill available space
                    dateTextView.setGravity(Gravity.START);
                    dateTextView.setPadding(10, 10, 5, 5);

                    TextView garbageTypeTextView = new TextView(requireContext());
                    garbageTypeTextView.setText(garbageType);
                    garbageTypeTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f )); // Use layout weight to fill available space
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


//        for displaying dot to the calendar view.
        materialCalendarView = view.findViewById(R.id.Admin_calendarView);

        // Step 1: Retrieve Schedules from Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference schedulesRefs = database.getReference("schedules");

        schedulesRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Step 2: Process Schedule Data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Extract date from schedule
                    String date = snapshot.child("date").getValue(String.class);

                    // Convert date string to CalendarDay object
                    CalendarDay calendarDay = convertStringToCalendarDay(date);
                    scheduledDates_CalendarDay.add(calendarDay);
                }

                // Step 3: Apply Decorator to MaterialCalendarView
                applyDecorator();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });

        return view;
    }


//    displaying List of Adress on the firebase realtime database to the spinner
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

                // Create an ArrayAdapter with the unique addresses
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        requireActivity(),
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

    //for the spinner
    // Implement the onItemSelected method
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // Get the selected address from the spinner
        selectedAddress = parent.getItemAtPosition(position).toString();

        // Update the table with the filtered data
        updateTableWithFilteredData();
    }

    private void updateTableWithFilteredData() {
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Check if the fragment is still attached to a context
                if (!isAdded()) {
                    return;
                }
                if (mContext == null) {
                    return;
                }

                TableLayout tableLayout = requireView().findViewById(R.id.schedule_tableLayout);
                TableLayout dataTableLayout = requireView().findViewById(R.id.data_table_layout);

                // Clear existing data rows
                dataTableLayout.removeAllViews();

                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
                    String date = scheduleSnapshot.child("date").getValue(String.class);
                    String garbageType = scheduleSnapshot.child("garbageType").getValue(String.class);
                    String address = scheduleSnapshot.child("address").getValue(String.class);

                    // Check if the schedule's address matches the selected address
                    if (selectedAddress != null && selectedAddress.equals(address)) {
                        // Create a new TableRow for the data entry
                        TableRow dataRow = new TableRow(requireContext());

                        // Create TextViews for the data
                        TextView dateTextView = new TextView(mContext); // Use stored context
                        dateTextView.setText(date);
                        dateTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        dateTextView.setGravity(Gravity.START);
                        dateTextView.setPadding(10, 10, 5, 5);

                        TextView garbageTypeTextView = new TextView(requireContext());
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
    public void onNothingSelected(AdapterView<?> parent) {
        // Handle the case when nothing is selected
    }



// Method to convert date string to CalendarDay object
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
        if (isAdded()) {
            materialCalendarView.addDecorator(new ScheduleDecorator(requireContext(), scheduledDates_CalendarDay));
        }
    }
}