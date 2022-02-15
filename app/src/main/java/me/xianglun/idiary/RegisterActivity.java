package me.xianglun.idiary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout registerEmail, registerPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);

        TextView loginNowBtn = findViewById(R.id.register_login_button);
        loginNowBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right_slow, R.anim.slide_out_left_slow);
        });

        MaterialButton registerBtn = findViewById(R.id.register_button);
        registerBtn.setOnClickListener(v -> registerNewUser());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_slow, R.anim.slide_out_left_slow);
    }

    private void registerNewUser() {
        registerEmail.setError(null);
        registerPassword.setError(null);

        String emailEntered = Objects.requireNonNull(registerEmail.getEditText()).getText().toString();
        String passwordEntered = Objects.requireNonNull(registerPassword.getEditText()).getText().toString();

        if (emailEntered.isEmpty()) {
            registerEmail.setError("Email cannot be empty");
        }
        if (passwordEntered.isEmpty()) {
            registerPassword.setError("Password cannot be empty");
        }
        if (!passwordEntered.isEmpty() && !emailEntered.isEmpty()) {
            mAuth.createUserWithEmailAndPassword(emailEntered, passwordEntered).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right_slow, R.anim.slide_out_left_slow);
                } else {
                    Toast.makeText(RegisterActivity.this, "Register failed. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}