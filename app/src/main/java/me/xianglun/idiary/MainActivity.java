package me.xianglun.idiary;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.xianglun.idiary.databinding.ActivityMainBinding;
import me.xianglun.idiary.model.UserModel;

public class MainActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DrawerLayout drawer;
    private TextView userName, quotes;
    private ImageView profileImage;
    private FloatingActionButton addDiaryBtn;
    private boolean isAtProfile;
    private boolean isAtFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.mainToolbar);

        isAtProfile = false;
        isAtFeedback = false;
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);

        addDiaryBtn = binding.appBarMain.mainFab;
        addDiaryBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewDiaryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        LinearLayout layout = (LinearLayout) binding.navView.getHeaderView(0);
        profileImage = layout.findViewById(R.id.navigation_imageView);
        userName = layout.findViewById(R.id.navigation_drawer_username);
        quotes = layout.findViewById(R.id.navigation_quotes);
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.nav_profile) {
                isAtProfile = true;
                isAtFeedback = false;
                if (addDiaryBtn.getVisibility() == View.VISIBLE) {
                    addDiaryBtn.setVisibility(View.INVISIBLE);
                }

            } else if (navDestination.getId() == R.id.nav_home) {
                if (addDiaryBtn.getVisibility() == View.INVISIBLE) {
                    addDiaryBtn.setVisibility(View.VISIBLE);
                }
                isAtProfile = false;
                isAtFeedback = false;
            } else if (navDestination.getId() == R.id.nav_feedback) {
                if (addDiaryBtn.getVisibility() == View.VISIBLE) {
                    addDiaryBtn.setVisibility(View.INVISIBLE);
                }
                isAtProfile = false;
                isAtFeedback = true;
            }
        });
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference currentUserNode = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            currentUserNode.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null) {
                        if (user.getUsername() != null) userName.setText(user.getUsername());
                        if (user.getStatus() != null) quotes.setText(user.getStatus());
                        if (user.getImagePath() != null)
                            Glide.with(getApplicationContext()).load(user.getImagePath()).into(profileImage);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        } else if (item.getItemId() == R.id.action_log_out) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!isAtFeedback && !isAtProfile) {
                if (doubleBackToExitPressedOnce) {
                    this.finishAffinity();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            } else {
                super.onBackPressed();
            }
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}