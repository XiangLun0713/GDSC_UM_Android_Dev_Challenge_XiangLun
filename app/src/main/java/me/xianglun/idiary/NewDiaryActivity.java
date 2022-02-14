package me.xianglun.idiary;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class NewDiaryActivity extends AppCompatActivity {

    private static final int STORAGE_REQUEST_CODE = 100;
    private static final int GALLERY_PICK_IMAGE_CODE = 200;
    private String[] storagePermission;
    private LinearLayout linearLayout;
    private RelativeLayout imageTemplateLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_diary);

        storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        linearLayout = findViewById(R.id.new_diary_linear_layout);
        imageTemplateLayout = findViewById(R.id.diary_image_template);

        setSupportActionBar(findViewById(R.id.new_diary_toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("E, dd-MMM-yyyy   hh:mm a", Locale.getDefault());
        String formattedDate = df.format(c);
        TextView dateAndTimeLabel = findViewById(R.id.date_and_time_label);
        dateAndTimeLabel.setText(formattedDate);

        EditText titleText = findViewById(R.id.diary_title);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayImagePickerDialog() {
        String[] items = {"Gallery", "Camera"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Choose image from");
        dialog.setItems(items, (dialog1, which) -> {
            switch (which) {
                case 0:
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickImageFromGallery();
                    }
                    break;
                case 1:
                    // TODO: 2/13/2022 insert code for camera
                    break;
            }
        });
        dialog.show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_IMAGE_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_IMAGE_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5), convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5));

                View template = getLayoutInflater().inflate(R.layout.template_diary_image, linearLayout, false);
                ImageView image = template.findViewById(R.id.diary_image);
                image.setImageURI(data.getData());
                FloatingActionButton deleteBtn = template.findViewById(R.id.delete_button);
                deleteBtn.setOnClickListener(v -> {
                    ViewParent parent = deleteBtn.getParent();
                    int parentIndex = linearLayout.indexOfChild((View) parent);
                    View editTextAfterImage = linearLayout.getChildAt(parentIndex + 1);
                    View editTextBeforeImage = linearLayout.getChildAt(parentIndex - 1);
                    if (editTextAfterImage instanceof EditText && editTextBeforeImage instanceof EditText) {
                        if (((EditText) editTextBeforeImage).getText().toString().isEmpty() && !((EditText) editTextAfterImage).getText().toString().isEmpty()){
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
        }
    }

    public int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}