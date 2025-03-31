//package com.example.justacupofjavapersonal.ui.addmoodfragment;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.justacupofjavapersonal.R;
//import com.example.justacupofjavapersonal.class_resources.mood.Mood;
//
//import java.util.ArrayList;
//
///**
// * Adapter class for the RecyclerView in the AddMoodEventFragment.
// * This class is responsible for creating the view holders for the RecyclerView and binding the data to them.
// */
//public class MoodActionsAdapter extends RecyclerView.Adapter<MoodActionsAdapter.MoodViewHolder> {
//    private final ArrayList<Mood> moodList;
//    private final Context context;
//    private final OnMoodDeleteListener deleteListener;
//
//    /**
//     * Constructor for the MoodActionsAdapter.
//     *
//     * @param context the context of the adapter
//     * @param moodList the list of moods to display
//     * @param deleteListener the listener for the delete button
//     */
//    public MoodActionsAdapter(Context context, ArrayList<Mood> moodList, OnMoodDeleteListener deleteListener) {
//        this.context = context;
//        this.moodList = moodList;
//        this.deleteListener = deleteListener;
//    }
//
//    /**
//     * Creates a new MoodViewHolder object.
//     *
//     * @param parent the parent view group
//     * @param viewType the view type
//     * @return a new MoodViewHolder object
//     */
//    @NonNull
//    @Override
//    public MoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.mood_list_item, parent, false);
//        return new MoodViewHolder(view);
//    }
//
//    /**
//     * Binds the data to the view holder at the specified position.
//     *
//     * @param holder the view holder to bind the data to
//     * @param position the position of the view holder in the RecyclerView
//     */
//    @Override
//    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
//        Mood mood = moodList.get(position);
//
//        // Bind data to the views based on the new XML structure
//        holder.socialSituation.setText(mood.getSocialSituation());
//        holder.emotionTextView.setText(mood.getEmotion());
//        holder.detailsTextView.setText(mood.getTime() + " • " + mood.getPrivacy());
//        holder.triggerTextView.setText("Reason: " + mood.getTrigger());
//
//        // Set the click listener for delete button
//        holder.deleteButton.setOnClickListener(v -> {
//            int currentPosition = holder.getAdapterPosition();
//            if (currentPosition != RecyclerView.NO_POSITION) {
//                deleteListener.onMoodDelete(currentPosition);
//            }
//        });
//    }
//
//    /**
//     * Returns the number of items in the RecyclerView.
//     *
//     * @return the number of items in the RecyclerView
//     */
//    @Override
//    public int getItemCount() {
//        return moodList.size();
//    }
//
//    /**
//     * ViewHolder class for displaying moods in a RecyclerView.
//     * This class holds the reference to the TextView that displays the mood and the delete button.
//     */
//    public static class MoodViewHolder extends RecyclerView.ViewHolder {
//        TextView emotionTextView, socialSituation, detailsTextView, triggerTextView;
//        ImageButton deleteButton;
//
//        public MoodViewHolder(@NonNull View itemView) {
//            super(itemView);
//            emotionTextView = itemView.findViewById(R.id.emotionTextView);
//            socialSituation = itemView.findViewById(R.id.socialSituation);
//            detailsTextView = itemView.findViewById(R.id.detailsTextView);
//            triggerTextView = itemView.findViewById(R.id.triggerTextView);
//            deleteButton = itemView.findViewById(R.id.deleteMoodButton);
//        }
//    }
//
//    /**
//     * Interface for the delete button click listener.
//     */
//    public interface OnMoodDeleteListener {
//        void onMoodDelete(int position);
//    }
//}






package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import androidx.recyclerview.widget.RecyclerView;
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.ui.comments.CommentBottomSheet;
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
        View view = LayoutInflater.from(context).inflate(R.layout.mood_card_item, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoodViewHolder holder, int position) {
        Mood mood = moodList.get(position);

        holder.socialSituation.setText(mood.getSocialSituation());
        holder.emotionTextView.setText(mood.getEmotion());
        holder.detailsTextView.setText(mood.getTime() + " • " + mood.getPrivacy());
        holder.triggerTextView.setText("Reason: " + mood.getTrigger());

        // Show/hide image view button based on whether a photo exists
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
            ImageButton closeButton = dialog.findViewById(R.id.closeButton);

            try {
                byte[] decodedBytes = Base64.decode(mood.getPhoto(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            closeButton.setOnClickListener(v2 -> dialog.dismiss());
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

        holder.commentButton.setOnClickListener(v -> {
            String moodId = mood.getMoodID();  // Make sure this returns the correct mood ID
            if (moodId != null && !moodId.isEmpty()) {
                CommentBottomSheet bottomSheet = new CommentBottomSheet(moodId);
                if (context instanceof FragmentActivity) {
                    FragmentActivity activity = (FragmentActivity) context;
                    bottomSheet.show(activity.getSupportFragmentManager(), "CommentBottomSheet");
                }
            }
        });

        holder.commentButton.setColorFilter(Color.parseColor("#E0E0E0"));


    }

    @Override
    public int getItemCount() {
        return moodList.size();
    }

    public static class MoodViewHolder extends RecyclerView.ViewHolder {
        TextView emotionTextView, socialSituation, detailsTextView, triggerTextView;
        ImageButton deleteButton, editButton, commentButton , viewImageButton;

        public MoodViewHolder(@NonNull View itemView) {
            super(itemView);
            emotionTextView = itemView.findViewById(R.id.emotionTextView);
            socialSituation = itemView.findViewById(R.id.socialSituation);
            detailsTextView = itemView.findViewById(R.id.detailsTextView);
            triggerTextView = itemView.findViewById(R.id.triggerTextView);
            deleteButton = itemView.findViewById(R.id.deleteMoodButton);
            editButton = itemView.findViewById(R.id.editMoodButton); // 🔹 Make sure this exists in mood_list_item.xml
            viewImageButton = itemView.findViewById(R.id.viewImageButton);
            commentButton = itemView.findViewById(R.id.commentButton);


        }
    }

    public interface OnMoodDeleteListener {
        void onMoodDelete(int position);
    }

    public interface OnMoodEditListener {
        void onMoodEdit(int position);
    }
}
