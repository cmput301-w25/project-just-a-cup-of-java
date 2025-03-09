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
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.databinding.FragmentAddMoodBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMoodEventFragment extends Fragment {

    private FragmentAddMoodBinding binding;
    private AddMoodEventViewModel viewModel;
    private String selectedMood;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMoodBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AddMoodEventViewModel.class);

        if (getArguments() != null) {
            String calendarSelectedDate = getArguments().getString("selectedDate");
            selectedMood = getArguments().getString("selectedMood", "");
            viewModel.initializeSelectedDate(calendarSelectedDate);
        }

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            binding.selectedDateTextView.setText("Selected Date: " + date);
        });

        if (selectedMood != null && !selectedMood.isEmpty()) {
            binding.MoodText.setText(selectedMood);
        }

        binding.addingMood.setOnClickListener(v -> {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String currentTime = timeFormat.format(new Date());

            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", viewModel.getSelectedDate().getValue());
            bundle.putString("selectedTime", currentTime);
            bundle.putString("selectedMood", selectedMood); // Pass selected mood forward

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_post_mood, bundle);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
