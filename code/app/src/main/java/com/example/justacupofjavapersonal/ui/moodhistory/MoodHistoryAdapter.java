package com.example.justacupofjavapersonal.ui.moodhistory;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.ui.comments.CommentBottomSheet;

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
        View view = LayoutInflater.from(context).inflate(R.layout.mood_card_item, parent, false);
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


        // Show/hide image view button based on photo presence
        if (mood.getPhoto() != null && !mood.getPhoto().isEmpty()) {
            holder.viewImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.viewImageButton.setVisibility(View.GONE);
        }

        // View Image click handler
        holder.viewImageButton.setOnClickListener(v -> {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dialog_image_view);

                    ImageView imageView = dialog.findViewById(R.id.dialogImageView);
                    ImageButton closeButton = dialog.findViewById(R.id.closeButton); // ✅ Button not ImageButton
                    try {
                        byte[] decodedString = Base64.decode(mood.getPhoto(), Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imageView.setImageBitmap(decodedByte);
                    } catch (Exception e) {
                        imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                    }

            closeButton.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                deleteListener.onMoodDelete(currentPosition);
            }
        });

        // Optional: set edit button functionality later
        holder.editButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("moodToEdit", mood); // Mood must implement Serializable
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_post_mood, bundle);
        });


        holder.commentButton.setOnClickListener(v -> {
            CommentBottomSheet bottomSheet = new CommentBottomSheet(mood.getMoodID());
            FragmentActivity activity = (FragmentActivity) context;
            bottomSheet.show(activity.getSupportFragmentManager(), "CommentBottomSheet");
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
        ImageButton commentButton;

        Button viewImageButton;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            emotionTextView = itemView.findViewById(R.id.emotionTextView);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            triggerTextView = itemView.findViewById(R.id.triggerTextView);
            socialSituation = itemView.findViewById(R.id.socialSituation);
            deleteButton = itemView.findViewById(R.id.deleteMoodButton);
            editButton = itemView.findViewById(R.id.editMoodButton);
            commentButton = itemView.findViewById(R.id.commentButton);
            viewImageButton = itemView.findViewById(R.id.viewImageButton);
        }
    }


    /**
     * Interface for the delete button click listener.
     */
    public interface OnMoodDeleteListener {
        void onMoodDelete(int position);
    }

    public void updateMoodList(ArrayList<Mood> newMoodList) {
        this.moodList.clear();
        this.moodList.addAll(newMoodList);
        notifyDataSetChanged();
    }
}