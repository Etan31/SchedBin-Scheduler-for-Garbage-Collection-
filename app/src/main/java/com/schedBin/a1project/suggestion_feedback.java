package com.schedBin.a1project;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class suggestion_feedback extends AppCompatActivity {

    private Spinner featureSpinner;
    private TextInputEditText suggestionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_feedback);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize UI elements
        featureSpinner = findViewById(R.id.feature_spinner);
        suggestionEditText = findViewById(R.id.textInputLayout).findViewById(R.id.textInputEditText); // Corrected reference
        Button sendBtn = findViewById(R.id.sendBtn);

        // Set up the Spinner with an ArrayAdapter (you need to define @array/features in your strings.xml)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.features, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        featureSpinner.setAdapter(adapter);

        // Set up the click listener for the "Send" button
        sendBtn.setOnClickListener(v -> sendDataToFirebase());

        // Optional: Set up a back button listener
        Button backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> finish());
    }

//    This send data from the database, Datas to be sent are text from spinner, and Generated Id, and user feedbacks
    private long lastFeedbackTimestamp = 0; // Variable to store the timestamp of the last feedback

    private void sendDataToFirebase() {
        long currentTimestamp = System.currentTimeMillis();

        // Check if enough time has passed since the last feedback
        if (currentTimestamp - lastFeedbackTimestamp < 60000) { // 60000 milliseconds = 1 minute
            // If less than 1 minute has passed, show a message and do not proceed
            Toast.makeText(this, "Please wait before sending another feedback.", Toast.LENGTH_SHORT).show();
            return;
        }

        // If enough time has passed, proceed to send feedback
        Log.d("SendDataToFirebase", "Before creating ProgressDialog");
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending feedback...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String selectedFeature = featureSpinner.getSelectedItem().toString();
        String userSuggestion = suggestionEditText.getText().toString();

        if (!TextUtils.isEmpty(userSuggestion)) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("feedback");
            String feedbackId = databaseReference.push().getKey();

            Feedback feedback = new Feedback(feedbackId, selectedFeature, userSuggestion);
            databaseReference.child(feedbackId).setValue(feedback);

            lastFeedbackTimestamp = currentTimestamp; // Update the timestamp of the last feedback

            clearForms();
            progressDialog.dismiss(); // Dismiss the progress dialog
            Toast.makeText(this, "Feedback sent, Thank you!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.dismiss(); // Dismiss the progress dialog in case of empty suggestion
            Toast.makeText(this, "Please enter your suggestion", Toast.LENGTH_SHORT).show();
        }
        Log.d("SendDataToFirebase", "After dismissing ProgressDialog");
    }



    private void clearForms() {
        // Clear the selected item in the spinner
        featureSpinner.setSelection(0);

        // Clear the text in the suggestionEditText
        suggestionEditText.setText("");
    }
}
