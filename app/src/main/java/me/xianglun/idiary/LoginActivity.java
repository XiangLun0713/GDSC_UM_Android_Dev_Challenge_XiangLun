package me.xianglun.idiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout loginEmail, loginPassword;
    private boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        TextView registerNow = findViewById(R.id.login_register_now_button);
        MaterialButton loginBtn = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();
        loginPassword = findViewById(R.id.login_password);
        loginEmail = findViewById(R.id.login_email);

        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left_slow, R.anim.slide_out_right_slow);
        });
        loginBtn.setOnClickListener(v -> login());
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    private void login() {
        loginEmail.setError(null);
        loginPassword.setError(null);

        String emailEntered = Objects.requireNonNull(loginEmail.getEditText()).getText().toString().trim();
        String passwordEntered = Objects.requireNonNull(loginPassword.getEditText()).getText().toString().trim();

        if (emailEntered.isEmpty()) {
            loginEmail.setError("Email cannot be empty");
        }
        if (passwordEntered.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
        }
        if (!passwordEntered.isEmpty() && !emailEntered.isEmpty()) {
            mAuth.signInWithEmailAndPassword(emailEntered, passwordEntered).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right_slow, R.anim.slide_out_left_slow);
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}