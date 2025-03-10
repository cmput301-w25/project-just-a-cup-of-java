package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.databinding.FragmentAddMoodBinding;
import com.example.justacupofjavapersonal.ui.weekadapter.WeekAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddMoodEventFragment extends Fragment {

    private FragmentAddMoodBinding binding;
    private AddMoodEventViewModel viewModel;
    private String selectedMood;
    private String selectedDate;
    private ArrayList<String> weekDates;
    private WeekAdapter weekAdapter;
    private ArrayList<String> moodList = new ArrayList<>();
    private ArrayAdapter<String> moodAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMoodBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AddMoodEventViewModel.class);

        // Retrieve values from arguments (Navigated from HomeFragment or PostMoodFragment)
        String selectedSocialSituation = "No social situation";
        String optionalTrigger = "No trigger";

        if (getArguments() != null) {
            String calendarSelectedDate = AddMoodEventFragmentArgs.fromBundle(getArguments()).getSelectedDate();
            selectedMood = getArguments().getString("selectedMood", "");
            selectedSocialSituation = getArguments().getString("selectedSocialSituation", "No social situation");
            optionalTrigger = getArguments().getString("optionalTrigger", "No trigger");

            viewModel.initializeSelectedDate(calendarSelectedDate);

            // Store mood event details in list
            if (selectedMood != null && !selectedMood.isEmpty()) {
                moodList.add("Mood: " + selectedMood);
            }
            if (!selectedSocialSituation.equals("No social situation")) {
                moodList.add("Social Situation: " + selectedSocialSituation);
            }
            if (!optionalTrigger.equals("No trigger")) {
                moodList.add("Trigger: " + optionalTrigger);
            }
        }

        // Setup mood list
        setupMoodList();

        // Observe selected date from ViewModel
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            binding.selectedDateTextView.setText("Selected Date: " + selectedDate);
            weekDates = getWeekDates(selectedDate);
            setupWeekRecyclerView();
        });
// Use a final array wrapper to store selectedSocialSituation and optionalTrigger
        final String[] selectedSocialSituationWrapper = {selectedSocialSituation};
        final String[] optionalTriggerWrapper = {optionalTrigger};

        getParentFragmentManager().setFragmentResultListener("moodEvent", this, (requestKey, bundle) -> {
            if ("moodEvent".equals(requestKey)) {
                String newSelectedDate = bundle.getString("selectedDate", "No date selected");
                String selectedTime = bundle.getString("selectedTime", "No time selected");
                String selectedMood = bundle.getString("selectedMood", "");
//                String selectedSocialSituation = bundle.getString("selectedSocialSituation", "No social situation");
//                String optionalTrigger = bundle.getString("optionalTrigger", "No trigger");
                // 🔹 Update existing variables instead of redeclaring them
                selectedSocialSituationWrapper[0] = bundle.getString("selectedSocialSituation", selectedSocialSituationWrapper[0]);
                optionalTriggerWrapper[0] = bundle.getString("optionalTrigger", optionalTriggerWrapper[0]);

                binding.selectedDateTextView.setText("Selected Date: " + newSelectedDate);

                // Append new mood event to the list
                String moodEntry = "Mood: " + selectedMood + "\n"
                        + "Social Situation: " + selectedSocialSituationWrapper[0] + "\n"
                        + "Trigger: " + optionalTriggerWrapper[0] + "\n"
                        + "Time: " + selectedTime;
                moodList.add(moodEntry);
                moodAdapter.notifyDataSetChanged();
            }
        });


        // Handle "Add Mood" button click
        binding.addingMood.setOnClickListener(v -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentTime = timeFormat.format(new Date());

            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate);
            bundle.putString("selectedTime", currentTime);
            bundle.putString("selectedMood", selectedMood);

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_post_mood, bundle);
        });

        return view;
    }

    // Setup mood list adapter
    private void setupMoodList() {
        moodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, moodList);
        binding.moodListView.setAdapter(moodAdapter);

        moodAdapter.notifyDataSetChanged();
    }

    // Get the week dates based on the selected date
    private ArrayList<String> getWeekDates(String selectedDate) {
        ArrayList<String> weekList = new ArrayList<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = sdf.parse(selectedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            for (int i = 0; i < 7; i++) {
                weekList.add(new SimpleDateFormat("EEE dd", Locale.getDefault()).format(calendar.getTime()));
                calendar.add(Calendar.DATE, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return weekList;
    }

    // Setup the week RecyclerView (Keeps week display the same while allowing date selection)
    private void setupWeekRecyclerView() {
        if (weekDates == null || weekDates.isEmpty()) {
            Log.e("AddMoodEventFragment", "weekDates is null or empty. Cannot setup RecyclerView.");
            return;
        }

        int selectedPosition = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd", Locale.getDefault());
        String formattedSelectedDate = "";
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(selectedDate);
            formattedSelectedDate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Find the position of the selected date in the week
        for (int i = 0; i < weekDates.size(); i++) {
            if (weekDates.get(i).equals(formattedSelectedDate)) {
                selectedPosition = i;
                break;
            }
        }

        // Set up the adapter
        weekAdapter = new WeekAdapter(weekDates, selectedPosition, (position, date) -> {
            selectedDate = convertToFullDate(date);
            viewModel.setSelectedDate(selectedDate);
            binding.selectedDateTextView.setText("Selected Date: " + date);
        });

        binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.weekRecyclerView.setAdapter(weekAdapter);
    }

    // Convert "Mon 12" format to "dd-MM-yyyy"
    private String convertToFullDate(String weekDayAndDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = inputFormat.parse(weekDayAndDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
            return outputFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
