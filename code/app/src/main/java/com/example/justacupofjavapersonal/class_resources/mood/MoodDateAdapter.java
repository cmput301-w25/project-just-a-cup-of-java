package com.example.justacupofjavapersonal.class_resources.mood;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;

import java.util.List;

public class MoodDateAdapter extends RecyclerView.Adapter<MoodDateAdapter.MoodViewHolder> {
    private final List<FeedItem> feedItems;

    public MoodDateAdapter(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
    }

    public void updateList(List<FeedItem> newList) {
        feedItems.clear();
        feedItems.addAll(newList);
        notifyDataSetChanged();
    }

    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture, socialSituationIcon, moodIndicator;
        ImageView containsPhoto, containsExplanation, containsLocation;
        TextView feedName, feedTime;
        TextView socialSituationText, triggerText;

        public MoodViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            feedName = itemView.findViewById(R.id.feedName);
            feedTime = itemView.findViewById(R.id.feedTime);
            socialSituationIcon = itemView.findViewById(R.id.socialSituation);
            moodIndicator = itemView.findViewById(R.id.moodIndicator);
            containsPhoto = itemView.findViewById(R.id.containsPhoto);
            containsExplanation = itemView.findViewById(R.id.containsExplanation);
            containsLocation = itemView.findViewById(R.id.containsLocation);
            socialSituationText = itemView.findViewById(R.id.socialSituationText);
            triggerText = itemView.findViewById(R.id.triggerText);
        }
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mood_list_item, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        FeedItem item = feedItems.get(position);
        Mood mood = item.getMood();

        holder.feedName.setText(mood.getEmotion() != null ? mood.getEmotion() : "No emotion");

        if (mood.getPostDate() != null) {
            long timeDifference = System.currentTimeMillis() - mood.getPostDate().getTime();
            long seconds = timeDifference / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) holder.feedTime.setText(days + " days ago");
            else if (hours > 0) holder.feedTime.setText(hours + " hours ago");
            else if (minutes > 0) holder.feedTime.setText(minutes + " minutes ago");
            else if (seconds > 1) holder.feedTime.setText(seconds + " seconds ago");
            else holder.feedTime.setText("now");
        } else if (mood.getDate() != null) {
            holder.feedTime.setText(mood.getDate());
        } else {
            holder.feedTime.setText("Unknown date");
        }

        if (mood.getSocialSituation() != null && !mood.getSocialSituation().isEmpty()) {
            holder.socialSituationText.setText("Social: " + mood.getSocialSituation());
            holder.socialSituationText.setVisibility(View.VISIBLE);
        } else {
            holder.socialSituationText.setVisibility(View.GONE);
        }

        if (mood.getTrigger() != null && !mood.getTrigger().isEmpty()) {
            holder.triggerText.setText("Trigger: " + mood.getTrigger());
            holder.triggerText.setVisibility(View.VISIBLE);
        } else {
            holder.triggerText.setVisibility(View.GONE);
        }

        if (mood.getSocialSituation() != null) {
            switch (mood.getSocialSituation()) {
                case "ALONE":
                    holder.socialSituationIcon.setImageResource(R.drawable.socialsituation1);
                    break;
                case "WITH_ONE_OTHER":
                    holder.socialSituationIcon.setImageResource(R.drawable.socialsituation2);
                    break;
                case "WITH_TWO_TO_SEVERAL":
                case "WITH_A_CROWD":
                    holder.socialSituationIcon.setImageResource(R.drawable.socialsituation3);
                    break;
                default:
                    holder.socialSituationIcon.setVisibility(View.GONE);
                    break;
            }
        }

        if (mood.getState() != null) {
            switch (mood.getState()) {
                case HAPPINESS:
                    holder.moodIndicator.setImageResource(R.drawable.mood_happy);
                    break;
                // Add other cases as needed
                default:
                    holder.moodIndicator.setVisibility(View.GONE);
            }
        } else {
            holder.moodIndicator.setVisibility(View.GONE);
        }

        holder.containsPhoto.setVisibility(mood.hasPhoto() ? View.VISIBLE : View.GONE);
        holder.containsExplanation.setVisibility(mood.hasTrigger() ? View.VISIBLE : View.GONE);
        holder.containsLocation.setVisibility(mood.hasLocation() ? View.VISIBLE : View.GONE);

        // Profile navigation
        holder.profilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UserMoodActivity.class);
            intent.putExtra("userUid", mood.getUid());
            v.getContext().startActivity(intent);
        });

        holder.feedName.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UserMoodActivity.class);
            intent.putExtra("userUid", mood.getUid());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }
}
