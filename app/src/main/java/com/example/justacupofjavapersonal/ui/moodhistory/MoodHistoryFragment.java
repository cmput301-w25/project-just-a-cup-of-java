package com.example.justacupofjavapersonal.ui.moodhistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;

public class MoodHistoryFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mood_history, container, false);
        // Find the Calendar button
        Button calendarButton = view.findViewById(R.id.button);


        // Navigate to HomeFragment (Calendar) when clicked
        calendarButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_moodHistory_to_home);
        });


        // Find the filter button
        Button filterButton = view.findViewById(R.id.filterbutton);

        filterButton.setOnClickListener(v -> {
            MoodFilterDialog dialog = new MoodFilterDialog((recentWeek, emotion, reasonKeyword) -> {
                // For now, just log or print the selected filters (later, we'll apply them)
                System.out.println("Filters applied: RecentWeek=" + recentWeek + ", Emotion=" + emotion + ", Reason=" + reasonKeyword);
            });

            dialog.show(getParentFragmentManager(), "MoodFilterDialog");
        });


        return view;
    }



}
