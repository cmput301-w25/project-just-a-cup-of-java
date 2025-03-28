package com.example.justacupofjavapersonal.class_resources.mood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;

import java.util.List;

public class MoodDateAdapter extends RecyclerView.Adapter<MoodDateAdapter.MoodHolder> {
    private List<FeedItem> feedItems; // Changed to List<FeedItem>

    public MoodDateAdapter(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
    }

    @NonNull
    @Override
    public MoodHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mood_list_item, parent, false);
        return new MoodHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodHolder moodHolder, int position) {
        FeedItem feedItem = feedItems.get(position);
        Mood mood = feedItem.getMood(); // Access the Mood object from FeedItem

        // Update to use getTrigger() as fixed previously
        moodHolder.containsExplanation.setVisibility(mood.getTrigger() != null ? View.VISIBLE : View.GONE);

        // Bind other fields from the Mood object
        moodHolder.emotionTextView.setText(mood.getEmotion());
        moodHolder.socialSituation.setText(mood.getSocialSituation());
        moodHolder.detailsTextView.setText(mood.getTime() + " • " + mood.getPrivacy());
        moodHolder.triggerTextView.setText("Reason: " + (mood.getTrigger() != null ? mood.getTrigger() : ""));
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public static class MoodHolder extends RecyclerView.ViewHolder {
        TextView emotionTextView, socialSituation, detailsTextView, triggerTextView;
        View containsExplanation; // Assumed to be a View (e.g., a layout containing the explanation)

        public MoodHolder(@NonNull View itemView) {
            super(itemView);
            emotionTextView = itemView.findViewById(R.id.emotionTextView);
            socialSituation = itemView.findViewById(R.id.socialSituation);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            triggerTextView = itemView.findViewById(R.id.triggerTextView);
            containsExplanation = itemView.findViewById(R.id.triggerTextView); // Adjust based on your actual layout
        }
    }
}