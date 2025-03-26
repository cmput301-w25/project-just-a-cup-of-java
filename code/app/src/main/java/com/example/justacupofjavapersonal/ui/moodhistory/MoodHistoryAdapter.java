package com.example.justacupofjavapersonal.ui.moodhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;

import java.util.ArrayList;

/**
 * Adapter class for the RecyclerView in the AddMoodEventFragment.
 * This class is responsible for creating the view holders for the RecyclerView and binding the data to them.
 */
public class MoodHistoryAdapter extends RecyclerView.Adapter<MoodHistoryAdapter.MoodViewHolder> {
    private final ArrayList<Mood> moodList;
    private final Context context;
    private final OnMoodDeleteListener deleteListener;

    /**
     * Constructor for the MoodActionsAdapter.
     *
     * @param context the context of the adapter
     * @param moodList the list of moods to display
     * @param deleteListener the listener for the delete button
     */
    public MoodHistoryAdapter(Context context, ArrayList<Mood> moodList, OnMoodDeleteListener deleteListener) {
        this.context = context;
        this.moodList = moodList;
        this.deleteListener = deleteListener;
    }

    /**
     * Creates a new MoodViewHolder object.
     *
     * @param parent the parent view group
     * @param viewType the view type
     * @return a new MoodViewHolder object
     */
    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mood_history_item, parent, false);
        return new MoodViewHolder(view);
    }

    /**
     * Binds the data to the view holder at the specified position.
     *
     * @param holder the view holder to bind the data to
     * @param position the position of the view holder in the RecyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        Mood mood = moodList.get(position);

        holder.dateTextView.setText("Posted on: " + mood.getDate());
        holder.emotionTextView.setText(mood.getEmotion());

        String details =  mood.getTime() + " • " + mood.getPrivacy();
        holder.detailsTextView.setText(details);

        holder.socialSituation.setText(mood.getSocialSituation());

        holder.triggerTextView.setText("Reason: " + mood.getTrigger());

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                deleteListener.onMoodDelete(currentPosition);
            }
        });

        // Optional: set edit button functionality later
        holder.editButton.setOnClickListener(v -> {
            // TODO: Implement edit logic
        });
    }

    /**
     * Returns the number of items in the RecyclerView.
     *
     * @return the number of items in the RecyclerView
     */
    @Override
    public int getItemCount() {
        return moodList.size();
    }
    
    /**
     * ViewHolder class for displaying moods in a RecyclerView.
     * This class holds the reference to the TextView that displays the mood and the delete button.
     */
    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView emotionTextView;
        TextView detailsTextView;
        TextView triggerTextView;
        TextView socialSituation;
        ImageButton deleteButton;
        ImageButton editButton; // You can wire this later if needed

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            emotionTextView = itemView.findViewById(R.id.emotionTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            triggerTextView = itemView.findViewById(R.id.triggerTextView);
            socialSituation = itemView.findViewById(R.id.socialSituation);
            deleteButton = itemView.findViewById(R.id.deleteMoodButton);
            editButton = itemView.findViewById(R.id.editMoodButton);
        }
    }


    /**
     * Interface for the delete button click listener.
     */
    public interface OnMoodDeleteListener {
        void onMoodDelete(int position);
    }
}