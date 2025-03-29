////IQRAS WORKING CODE
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
import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnItemClickListener listener;
    private FirebaseDB db;
    private List<String> requests;
    public UserAdapter(List<User> userList, OnItemClickListener listener, FirebaseDB db) {
        this.userList = userList;
        this.listener = listener;
        this.db = db;
    }

    public interface OnItemClickListener {
        void onFollowClick(int position);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_follow, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        holder.bind(userList.get(position), listener, position);
    }
    private void loadRequests(String userID) {
        db.getAllRequestIds(userID, users -> {
            requests.clear();
            requests.addAll(users);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView userName;
        Button followButton;

        public UserViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            userName = itemView.findViewById(R.id.userName);
            followButton = itemView.findViewById(R.id.follow_button);
        }

        public void bind(User user, OnItemClickListener listener, int position) {
            userName.setText(user.getName());
            requests = new ArrayList<>();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                loadRequests(FirebaseAuth.getInstance().getCurrentUser().getUid());

                followButton.setText(requests.contains(user.getUid()) ? "Requested" : "Follow");
                //Set user profile picture

                followButton.setOnClickListener(v -> {
                    listener.onFollowClick(position);

                    followButton.setText(followButton.getText().toString().equals("Follow") ? "Requested" : "Follow");
                    if (!requests.contains(user.getUid())) {
                        db.sendRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
                    }
                    if (followButton.getText().toString().equals("Follow")) {
                        db.removeRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
                    }
                });
            }

//            followButton.setOnClickListener(v -> {
//                listener.onFollowClick(position);
//
//                String currentText = followButton.getText().toString();
//                followButton.setText(currentText.equals("Follow") ? "Requested" : "Follow");
//
//                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//                    db.sendRequest(FirebaseAuth.getInstance().getCurrentUser().getUid(), user.getUid());
//                }
//            });
        }
    }
}
