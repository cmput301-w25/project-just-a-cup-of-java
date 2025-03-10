package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.databinding.FragmentAddMoodBinding;
import com.example.justacupofjavapersonal.ui.mood.MoodActionsAdapter;
import com.example.justacupofjavapersonal.ui.weekadapter.WeekAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddMoodEventFragment extends Fragment {
    private HashMap<String, ArrayList<String>> moodMap = new HashMap<>(); // Stores moods by date
    private String selectedDate;
    private FragmentAddMoodBinding binding;
    private AddMoodEventViewModel viewModel;
    private String selectedMood;
    private ArrayList<String> weekDates;
    private WeekAdapter weekAdapter;
    private ArrayList<String> moodList = new ArrayList<>();
    private MoodActionsAdapter moodAdapter;

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

        // Setup mood list with RecyclerView
        setupMoodRecyclerView();

        // Observe selected date from ViewModel
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            binding.selectedDateTextView.setText("Selected Date: " + selectedDate);
            weekDates = getWeekDates(selectedDate);
            setupWeekRecyclerView();
        });

        final String[] selectedSocialSituationWrapper = {selectedSocialSituation};
        final String[] optionalTriggerWrapper = {optionalTrigger};

        // 🔹 Listen for the mood event from PostMoodFragment
        getParentFragmentManager().setFragmentResultListener("moodEvent", this, (requestKey, bundle) -> {
            if ("moodEvent".equals(requestKey)) {
                String newSelectedDate = bundle.getString("selectedDate", "No date selected");
                String selectedTime = bundle.getString("selectedTime", "No time selected");
                String selectedMood = bundle.getString("selectedMood", "");

                selectedSocialSituationWrapper[0] = bundle.getString("selectedSocialSituation", selectedSocialSituationWrapper[0]);
                optionalTriggerWrapper[0] = bundle.getString("optionalTrigger", optionalTriggerWrapper[0]);

                binding.selectedDateTextView.setText("Selected Date: " + newSelectedDate);

                // 🔹 Store mood events specific to the selected date
                if (!moodMap.containsKey(newSelectedDate)) {
                    moodMap.put(newSelectedDate, new ArrayList<>()); // Initialize list if not present
                }
                ArrayList<String> moodsForDate = moodMap.get(newSelectedDate);

                // Append new mood event to the list
                String moodEntry = "Mood: " + selectedMood + "\n"
                        + "Social Situation: " + selectedSocialSituationWrapper[0] + "\n"
                        + "Trigger: " + optionalTriggerWrapper[0] + "\n"
                        + "Time: " + selectedTime;
                moodsForDate.add(moodEntry);
                moodAdapter.notifyDataSetChanged();
                selectedDate = newSelectedDate;
                loadMoodsForDate(newSelectedDate);
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

    // Setup mood list adapter using RecyclerView
    private void setupMoodRecyclerView() {
        moodAdapter = new MoodActionsAdapter(getContext(), moodList, position -> deleteMood(position));
        binding.moodListView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.moodListView.setAdapter(moodAdapter);
    }

    private void deleteMood(int position) {
        if (position >= 0 && position < moodList.size()) {
            moodList.remove(position);
            moodAdapter.notifyItemRemoved(position);
        }
    }

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

        for (int i = 0; i < weekDates.size(); i++) {
            if (weekDates.get(i).equals(formattedSelectedDate)) {
                selectedPosition = i;
                break;
            }
        }

        weekAdapter = new WeekAdapter(weekDates, selectedPosition, (position, date) -> {
            selectedDate = convertToFullDate(date);
            viewModel.setSelectedDate(selectedDate);
            binding.selectedDateTextView.setText("Selected Date: " + selectedDate);
            loadMoodsForDate(selectedDate);
        });

        binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.weekRecyclerView.setAdapter(weekAdapter);
    }

    private void loadMoodsForDate(String date) {
        moodList.clear();
        if (moodMap.containsKey(date) && !moodMap.get(date).isEmpty()) {
            moodList.addAll(moodMap.get(date));
        }
        moodAdapter.notifyDataSetChanged();
    }

    private String convertToFullDate(String weekDayAndDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = inputFormat.parse(weekDayAndDate);
            return outputFormat.format(date);
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
