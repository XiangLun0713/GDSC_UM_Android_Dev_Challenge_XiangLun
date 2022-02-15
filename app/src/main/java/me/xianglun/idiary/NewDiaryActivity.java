package me.xianglun.idiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class NewDiaryActivity extends AppCompatActivity {

    private static final int READ_STORAGE_REQUEST_CODE = 100;
    private static final int GALLERY_PICK_IMAGE_CODE = 200;
    private static final int CAMERA_REQUEST_CODE = 300;
    private static final int CAMERA_TAKE_IMAGE_CODE = 400;

    private String currentPhotoPath;
    private LinearLayout linearLayout;
    private CircularProgressIndicator progressIndicator;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView dateAndTimeLabel;
    private EditText titleText;
    private EditText diaryMainEditText;

    private String[] readStoragePermission;
    private String[] cameraPermission;
    private final String[] hints = {"How was your day?", "What's in your mind?",
            "What was the best part of your day?", "How are you feeling today?",
            "Did you learn anything new today?", "What are you most proud of today?",
            "Did you get the chance to help anyone today?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_diary);

        // Declare/Initialize variables
        readStoragePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        cameraPermission = new String[]{Manifest.permission.CAMERA};
        progressIndicator = findViewById(R.id.add_diary_progress_bar);
        linearLayout = findViewById(R.id.new_diary_linear_layout);
        dateAndTimeLabel = findViewById(R.id.date_and_time_label);
        titleText = findViewById(R.id.diary_title);
        diaryMainEditText = findViewById(R.id.diary_text);

        // Configure the toolbar
        setSupportActionBar(findViewById(R.id.new_diary_toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Set up date, time, title, and hint
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("E, dd-MMM-yyyy   hh:mm a", Locale.getDefault());
        String formattedDate = df.format(c);
        dateAndTimeLabel.setText(formattedDate);
        diaryMainEditText.setHint(hints[new Random().nextInt(hints.length)]);
        titleText.requestFocus();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_diary_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_photo) {
            displayImagePickerDialog();
            return true;
        } else if (item.getItemId() == R.id.save_diary) {
            // TODO: 2/15/2022 save diary to firebase
/*
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance("https://idiary-341301-default-rtdb.asia-southeast1.firebasedatabase.app");
            databaseReference = firebaseDatabase.getReference();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

            String dateAndTime = dateAndTimeLabel.getText().toString();
            String title = titleText.getText().toString();
            String diaryMainText = diaryMainEditText.getText().toString();

            if (firebaseUser != null) {
                progressIndicator.setVisibility(View.VISIBLE);
                progressIndicator.setProgressCompat(500,true);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            saveDiaryToDatabase();
            System.out.println("saved");
            return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveDiaryToDatabase() {

    }

    private void displayImagePickerDialog() {
        String[] items = {"Gallery", "Camera"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose image from");
        dialog.setItems(items, (dialog1, which) -> {
            switch (which) {
                case 0:
                    if (!checkReadStoragePermission()) {
                        requestReadStoragePermission();
                    } else {
                        pickImageFromGallery();
                    }
                    break;
                case 1:
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        dispatchTakePictureIntent();
                    }
                    break;
            }
        });
        dialog.show();
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void requestReadStoragePermission() {
        ActivityCompat.requestPermissions(this, readStoragePermission, READ_STORAGE_REQUEST_CODE);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_IMAGE_CODE);
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkReadStoragePermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(Arrays.toString(grantResults));
        if (requestCode == READ_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                addDiaryImageTemplate(data.getData());
            }
        } else if (requestCode == CAMERA_TAKE_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                File file = new File(currentPhotoPath);
                addDiaryImageTemplate(Uri.fromFile(file));
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error in creating file", Toast.LENGTH_SHORT).show();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                System.out.println("image created");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "me.xianglun.idiary.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_TAKE_IMAGE_CODE);
            }
        }
    }

    private void addDiaryImageTemplate(Uri data) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5), convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5));

        View template = getLayoutInflater().inflate(R.layout.template_diary_image, linearLayout, false);
        ImageView image = template.findViewById(R.id.diary_image);
        image.setImageURI(data);
        FloatingActionButton deleteBtn = template.findViewById(R.id.delete_button);
        deleteBtn.setOnClickListener(v -> {
            ViewParent parent = deleteBtn.getParent();
            int parentIndex = linearLayout.indexOfChild((View) parent);
            View editTextAfterImage = linearLayout.getChildAt(parentIndex + 1);
            View editTextBeforeImage = linearLayout.getChildAt(parentIndex - 1);
            if (editTextAfterImage instanceof EditText && editTextBeforeImage instanceof EditText) {
                if (((EditText) editTextBeforeImage).getText().toString().isEmpty() && !((EditText) editTextAfterImage).getText().toString().isEmpty()) {
                    ((EditText) editTextBeforeImage).setText(((EditText) editTextAfterImage).getText().toString());
                } else if (!((EditText) editTextBeforeImage).getText().toString().isEmpty() && !((EditText) editTextAfterImage).getText().toString().isEmpty()) {
                    ((EditText) editTextBeforeImage).setText(((EditText) editTextBeforeImage).getText().toString().concat("\n").concat(((EditText) editTextAfterImage).getText().toString()));
                }
                linearLayout.removeView(editTextAfterImage);
                editTextBeforeImage.requestFocus();
                ((EditText) editTextBeforeImage).setSelection(((EditText) editTextBeforeImage).getText().length());
            }
            if (parent != null) {
                linearLayout.removeView((View) parent);
            }
        });
        linearLayout.addView(template, linearParams);

        //add a new edit text view after the image
        EditText newEditText = new EditText(this);
        newEditText.setHint("Tell us more!");
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        newEditText.setBackgroundColor(Color.TRANSPARENT);
        newEditText.setLineSpacing(convertDpToPx(this, 8), 1);
        newEditText.requestFocus();
        linearLayout.addView(newEditText, linearParams);
    }

    public int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}