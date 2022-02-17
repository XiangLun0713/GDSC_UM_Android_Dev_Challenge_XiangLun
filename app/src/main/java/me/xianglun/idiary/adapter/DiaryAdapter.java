package me.xianglun.idiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
        holder.moreOptionButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.diary_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.diary_action_delete) {
                    deleteDiaryRecord(diary.getDiaryId());
                    return true;
                } else if (item.getItemId() == R.id.diary_action_edit) {
                    // TODO: 2/17/2022 implement the edit button
                    createViewDiaryLayout(diary);
                    Toast.makeText(context, "edit diary", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
            popup.show();
        });
        if (diary.getImagePaths() != null && diary.getImagePaths().size() > 0) {
            if (!diary.getImagePaths().get(0).isEmpty()) {
                Glide.with(context).load(diary.getImagePaths().get(0)).into(holder.imageView);
                holder.imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void createViewDiaryLayout(DiaryModel diary) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_new_diary, null, false);
//
//        Toolbar toolbar = layout.findViewById(R.id.new_diary_toolbar);
//        toolbar.setTitle("View Diary");
//        toolbar.setTitleTextColor(Color.BLACK);
//
//        TextView dataAndTime = layout.findViewById(R.id.date_and_time_label);
//        String dataAndTimeString = diary.getDate() + "   " + diary.getTime();
//        dataAndTime.setText(dataAndTimeString);
//
//        TextView title = layout.findViewById(R.id.diary_title);
//        title.setText(diary.getTitle());
//
//        // TODO: 2/17/2022 Figure a way to open the new_diary_activity from here to let the user edit the diary
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    private void deleteDiaryRecord(String diaryId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("diaries").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child(diaryId);
        databaseReference.removeValue().addOnSuccessListener(unused -> Toast.makeText(context, "Diary deleted successfully", Toast.LENGTH_SHORT).show());
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView time;
        private final TextView title;
        private final TextView diaryMainText;
        private final ImageButton moreOptionButton;
        private final ImageView imageView;

        public DiaryViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            date = itemView.findViewById(R.id.adapter_diary_date);
            time = itemView.findViewById(R.id.adepter_diary_time);
            title = itemView.findViewById(R.id.adapter_diary_title);
            diaryMainText = itemView.findViewById(R.id.adapter_diary_text);
            moreOptionButton = itemView.findViewById(R.id.adepter_diary_more_option_button);
            imageView = itemView.findViewById(R.id.adapter_diary_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
