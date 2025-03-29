package com.example.justacupofjavapersonal.ui.feed;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;
import com.example.justacupofjavapersonal.class_resources.mood.UserMoodActivity;
import com.example.justacupofjavapersonal.databinding.FragmentFeedBinding;
import com.example.justacupofjavapersonal.ui.follow.FollowedUserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private MoodDateAdapter moodDateAdapter;
    private FollowedUserAdapter followedUserAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Mood> moods = new ArrayList<>();
    private List<FeedItem> moodFeedItems = new ArrayList<>();
    private List<User> followedUsers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moods = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        moodDateAdapter = new MoodDateAdapter(moodFeedItems);
        followedUserAdapter = new FollowedUserAdapter(followedUsers, user -> {
            Intent intent = new Intent(getContext(), UserMoodActivity.class);
            intent.putExtra("userUid", user.getUid());
            intent.putExtra("userName", user.getName());
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(followedUserAdapter);

        binding.feedFollowingButton.setOnClickListener(v -> {
            binding.recyclerView.setAdapter(followedUserAdapter);
            loadFollowedUsers();
        });

        binding.feedRecentButton.setOnClickListener(v -> {
            binding.recyclerView.setAdapter(moodDateAdapter);
            loadRecentMoods();
        });

        binding.filterButton.setOnClickListener(v -> showFilterDialog());

        return root;
    }

    private void loadFollowedUsers() {
        FirebaseDB dbHelper = new FirebaseDB();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            dbHelper.getFollowing(currentUser.getUid(), new FirebaseDB.OnUsersRetrievedListener() {
                @Override
                public void onUsersRetrieved(List<User> userList) {
                    followedUsers.clear();
                    followedUsers.addAll(userList);
                    followedUserAdapter.notifyDataSetChanged();
                }

                @Override
                public void onUsersRetrievedFailed(Exception e) {
                    Log.e("FeedFragment", "Failed to fetch followed users", e);
                }
            });
        }
    }

    private void loadRecentMoods() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseDB dbHelper = new FirebaseDB();
        dbHelper.getFollowing(currentUser.getUid(), new FirebaseDB.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                List<String> followedUids = new ArrayList<>();
                for (User user : userList) {
                    followedUids.add(user.getUid());
                }

                db.collection("moods")
                        .whereIn("uid", followedUids)
                        .get()
                        .addOnSuccessListener(snapshot -> {
                            moods.clear();

                            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                                Mood mood = doc.toObject(Mood.class);
                                if (mood != null && mood.getPostDate() != null) {
                                    moods.add(mood);
                                }
                            }

                            moods.sort((m1, m2) -> m2.getPostDate().compareTo(m1.getPostDate()));
                            List<Mood> top3Moods = moods.size() > 3 ? moods.subList(0, 3) : moods;
                            List<FeedItem> recentFeedItems = MoodListBuilder.buildMoodList(top3Moods);
                            moodDateAdapter.updateList(recentFeedItems);
                        })
                        .addOnFailureListener(e -> Log.e("FeedFragment", "Error fetching moods", e));
            }

            @Override
            public void onUsersRetrievedFailed(Exception e) {
                Log.e("FeedFragment", "Failed to fetch followed users", e);
            }
        });
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Filter Mood Events");

        String[] filterOptions = {"Last 7 Days", "By Emotional State", "By Reason Text"};

        builder.setItems(filterOptions, (dialog, which) -> {
            switch (which) {
                case 0: showToast("Filtering: Last 7 Days"); break;
                case 1: showEmotionFilterDialog(); break;
                case 2: showToast("Filtering: By Reason Text"); break;
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showEmotionFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Emotional State");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.emotion_spinner_dialog, null);
        Spinner spinner = dialogView.findViewById(R.id.spinnerEmotions);

        String[] emotions = {"Happiness", "Sadness", "Anger", "Fear", "Surprise", "Neutral"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, emotions);
        spinner.setAdapter(adapter);

        builder.setView(dialogView);
        builder.setPositiveButton("Filter", (dialog, which) -> {
            String selectedEmotion = spinner.getSelectedItem().toString();
            showToast("Filtering: " + selectedEmotion);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
