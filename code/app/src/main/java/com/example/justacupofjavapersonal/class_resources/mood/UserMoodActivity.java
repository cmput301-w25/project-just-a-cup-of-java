package com.example.justacupofjavapersonal.class_resources.mood;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class UserMoodActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MoodDateAdapter adapter;
    private TextView userNameHeader;
    private List<Mood> allMoods = new ArrayList<>();
    private boolean showRecentOnly = false;


    private List<FeedItem> feedItems = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button showRecentButton = findViewById(R.id.toggleRecentButton);
        showRecentButton.setOnClickListener(v -> {
            showRecentOnly = !showRecentOnly; // Toggle the flag
            updateFeedItems();                // Rebuild list based on flag

            // Optional: Update the button text
            showRecentButton.setText(showRecentOnly ? "Show All" : "Show Recent Only");
        });


        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        String userUid = getIntent().getStringExtra("userUid");
        String userName = getIntent().getStringExtra("userName");
        Log.d("UserMoodActivity", "Received userUid: " + userUid + ", userName: " + userName);

        TextView userNameText = findViewById(R.id.usernameTitle);
        if (userName != null) {
            userNameText.setText(userName + "'s Mood Events");
        }


        recyclerView = findViewById(R.id.userMoodRecyclerView);
        adapter = new MoodDateAdapter(feedItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        if (userUid != null) {
            fetchUserMoods(userUid);
        }
    }

    private void updateFeedItems() {
//        List<Mood> moodsToShow = showRecentOnly && allMoods.size() > 3
//                ? allMoods.subList(0, 3)
//                : allMoods;
//
//        Log.d("UserMoodActivity", "Updating feed, moodsToShow size: " + moodsToShow.size());
//        for (Mood m : moodsToShow) {
//            Log.d("UserMoodActivity", "Mood: " + m.getEmotion() + " | Date: " + m.getPostDate());
//        }
//
//        List<FeedItem> feedList = MoodListBuilder.buildFeedList(moodsToShow);
//        feedItems.clear();
//        feedItems.addAll(feedList);
//        adapter.notifyDataSetChanged();
        List<Mood> moodsToShow;

        if (showRecentOnly) {
            List<Mood> sortedByTimestamp = new ArrayList<>(allMoods);
            sortedByTimestamp.sort((a, b) -> {
                if (a.getTimestamp() == null && b.getTimestamp() == null) return 0;
                if (a.getTimestamp() == null) return 1;
                if (b.getTimestamp() == null) return -1;
                return b.getTimestamp().compareTo(a.getTimestamp());
            });

            moodsToShow = sortedByTimestamp.size() > 3
                    ? sortedByTimestamp.subList(0, 3)
                    : sortedByTimestamp;
        } else {
            moodsToShow = allMoods;
        }
        Log.d("UserMoodActivity", "Updating feed, moodsToShow size: " + moodsToShow.size());
        for (Mood m : moodsToShow) {
            Log.d("UserMoodActivity", "Mood: " + m.getEmotion() + " | Timestamp: " + m.getTimestamp());
        }
        List<FeedItem> feedList = MoodListBuilder.buildFeedList(moodsToShow);
        feedItems.clear();
        feedItems.addAll(feedList);
        adapter.notifyDataSetChanged();
    }


    private void fetchUserMoods(String uid) {
        com.example.justacupofjavapersonal.class_resources.FirebaseDB firebaseDB = new com.example.justacupofjavapersonal.class_resources.FirebaseDB();
        firebaseDB.getUserMoods(uid, new com.example.justacupofjavapersonal.class_resources.FirebaseDB.OnMoodLoadedListener() {
            @Override
            public void onMoodsLoaded(List<Mood> moods) {
                allMoods.clear();
                allMoods.addAll(moods);
                updateFeedItems();
            }

            @Override
            public void onMoodsLoadedFailed(Exception e) {
                Log.e("UserMoodActivity", "Error fetching moods", e);
            }
        });
//        Log.d("UserMoodActivity", "Fetching moods for UID: " + uid);
//
//        db.collection("moods")
//                .whereEqualTo("uid", uid)
//                .get()
//                .addOnSuccessListener(querySnapshot -> {
//                    Log.d("UserMoodActivity", "Got moods: " + querySnapshot.size());
//
//                    allMoods.clear(); // ✅ Step 1
//                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
//                        Mood mood = doc.toObject(Mood.class);
//                        if (mood != null) {
//                            Log.d("UserMoodActivity", "Fetched mood: " + mood.getEmotion() + " | UID: " + mood.getUid());
//
//                            allMoods.add(mood); // ✅ Save all moods
//                        } else {
//                            Log.w("UserMoodActivity", "Skipped null mood");
//                        }
//                    }
//                    // ✅ Step 2: Sort by date descending
//// Step 2: Sort by date descending, null-safe
//                    allMoods.sort((a, b) -> {
//                        if (a.getPostDate() == null && b.getPostDate() == null) return 0;
//                        if (a.getPostDate() == null) return 1; // nulls last
//                        if (b.getPostDate() == null) return -1;
//                        return b.getPostDate().compareTo(a.getPostDate());
//                    });
//                    // ✅ Step 3: Use helper method to decide what to show
//                    updateFeedItems();
//                })
//                .addOnFailureListener(e -> Log.e("UserMoodActivity", "Error fetching moods", e));
    }
}

