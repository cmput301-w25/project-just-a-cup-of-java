package com.example.just_a_cup_of_java;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.just_a_cup_of_java.Mood;
import com.example.just_a_cup_of_java.MoodAdapter;
import com.example.just_a_cup_of_java.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MoodHistoryFragment extends Fragment {

    private RecyclerView moodRecyclerView;
    private MoodAdapter moodAdapter;
    private List<Mood> moodList;
    private FirebaseFirestore db;
    private CollectionReference moodsRef;
    private Button btnFilterDates, btnFilterMoodHistory;

    public MoodHistoryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mood_history, container, false);

        moodRecyclerView = view.findViewById(R.id.rvMoodHistory);
        btnFilterDates = view.findViewById(R.id.btnDates);
        btnFilterMoodHistory = view.findViewById(R.id.btnMyMoodHistory);

        moodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        moodList = new ArrayList<>();
        moodAdapter = new MoodAdapter(moodList);
        moodRecyclerView.setAdapter(moodAdapter);

        db = FirebaseFirestore.getInstance();
        moodsRef = db.collection("Moods");

        loadMoodHistory();

        btnFilterDates.setOnClickListener(v -> filterByDate());
        btnFilterMoodHistory.setOnClickListener(v -> filterByMoodHistory());

        return view;
    }

    private void loadMoodHistory() {
        moodsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                moodList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Mood mood = document.toObject(Mood.class);
                    moodList.add(mood);
                }
                moodAdapter.notifyDataSetChanged();
            }
        });
    }

    private void filterByDate() {
        // Implement date filtering logic
    }

    private void filterByMoodHistory() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            moodsRef.whereEqualTo("user.uid", user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    moodList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Mood mood = document.toObject(Mood.class);
                        moodList.add(mood);
                    }
                    moodAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
