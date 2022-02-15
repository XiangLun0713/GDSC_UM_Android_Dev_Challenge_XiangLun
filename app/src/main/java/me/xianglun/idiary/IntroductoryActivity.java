package me.xianglun.idiary;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Objects;

public class IntroductoryActivity extends AppCompatActivity {

    ImageView backgroundImage;
    ConstraintLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);
        Objects.requireNonNull(getSupportActionBar()).hide();

        relativeLayout = findViewById(R.id.splash_screen_items_layout);
        backgroundImage = findViewById(R.id.splash_screen_image);

        relativeLayout.animate().translationY(2000).setDuration(700).setStartDelay(2500).withEndAction(() -> {
            // If the user had logged in before,
            // Direct them to the main activity
            Intent intent = new Intent(IntroductoryActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        backgroundImage.animate().translationY(-3000).setDuration(1000).setStartDelay(2500);

    }
}