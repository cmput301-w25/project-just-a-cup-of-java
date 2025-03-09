package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.databinding.FragmentAddMoodBinding;
import com.example.justacupofjavapersonal.ui.viewmodel.MoodViewModel;
import com.example.justacupofjavapersonal.ui.weekadapter.WeekAdapter;
import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddMoodEventFragment extends Fragment {
    private FragmentAddMoodBinding binding;
    private ListView moodListView;
    private ArrayAdapter<String> adapter;
    private List<String> moodList = new ArrayList<>();
    private ArrayList<String> weekDates;
    private WeekAdapter weekAdapter;
    private MoodViewModel moodViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMoodBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // ✅ Initialize ViewModel
        moodViewModel = new ViewModelProvider(requireActivity()).get(MoodViewModel.class);

        // Initialize ListView and Adapter
        moodListView = binding.moodListView;
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1);
        moodListView.setAdapter(adapter);

        // ✅ Observe the mood list from ViewModel
        moodViewModel.getMoodList().observe(getViewLifecycleOwner(), moods -> {
            adapter.clear();
            adapter.addAll(moods);
            adapter.notifyDataSetChanged();
        });

        // ✅ Receive selected mood from PostMoodFragment
        if (getArguments() != null) {
            String selectedMood = getArguments().getString("selectedMood");
            if (selectedMood != null) {
                moodViewModel.addMood(selectedMood);
            }
        }

        // ✅ Handle navigation to PostMoodFragment when clicking "+"
        view.findViewById(R.id.addingMood).setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_post_mood);
        });

        setupWeekRecyclerView();
        return view;
    }

    private void setupWeekRecyclerView() {
        weekDates = getWeekDates(); // No need for selectedDate
        weekAdapter = new WeekAdapter(weekDates);
        binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.weekRecyclerView.setAdapter(weekAdapter);
    }

    // ✅ Get a static list of week days instead of using selectedDate
    private ArrayList<String> getWeekDates() {
        ArrayList<String> weekList = new ArrayList<>();
        String[] days = {"Sun 12", "Mon 13", "Tue 14", "Wed 15", "Thu 16", "Fri 17", "Sat 18"};
        for (String day : days) {
            weekList.add(day);
        }
        return weekList;
    }
//
//    //when clicking on the mood posted goes to the post mood update page
//    private void navigateToPostMoodUpdate(String selectedMood) {
//        Bundle bundle = new Bundle();
//        bundle.putString("selectedMood", selectedMood);
//        Navigation.findNavController(getView()).navigate(R.id.action_addMood_to_postMood, bundle);
//    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("moodList", new ArrayList<>(moodList));
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            moodList.addAll(savedInstanceState.getStringArrayList("moodList"));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


