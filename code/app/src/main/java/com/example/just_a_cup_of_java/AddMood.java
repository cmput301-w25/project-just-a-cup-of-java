package com.example.just_a_cup_of_java;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AddMood extends AppCompatActivity {

    private RecyclerView weekRecyclerView;
    private Adapter_for_week weekAdapter;
    private List<days_week> weekDays;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmoodevent);

        weekRecyclerView = findViewById(R.id.weekRecyclerView);
        weekRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Sample Data (Replace with actual logic)
        weekDays = new ArrayList<>();
        weekDays.add(new days_week ("MON", "19"));
        weekDays.add(new days_week("TUE", "20"));
        weekDays.add(new days_week("WED", "21"));
        weekDays.add(new days_week("THU", "22"));
        weekDays.add(new days_week("FRI", "23"));
        weekDays.add(new days_week("SAT", "24"));
        weekDays.add(new days_week("SUN", "25"));

        weekAdapter = new Adapter_for_week(weekDays, position -> {
            // Handle item click
        });

        weekRecyclerView.setAdapter(weekAdapter);
    }
}
