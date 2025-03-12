package com.example.justacupofjavapersonal.class_resources.mood;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;

import java.util.List;

public class MoodDateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<FeedItem> feedList;

    private static final int VIEW_TYPE_MOOD = 0;
    private static final int VIEW_TYPE_DATE = 1;

    public MoodDateAdapter(List<FeedItem> feedList) {
        this.feedList = feedList;
    }

    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        ImageView socialSituation, moodIndicator;
        ImageView containsPhoto, containsExplanation, containsLocation;
        TextView feedName, feedTime;

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
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        public DateViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayTextView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return feedList.get(position).isMood() ? VIEW_TYPE_MOOD : VIEW_TYPE_DATE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MOOD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood_feed, parent, false);
            return new MoodViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_feed, parent, false);
            return new DateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FeedItem item = feedList.get(position);
        
        if (holder instanceof MoodViewHolder) {
            Mood mood = item.getMood();
            MoodViewHolder moodHolder = (MoodViewHolder) holder;
            //moodHolder.profilePicture.setImageResource(mood.getUser().getProfilePicture());
                // on hold until database is put here
            if (mood.getSocialSituation() != null) {
                switch (mood.getSocialSituation()) {
                    case ALONE:
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation1);
                        break;
                    case WITH_ONE_OTHER:
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation2);
                        break;
                    case WITH_TWO_TO_SEVERAL:
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation3);
                        break;
                    case WITH_A_CROWD:
                        moodHolder.socialSituation.setImageResource(R.drawable.socialsituation3);
                        break;
                    default:
                        moodHolder.socialSituation.setVisibility(View.GONE);
                        break;
                }
            }

            moodHolder.feedName.setText(mood.getUid());
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
            } else if (seconds > 1){
                moodHolder.feedTime.setText(seconds + " seconds ago");
            } else {
                moodHolder.feedTime.setText("now");
            }
            
            
            // i dont have any other moods rn lol
            switch (mood.getState()) {
                case HAPPINESS:
                    moodHolder.moodIndicator.setImageResource(R.drawable.mood_happy);
                    break;
                default:
                    moodHolder.moodIndicator.setVisibility(View.GONE);
                    break;
            }

            moodHolder.containsPhoto.setVisibility(mood.hasPhoto() ? View.VISIBLE : View.GONE);
            moodHolder.containsExplanation.setVisibility(mood.hasTrigger() ? View.VISIBLE : View.GONE);
            moodHolder.containsLocation.setVisibility(mood.hasLocation() ? View.VISIBLE : View.GONE);
        } else if (holder instanceof DateViewHolder) {
            ((DateViewHolder) holder).dayText.setText(item.getDateHeader());
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }
}
