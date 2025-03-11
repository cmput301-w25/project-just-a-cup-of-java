package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.justacupofjavapersonal.R;
import java.util.ArrayList;

public class MoodActionsAdapter extends RecyclerView.Adapter<MoodActionsAdapter.MoodViewHolder> {
    private final ArrayList<String> moodList;
    private final Context context;
    private final OnMoodDeleteListener deleteListener;

    public MoodActionsAdapter(Context context, ArrayList<String> moodList, OnMoodDeleteListener deleteListener) {
        this.context = context;
        this.moodList = moodList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mood_list_item, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        String moodEntry = moodList.get(position);
        holder.moodTextView.setText(moodEntry);

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition(); // Ensure correct position
            if (currentPosition != RecyclerView.NO_POSITION) {
                deleteListener.onMoodDelete(currentPosition);
            }
        });
    }


    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView moodTextView;
        ImageButton deleteButton;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            moodTextView = itemView.findViewById(R.id.moodTextView);
            deleteButton = itemView.findViewById(R.id.deleteMoodButton);
        }
    }

    public interface OnMoodDeleteListener {
        void onMoodDelete(int position);
    }
}
