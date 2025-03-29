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
import java.util.List;

public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private MoodDateAdapter moodDateAdapter;
    private UserAdapter userAdapter;
    private FollowedUserAdapter followedUserAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Mood> moods = new ArrayList<>();
    private List<FeedItem> moodFeedItems = new ArrayList<>();

    private List<User> followedUsers = new ArrayList<>();

//    private List<User> followedUsers = Arrays.asList(
//            new User("Tinashe", "tinashe@masoka.com", "", "", "", "", "36ji4ZbT2CSwKeh4mJpYhyfY0Pp1")
//            //new User("Kamani", "kamani@gmail.com", "", "", "", "", "wNgUDigFOWOEtL2cPo503sP36Xj1")
//
//    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moods = new ArrayList<>();

        Date t1 = new Date(System.currentTimeMillis() - 47363234L);
        Date t2 = new Date(System.currentTimeMillis() - 56152438L);
        Date t3 = new Date(System.currentTimeMillis() - 20252171L);
        Date t4 = new Date(System.currentTimeMillis() - 300525030L);

        // Create user objects
        User user1 = new User("person", "a", "a", "a", "a", "a", "a");
        User user2 = new User("person also", "a", "a", "a", "a", "a", "a");

        // Pass user IDs instead of full User objects
        Mood m1 = new Mood(EmotionalState.HAPPINESS, t1);
        Mood m2 = new Mood(EmotionalState.HAPPINESS, t2);
        Mood m3 = new Mood(EmotionalState.HAPPINESS, t3);
        Mood m4 = new Mood(EmotionalState.HAPPINESS, t4);

        Location l1 = new Location("location");
        l1.setLatitude(1.0);
        l1.setLongitude(1.0);

        m1.setPhoto(null);
        m2.setLocation(l1);
        m3.setSocialSituation("WITH_TWO_TO_SEVERAL");
        m4.setPhoto(null);
        m4.setLocation(l1);
        m4.setSocialSituation("WITH_ONE_OTHER");

        moods.add(m1);
        moods.add(m2);
        moods.add(m3);
        moods.add(m4);
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


        binding.feedFollowingButton.setOnClickListener(v -> {
            // Switch to user list (default view)
            binding.recyclerView.setAdapter(followedUserAdapter);
            loadFollowedUsers(); // 🔁 Refresh the list dynamically


        });

        binding.feedRecentButton.setOnClickListener(v -> {
            // Switch to recent moods view
            binding.recyclerView.setAdapter(moodDateAdapter);
            //loadFollowedUsers();
            loadRecentMoods();
        });

        binding.filterButton.setOnClickListener(v -> showFilterDialog());

        return root;
    }

    private void loadFollowedUsers() {
        //List<User> followedUsers = new ArrayList<>();

        // Manually create dummy followed users (for now)

        //followedUsers.add(new User("Kamani", "kamani@gmail.com", "", "", "", "", "wNgUDigFOWOEtL2cPo503sP36Xj1"));
        //followedUsers.add(new User("Heena", "hee@gmail.com", "", "", "", "", "3pW7r8DISncLHCJVlaDE5PbGlHJ3"));
        //followedUsers.add(new User("Tinashe", "tinashe@masoka.com", "", "", "", "", "36ji4ZbT2CSwKeh4mJpYhyfY0Pp1"));

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