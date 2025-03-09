package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.example.justacupofjavapersonal.R;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavArgs;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.justacupofjavapersonal.databinding.FragmentAddMoodBinding;
//import com.example.justacupofjavapersonal.AddMoodEventFragmentArgs;
import com.example.justacupofjavapersonal.databinding.FragmentAddMoodBinding;
import com.example.justacupofjavapersonal.ui.weekadapter.WeekAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class AddMoodEventFragment extends Fragment {

    private FragmentAddMoodBinding binding;
    private String selectedDate;

    private ArrayList<String> weekDates;
    private WeekAdapter weekAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize View Binding
        binding = FragmentAddMoodBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Retrieve selected date from arguments
        if (getArguments() != null) {
            selectedDate = AddMoodEventFragmentArgs.fromBundle(getArguments()).getSelectedDate();
        }

        // Display selected date in TextView
        //binding.selectedDateTextView.setText("Selected Date: " + selectedDate);


        weekDates = getWeekDates(selectedDate);
        // Set up RecyclerView
        setupWeekRecyclerView();

        view.findViewById(R.id.addingMood).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_post_mood);
        });
        return view;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Get NavController for navigating between fragments

//
        // Set click listener on the plus image (addingMood)

//    }

    private ArrayList<String> getWeekDates(String selectedDate) {
        ArrayList<String> weekList = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(selectedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Move to the start of the week (Sunday)
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            // Debugging: Print the start date
            Log.d("WeekDebug", "Start of Week: " + sdf.format(calendar.getTime()));

            // Loop 7 times to ensure a full week is added
            for (int i = 0; i < 7; i++) {
                String formattedDate = new SimpleDateFormat("EEE dd", Locale.getDefault()).format(calendar.getTime());
                weekList.add(formattedDate);

                // Debugging: Print each day's value
                Log.d("WeekDebug", "Added Day: " + formattedDate);

                calendar.add(Calendar.DATE, 1); // Move to the next day
            }

            // Debugging: Print final week list
            Log.d("WeekDebug", "Final Week List: " + weekList.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return weekList;
    }
//PREVIOUS
//    private void setupWeekRecyclerView() {
//        weekAdapter = new WeekAdapter(weekDates);
//        binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
//        binding.weekRecyclerView.setAdapter(weekAdapter);
//    }
private void setupWeekRecyclerView() {
    // Find the position of the selected date in the week list
    int selectedPosition = -1;

    // Format selectedDate into "EEE dd" format to match the week dates
    SimpleDateFormat sdf = new SimpleDateFormat("EEE dd", Locale.getDefault());
    String formattedSelectedDate = "";

    try {
        // Parse the selected date to the desired format
        Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(selectedDate);
        formattedSelectedDate = sdf.format(date);
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Now compare formattedSelectedDate with each date in the week list
    for (int i = 0; i < weekDates.size(); i++) {
        if (weekDates.get(i).equals(formattedSelectedDate)) { // Compare full date string
            selectedPosition = i;
            break;
        }
    }

    // Initialize adapter with selected position
    weekAdapter = new WeekAdapter(weekDates, selectedPosition, (position, date) -> {
        binding.selectedDateTextView.setText("Selected Date: " + date);
    });

    binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    binding.weekRecyclerView.setAdapter(weekAdapter);
}
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}