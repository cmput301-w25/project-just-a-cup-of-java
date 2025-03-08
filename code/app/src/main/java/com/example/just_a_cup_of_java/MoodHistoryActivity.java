package com.example.just_a_cup_of_java;


import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MoodHistoryActivity extends AppCompatActivity {


    private RecyclerView moodRecyclerView;
    private MoodAdapter moodAdapter;
    private List<Mood> moodList;
    private FirebaseFirestore db;
    private CollectionReference moodsRef;
    private Button btnFilterDates, btnFilterMoodHistory;
    private FirebaseUser currentUser; // ✅ Tracks logged-in user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moodhistory);


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
        currentUser = FirebaseAuth.getInstance().getCurrentUser(); // ✅ Get logged-in user


        // Load all moods initially
        loadMoodHistory();


        // Set up button click listeners for filters
        btnFilterDates.setOnClickListener(v -> showDatePicker());
        btnFilterMoodHistory.setOnClickListener(v -> filterMyMoodHistory()); // ✅ Filter only user's moods
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
            } else {
                Toast.makeText(MoodHistoryActivity.this, "Failed to load moods", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, dayOfMonth);
            filterByDate(selectedDate.getTime()); // Call filter method
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }


    private void filterByDate(Date selectedDate) {
        moodsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                moodList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Mood mood = document.toObject(Mood.class);


                    if (mood.getPostDate() == null) continue; // Prevent null errors


                    Calendar moodCalendar = Calendar.getInstance();
                    moodCalendar.setTime(mood.getPostDate());


                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.setTime(selectedDate);


                    if (moodCalendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
                            moodCalendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
                            moodCalendar.get(Calendar.DAY_OF_MONTH) == selectedCalendar.get(Calendar.DAY_OF_MONTH)) {
                        moodList.add(mood);
                    }
                }
                moodAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MoodHistoryActivity.this, "Failed to filter moods", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void filterMyMoodHistory() {
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }


        String loggedInUserId = currentUser.getUid(); // ✅ Get user's unique ID


        moodsRef.whereEqualTo("user.uid", loggedInUserId) // ✅ Filter moods by user's ID
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        moodList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Mood mood = document.toObject(Mood.class);
                            moodList.add(mood);
                        }
                        moodAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MoodHistoryActivity.this, "Failed to filter your moods", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
