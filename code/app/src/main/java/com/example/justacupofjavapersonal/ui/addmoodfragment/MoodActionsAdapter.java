package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class MoodActionsAdapter extends RecyclerView.Adapter<MoodActionsAdapter.MoodViewHolder> {
    private final ArrayList<Mood> moodList;
    private final Context context;
    private final OnMoodDeleteListener deleteListener;
    private final OnMoodEditListener editListener;

    /**
     * Constructor for the MoodActionsAdapter.
     *
     * @param context the context of the adapter
     * @param moodList the list of moods to display
     * @param deleteListener the listener for the delete button
     * @param editListener the listener for the edit button
     */
    public MoodActionsAdapter(Context context, ArrayList<Mood> moodList,
                              OnMoodDeleteListener deleteListener,
                              OnMoodEditListener editListener) {
        this.context = context;
        this.moodList = moodList;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    @NonNull
    @Override
    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mood_list_item, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        Mood mood = moodList.get(position);

        holder.socialSituation.setText(mood.getSocialSituation());
        holder.emotionTextView.setText(mood.getEmotion());
        holder.detailsTextView.setText(mood.getTime() + " • " + mood.getPrivacy());
        holder.triggerTextView.setText("Reason: " + mood.getTrigger());

        holder.socialSituation.setText(mood.getSocialSituation());
        holder.emotionTextView.setText(mood.getEmotion());
        holder.detailsTextView.setText(mood.getTime() + " • " + mood.getPrivacy());
        holder.triggerTextView.setText("Reason: " + mood.getTrigger());

        // Delete button
        // Show or hide the "View Image" button based on whether a photo exists
        if (mood.getPhoto() != null && !mood.getPhoto().isEmpty()) {
            holder.viewImageButton.setVisibility(View.VISIBLE);
        } else {
            holder.viewImageButton.setVisibility(View.GONE);
        }

        // "View Image" button click listener
        holder.viewImageButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_image_view);

            ImageView imageView = dialog.findViewById(R.id.dialogImageView);
            ImageButton closeButton = dialog.findViewById(R.id.closeButton);
            if (mood.getPhoto() != null) {
                try {
                    byte[] decodedString = Base64.decode(mood.getPhoto(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    imageView.setImageBitmap(decodedByte);
                } catch (Exception e) {
                    imageView.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            closeButton.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });

        // Delete button
        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                deleteListener.onMoodDelete(currentPosition);
            }
        });

        // Edit button
        holder.editButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                editListener.onMoodEdit(currentPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView emotionTextView, socialSituation, detailsTextView, triggerTextView;
        ImageButton deleteButton, editButton;
        Button viewImageButton; // Changed to Button to avoid ClassCastException
        TextView emotionTextView, socialSituation, detailsTextView, triggerTextView;
        ImageButton deleteButton, editButton;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            emotionTextView = itemView.findViewById(R.id.emotionTextView);
            socialSituation = itemView.findViewById(R.id.socialSituation);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            triggerTextView = itemView.findViewById(R.id.triggerTextView);
            deleteButton = itemView.findViewById(R.id.deleteMoodButton);
            editButton = itemView.findViewById(R.id.editMoodButton);
            viewImageButton = itemView.findViewById(R.id.viewImageButton);
            editButton = itemView.findViewById(R.id.editMoodButton); // 🔹 Make sure this exists in mood_list_item.xml
        }
    }

    public interface OnMoodDeleteListener {
        void onMoodDelete(int position);
    }

    public interface OnMoodEditListener {
        void onMoodEdit(int position);
    }
}