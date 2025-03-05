package com.example.just_a_cup_of_java;

package com.example.moodapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MoodHistoryActivity extends AppCompatActivity {

    private RecyclerView moodRecyclerView;
    private MoodAdapter moodAdapter;
    private List<Mood> moodList;
    private FirebaseFirestore db;
    private CollectionReference moodsRef;
    private Button btnFilterDates, btnFilterMoodHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);

        // Initialize UI components
        moodRecyclerView = findViewById(R.id.rvMoodHistory);
        btnFilterDates = findViewById(R.id.btnDates);
        btnFilterMoodHistory = findViewById(R.id.btnMyMoodHistory);

        // Set up RecyclerView
        moodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        moodList = new ArrayList<>();
        moodAdapter = new MoodAdapter(moodList);
        moodRecyclerView.setAdapter(moodAdapter);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        moodsRef = db.collection("Moods");

        // Load moods in real-time
        loadMoodHistory();

        // Set up button click listeners for filters
        btnFilterDates.setOnClickListener(v -> filterByDate());
        btnFilterMoodHistory.setOnClickListener(v -> filterByMoodType());
    }

    private void loadMoodHistory() {
        moodsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }

                moodList.clear();
                for (QueryDocumentSnapshot document : value) {
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

    private void filterByMoodType() {
        // Implement mood type filtering logic
    }
}
