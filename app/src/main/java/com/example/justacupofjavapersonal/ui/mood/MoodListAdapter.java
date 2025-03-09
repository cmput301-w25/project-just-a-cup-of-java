package com.example.justacupofjavapersonal.ui.mood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.justacupofjavapersonal.R;
import java.util.List;

public class MoodListAdapter extends RecyclerView.Adapter<MoodListAdapter.ViewHolder> {
    private final List<String> moodList;
    private final Context context;

    public MoodListAdapter(Context context, List<String> moodList) {
        this.context = context;
        this.moodList = moodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mood, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String mood = moodList.get(position);
        holder.moodTextView.setText(mood);

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            moodList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, moodList.size());
        });
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView moodTextView;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moodTextView = itemView.findViewById(R.id.moodText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
