package com.example.a1project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity2 extends AppCompatActivity {

    //        TODO: Fix the bug of the BackPressed, it should be redirected to SettingsFragment instead of Schedulefragment


    Button backBtn2;
    private FirebaseAuth mAuth;

    private TextInputEditText passwordEditText, emailEditText, addressEditText, nameEditText, houseNameEditText, numberEditText ;

    Button save_btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> BackPressed());

        TextInputLayout passwordLayout = findViewById(R.id.passwordInput_createAcount);
        TextInputLayout emailLayout = findViewById(R.id.emailInput_createaAcount);

        //Start of creating account to save in firebase
        passwordEditText = (TextInputEditText) passwordLayout.getEditText();
        emailEditText = (TextInputEditText) emailLayout.getEditText();

        save_btn2 = (Button) findViewById(R.id.save_btn2);
        save_btn2.setOnClickListener(view -> insertData());

    }

    private void BackPressed() {
        //                working but redirected to the schedule fragment instead of setting fragment
        Intent intent = new Intent(this, Admin_Home_activity.class);
        startActivity(intent);
        finish();

    }

    private void replaceFragment(androidx.fragment.app.Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.BaseFrameLayout, fragment);
        fragmentTransaction.commit();
    }


    //for inserting data to the firebase
    private void insertData() {
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String email = Objects.requireNonNull(emailEditText.getText()).toString();

        checkEmailExists(email, (emailExists) -> {
            if (emailExists) {
                Toast.makeText(MainActivity2.this, "This email is already in use. Please use a different email.", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email address");
            } else {


                //email verification
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser user = authResult.getUser();
                            if (user != null) {
                                // Send email verification link
                                user.sendEmailVerification()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Verification email sent
                                                Toast.makeText(MainActivity2.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Failed to send verification email
                                                Toast.makeText(MainActivity2.this, "Failed to send verification email. Please try again later.", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                String userId = user.getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin_users").child(userId);
                                Map<String, Object> userValues = new HashMap<>();
                                userValues.put("password", password);
                                userValues.put("email", email);
//                                    userValues.put("name", name);
//                                    userValues.put("address", address);
//                                    userValues.put("number", number);
//                                    userValues.put("houseName", houseName);

                                databaseReference.setValue(userValues)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(MainActivity2.this, "Success! Check your email for verification", Toast.LENGTH_SHORT).show();
                                            passwordEditText.setText("");
                                            emailEditText.setText("");
//                                                nameEditText.setText("");
//                                                addressEditText.setText("");
//                                                numberEditText.setText("");
//                                                houseNameEditText.setText("");
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_LONG).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    //End of inserting data to the firebase

    //for checking if the email is used
    private void checkEmailExists(String email, OnEmailCheckListener callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("regular_users");

        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean emailExists = dataSnapshot.exists();
                callback.onEmailCheck(emailExists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database errors if necessary
            }
        });
    }
    //end of checking if the email is used
}
