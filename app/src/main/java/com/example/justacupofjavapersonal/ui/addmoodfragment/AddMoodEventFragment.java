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
import androidx.lifecycle.ViewModelProvider;
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
    private AddMoodEventViewModel viewModel;

    private String selectedDate;

    private ArrayList<String> weekDates;
    private WeekAdapter weekAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialize View Binding
        binding = FragmentAddMoodBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AddMoodEventViewModel.class);

        // Retrieve selected date from arguments
        if (getArguments() != null) {
            String calendarSelectedDate = AddMoodEventFragmentArgs.fromBundle(getArguments()).getSelectedDate();
            viewModel.initializeSelectedDate(calendarSelectedDate); // Ensure ViewModel has an initial value
        }

        // Observe changes in ViewModel to update UI
        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            binding.selectedDateTextView.setText("Selected Date: " + selectedDate);
            weekDates = getWeekDates(selectedDate);
            setupWeekRecyclerView(); // Ensure RecyclerView updates
        });



//        weekDates = getWeekDates(selectedDate);
//        // Set up RecyclerView
//        setupWeekRecyclerView();

        view.findViewById(R.id.addingMood).setOnClickListener(v -> {
            // Get the current time
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentTime = timeFormat.format(new Date());


            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate); // Ensure selectedDate is stored correctly
            bundle.putString("selectedTime", currentTime);

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_post_mood,bundle);
        });
        return view;
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

            for (int i = 0; i < 7; i++) {
                String formattedDate = new SimpleDateFormat("EEE dd", Locale.getDefault()).format(calendar.getTime());
                weekList.add(formattedDate);



                calendar.add(Calendar.DATE, 1); // Move to the next day
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return weekList;
    }

private void setupWeekRecyclerView() {
    if (selectedDate == null || selectedDate.isEmpty()) {
        selectedDate = viewModel.getSelectedDate().getValue(); // Ensure it has a value
    }
    // ✅ Prevent crash if weekDates is null
    if (weekDates == null || weekDates.isEmpty()) {
        Log.e("AddMoodEventFragment", "weekDates is null or empty. Cannot setup RecyclerView.");
        return;
    }
    // Find the position of the selected date in the week list
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
        if (weekDates.get(i).equals(formattedSelectedDate)) { // Compare full date string
            selectedPosition = i;
            break;
        }
    }

    // Initialize adapter with selected position
    weekAdapter = new WeekAdapter(weekDates, selectedPosition, (position, date) -> {
        selectedDate = convertToFullDate(date);
        viewModel.setSelectedDate(selectedDate); // Save the updated date

        binding.selectedDateTextView.setText("Selected Date: " + date);
        Log.d("AddMoodEventFragment", "Updated Selected Date: " + selectedDate);


    });

    binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    binding.weekRecyclerView.setAdapter(weekAdapter);
}
    private String convertToFullDate(String weekDayAndDate) {
        try {
            // Convert "Mon 12" → "12-03-2025"
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