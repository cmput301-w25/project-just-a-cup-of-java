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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;


import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;
import com.example.justacupofjavapersonal.class_resources.mood.UserMoodActivity;
import com.example.justacupofjavapersonal.databinding.FragmentFeedBinding;
import com.example.justacupofjavapersonal.ui.follow.FollowedUserAdapter;
import com.example.justacupofjavapersonal.ui.follow.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private MoodDateAdapter moodDateAdapter;
    private UserAdapter userAdapter;
    private FollowedUserAdapter followedUserAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Mood> moods = new ArrayList<>();
    private List<FeedItem> moodFeedItems = new ArrayList<>();

    private List<User> followedUsers = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Setup RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Mood adapter (default)
        moodDateAdapter = new MoodDateAdapter(moodFeedItems);
//        userAdapter = new UserAdapter(followedUsers, position -> {
//            User clickedUser = followedUsers.get(position);
//            // TODO: launch UserMoodActivity
//            Toast.makeText(getContext(), "Clicked: " + clickedUser.getName(), Toast.LENGTH_SHORT).show();
//        });
        followedUserAdapter = new FollowedUserAdapter(followedUsers, user -> {
            Intent intent = new Intent(getContext(), UserMoodActivity.class);
            intent.putExtra("userUid", user.getUid());
            intent.putExtra("userName", user.getName());
            startActivity(intent);
        });

        // Show user profiles by default
        binding.recyclerView.setAdapter(followedUserAdapter);
        loadFollowedUsers();  // 👈 This loads users as soon as the screen shows



        binding.feedFollowingButton.setOnClickListener(v -> {
            //Navigation.findNavController(v).navigate(R.id.action_feedFragment_to_allFriendsFollowFragment);

            // Switch to user list (default view)
            binding.recyclerView.setAdapter(followedUserAdapter);
            loadFollowedUsers(); // 🔁 Refresh the list dynamically


        });

        binding.allMoodsButton.setOnClickListener(v -> {
            // Switch to recent moods view
//            binding.recyclerView.setAdapter(moodDateAdapter);
//            //loadFollowedUsers();
//            loadRecentMoods();
            Navigation.findNavController(v).navigate(R.id.navigation_all_moods);

        });

        binding.feedNearby.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_map);
        });

        //binding.filterButton.setOnClickListener(v -> showFilterDialog());

        return root;
    }

    private void loadFollowedUsers() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseDB dbHelper = new FirebaseDB();
        dbHelper.getFollowing(currentUser.getUid(), new FirebaseDB.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> users) {
                followedUsers.clear();
                followedUsers.addAll(users);
                followedUserAdapter.notifyDataSetChanged(); // ✅ update UI
            }

            @Override
            public void onUsersRetrievedFailed(Exception e) {
                Log.e("FeedFragment", "Failed to fetch followed users", e);
            }
        });
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // ✅ add this line
//        if (currentUser == null) return;
//
//        FirebaseDB dbHelper = new FirebaseDB();
//        dbHelper.getFollowedUserMoodsGrouped(currentUser.getUid(), new FirebaseDB.OnUserMoodsGroupedListener() {
//            @Override
//            public void onUserMoodsGrouped(Map<String, List<Mood>> moodMap, Map<String, User> userMap) {
//                List<Mood> combinedMoods = new ArrayList<>();
//
//                for (List<Mood> moodList : moodMap.values()) {
//                    combinedMoods.addAll(moodList);
//                }
//
//                // Sort all moods by postDate (descending)
//                Collections.sort(moods, (m1, m2) -> {
//                    Date d1 = m1.getPostDate();
//                    Date d2 = m2.getPostDate();
//                    if (d1 == null && d2 == null) return 0;
//                    if (d1 == null) return 1; // push nulls to the end
//                    if (d2 == null) return -1;
//                    return d2.compareTo(d1); // newest first
//                });
//
//                // Limit to top 3 if you want
//                //List<Mood> top3Moods = combinedMoods.size() > 3 ? combinedMoods.subList(0, 3) : combinedMoods;
//                List<Mood> top3Moods = combinedMoods;
//
//                // Build FeedItem list and show
//                List<FeedItem> recentFeedItems = MoodListBuilder.buildMoodList(top3Moods);
//                moodDateAdapter.updateList(recentFeedItems);
//            }
//
//            @Override
//            public void onUserMoodsGroupedFailed(Exception e) {
//                Log.e("FeedFragment", "Error fetching grouped moods", e);
//            }
//        });

    }
//        followedUserAdapter = new FollowedUserAdapter(followedUsers, user -> {
//            Log.d("FeedFragment", "Launching UserMoodActivity for: " + user.getName() + ", UID: " + user.getUid());
//
//            Intent intent = new Intent(getContext(), UserMoodActivity.class);
//            intent.putExtra("userUid", user.getUid());
//            intent.putExtra("userName", user.getName());
//
//            startActivity(intent);
//        });
//
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.recyclerView.setAdapter(followedUserAdapter);



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

                            for (DocumentSnapshot doc : snapshot) {
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