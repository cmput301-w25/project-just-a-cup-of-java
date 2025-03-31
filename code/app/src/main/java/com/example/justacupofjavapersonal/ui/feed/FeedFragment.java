//package com.example.justacupofjavapersonal.ui.feed;
//
//import android.app.AlertDialog;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.example.justacupofjavapersonal.R;
//import com.example.justacupofjavapersonal.class_resources.User;
//import com.example.justacupofjavapersonal.class_resources.mood.FeedItem;
//import com.example.justacupofjavapersonal.class_resources.mood.Mood;
//import com.example.justacupofjavapersonal.class_resources.mood.MoodDateAdapter;
//import com.example.justacupofjavapersonal.class_resources.mood.MoodListBuilder;
//import com.example.justacupofjavapersonal.class_resources.mood.EmotionalState;
//import com.example.justacupofjavapersonal.class_resources.mood.SocialSituation;
//import com.example.justacupofjavapersonal.databinding.FragmentFeedBinding;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//
///**
// * FeedFragment is a fragment that displays a list of moods in a RecyclerView.
// * It initializes a list of Mood objects with sample data and sets up the RecyclerView
// * with a MoodDateAdapter to display the moods.
// */
//public class FeedFragment extends Fragment {
//
//    private FragmentFeedBinding binding;
//    private ArrayList<Mood> moods;
//    private MoodDateAdapter moodDateAdapter;
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private List<String> followedUserUids = Arrays.asList(
//            //"u8cE0xFr8LeB6xx6V8JMsl2L4ns1"
//            "36ji4ZbT2CSwKeh4mJpYhyfY0Pp1"
//
//    );
//
//    /**
//     * Called to do initial creation of a fragment.
//     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
//     *
//     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        moods = new ArrayList<>();
//    }
//
//    /**
//     * Called to have the fragment instantiate its user interface view.
//     *
//     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
//     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
//     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
//     * @return The View for the fragment's UI, or null.
//     */
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//
//
//        binding = FragmentFeedBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        moodDateAdapter = new MoodDateAdapter(new ArrayList<>());
//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.recyclerView.setAdapter(moodDateAdapter);
//
//        binding.feedFollowingButton.setOnClickListener(v -> loadFollowedMoods());
//        binding.filterButton.setOnClickListener(v -> showFilterDialog());
//
//        return root;
//    }
//    private void loadFollowedMoods() {
//        Log.d("MoodQuery", "Fetching moods for UIDs: " + followedUserUids.toString());
//
//        db.collection("moods")
//                .whereIn("uid", followedUserUids)
//                .get()
//                .addOnSuccessListener(snapshot -> {
//                    moods.clear();
//
//                    for (DocumentSnapshot doc : snapshot) {
//                        Log.d("FeedDebug", "Doc snapshot: " + doc.getData());
//
//                        Mood mood = doc.toObject(Mood.class);
//
//
//                        if (mood == null) {
//                            Log.w("FeedDebug", "Mood is null for doc: " + doc.getId());
//                            continue;
//                        }
//                        Log.d("FeedDebug", "Parsed mood - ID: " + mood.getMoodID() +
//                                ", Date: " + (mood.getDate() != null ? mood.getDate() : "N/A") +
//                                ", Emotion: " + (mood.getEmotion() != null ? mood.getEmotion() : "N/A") +
//                                ", Trigger: " + mood.getTrigger() +
//                                ", Social Situation: " + mood.getSocialSituation());
//
//
//                        moods.add(mood);
//                    }
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//
//                    //Collections.sort(moods, (m1, m2) -> m2.getDate().compareTo(m1.getDate()));
//                    Collections.sort(moods, (m1, m2) -> {
//                        try {
//                            Date d1 = m1.getDate() != null ? sdf.parse(m1.getDate()) : null;
//                            Date d2 = m2.getDate() != null ? sdf.parse(m2.getDate()) : null;
//                            if (d1 == null && d2 == null) return 0;
//                            if (d1 == null) return 1;
//                            if (d2 == null) return -1;
//                            return d2.compareTo(d1); // latest dates first
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            return 0;
//                        }
//                    });
//                    List<FeedItem> finalList = MoodListBuilder.buildMoodList(moods);
//                    Log.d("FeedDebug", "Final list size: " + finalList.size());
//
//                    moodDateAdapter.updateList(finalList);
//                })
//                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load followed moods", Toast.LENGTH_SHORT).show());
//    }
//
//    private void showFilterDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Filter Mood Events");
//
//        // List of filter options
//        String[] filterOptions = {
//                "Last 7 Days",
//                "By Emotional State",
//                "By Reason Text"
//        };
//
//        builder.setItems(filterOptions, (dialog, which) -> {
//            switch (which) {
//                case 0:
//                    // User selected "Last 7 Days"
//                    showToast("Filtering: Last 7 Days");
//                    break;
//                case 1:
//                    // User selected "By Emotional State"
//                    showEmotionFilterDialog();  // Open the spinner dialog
//
//                    showToast("Filtering: By Emotional State");
//
//                    break;
//                case 2:
//                    // User selected "By Reason Text"
//                    showToast("Filtering: By Reason Text");
//                    break;
//            }
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//        builder.create().show();
//    }
//
//
//    private void showEmotionFilterDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Select Emotional State");
//
//        // Create a layout for the spinner
//        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.emotion_spinner_dialog, null);
//
//        Spinner spinner = dialogView.findViewById(R.id.spinnerEmotions);
//
//        // Define the emotional states
//        String[] emotions = {"Happiness", "Sadness", "Anger", "Fear", "Surprise", "Neutral"};
//
//        // Set up the adapter for the spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, emotions);
//        spinner.setAdapter(adapter);
//
//        builder.setView(dialogView);
//
//        builder.setPositiveButton("Filter", (dialog, which) -> {
//            String selectedEmotion = spinner.getSelectedItem().toString();
//            showToast("Filtering: " + selectedEmotion);
//        });
//
//        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//        builder.create().show();
//    }
//
//    private void showToast(String message) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
//    }
//
//
//
//
//    /**
//     * Called when the view previously created by onCreateView() has been detached from the fragment.
//     * This is where you should clean up resources related to the view.
//     * It is important to call the superclass's implementation of this method.
//     * In this implementation, the binding is set to null to avoid memory leaks.
//     */
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}
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

        binding.filterButton.setOnClickListener(v -> showFilterDialog());

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
