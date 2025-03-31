package com.example.justacupofjavapersonal.ui.feed;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.ui.feed.AllMoodsAdapter;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.ui.moodhistory.MoodFilterDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllMoodsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllMoodsAdapter adapter;
    private FirebaseDB firebaseDB;
    private static final String TAG = "AllMoodsFragment";
    private Map<String, List<Mood>> originalMoodMap;


    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_all_moods, container, false);

        recyclerView = view.findViewById(R.id.allMoodsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AllMoodsAdapter(new HashMap<>(), new HashMap<>());

        recyclerView.setAdapter(adapter);

        firebaseDB = new FirebaseDB();

        Button filterButton = view.findViewById(R.id.filterbutton); // Add this to your layout
        filterButton.setOnClickListener(v -> {
            MoodFilterDialog dialog = new MoodFilterDialog((recentWeek, emotion, reasonKeyword) -> {
                applyFilters(recentWeek, emotion, reasonKeyword);
            });
            dialog.show(getParentFragmentManager(), "MoodFilterDialog");
        });

        loadFollowedUserMoods();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Fragment is active");

        // Optional: Confirm visually that it's being called
        Toast.makeText(getContext(), "All Moods Fragment Loaded", Toast.LENGTH_SHORT).show();

        // Optional: double-check mood loading from here too
        // loadFollowedUserMoods();
    }

        private void loadFollowedUserMoods() {
            Log.d(TAG, "loadFollowedUserMoods called");

            String currentUserId = FirebaseDB.getCurrentUserId();
            Log.d(TAG, "Current user ID: " + currentUserId);
            Log.d(TAG, "Current UID: " + FirebaseDB.getCurrentUserId());


            Log.d("AllMoodsFragment", "Current user ID: " + currentUserId);
            if (currentUserId == null) {
                Log.e(TAG, "currentUserId is null. User might not be logged in.");
                return;
            }

            firebaseDB.getFollowedUserMoodsGrouped(currentUserId, new FirebaseDB.OnUserMoodsGroupedListener() {
                @Override
                public void onUserMoodsGrouped(Map<String, List<Mood>> userMoodMap, Map<String, User> userMap) {
                    Log.d(TAG, "onUserMoodsGrouped called");
                    // ✅ ADD THESE TWO LINES BELOW
                    Log.d(TAG, "UserMap keys: " + userMap.keySet());
                    Log.d(TAG, "MoodMap keys: " + userMoodMap.keySet());
                    Log.d("AllMoodsFragment", "userMoodMap size: " + userMoodMap.size());
                    for (String uid : userMoodMap.keySet()) {
                        List<Mood> moods = userMoodMap.get(uid);
                        Log.d(TAG, "User: " + uid + " has " + moods.size() + " moods");


                        Log.d("AllMoodsFragment", "User: " + uid + " Moods: " + userMoodMap.get(uid).size());
                        for (Mood mood : moods) {
                            Log.d("AllMoodsFragment", "Mood details: " + mood.toString());
                        }
                    }
                    originalMoodMap = new HashMap<>(userMoodMap);
                    adapter.updateData(userMoodMap, userMap);
                }

                @Override
                public void onUserMoodsGroupedFailed(Exception e) {
                    Log.e("AllMoodsFragment", "Failed to load followed moods", e);
                }
            });


        }

    private void applyFilters(boolean recentWeek, String emotion, String reasonKeyword) {
        if (originalMoodMap == null) return;

        Map<String, List<Mood>> filteredMoodMap = new HashMap<>();
        long oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);

        for (String userId : originalMoodMap.keySet()) {
            List<Mood> moods = originalMoodMap.get(userId);
            List<Mood> filteredMoods = new ArrayList<>();

            for (Mood mood : moods) {
                boolean matches = true;

                if (recentWeek && mood.getTimestamp().getSeconds() * 1000 < oneWeekAgo) {
                    matches = false;
                }

                if (emotion != null && !emotion.isEmpty() && !mood.getEmotion().toLowerCase().contains(emotion.toLowerCase())) {
                    matches = false;
                }

                if (reasonKeyword != null && !reasonKeyword.isEmpty()) {
                    String regex = "\\b" + reasonKeyword.toLowerCase() + "\\b";
                    if (!mood.getTrigger().toLowerCase().matches(".*" + regex + ".*")) {
                        matches = false;
                    }
                }

                if (matches) {
                    filteredMoods.add(mood);
                }
            }

            if (!filteredMoods.isEmpty()) {
                filteredMoodMap.put(userId, filteredMoods);
            }
        }

        adapter.updateData(filteredMoodMap, adapter.usersById);
    }
}

