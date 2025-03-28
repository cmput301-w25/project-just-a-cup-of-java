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

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestAdapter.UserRequestViewHolder> {
    private List<User> userList;
    private OnItemClickListener listener;
    private FirebaseDB db;

    public UserRequestAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onAcceptClick(int position);
        void onDenyClick(int position);
    }

    @NonNull
    @Override
    public UserRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow_requests, parent, false);
        return new UserRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserRequestAdapter.UserRequestViewHolder holder, int position) {
        holder.bind(userList.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }



    public class UserRequestViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView userName;
        Button acceptButton;
        Button denyButton;
        public UserRequestViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            userName = itemView.findViewById(R.id.userName);
            acceptButton = itemView.findViewById(R.id.accept_follow_button);
            denyButton = itemView.findViewById(R.id.deny_follow_button);
        }

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

    public void removeItem(int position) {
        userList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, userList.size());
    }
}