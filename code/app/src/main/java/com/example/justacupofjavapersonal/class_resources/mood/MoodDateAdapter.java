package com.example.justacupofjavapersonal.class_resources.mood;

import android.content.Intent;
import android.util.Log;
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
public class MoodDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<FeedItem> feedList;

    private static final int VIEW_TYPE_MOOD = 0;
    private static final int VIEW_TYPE_DATE = 1;

    public MoodDateAdapter(List<FeedItem> feedList) {
        this.feedList = feedList;
    }
    public void updateList(List<FeedItem> newList) {
        feedList.clear();
        feedList.addAll(newList);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for displaying mood-related data in a RecyclerView.
     * This class holds references to the views that will be populated with data.
     */
    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        ImageView socialSituation, moodIndicator;
        ImageView containsPhoto, containsExplanation, containsLocation;
        TextView feedName, feedTime;
        TextView socialSituationText,triggerText,whyFeelText;

    public MoodDateAdapter(List<FeedItem> feedItems) {
        this.feedItems = feedItems;
        public MoodViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            feedName = itemView.findViewById(R.id.feedName);
            feedTime = itemView.findViewById(R.id.feedTime);
            socialSituation = itemView.findViewById(R.id.socialSituation);
            moodIndicator = itemView.findViewById(R.id.moodIndicator);
            containsPhoto = itemView.findViewById(R.id.containsPhoto);
            containsExplanation = itemView.findViewById(R.id.containsExplanation);
            containsLocation = itemView.findViewById(R.id.containsLocation);
            socialSituationText = itemView.findViewById(R.id.socialSituationText);
            triggerText = itemView.findViewById(R.id.triggerText);
            whyFeelText = itemView.findViewById(R.id.whyFeelText);
        }
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
        if (holder instanceof MoodViewHolder) {
            MoodViewHolder moodHolder = (MoodViewHolder) holder; // ✅ move this up

            Mood mood = item.getMood();
            moodHolder.profilePicture.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), UserMoodActivity.class);
                intent.putExtra("userUid", mood.getUid());
                v.getContext().startActivity(intent);
            });

            moodHolder.feedName.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), UserMoodActivity.class);
                intent.putExtra("userUid", mood.getUid());
                v.getContext().startActivity(intent);
            });
            if (mood.getSocialSituation() != null && !mood.getSocialSituation().isEmpty()) {
                moodHolder.socialSituationText.setText("Social: " + mood.getSocialSituation());
                moodHolder.socialSituationText.setVisibility(View.VISIBLE);
            } else {
                moodHolder.socialSituationText.setVisibility(View.GONE);
            }

            // Show trigger text if not null
            if (mood.getTrigger() != null && !mood.getTrigger().isEmpty()) {
                moodHolder.triggerText.setText("Trigger: " + mood.getTrigger());
                moodHolder.triggerText.setVisibility(View.VISIBLE   );
            } else {
                moodHolder.triggerText.setVisibility(View.GONE);
            }

            // Show whyFeel text if not null
//            if (mood.getWhyFeel() != null && !mood.getWhyFeel().isEmpty()) {
//                moodHolder.whyFeelText.setText("Why: " + mood.getWhyFeel());
//                moodHolder.whyFeelText.setVisibility(View.VISIBLE);
//            } else {
//                moodHolder.whyFeelText.setVisibility(View.GONE);
//            }
            //moodHolder.profilePicture.setImageResource(mood.getUser().getProfilePicture());
                // on hold until database is put here
            if (mood.getSocialSituation() != null) {
                switch (mood.getSocialSituation()) {
                    case "ALONE":
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation1);
                        break;
                    case "WITH_ONE_OTHER":
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation2);
                        break;
                    case "WITH_TWO_TO_SEVERAL":
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation3);
                        break;
                    case "WITH_A_CROWD":
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation3);
                        break;
                    default:
                        moodHolder.socialSituation.setVisibility(View.GONE);
                        break;
                }
            }

        // Bind other fields from the Mood object
        moodHolder.emotionTextView.setText(mood.getEmotion());
        moodHolder.socialSituation.setText(mood.getSocialSituation());
        moodHolder.detailsTextView.setText(mood.getTime() + " • " + mood.getPrivacy());
        moodHolder.triggerTextView.setText("Reason: " + (mood.getTrigger() != null ? mood.getTrigger() : ""));
    }
            //moodHolder.feedName.setText(mood.getUid());
            String emotion = mood.getEmotion() != null ? mood.getEmotion() : "No emotion";
            moodHolder.feedName.setText(emotion);
            if (mood.getPostDate() != null) {
                long timeDifference = System.currentTimeMillis() - mood.getPostDate().getTime();
                long seconds = timeDifference / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (days > 0) {
                    moodHolder.feedTime.setText(days + " days ago");
                } else if (hours > 0) {
                    moodHolder.feedTime.setText(hours + " hours ago");
                } else if (minutes > 0) {
                    moodHolder.feedTime.setText(minutes + " minutes ago");
                } else if (seconds > 1) {
                    moodHolder.feedTime.setText(seconds + " seconds ago");
                } else {
                    moodHolder.feedTime.setText("now");
                }
            } else if (mood.getDate() != null) {
                moodHolder.feedTime.setText(mood.getDate()); // fallback to string-based date
            } else {
                moodHolder.feedTime.setText("Unknown date");
            }



            // i dont have any other moods rn lol
            if (mood.getState() != null) {
                switch (mood.getState()) {
                    case HAPPINESS:
                        moodHolder.moodIndicator.setImageResource(R.drawable.mood_happy);
                        break;
                    // add other emotion cases if needed
                    default:
                        moodHolder.moodIndicator.setVisibility(View.GONE);
                        break;
                }
            } else {
                moodHolder.moodIndicator.setVisibility(View.GONE);
            }


            moodHolder.containsPhoto.setVisibility(mood.hasPhoto() ? View.VISIBLE : View.GONE);
            moodHolder.containsExplanation.setVisibility(mood.hasTrigger() ? View.VISIBLE : View.GONE);
            moodHolder.containsLocation.setVisibility(mood.hasLocation() ? View.VISIBLE : View.GONE);
        } else if (holder instanceof DateViewHolder) {
            ((DateViewHolder) holder).dayText.setText(item.getDateHeader());
        }

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in the feed list.
     */
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