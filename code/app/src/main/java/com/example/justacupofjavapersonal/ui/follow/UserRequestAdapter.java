package com.example.justacupofjavapersonal.ui.follow;

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

import java.util.List;

/**
 * Adapter for displaying user follow requests in a RecyclerView.
 *
 * <p>This adapter allows the user to accept or deny follow requests and updates the UI accordingly.</p>
 */
public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.UserRequestViewHolder> {
    private List<User> userList;
    private OnItemClickListener listener;
    private FirebaseDB db;

    /**
     * Constructor for UserRequestAdapter.
     *
     * @param userList The list of users who have sent follow requests.
     * @param listener A listener for handling accept and deny button clicks.
     * @param db       The Firebase database instance for handling request operations.
     */
    public UserRequestAdapter(List<User> userList, OnItemClickListener listener, FirebaseDB db) {
        this.userList = userList;
        this.listener = listener;
        this.db = db;
    }

    /**
     * Interface for handling user follow request actions.
     */
    public interface OnItemClickListener {
        /**
         * Called when the accept button is clicked.
         *
         * @param position The position of the item in the RecyclerView.
         */
        void onAcceptClick(int position);
        /**
         * Called when the deny button is clicked.
         *
         * @param position The position of the item in the RecyclerView.
         */
        void onDenyClick(int position);
    }

    /**
     * Creates a new ViewHolder for a follow request item.
     *
     * @param parent   The parent ViewGroup where the item view will be added.
     * @param viewType The type of the view.
     * @return A new instance of UserRequestViewHolder.
     */
    @NonNull
    @Override
    public UserRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow_requests, parent, false);
        return new UserRequestViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder for a given position.
     *
     * @param holder   The ViewHolder to bind the data to.
     * @param position The position of the item in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull UserRequestAdapter.UserRequestViewHolder holder, int position) {
        holder.bind(userList.get(position), listener, position);
    }

    /**
     * Returns the total number of follow request items.
     *
     * @return The number of items in the user list.
     */
    @Override
    public int getItemCount() {
        return userList.size();
    }


    /**
     * ViewHolder class that holds references to the views for displaying follow request data.
     */
    public class UserRequestViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView userName;
        Button acceptButton;
        Button denyButton;

        /**
         * Constructor for UserRequestViewHolder.
         *
         * @param itemView The view for a single follow request item.
         */
        public UserRequestViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            userName = itemView.findViewById(R.id.userName);
            acceptButton = itemView.findViewById(R.id.accept_follow_button);
            denyButton = itemView.findViewById(R.id.deny_follow_button);
        }

        /**
         * Binds the user data to the views and sets click listeners for the accept and deny buttons.
         *
         * @param user     The user who sent the follow request.
         * @param listener The listener for button click actions.
         * @param position The position of the item in the list.
         */
        public void bind(User user, OnItemClickListener listener, int position) {
            userName.setText(user.getName());

            //Set user profile picture

            acceptButton.setOnClickListener(v -> {
                listener.onAcceptClick(position);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    db.acceptRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
                }
                // Accept follow, add to following
                // Remove item from list
            });

            denyButton.setOnClickListener(v -> {
                listener.onDenyClick(position);
                // Remove from list, nothing happens
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    db.removeRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
                }
            });

        }
    }

    /**
     * Removes an item from the user list and notifies the adapter of the change.
     *
     * @param position The position of the item to remove.
     */
    public void removeItem(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userList.size());
    }
}
