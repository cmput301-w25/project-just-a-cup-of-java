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


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get NavController for navigating between fragments
        NavController navController = Navigation.findNavController(view);

        // Set click listener on the plus image (addingMood)
        view.findViewById(R.id.addingMood).setOnClickListener(v -> {
            navController.navigate(R.id.navigation_post_mood);
        });
    }

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

    private void setupWeekRecyclerView() {
        weekAdapter = new WeekAdapter(weekDates);
        binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.weekRecyclerView.setAdapter(weekAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}