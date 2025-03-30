package com.example.justacupofjavapersonal.ui.feed;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllMoodsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllMoodsAdapter adapter;
    private FirebaseDB firebaseDB;
    private static final String TAG = "AllMoodsFragment";


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

            String currentUserId = FirebaseDB.getCurrentUserId(); // <-- Make sure you have this method
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
                                Log.d("AllMoodsFragment", "Mood details: " + mood.toString()); // Optional
                            }
                        }
                        adapter.updateData(userMoodMap, userMap);
                    }

                    @Override
                    public void onUserMoodsGroupedFailed(Exception e) {
                        Log.e("AllMoodsFragment", "Failed to load followed moods", e);
                    }
                });


        }
}

