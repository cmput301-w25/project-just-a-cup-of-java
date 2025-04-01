package com.example.justacupofjavapersonal.ui.follow;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.UserMoodActivity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//java docs

/**
 * Adapter for displaying a list of followed users.
 * @param followedUsers A list of followed users.
 *                      Each user is represented by a {@link User} object.
 * @returns A RecyclerView adapter that displays a list of followed users.
 */
public class FollowedUserAdapter extends RecyclerView.Adapter<FollowedUserAdapter.ViewHolder> {
    private List<User> followedUsers;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public FollowedUserAdapter(List<User> followedUsers, OnUserClickListener listener) {
        this.followedUsers = followedUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FollowedUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_follow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowedUserAdapter.ViewHolder holder, int position) {
        //holder.bind(followedUsers.get(position), listener);
        User user = followedUsers.get(position);
        holder.userName.setText(user.getName());
        // ✅ Hide follow button (because it's the 'Following' tab)
        holder.followButton.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), UserMoodActivity.class);
            intent.putExtra("userUid", user.getUid());
            intent.putExtra("userName", user.getName());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return followedUsers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView userName;
        Button followButton; // 👈 Add this line


        ViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            userName = itemView.findViewById(R.id.userName);
            followButton = itemView.findViewById(R.id.follow_button); // 👈 And this line

        }

        void bind(User user, OnUserClickListener listener) {
            userName.setText(user.getName());
            // profilePicture.setImage... if you add that later

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
                // 👇 This part opens UserMoodActivity with userUid and userName
                Intent intent = new Intent(itemView.getContext(), UserMoodActivity.class);
                intent.putExtra("userUid", user.getUid());
                intent.putExtra("userName", user.getName());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}

