package com.example.justacupofjavapersonal.ui.mood;

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

/**
 * Adapter class for the RecyclerView in the AddMoodEventFragment.
 * This class is responsible for creating the view holders for the RecyclerView and binding the data to them.
 */
public class MoodActionsAdapter extends RecyclerView.Adapter<MoodActionsAdapter.MoodViewHolder> {
    private final ArrayList<String> moodList;
    private final Context context;
    private final OnMoodDeleteListener deleteListener;

    /**
     * Constructor for the MoodActionsAdapter.
     *
     * @param context the context of the adapter
     * @param moodList the list of moods to display
     * @param deleteListener the listener for the delete button
     */
    public MoodActionsAdapter(Context context, ArrayList<String> moodList, OnMoodDeleteListener deleteListener) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.mood_list_item, parent, false);
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
        TextView moodTextView;
        ImageButton deleteButton;

        /**
         * Constructor for the MoodViewHolder.
         *
         * @param itemView the view for the view holder
         */
        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            moodTextView = itemView.findViewById(R.id.moodTextView);
            deleteButton = itemView.findViewById(R.id.deleteMoodButton);
        }
    }

    /**
     * Interface for the delete button click listener.
     */
    public interface OnMoodDeleteListener {
        void onMoodDelete(int position);
    }
}