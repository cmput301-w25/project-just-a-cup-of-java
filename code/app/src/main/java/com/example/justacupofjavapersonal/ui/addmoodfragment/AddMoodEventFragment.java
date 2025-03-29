package com.example.justacupofjavapersonal.ui.addmoodfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.databinding.FragmentAddMoodBinding;
import com.example.justacupofjavapersonal.ui.weekadapter.WeekAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddMoodEventFragment extends Fragment {
    private FragmentAddMoodBinding binding;
    private AddMoodEventViewModel viewModel;
    private ArrayList<Mood> moodList = new ArrayList<>();
    private MoodActionsAdapter moodAdapter;
    private String selectedDate;
    private ArrayList<String> weekDates;
    private WeekAdapter weekAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMoodBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(AddMoodEventViewModel.class);

        setupMoodRecyclerView();

        // Handle argument from navigation
        if (getArguments() != null) {
            String calendarSelectedDate = AddMoodEventFragmentArgs.fromBundle(getArguments()).getSelectedDate();
            viewModel.initializeSelectedDate(calendarSelectedDate);
        }

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            selectedDate = date;
            binding.selectedDateTextView.setText("Selected Date: " + selectedDate);
            Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
            weekDates = getWeekDates(selectedDate);
            loadMoodsForDate(selectedDate);
            setupWeekRecyclerView();
        });

        // Listen for result from PostMoodFragment
        getParentFragmentManager().setFragmentResultListener("moodEvent", getViewLifecycleOwner(), (requestKey, result) -> {
            String date = result.getString("selectedDate");
            loadMoodsForDate(date); // Refresh Firebase mood list
        });

        binding.addingMood.setOnClickListener(v -> {
            String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

            Bundle bundle = new Bundle();
            bundle.putString("selectedDate", selectedDate);
            bundle.putString("selectedTime", currentTime);

            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_post_mood, bundle);
        });

        return view;
    }

    private void setupMoodRecyclerView() {
        moodAdapter = new MoodActionsAdapter(
                getContext(),
                moodList,
                this::deleteMood,
                this::editMood
        );

        binding.moodListView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.moodListView.setAdapter(moodAdapter);
    }

    private void editMood(int position) {
        if (position >= 0 && position < moodList.size()) {
            Mood moodToEdit = moodList.get(position);

            Bundle bundle = new Bundle();
            bundle.putString("editDate", moodToEdit.getDate());
            bundle.putString("editTime", moodToEdit.getTime());
            bundle.putString("editMood", moodToEdit.getEmotion());
            bundle.putString("editSocialSituation", moodToEdit.getSocialSituation());
            bundle.putString("editTrigger", moodToEdit.getTrigger());
            bundle.putString("editPrivacy", moodToEdit.getPrivacy());
            bundle.putString("editPhoto", moodToEdit.getPhoto());
            bundle.putString("editMoodID", moodToEdit.getMoodID());
            bundle.putInt("editPosition", position);

            Navigation.findNavController(requireView()).navigate(R.id.navigation_post_mood, bundle);
        }
    }

    private void deleteMood(int position) {
        if (position >= 0 && position < moodList.size()) {
            Mood moodToDelete = moodList.get(position);
            moodList.remove(position);
            moodAdapter.notifyItemRemoved(position);

            FirebaseDB firebaseDB = new FirebaseDB();
            firebaseDB.deleteMood(moodToDelete);
            Toast.makeText(getContext(), "Mood deleted", Toast.LENGTH_SHORT).show();
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
        if (weekDates == null || weekDates.isEmpty()) return;

        int selectedPosition = -1;
        String formattedSelectedDate = "";
        try {
            Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(selectedDate);
            formattedSelectedDate = new SimpleDateFormat("EEE dd", Locale.getDefault()).format(date);
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
        });

        binding.weekRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.weekRecyclerView.setAdapter(weekAdapter);
    }

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

    public void loadMoodsForDate(String date) {
        moodList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            db.collection("moods")
                    .whereEqualTo("uid", currentUser.getUid())
                    .whereEqualTo("date", date)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Mood mood = document.toObject(Mood.class);
                            if (mood != null) {
                                mood.setMoodID(document.getId());
                                moodList.add(mood);
                            }
                        }
                        moodAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error fetching moods: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
