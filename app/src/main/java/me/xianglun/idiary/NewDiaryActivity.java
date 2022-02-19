package me.xianglun.idiary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class NewDiaryActivity extends AppCompatActivity {

    private String date, time;
    private String diaryId;
    private LinearLayout linearLayout;
    private CardView progressCardView;
    private EditText titleText;
    private EditText diaryMainEditText;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    private List<String> textList;
    private List<String> imageList;
    private final String[] hints = {"How was your day?", "What's in your mind?",
            "What was the best part of your day?", "How are you feeling today?",
            "Did you learn anything new today?", "What are you most proud of today?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_diary);

        // Declare/Initialize variables
        progressCardView = findViewById(R.id.progress_card_view);
        linearLayout = findViewById(R.id.new_diary_linear_layout);
        TextView dateAndTimeLabel = findViewById(R.id.date_and_time_label);
        titleText = findViewById(R.id.diary_title);
        diaryMainEditText = findViewById(R.id.diary_text);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://idiary-341301-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        Intent intent = getIntent();
        textList = new ArrayList<>();
        imageList = new ArrayList<>();

        if (intent.getStringExtra("diaryId") == null) {
            // Configure the toolbar
            setSupportActionBar(findViewById(R.id.new_diary_toolbar));
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("New Diary");

            // Set up date, time, title, and hint accordingly
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("E, dd-MMM-yyyy   hh:mm a", Locale.getDefault());
            String formattedDate = df.format(c);
            dateAndTimeLabel.setText(formattedDate);
            df = new SimpleDateFormat("E, dd-MMM-yyyy", Locale.getDefault());
            date = df.format(c);
            df = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            time = df.format(c);
            diaryMainEditText.setHint(hints[new Random().nextInt(hints.length)]);
            titleText.requestFocus();

        } else {
            // Configure the toolbar
            setSupportActionBar(findViewById(R.id.new_diary_toolbar));
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("View Diary");

            // get data from intent
            diaryId = intent.getStringExtra("diaryId");
            String title = intent.getStringExtra("title");
            String mainText = intent.getStringExtra("mainText");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String dateAndTime = date + "   " + time;
            imageList = intent.getStringArrayListExtra("imagePaths");
            textList = intent.getStringArrayListExtra("texts");

            // set data accordingly
            dateAndTimeLabel.setText(dateAndTime);
            titleText.setText(title);
            diaryMainEditText.setText(mainText);

            // generate previous images and texts
            if (imageList != null) {
                for (int i = 0; i < imageList.size(); i++) {
                    LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearParams.setMargins(convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5), convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5));

                    View template = getLayoutInflater().inflate(R.layout.template_diary_image, linearLayout, false);
                    ImageView image = template.findViewById(R.id.diary_image);
                    Glide.with(this).load(imageList.get(i)).into(image);
                    FloatingActionButton deleteBtn = template.findViewById(R.id.delete_button);

                    deleteBtn.setTag(imageList.get(i));
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
                        imageList.remove((String) deleteBtn.getTag());
                        linearLayout.removeView((View) parent);
                    });
                    linearLayout.addView(template, linearParams);

                    //add a new edit text view after the image
                    EditText newEditText = new EditText(this);
                    newEditText.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    newEditText.setText(textList.get(i));
                    newEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    newEditText.setBackgroundColor(Color.TRANSPARENT);
                    newEditText.setLineSpacing(convertDpToPx(this, 8), 1);
                    linearLayout.addView(newEditText, linearParams);
                }
            }

        }
        textList = new ArrayList<>();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_photo) {
            displayImagePickerDialog();
            return true;

        } else if (item.getItemId() == R.id.save_diary) {
            if (!titleText.getText().toString().isEmpty() && !diaryMainEditText.getText().toString().isEmpty()) {
                if (firebaseUser != null) {
                    progressCardView.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    saveDiaryToDatabase(firebaseUser.getUid());
                }
            } else {
                Toast.makeText(this, "Please enter the title and some texts", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void saveDiaryToDatabase(String userId) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();

        String title = titleText.getText().toString();
        String diaryMainText = diaryMainEditText.getText().toString();

        HashMap<String, Object> diaryMap = new HashMap<>();
        diaryMap.put("title", title);
        diaryMap.put("diaryMainText", diaryMainText);

        List<Task<UploadTask.TaskSnapshot>> storeImageOnDatabaseTaskList = new ArrayList<>();
        List<Task<Uri>> getUriTaskList = new ArrayList<>();

        for (int i = 3; i < linearLayout.getChildCount(); i += 2) {
            if (linearLayout.getChildAt(i) instanceof RelativeLayout) {
                RelativeLayout relativeLayout = (RelativeLayout) linearLayout.getChildAt(i);
                ImageView image = relativeLayout.findViewById(R.id.diary_image);
                if (image.getTag() instanceof Uri) {
                    Uri uri = (Uri) image.getTag();
                    Task<UploadTask.TaskSnapshot> storeImageOnDatabaseTask = storageReference.child(userId).child(uri.getLastPathSegment()).putFile(uri).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Task<Uri> getUriTask = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getMetadata()).getReference()).getDownloadUrl().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    String imageURL = Objects.requireNonNull(task1.getResult()).toString();
                                    imageList.add(imageURL);
                                }
                            }).addOnFailureListener(e -> Toast.makeText(NewDiaryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            getUriTaskList.add(getUriTask);
                        }
                    }).addOnFailureListener(e -> Toast.makeText(NewDiaryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                    storeImageOnDatabaseTaskList.add(storeImageOnDatabaseTask);
                }
            }
            if (linearLayout.getChildAt(i + 1) instanceof EditText) {
                EditText text = (EditText) linearLayout.getChildAt(i + 1);
                textList.add(text.getText().toString());
            }
        }

        Tasks.whenAllComplete(storeImageOnDatabaseTaskList).addOnCompleteListener(task -> Tasks.whenAllComplete(getUriTaskList).addOnCompleteListener(task12 -> {
            DatabaseReference diaryNode;
            if (diaryId == null) {
                diaryNode = databaseReference.child("diaries").child(userId).push();
                String diaryNodeId = diaryNode.getKey();
                diaryMap.put("diaryId", diaryNodeId);
                diaryMap.put("time", time);
                diaryMap.put("date", date);
            } else {
                diaryNode = databaseReference.child("diaries").child(userId).child(diaryId);
                diaryNode.child("texts").removeValue();
            }
            diaryMap.put("imagePaths", imageList);
            diaryMap.put("texts", textList);

            diaryNode.updateChildren(diaryMap).addOnCompleteListener(voidTask -> {
                if (voidTask.isSuccessful()) {
                    progressCardView.setVisibility(View.INVISIBLE);
                    Toast.makeText(NewDiaryActivity.this, "Diary saved", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewDiaryActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            });
        }));


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (diaryId == null) {
            if (!titleText.getText().toString().isEmpty() || !diaryMainEditText.getText().toString().isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setTitle("Discard Diary");
                builder.setMessage("Are you sure you want to quit writing this diary?");
                builder.setPositiveButton("Discard", (dialog, id) -> {
                    NewDiaryActivity.super.onBackPressed();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                });
                builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                });
                builder.show();
            } else {
                NewDiaryActivity.super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setTitle("Save Changes");
            builder.setMessage("Do you want to save your changes to this diary?");
            builder.setPositiveButton("Save", (dialog, id) -> {
                if (firebaseUser != null) {
                    progressCardView.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    saveDiaryToDatabase(firebaseUser.getUid());
                }
            });
            builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
            });
            builder.setNeutralButton("Discard", (dialog, id) -> {
                NewDiaryActivity.super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            });
            builder.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_diary_toolbar_menu, menu);
        return true;
    }

    private void displayImagePickerDialog() {
        ImagePicker.with(this).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                addDiaryImageTemplate(uri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDiaryImageTemplate(Uri data) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearParams.setMargins(convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5), convertDpToPx(getApplicationContext(), 10), convertDpToPx(getApplicationContext(), 5));

        View template = getLayoutInflater().inflate(R.layout.template_diary_image, linearLayout, false);
        ImageView image = template.findViewById(R.id.diary_image);
        image.setImageURI(data);
        image.setTag(data);
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
            linearLayout.removeView((View) parent);
        });
        linearLayout.addView(template, linearParams);

        //add a new edit text view after the image
        EditText newEditText = new EditText(this);
        newEditText.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
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