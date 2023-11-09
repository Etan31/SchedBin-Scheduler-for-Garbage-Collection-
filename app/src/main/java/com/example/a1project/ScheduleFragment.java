package com.example.a1project;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
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

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ScheduleFragment extends Fragment {
    private CalendarView calendarView;
    private Map<String, String> dateGarbageTypeMap;

    //for displaying schedule to the tablelayout
    private TableLayout tableLayout;



    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public void startMotionLayoutTransition() {
//        MotionLayout motionLayout = requireView().findViewById(R.id.motionLayout);
//        motionLayout.transitionToEnd(); // Start the animation

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




        ////////////////////////////////
        TableLayout tableLayout = view.findViewById(R.id.schedule_tableLayout);
        TableLayout dataTableLayout = view.findViewById(R.id.data_table_layout);
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules");

        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear existing data rows
                dataTableLayout.removeAllViews();

                for (DataSnapshot scheduleSnapshot : dataSnapshot.getChildren()) {
//                    String address = scheduleSnapshot.child("address").getValue(String.class);
                    String date = scheduleSnapshot.child("date").getValue(String.class);
                    String garbageType = scheduleSnapshot.child("garbageType").getValue(String.class);

                    // Create a new TableRow for the data entry
                    TableRow dataRow = new TableRow(requireContext());

                    // Create TextViews for the data
                    TextView dateTextView = new TextView(requireContext());
                    dateTextView.setText(date);
                    dateTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT));
                    dateTextView.setGravity(Gravity.START);

//                    TextView addressTextView = new TextView(requireContext());
//                    addressTextView.setText(address);
//                    addressTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
//                    addressTextView.setGravity(Gravity.START);

                    TextView garbageTypeTextView = new TextView(requireContext());
                    garbageTypeTextView.setText(garbageType);
                    garbageTypeTextView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    garbageTypeTextView.setGravity(Gravity.START);

                    // Add the TextViews to the dataRow
                    dataRow.addView(dateTextView);
//                    dataRow.addView(addressTextView);
                    dataRow.addView(garbageTypeTextView);

                    // Add the dataRow to the dataTableLayout (inside the ScrollView)
                    dataTableLayout.addView(dataRow);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
            }
        });

        return view;
    }

}