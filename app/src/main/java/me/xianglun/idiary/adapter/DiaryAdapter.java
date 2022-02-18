package me.xianglun.idiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import me.xianglun.idiary.R;
import me.xianglun.idiary.model.DiaryModel;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private final Context context;
    private final ArrayList<DiaryModel> diaryList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public DiaryAdapter(Context context, ArrayList<DiaryModel> diaryList) {
        this.context = context;
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public DiaryAdapter.DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.template_diary, parent, false);
        return new DiaryViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.DiaryViewHolder holder, int position) {
        DiaryModel diary = diaryList.get(position);
        holder.title.setText(diary.getTitle());
        holder.diaryMainText.setText(diary.getDiaryMainText());
        holder.time.setText(diary.getTime());
        holder.date.setText(diary.getDate());
        holder.deleteButton.setOnClickListener(v -> deleteDiaryRecord(diary.getDiaryId()));
        if (diary.getImagePaths() != null && diary.getImagePaths().size() > 0) {
            if (!diary.getImagePaths().get(0).isEmpty()) {
                Glide.with(context).load(diary.getImagePaths().get(0)).into(holder.imageView);
                holder.imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    private void deleteDiaryRecord(String diaryId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("diaries").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(diaryId);
        databaseReference.removeValue().addOnSuccessListener(unused -> Toast.makeText(context, "Diary deleted", Toast.LENGTH_SHORT).show());
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView time;
        private final TextView title;
        private final TextView diaryMainText;
        private final ImageButton deleteButton;
        private final ImageView imageView;

        public DiaryViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            date = itemView.findViewById(R.id.adapter_diary_date);
            time = itemView.findViewById(R.id.adepter_diary_time);
            title = itemView.findViewById(R.id.adapter_diary_title);
            diaryMainText = itemView.findViewById(R.id.adapter_diary_text);
            deleteButton = itemView.findViewById(R.id.adepter_diary_delete_icon);
            imageView = itemView.findViewById(R.id.adapter_diary_image);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
