package me.xianglun.idiary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LockScreenActivity extends AppCompatActivity {
    private static final String KEY_PASSWORD = "password";

    private LottieAnimationView animationView;
    private TextInputLayout passwordInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        Objects.requireNonNull(getSupportActionBar()).hide();

        MaterialButton unlockBtn = findViewById(R.id.lockscreen_button);
        animationView = findViewById(R.id.lockscreen_animation_view);
        passwordInputLayout = findViewById(R.id.lockscreen_password);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordInputLayout.setError(null);

                String passwordEntered = Objects.requireNonNull(passwordInputLayout.getEditText()).getText().toString();
                if (passwordEntered.isEmpty()) {
                    passwordInputLayout.setError("Password cannot be empty");
                } else {
                    String passwordFromSettings = sharedPreferences.getString(KEY_PASSWORD, "");
                    if (passwordEntered.equals(passwordFromSettings)) {
                        animationView.addAnimatorListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                Intent intent = new Intent(LockScreenActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });
                        animationView.playAnimation();
                    } else {
                        passwordInputLayout.setError("Incorrect password");
                    }
                }
            }
        });
    }
}