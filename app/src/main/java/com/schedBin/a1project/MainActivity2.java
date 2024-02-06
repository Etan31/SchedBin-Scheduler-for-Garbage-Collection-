package com.schedBin.a1project;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
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
    private DatabaseReference databaseReference;

    private TextInputEditText passwordEditText, emailEditText;

    Button save_btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("schedules");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("schedules");


//        backBtn2 = findViewById(R.id.backBtn2);
//        backBtn2.setOnClickListener(v -> BackPressed());

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
//    private void sendVerificationEmail(String targetEmail) {
//        // You can customize the verification link or code generation logic here
//        String verificationCode = generateVerificationCode();
//
//        // Send the verification code to the specified email address
//        sendVerificationCodeByEmail(targetEmail, verificationCode);
//    }

//    private String generateVerificationCode() {
//        // Implement your own logic to generate a verification code (e.g., a random string or number)
//        // For demonstration purposes, let's assume a simple 6-digit numeric code
//        return String.valueOf((int) ((Math.random() * 900000) + 100000));
//    }
//    private void sendVerificationCodeByEmail(String targetEmail, String verificationCode) {
//        // You need to implement your own logic to send the verification code via email
//        // This could involve using an email sending service or sending an HTTP request to a server
//
//        // For demonstration purposes, let's assume you have a method to send an email
//        sendEmail(targetEmail, "Verification Code", "Your verification code is: " + verificationCode);
//    }
    private void sendEmail(String toEmail, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmail});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainActivity2.this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }



    //for inserting data to the firebase
//    private void insertData() {
//        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
//        String email = Objects.requireNonNull(emailEditText.getText()).toString();
//
//        checkEmailExists(email, (emailExists) -> {
//            if (emailExists) {
//                Toast.makeText(MainActivity2.this, "This email is already in use. Please use a different email.", Toast.LENGTH_SHORT).show();
//            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                emailEditText.setError("Please enter a valid email address");
//            } else {
//
//
//                //email verification
//                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//                        .addOnSuccessListener(authResult -> {
//                            FirebaseUser user = authResult.getUser();
//                            if (user != null) {
//                                // Send email verification link
//                                user.sendEmailVerification()
//                                        .addOnCompleteListener(task -> {
//                                            if (task.isSuccessful()) {
//                                                // Verification email sent
//                                                Toast.makeText(MainActivity2.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                // Failed to send verification email
//                                                Toast.makeText(MainActivity2.this, "Failed to send verification email. Please try again later.", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//                                String userId = user.getUid();
////                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin_users").child(userId);
//                                Map<String, Object> userValues = new HashMap<>();
//                                userValues.put("password", password);
//                                userValues.put("email", email);
//
//                                databaseReference.setValue(userValues)
//                                        .addOnSuccessListener(unused -> {
//                                            Toast.makeText(MainActivity2.this, "Success! Check your email for verification", Toast.LENGTH_SHORT).show();
//                                            passwordEditText.setText("");
//                                            emailEditText.setText("");
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_LONG).show();
//                                        });
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_SHORT).show();
//                        });
//            }
//        });
//    }


    //working on adding the user account to the database but the verified account is the developers account.
//    -----------------------------------------------------------------------------------------
//    private void insertData() {
//        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
//        String email = Objects.requireNonNull(emailEditText.getText()).toString();
//
//        // Set the target email for authentication
//        String developerEmail = "schedbinapp@gmail.com";
//
//        checkEmailExists(email, (emailExists) -> {
//            if (emailExists) {
//                Toast.makeText(MainActivity2.this, "This email is already in use. Please use a different email.", Toast.LENGTH_SHORT).show();
//            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                emailEditText.setError("Please enter a valid email address");
//            } else {
//                // Email verification to the user's input email address
//                FirebaseAuth.getInstance().createUserWithEmailAndPassword(developerEmail, password)
//                        .addOnSuccessListener(authResult -> {
//                            FirebaseUser user = authResult.getUser();
//                            if (user != null) {
//                                // Send email verification link to the user's input email
//                                user.updateEmail(email)
//                                        .addOnCompleteListener(updateEmailTask -> {
//                                            if (updateEmailTask.isSuccessful()) {
//                                                user.sendEmailVerification()
//                                                        .addOnCompleteListener(task -> {
//                                                            if (task.isSuccessful()) {
//                                                                // Verification email sent
//                                                                Toast.makeText(MainActivity2.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
//                                                            } else {
//                                                                // Failed to send verification email
//                                                                Toast.makeText(MainActivity2.this, "Failed to send verification email. Please try again later.", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        });
//                                            } else {
//                                                // Failed to update email
//                                                Toast.makeText(MainActivity2.this, "Failed to update email. Please try again later.", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//                                String userId = user.getUid();
//                                 DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Admin_users").child(userId);
//                                Map<String, Object> userValues = new HashMap<>();
//                                userValues.put("password", password);
//                                userValues.put("email", email);
//
//                                // Store user details in Firebase or your database
//                                databaseReference.setValue(userValues)
//                                        .addOnSuccessListener(unused -> {
//                                            Toast.makeText(MainActivity2.this, "Success! Check your email for verification", Toast.LENGTH_SHORT).show();
//                                            passwordEditText.setText("");
//                                            emailEditText.setText("");
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_LONG).show();
//                                        });
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_SHORT).show();
//                        });
//            }
//        });
//    }


    //=======================both user and the admin is being sent to the authenticated database
//    private void insertData() {
//        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
//        String email = Objects.requireNonNull(emailEditText.getText()).toString();
//
//        // Set the target email for authentication
//        String developerEmail = "schedbinapp@gmail.com";
//
//        checkEmailExists(email, (emailExists) -> {
//            if (emailExists) {
//                Toast.makeText(MainActivity2.this, "This email is already in use. Please use a different email.", Toast.LENGTH_SHORT).show();
//            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                emailEditText.setError("Please enter a valid email address");
//            } else {
//                // Email verification to the developer's account
//                FirebaseAuth.getInstance().createUserWithEmailAndPassword(developerEmail, password)
//                        .addOnSuccessListener(authResult -> {
//                            FirebaseUser developerUser = authResult.getUser();
//                            if (developerUser != null) {
//                                // Send email verification link to the developer's account
//                                developerUser.sendEmailVerification()
//                                        .addOnCompleteListener(task -> {
//                                            if (task.isSuccessful()) {
//                                                // Verification email sent to developer
//                                                Toast.makeText(MainActivity2.this, "Verification email sent to developer. Please check your inbox.", Toast.LENGTH_SHORT).show();
//                                            } else {
//                                                // Failed to send verification email
//                                                Toast.makeText(MainActivity2.this, "Failed to send verification email. Please try again later.", Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .addOnCompleteListener(task -> {
//                                            // Clean up, remove developer's temporary account
//                                            FirebaseAuth.getInstance().signOut();
//                                        });
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_SHORT).show();
//                        });
//
//                // Now, add the user's input email to the authenticated account
//                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
//                        .addOnSuccessListener(userAuthResult -> {
//                            FirebaseUser user = userAuthResult.getUser();
//                            if (user != null) {
//                                String userId = user.getUid();
//                                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Admin_users").child(userId);
//                                Map<String, Object> userValues = new HashMap<>();
//                                userValues.put("password", password);
//                                userValues.put("email", email);
//
//                                // Store user details in Firebase or your database
//                                userReference.setValue(userValues)
//                                        .addOnSuccessListener(unused -> {
//                                            Toast.makeText(MainActivity2.this, "Success! Check your email for verification", Toast.LENGTH_SHORT).show();
//                                            passwordEditText.setText("");
//                                            emailEditText.setText("");
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_LONG).show();
//                                        });
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(MainActivity2.this, "Account creation failed. Please check your details and retry.", Toast.LENGTH_SHORT).show();
//                        });
//            }
//        });
//    }

    private void insertData() {
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String email = Objects.requireNonNull(emailEditText.getText()).toString();

        // Set the target email for authentication
        String developerEmail = "schedbinapp@gmail.com";

        checkEmailExists(email, (emailExists) -> {
            if (emailExists) {
                Toast.makeText(MainActivity2.this, "This email is already in use. Please use a different email.", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email address");
            } else {
                // Email verification to the developer's account
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(developerEmail, password)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser developerUser = authResult.getUser();
                            if (developerUser != null) {
                                // Send email verification link to the developer's account
                                developerUser.sendEmailVerification()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                // Verification email sent to developer
                                                Toast.makeText(MainActivity2.this, "Verification email sent to developer. Please check your inbox.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Failed to send verification email
                                                Toast.makeText(MainActivity2.this, "Failed to send verification email. Please try again later.", Toast.LENGTH_SHORT).show();
                                            }

                                            // Clean up, remove developer's temporary account
                                            developerUser.delete()
                                                    .addOnCompleteListener(deleteTask -> {
                                                        if (deleteTask.isSuccessful()) {
                                                            Log.d("TAG", "Temporary developer account deleted");
                                                        }
                                                    });
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainActivity2.this, "Failed to create temporary developer account. Please try again later.", Toast.LENGTH_SHORT).show();
                        });

                // Now, add the user's input email to the authenticated account
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(userAuthResult -> {
                            FirebaseUser user = userAuthResult.getUser();
                            if (user != null) {
                                String userId = user.getUid();
                                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("Admin_users").child(userId);
                                Map<String, Object> userValues = new HashMap<>();
                                userValues.put("password", password);
                                userValues.put("email", email);

                                // Store user details in Firebase or your database
                                userReference.setValue(userValues)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(MainActivity2.this, "Success! Check your email for verification", Toast.LENGTH_SHORT).show();
                                            passwordEditText.setText("");
                                            emailEditText.setText("");
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
