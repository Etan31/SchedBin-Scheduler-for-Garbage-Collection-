package com.example.a1project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

public class addSchedule_activity extends AppCompatActivity {

    Button backBtn2;

//    String[] item = {"Biodegradable", "Non-Biodegradable", "Recyclable", "Hazardous"};
//
//    AutoCompleteTextView autoCompleteTextView;
//    ArrayAdapter<String> adapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> finish());


//        autoCompleteTextView = findViewById(R.id.addSched_garbageType);
//        adapterItems = new ArrayAdapter<>(this, R.layout.list_item_type_of_garbage, item);
//
//        autoCompleteTextView.setAdapter(adapterItems);
//
//        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
//            String item = adapterView.getItemAtPosition(i).toString();
//            Toast.makeText(addSchedule_activity.this, "Item:" + item, Toast.LENGTH_SHORT).show();
//        });


    }
}   