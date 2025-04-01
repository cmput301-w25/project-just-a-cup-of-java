package com.example.justacupofjavapersonal.ui.moodhistory;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.ui.addmoodfragment.MoodActionsAdapter;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * The MoodHistoryFragment class represents a fragment that displays a list of moods
 * in a RecyclerView. It allows users to navigate to other fragments, filter moods,
 * and delete moods from the list. The moods are fetched from a Firestore database.
 */
public class MoodHistoryFragment extends Fragment {

    private RecyclerView moodHistoryRecyclerView;
    private MoodHistoryAdapter moodAdapter;
    private ArrayList<Mood> moodList = new ArrayList<>();
    private ArrayList<Mood> tempstorage = new ArrayList<>();


    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater  The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate
     *                  the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The View for the fragment's UI, or null.
     *
     * This method sets up the UI for the Mood History screen, including:
     * - Buttons for navigating to other fragments (calendar, map, and filter).
     * - A RecyclerView for displaying a list of moods with an adapter for managing the data.
     * - A filter dialog for applying filters to the mood list.
     * - Loading all moods into the RecyclerView.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);

        Button calendarButton = view.findViewById(R.id.button);
        calendarButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_moodHistory_to_home);
        });

        Button mapButton = view.findViewById(R.id.btn_mymap);
        mapButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_navigation_moodHistory_to_myMapFragment);
        });


        Button filterButton = view.findViewById(R.id.filterbutton);
        filterButton.setOnClickListener(v -> {
            MoodFilterDialog dialog = new MoodFilterDialog((recentWeek, emotion, reasonKeyword) -> {
                applyFilters(recentWeek, emotion, reasonKeyword); // Apply the filter based on selected options
            });
            dialog.show(getParentFragmentManager(), "MoodFilterDialog");
        });


        // RecyclerView setup
        moodHistoryRecyclerView = view.findViewById(R.id.moodHistoryRecyclerView);
        moodAdapter = new MoodHistoryAdapter(requireContext(), moodList, position -> deleteMood(position));
        moodHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        moodHistoryRecyclerView.setAdapter(moodAdapter);

        loadAllMoods();
        return view;
    }

    /**
     * Loads all mood entries from the Firestore database for the currently logged-in user.
     * 
     * This method retrieves mood data from the "moods" collection in Firestore, filtering
     * by the current user's UID and ordering the results by timestamp in descending order.
     * The retrieved moods are added to the mood list and displayed using the mood adapter.
     * 
     * If the user is not logged in, a toast message is displayed, and the method exits early.
     * If there is an error during the Firestore query, an error message is logged, and a
     * toast message is shown to the user.
     */
    private void loadAllMoods() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("moods")
                .whereEqualTo("uid", user.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    moodList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Mood mood = doc.toObject(Mood.class);
                        if (mood != null) {
                            moodList.add(mood);
                        }
                    }
                    tempstorage.addAll(moodList);
                    moodAdapter.notifyDataSetChanged();
                    Log.d("MoodHistory", "Loaded " + moodList.size() + " moods");
                })
                .addOnFailureListener(e -> {
                    Log.e("MoodHistory", "Error loading moods", e);
                    Toast.makeText(getContext(), "Error loading moods", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes a mood entry from the mood list at the specified position.
     * 
     * @param position The index of the mood to be deleted. Must be within the bounds of the mood list.
     * 
     * This method performs the following actions:
     * - Removes the mood at the specified position from the mood list.
     * - Notifies the adapter that an item has been removed to update the UI.
     * - Updates the temporary storage with the current state of the mood list.
     * - Deletes the mood entry from the Firebase database.
     * - Displays a toast message to confirm the deletion.
     */
    private void deleteMood(int position) {
        if (position >= 0 && position < moodList.size()) {
            Mood moodToDelete = moodList.get(position);
            moodList.remove(position);
            moodAdapter.notifyItemRemoved(position);
            tempstorage.clear();
            tempstorage.addAll(moodList);
            FirebaseDB firebaseDB = new FirebaseDB();
            firebaseDB.deleteMood(moodToDelete);
            Toast.makeText(getContext(), "Mood deleted", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Filters the list of moods based on the specified criteria and updates the mood adapter with the filtered list.
     *
     * @param recentWeek     If true, only moods from the last 7 days will be included in the filtered list.
     * @param emotion        The emotion to filter by. If not null or empty, only moods containing this emotion (case-insensitive) will be included.
     * @param reasonKeyword  A keyword to filter the reason/trigger by. If not null or empty, only moods whose trigger contains this keyword 
     *                       as a whole word (case-insensitive) will be included.
     */
    private void applyFilters(boolean recentWeek, String emotion, String reasonKeyword) {
        ArrayList<Mood> filteredList = new ArrayList<>();

        long oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000); // 7 days in milliseconds
        for (Mood mood : tempstorage) {
            boolean matches = true;

            if (recentWeek && mood.getTimestamp().getSeconds() * 1000 < oneWeekAgo) {
                matches = false;
            }


            Log.d("MoodFilter", "From DB Emotion: " + mood.getEmotion());
            Log.d("MoodFilter", "Filter Emotion: " + emotion);
            if (emotion != null && !emotion.isEmpty() && !mood.getEmotion().toLowerCase().contains(emotion.toLowerCase())) {
                matches = false;
            }

            if (reasonKeyword != null && !reasonKeyword.isEmpty()) {
                String regex = "\\b" + Pattern.quote(reasonKeyword.toLowerCase()) + "\\b";
                if (!mood.getTrigger().toLowerCase().matches(".*" + regex + ".*")) {
                    matches = false;
                }
            }


            if (matches) {
                filteredList.add(mood);
            }
        }
        moodAdapter.updateMoodList(filteredList);
        moodAdapter.notifyDataSetChanged();
    }
}