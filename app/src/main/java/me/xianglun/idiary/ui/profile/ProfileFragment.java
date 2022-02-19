package me.xianglun.idiary.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

import me.xianglun.idiary.R;
import me.xianglun.idiary.databinding.FragmentProfileBinding;
import me.xianglun.idiary.model.UserModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Context context;
    private ImageView profilePic;
    private String profilePicPath;
    private TextView usernameTextView, statusTextView;
    private DatabaseReference currentUserNode;
    private CardView progressCardView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getActivity();
        profilePic = binding.profilePicImageView;
        usernameTextView = binding.profileUsername;
        statusTextView = binding.profileStatus;
        progressCardView = binding.profileProgressCardView;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            currentUserNode = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            showProfileInfo();
        }

        usernameTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogThemeWithPadding);
            builder.setTitle("Enter username");

            final EditText input = new EditText(context);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            builder.setView(input);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                String username = input.getText().toString().trim();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("username", username);
                currentUserNode.updateChildren(hashMap);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        statusTextView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogThemeWithPadding);
            builder.setTitle("Enter status/quotes");

            final EditText input = new EditText(context);
            input.setLines(3);
            input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            builder.setView(input);

            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                String status = input.getText().toString().trim();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", status);
                currentUserNode.updateChildren(hashMap);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        profilePic.setOnClickListener(v -> ImagePicker.with(this).crop(1f, 1f).start());

        return root;
    }

    private void showProfileInfo() {
        currentUserNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null) {
                        if (user.getUsername() != null)
                            usernameTextView.setText(user.getUsername());
                        if (user.getStatus() != null) statusTextView.setText(user.getStatus());
                        if (user.getImagePath() != null) {
                            try {
                                Glide.with(context).load(user.getImagePath()).into(profilePic);
                            } catch (Exception e) {
                                Toast.makeText(context, "Please try again. Make sure you're connected to a stable network", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                progressCardView.setVisibility(View.VISIBLE);
                FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                StorageReference storageReference = firebaseStorage.getReference();
                requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Uri uri = data.getData();
                storageReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(uri.getLastPathSegment()).putFile(uri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getMetadata()).getReference()).getDownloadUrl().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                profilePicPath = Objects.requireNonNull(task1.getResult()).toString();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("imagePath", profilePicPath);
                                currentUserNode.updateChildren(hashMap);
                                requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                progressCardView.setVisibility(View.INVISIBLE);
                                Toast.makeText(context, "Profile profile saved", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}