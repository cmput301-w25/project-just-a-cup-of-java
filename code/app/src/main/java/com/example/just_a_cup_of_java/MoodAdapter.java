package com.example.just_a_cup_of_java;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MoodAdapter extends RecyclerView.Adapter<MoodAdapter.MoodViewHolder> {

    private List<Mood> moodList;
    private FirebaseFirestore db;

    public MoodAdapter(List<Mood> moodList) {
        this.moodList = moodList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        Mood mood = moodList.get(position);

        // Load user profile picture using Glide (Firebase Storage URL)
        if (mood.getUsername().getProfilePic() != null && !mood.getUsername().getProfilePic().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(mood.getUsername().getProfilePic())
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .into(holder.profilePic);
        } else {
            holder.profilePic.setImageResource(R.drawable.baseline_account_circle_24);
        }

        // Set username
        holder.username.setText(mood.getUsername().getUsername());

        // Set emoji for mood state
        holder.emoji.setText(mood.getState().getEmoticon());

        // Set mood description (trigger if available)
        holder.moodDescription.setText(mood.getTrigger() != null ? mood.getTrigger() : "No description");

        // Set background color based on emotional state
        holder.itemView.setBackgroundColor(Color.parseColor(mood.getState().getColor()));

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> deleteMood(mood, holder.getAdapterPosition()));
    }

    private void deleteMood(Mood mood, int position) {
        db.collection("Moods").document(mood.getMoodID().toString())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    moodList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    // Handle deletion failure
                });
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic, deleteButton;
        TextView username, moodDescription, emoji;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profilePic);
            username = itemView.findViewById(R.id.username);
            moodDescription = itemView.findViewById(R.id.moodDescription);
            emoji = itemView.findViewById(R.id.emoji);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}