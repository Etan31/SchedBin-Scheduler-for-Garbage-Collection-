    package com.schedBin.a1project;

    import android.app.ProgressDialog;
    import android.content.Intent;
    import android.content.pm.ActivityInfo;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.constraintlayout.motion.widget.MotionLayout;

    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textfield.TextInputLayout;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.Objects;

    public class resetPassword_Activity extends AppCompatActivity {
//        TODO: Fix the bug of the BackPressed, it should be redirected to SettingsFragment instead of Schedulefragment

        Button backBtn2;

        private FirebaseAuth mAuth;
        private TextInputEditText emailEditText;

        private MotionLayout motionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        backBtn2 = findViewById(R.id.backBtn2);
//        backBtn2.setOnClickListener(v -> BackPressed());

        mAuth = FirebaseAuth.getInstance();

        TextInputLayout emailLayout = findViewById(R.id.resetEmail_input);
        emailEditText = (TextInputEditText) emailLayout.getEditText();

        motionLayout = findViewById(R.id.motionLayout);

        Button resetPassword_button = findViewById(R.id.resetPassword_btn);

        resetPassword_button.setOnClickListener(v -> {
            Log.d("ResetPassword", "Reset Password Button Clicked");
            String email = Objects.requireNonNull(emailEditText.getText()).toString();

            if(!TextUtils.isEmpty(email)){
                resetPassword();
            } else {
                emailEditText.setError("Please enter your email address.");
            }

        });


    }

        private void BackPressed() {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser == null) {
                startActivity(new Intent(resetPassword_Activity.this, MainActivity.class));

            } else {
                //                working but redirected to the schedule fragment instead of setting fragment
                Intent intent = new Intent(resetPassword_Activity.this, Admin_Home_activity.class);
                startActivity(intent);
                finish();
            }
        }


        private void resetPassword() {
            ProgressDialog progressDialog = new ProgressDialog(resetPassword_Activity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            FirebaseUser user = mAuth.getCurrentUser();

            if (user != null && user.isEmailVerified()) {
                String email = Objects.requireNonNull(emailEditText.getText()).toString();

                DatabaseReference adminUsersRef = FirebaseDatabase.getInstance().getReference().child("admin_users");
                adminUsersRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();

                        if (dataSnapshot.exists()) {
                            mAuth.sendPasswordResetEmail(email)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(resetPassword_Activity.this, "Then link has been sent to your account.", Toast.LENGTH_LONG).show();

                                        if (motionLayout != null) {
                                            motionLayout.transitionToEnd();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ResetPassword", "Error: " + e.getMessage());
                                        Toast.makeText(resetPassword_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(resetPassword_Activity.this, "You do not have permission to reset the password.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        // Handle onCancelled event if needed
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(resetPassword_Activity.this, "Please log in and verify your email before resetting the password.", Toast.LENGTH_LONG).show();
            }

        }

// Rest of your code...





    }