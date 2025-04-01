package com.example.justacupofjavapersonal.ui.follow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.User;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying a list of users with a follow/unfollow button.
 *
 * <p>This adapter binds user data to a RecyclerView, allowing the user to follow or unfollow others.
 * It also handles the display of follow request statuses (e.g., "Requested", "Following").
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private List<String> requests;
    private OnItemClickListener listener;
    private FirebaseDB db;

    /**
     * Constructor for UserAdapter.
     *
     * @param userList The list of users to display in the RecyclerView.
     * @param listener A listener for handling click events on follow buttons.
     * @param db       The Firebase database instance for making database requests.
     */
    public UserAdapter(List<User> userList, OnItemClickListener listener, FirebaseDB db) {
        this.userList = userList;
        this.listener = listener;
        this.db = db;
    }

    /**
     * Interface for handling follow button click events.
     */
    public interface OnItemClickListener {
        /**
         * Called when the follow button is clicked.
         *
         * @param position The position of the item in the RecyclerView.
         */
        void onFollowClick(int position);
    }

    /**
     * Creates a new ViewHolder for a user item.
     *
     * @param parent   The parent ViewGroup where the item view will be added.
     * @param viewType The type of the view.
     * @return A new instance of UserViewHolder.
     */
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_follow, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for a given position.
     *
     * @param holder   The ViewHolder to bind the data to.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        holder.bind(userList.get(position), listener, position);
    }


    /**
     * Returns the total number of items in the user list.
     *
     * @return The number of items in the user list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder class that holds references to the views for displaying user data.
     */
    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView userName;
        Button followButton;

        /**
         * Constructor for UserViewHolder.
         *
         * @param itemView The view for a single user item.
         */
        public UserViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            userName = itemView.findViewById(R.id.userName);
            followButton = itemView.findViewById(R.id.follow_button);
        }

        /**
         * Binds the user data to the views and sets the follow button behavior.
         *
         * @param user     The user data to bind.
         * @param listener The listener for follow button clicks.
         * @param position The position of the item in the list.
         */
        public void bind(User user, OnItemClickListener listener, int position) {
            userName.setText(user.getName());
            requests = new ArrayList<>();

            if (FirebaseAuth.getInstance().getCurrentUser() != null){

                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                db.getAllRequestedIds(currentUserID, requestedIds -> {
                    db.getAllFollowingIds(currentUserID, followingIds -> {
                        if (followingIds.contains(user.getUid())) {
                            followButton.setText("Following");
                        } else if (requestedIds.contains(user.getUid())) {
                            followButton.setText("Requested");
                        } else {
                            followButton.setText("Follow");
                        }
                    });
                });

                //Set user profile picture

                followButton.setOnClickListener(v -> {
                    listener.onFollowClick(position);

                    if (followButton.getText().toString().equals("Follow")) {
                        followButton.setText(followButton.getText().toString().equals("Follow") ? "Requested" : "Follow");
                    } else if (followButton.getText().toString().equals("Following")) {
                        followButton.setText("Follow");
                        db.removeFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
                    } else if (followButton.getText().toString().equals("Requested")) {
                        db.removeRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
                        followButton.setText("Follow");
                    }
                    if (!requests.contains(user.getUid())) {
                        db.sendRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
                    }
                });
            }

        }
    }
}
