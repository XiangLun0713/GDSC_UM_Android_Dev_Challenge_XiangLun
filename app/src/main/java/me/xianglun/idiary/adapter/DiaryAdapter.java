package me.xianglun.idiary.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import me.xianglun.idiary.R;
import me.xianglun.idiary.model.DiaryModel;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private final Context context;
    private final ArrayList<DiaryModel> diaryList;

    public DiaryAdapter(Context context, ArrayList<DiaryModel> diaryList) {
        this.context = context;
        this.diaryList = diaryList;
    }

    @NonNull
    @Override
    public DiaryAdapter.DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.template_diary, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryAdapter.DiaryViewHolder holder, int position) {
        DiaryModel diary = diaryList.get(position);
        holder.title.setText(diary.getTitle());
        holder.diaryMainText.setText(diary.getDiaryMainText());
        holder.time.setText(diary.getTime());
        holder.date.setText(diary.getDate());
        holder.moreOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "It works!", Toast.LENGTH_SHORT).show();
            }
        });
        if (diary.getImageURL().size() > 0) {
            if (diary.getImageURL().get(0) != null) {
                holder.imageView.setImageURI(Uri.parse(diary.getImageURL().get(0)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public class DiaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView date;
        private final TextView time;
        private final TextView title;
        private final TextView diaryMainText;
        private final ImageButton moreOptionButton;
        private final ImageView imageView;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.adapter_diary_date);
            time = itemView.findViewById(R.id.adepter_diary_time);
            title = itemView.findViewById(R.id.adapter_diary_title);
            diaryMainText = itemView.findViewById(R.id.adapter_diary_text);
            moreOptionButton = itemView.findViewById(R.id.adepter_diary_more_option_button);
            imageView = itemView.findViewById(R.id.adapter_diary_image);
        }
    }
}
