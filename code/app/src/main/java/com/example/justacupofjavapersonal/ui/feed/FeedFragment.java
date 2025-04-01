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

/**
 * FeedFragment is responsible for displaying the feed screen in the application.
 * It shows a list of followed users or recent mood events and provides navigation
 * to other screens such as all moods and nearby moods. The fragment also includes
 * filtering options for mood events.
 */
public class FeedFragment extends Fragment {

    private FragmentFeedBinding binding;
    private MoodDateAdapter moodDateAdapter;
    private UserAdapter userAdapter;
    private FollowedUserAdapter followedUserAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Mood> moods = new ArrayList<>();
    private List<FeedItem> moodFeedItems = new ArrayList<>();

    private List<User> followedUsers = new ArrayList<>();


    /**
     * Called to do initial creation of the fragment.
     * This method is called when the fragment is first created.
     * It is typically used to perform one-time initialization tasks.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    
    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     *                  UI should be attached to. The fragment should not add the view itself,
     *                  but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The root View for the fragment's UI, or null.
     *
     * This method sets up the RecyclerView with adapters for displaying followed users
     * and their moods. It also initializes button click listeners for navigating to
     * different sections of the app, such as all moods and nearby moods on a map.
     * By default, it loads and displays the list of followed users.
     */
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

        // Show user profiles by default
        binding.recyclerView.setAdapter(followedUserAdapter);
        //loadFollowedUsers();
        loadRecentMoods();// 👈 This loads users as soon as the screen shows



        binding.feedFollowingButton.setOnClickListener(v -> {
            binding.recyclerView.setAdapter(followedUserAdapter);
            loadFollowedUsers(); // 🔁 Refresh the list dynamically


        });

        binding.allMoodsButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_all_moods);

        });

        binding.feedNearby.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.navigation_map);
        });

        return root;
    }

    /**
     * Loads the list of users that the current user is following from the database.
     * This method retrieves the current user from Firebase Authentication and fetches
     * the list of followed users using the FirebaseDB helper class. Once the data is
     * retrieved, it updates the local list of followed users and notifies the adapter
     * to refresh the UI.
     *
     * If the current user is not authenticated, the method returns immediately.
     * In case of a failure during data retrieval, an error is logged.
     */
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

    private void loadFollowedUsers() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseDB dbHelper = new FirebaseDB();
        dbHelper.getFollowing(currentUser.getUid(), new FirebaseDB.OnUsersRetrievedListener() {
            @Override
            public void onUsersRetrieved(List<User> userList) {
                followedUsers.clear();
                followedUsers.addAll(userList);
                followedUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUsersRetrievedFailed(Exception e) {
                Log.e("FeedFragment", "Failed to load followed users", e);
            }
        });
    }


    /**
     * Loads the recent moods of users that the current user is following and updates the UI.
     * 
     * This method retrieves the list of users that the current user is following from the database.
     * It then fetches the moods posted by these users, sorts them by post date in descending order,
     * and selects the top three most recent moods. Finally, it updates the adapter with the new list
     * of feed items to display in the UI.
     */
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

    
    /**
     * Displays a dialog with a spinner to allow the user to select an emotional state for filtering.
     * 
     * The dialog contains a spinner populated with a list of emotions: "Happiness", "Sadness", 
     * "Anger", "Fear", "Surprise", and "Neutral". The user can select an emotion and click the 
     * "Filter" button to apply the filter, or click "Cancel" to dismiss the dialog.
     * 
     * When the "Filter" button is clicked, the selected emotion is retrieved from the spinner 
     * and a toast message is displayed indicating the selected emotion.
     * 
     * The dialog is created using an AlertDialog.Builder and inflates a custom layout 
     * containing the spinner.
     */
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

    /**
     * Displays a toast message to the user.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the fragment's view is being destroyed.
     * This method is used to clean up resources associated with the view.
     * Specifically, it sets the binding object to null to prevent memory leaks.
     * Always call the superclass implementation to ensure proper cleanup.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
