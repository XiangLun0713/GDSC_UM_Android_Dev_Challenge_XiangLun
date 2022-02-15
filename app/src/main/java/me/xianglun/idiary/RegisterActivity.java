package me.xianglun.idiary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout registerEmail, registerPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();

        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);

        TextView loginNowBtn = findViewById(R.id.register_login_button);
        loginNowBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right_slow, R.anim.slide_out_left_slow);
        });

        MaterialButton registerBtn = findViewById(R.id.register_button);
        registerBtn.setOnClickListener(v -> {
            String emailEntered = Objects.requireNonNull(registerEmail.getEditText()).getText().toString();
            String passwordEntered = Objects.requireNonNull(registerPassword.getEditText()).getText().toString();

            //check whether the email is in use
            //if not, save user credential to firebase
            //if success, forward user to login activity
            Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right_slow, R.anim.slide_out_left_slow);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right_slow, R.anim.slide_out_left_slow);
    }
}