    package com.example.a1project;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.constraintlayout.motion.widget.MotionLayout;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    import com.example.a1project.databinding.ActivityAdminHomeBinding;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textfield.TextInputLayout;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;

    import java.util.Objects;

    public class resetPassword_Activity extends AppCompatActivity {

        Button backBtn2;

        private FirebaseAuth mAuth;
        private TextInputEditText emailEditText;

        private MotionLayout motionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> BackPressed());

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
                // User is signed in
                ActivityAdminHomeBinding binding;
                binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                replaceFragment(new SettingsFragment());
            }
        }

        private void replaceFragment(androidx.fragment.app.Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.BaseFrameLayout, fragment);
            fragmentTransaction.commit();
        }

        private void resetPassword() {

            Log.d("ResetPassword", "Reset Password method called");
            String email = Objects.requireNonNull(emailEditText.getText()).toString();

            mAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(resetPassword_Activity.this, "Reset Password Link has been sent to your account", Toast.LENGTH_SHORT).show();

                        // Start the animation when password reset is successful
                        if (motionLayout != null) {
                            motionLayout.transitionToEnd();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ResetPassword", "Error: " + e.getMessage());
                        Toast.makeText(resetPassword_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
    }

    }