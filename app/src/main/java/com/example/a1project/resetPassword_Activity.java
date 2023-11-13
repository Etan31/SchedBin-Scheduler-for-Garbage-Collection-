    package com.example.a1project;

    import android.content.Intent;
    import android.os.Bundle;
    import android.text.TextUtils;
    import android.view.View;
    import android.widget.Button;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;

    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.material.textfield.TextInputEditText;
    import com.google.android.material.textfield.TextInputLayout;
    import com.google.firebase.auth.FirebaseAuth;

    import java.util.Objects;

    public class resetPassword_Activity extends AppCompatActivity {

        private Button backBtn2;

        private FirebaseAuth mAuth;
        private TextInputEditText emailEditText;
        private TextInputLayout emailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(v -> finish());

        mAuth = FirebaseAuth.getInstance();

        emailLayout = findViewById(R.id.resetEmail_input);
        emailEditText = (TextInputEditText) emailLayout.getEditText();


        Button resetPassword_button = (Button) findViewById(R.id.resetPassword_btn);
        resetPassword_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(emailEditText.getText()).toString();

                if(!TextUtils.isEmpty(email)){
                    resetPassword();
                } else {
                    emailEditText.setError("Please enter your email address.");
                }

            }
        });

    }

    private void resetPassword() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString();



        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(resetPassword_Activity.this, "Reset Password Link has been sent to your account", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(resetPassword_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(resetPassword_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}