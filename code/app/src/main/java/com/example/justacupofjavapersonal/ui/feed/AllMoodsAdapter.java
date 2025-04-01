package com.example.justacupofjavapersonal.ui.feed;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// java docs

/**
 * @param userIds A list of user IDs.
 *                Each user ID corresponds to a user's unique identifier.
 * @returns A RecyclerView adapter for displaying user moods.
 */
public class AllMoodsAdapter extends RecyclerView.Adapter<AllMoodsAdapter.UserMoodsViewHolder> {

    private List<String> userIds;
    private Map<String, List<Mood>> moodsByUser;
    Map<String, User> usersById;

    public AllMoodsAdapter(Map<String, List<Mood>> moodsByUser, Map<String, User> usersById) {
        this.moodsByUser = moodsByUser;
        this.usersById = usersById;
        this.userIds = new ArrayList<>(moodsByUser.keySet());
    }

    @NonNull
    @Override
    public UserMoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_all_moods, parent, false);
        return new UserMoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserMoodsViewHolder holder, int position) {
        String userId = userIds.get(position);
        User user = usersById.get(userId);
        List<Mood> moods = moodsByUser.get(userId);

        holder.userNameText.setText(user != null ? user.getName() : "Unknown User");

        List<FeedItem> feedItems = MoodListBuilder.buildFeedList(moods);
        MoodDateAdapter moodAdapter = new MoodDateAdapter(feedItems);
        holder.moodRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.moodRecyclerView.setAdapter(moodAdapter);
    }

    @Override
    public int getItemCount() {
        return userIds.size();
    }

    static class UserMoodsViewHolder extends RecyclerView.ViewHolder {
        TextView userNameText;
        RecyclerView moodRecyclerView;

        public UserMoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.userNameText);
            moodRecyclerView = itemView.findViewById(R.id.moodRecycler);
        }
    }
    public void updateData(Map<String, List<Mood>> newMoodsByUser, Map<String, User> newUsersById) {
        this.moodsByUser = newMoodsByUser;
        this.usersById = newUsersById;
        this.userIds = new ArrayList<>(newMoodsByUser.keySet());
        notifyDataSetChanged();
    }

}
