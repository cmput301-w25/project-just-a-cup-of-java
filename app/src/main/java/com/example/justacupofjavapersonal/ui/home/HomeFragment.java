package com.example.justacupofjavapersonal.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.databinding.FragmentHomeBinding;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Reference to the calendar
        MaterialCalendarView calendarView = binding.calendarView;

        // Set up a listener for when a date is selected
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            if (selected) {
                    // Convert selected date into "dd-MM-yyyy" format
                    String selectedDate = String.format("%02d-%02d-%d", date.getDay(), date.getMonth() + 1, date.getYear());
                // Navigate to AddMoodEventFragment and pass the selected date
                NavController navController = Navigation.findNavController(requireView());
                HomeFragmentDirections.ActionNavigationHomeToAddMoodEventFragment action =
                        HomeFragmentDirections.actionNavigationHomeToAddMoodEventFragment(selectedDate);
                navController.navigate(action);
            }
        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get NavController for navigating between fragments
        NavController navController = Navigation.findNavController(view);

        // Add click listener for mood history button
        binding.btnMoodhistory.setOnClickListener(v ->
                navController.navigate(R.id.action_navigation_home_to_moodHistory)
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
