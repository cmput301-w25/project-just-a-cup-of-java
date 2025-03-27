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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);

        // Calendar button
        Button calendarButton = view.findViewById(R.id.button);
        calendarButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_moodHistory_to_home);
        });

        // Filter button
        Button filterButton = view.findViewById(R.id.filterbutton);
        filterButton.setOnClickListener(v -> {
            MoodFilterDialog dialog = new MoodFilterDialog((recentWeek, emotion, reasonKeyword) -> {
                // Future: Apply filters
                System.out.println("Filters applied: RecentWeek=" + recentWeek + ", Emotion=" + emotion + ", Reason=" + reasonKeyword);
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

        // Load moods for the current user, sorted by timestamp in descending order - I touched this
        db.collection("moods")
                .whereEqualTo("uid", user.getUid()) // Only load moods for the current user - I touched this
                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by timestamp - I touched this
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    moodList.clear(); // Clear the existing list - I touched this
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Mood mood = doc.toObject(Mood.class); // Convert document to Mood object - I touched this
                        if (mood != null) {
                            moodList.add(mood); // Add to the list - I touched this
                        }
                    }
                    moodAdapter.notifyDataSetChanged(); // Update the RecyclerView - I touched this
                    Log.d("MoodHistory", "Loaded " + moodList.size() + " moods"); // I touched this
                })
                .addOnFailureListener(e -> {
                    Log.e("MoodHistory", "Error loading moods", e); // I touched this
                    Toast.makeText(getContext(), "Error loading moods", Toast.LENGTH_SHORT).show(); // I touched this
                });
    }

    private void deleteMood(int position) {
        if (position >= 0 && position < moodList.size()) {
            Mood moodToDelete = moodList.get(position);
            moodList.remove(position);
            moodAdapter.notifyItemRemoved(position);

            FirebaseDB firebaseDB = new FirebaseDB();
            firebaseDB.deleteMood(moodToDelete);
            Toast.makeText(getContext(), "Mood deleted", Toast.LENGTH_SHORT).show();
        }
    }
}