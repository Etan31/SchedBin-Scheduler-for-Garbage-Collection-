package com.example.a1project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;

    // Firebase Authentication instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //declaration of the login input Layout
        TextInputLayout emailLayout = findViewById(R.id.emailInput_login);
        TextInputLayout passwordLayout = findViewById(R.id.passwordInput_login);

        //declaration of the login input EditText
        emailEditText = (TextInputEditText) emailLayout.getEditText();
        passwordEditText = (TextInputEditText) passwordLayout.getEditText();

        Button resetPassword = findViewById(R.id.reset_btn);
        resetPassword.setOnClickListener(v -> resetPassword_Activity());


        Button viewSchedule = findViewById(R.id.regular_user_btn);
        viewSchedule.setOnClickListener(v -> viewSchedule_Activity());


        //
        Button login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailEditText.getText()).toString();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Please enter email address");
                return; // Exit the onClick listener if email is empty
            }

            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Please enter password");
                return; // Exit the onClick listener if password is empty
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    // User is registered and email is verified, log them in.
                                    openActivityHome();
                                } else {
                                    Toast.makeText(MainActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Please register your email address", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Login failed Check your credentials.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }


    public void viewSchedule_Activity() {
//        Intent intent = new Intent(this, ActivityHomeRegularUser.class);
        Intent intent = new Intent(this, Admin_Home_activity.class);
        startActivity(intent);
    }

    public void resetPassword_Activity(){
        Intent intent = new Intent(this, resetPassword_Activity.class);
        startActivity(intent);
    }

    public void openActivityHome(){
        Intent intent = new Intent(this, Admin_Home_activity.class);
        startActivity(intent);
    }
}