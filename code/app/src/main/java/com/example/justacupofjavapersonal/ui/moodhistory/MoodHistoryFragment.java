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

public class MoodHistoryFragment extends Fragment {

    private RecyclerView moodHistoryRecyclerView;
    private MoodHistoryAdapter moodAdapter;
    private ArrayList<Mood> moodList = new ArrayList<>();
    private ArrayList<Mood> tempstorage = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);

        Button calendarButton = view.findViewById(R.id.button);
        calendarButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_moodHistory_to_home);
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

    private void applyFilters(boolean recentWeek, String emotion, String reasonKeyword) {
        ArrayList<Mood> filteredList = new ArrayList<>();

        long oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000); // 7 days in milliseconds
        for (Mood mood : tempstorage) {
            boolean matches = true;

            if (recentWeek && mood.getTimestamp().getSeconds() * 1000 < oneWeekAgo) {
                matches = false;
            }


            Log.d("MoodFilter", "From DB Emotion: " + mood.getEmotion());
//            String adjustedEmotion = mood.getEmotion().substring(0, mood.getEmotion().length() - 3); // used this so that I could remove the emoji stored within the db before comparison
//            Log.d("MoodFilter", "Adjusted Emotion: " + adjustedEmotion);
            Log.d("MoodFilter", "Filter Emotion: " + emotion);
            if (emotion != null && !emotion.isEmpty() && !mood.getEmotion().toLowerCase().contains(emotion.toLowerCase())) {
                matches = false;
            }

            if (reasonKeyword != null && !reasonKeyword.isEmpty() &&
                    !mood.getTrigger().toLowerCase().contains(reasonKeyword.toLowerCase())) {
                matches = false;
            }

            if (matches) {
                filteredList.add(mood);
            }
        }
        moodAdapter.updateMoodList(filteredList);
        moodAdapter.notifyDataSetChanged();
    }
}