package me.xianglun.idiary.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import me.xianglun.idiary.R;
import me.xianglun.idiary.databinding.FragmentProfileBinding;
import me.xianglun.idiary.model.UserModel;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Context context;
    private ImageButton profilePicBtn;
    private TextView usernameTextView, statusTextView;
    DatabaseReference currentUserNode;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getActivity();

        profilePicBtn = binding.profilePicButton;
        usernameTextView = binding.profileUsername;
        statusTextView = binding.profileStatus;

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

        return root;
    }

    private void showProfileInfo() {
        currentUserNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    if (user != null) {
                        usernameTextView.setText(user.getUsername());
                        statusTextView.setText(user.getStatus());
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

    public int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}