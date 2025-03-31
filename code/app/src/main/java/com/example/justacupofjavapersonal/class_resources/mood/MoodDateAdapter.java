package com.example.justacupofjavapersonal.class_resources.mood;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import com.example.justacupofjavapersonal.ui.comments.CommentBottomSheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;

import java.util.List;

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
        ImageButton commentButton;




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
            commentButton = itemView.findViewById(R.id.CommentButton);




        }
    }

    /**
     * ViewHolder class for displaying date information in a RecyclerView.
     * This class holds the reference to the TextView that displays the day.
     */
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayText;

        public DateViewHolder(View itemView) {
            super(itemView);
            dayText = itemView.findViewById(R.id.dayTextView);
        }
    }

    /**
     * Returns the view type of the item at the specified position.
     *
     * @param position The position of the item within the adapter's data set.
     * @return An integer representing the view type of the item.
     *         Returns VIEW_TYPE_MOOD if the item at the specified position is a mood,
     *         otherwise returns VIEW_TYPE_DATE.
     */
    @Override
    public int getItemViewType(int position) {
        return feedList.get(position).isMood() ? VIEW_TYPE_MOOD : VIEW_TYPE_DATE;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent an item.
     * This method will either create a new {@link MoodViewHolder} or {@link DateViewHolder} depending on the view type.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MOOD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mood_feed, parent, false);
            return new MoodViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_feed, parent, false);
            return new DateViewHolder(view);
        }
    }

    /**
     * Binds the data to the ViewHolder for the given position in the RecyclerView.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FeedItem item = feedList.get(position);

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

            // Handle comment button popup
//            moodHolder.commentButton.setOnClickListener(view -> {
//                LayoutInflater inflater = LayoutInflater.from(view.getContext());
//                View popupView = inflater.inflate(R.layout.popup_comment, null);
//
//                EditText commentInput = popupView.findViewById(R.id.commentInput);
//                Button postButton = popupView.findViewById(R.id.postCommentButton);
//
//                AlertDialog dialog = new AlertDialog.Builder(view.getContext())
//                        .setView(popupView)
//                        .create();
//
//                postButton.setOnClickListener(p -> {
//                    String commentText = commentInput.getText().toString().trim();
//                    if (!commentText.isEmpty()) {
//                        FirebaseDB db = new FirebaseDB();
//                        db.addCommentToMood(mood.getMoodID(), commentText);
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
//            });

            moodHolder.commentButton.setOnClickListener(view -> {
                CommentBottomSheet bottomSheet = new CommentBottomSheet(mood.getMoodID());
                FragmentActivity activity = (FragmentActivity) view.getContext();
                bottomSheet.show(activity.getSupportFragmentManager(), "CommentBottomSheet");
            });


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
        return feedList.size();
    }
}
