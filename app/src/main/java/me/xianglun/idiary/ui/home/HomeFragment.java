package me.xianglun.idiary.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.xianglun.idiary.NewDiaryActivity;
import me.xianglun.idiary.R;
import me.xianglun.idiary.adapter.DiaryAdapter;
import me.xianglun.idiary.databinding.FragmentHomeBinding;
import me.xianglun.idiary.model.DiaryModel;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Context context;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        context = getActivity();

        recyclerView = binding.homeRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            showDiaries(user.getUid());
        }
        return root;
    }

    private void showDiaries(String uid) {
        ArrayList<DiaryModel> diaryArrayList = new ArrayList<>();
        DiaryAdapter diaryAdapter = new DiaryAdapter(context, diaryArrayList);
        recyclerView.setAdapter(diaryAdapter);
        diaryAdapter.setOnItemClickListener(position -> {
            DiaryModel diary = diaryArrayList.get(position);
            Intent intent = new Intent(context, NewDiaryActivity.class);
            intent.putExtra("diaryId", diary.getDiaryId());
            intent.putExtra("title", diary.getTitle());
            intent.putExtra("mainText", diary.getDiaryMainText());
            intent.putExtra("date", diary.getDate());
            intent.putExtra("time", diary.getTime());
            intent.putStringArrayListExtra("imagePaths", (ArrayList<String>) diary.getImagePaths());
            intent.putStringArrayListExtra("texts", (ArrayList<String>) diary.getTexts());
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("diaries").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                diaryArrayList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DiaryModel diary = dataSnapshot.getValue(DiaryModel.class);
                        diaryArrayList.add(diary);
                    }
                } else {
                    if (binding != null) {
                        binding.homeAnimationLayout.setVisibility(View.VISIBLE);
                    }
                }
                diaryAdapter.notifyDataSetChanged();
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
}