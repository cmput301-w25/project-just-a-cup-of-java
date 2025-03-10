package com.example.justacupofjavapersonal.ui.addmoodfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class AddMoodEventViewModel extends ViewModel {
    private final MutableLiveData<String> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<List<String>> moodList = new MutableLiveData<>(new ArrayList<>());

    public void setSelectedDate(String date) {
        selectedDate.setValue(date);
    }

    public LiveData<String> getSelectedDate() {
        return selectedDate;
    }

    public void initializeSelectedDate(String date) {
        if (selectedDate.getValue() == null) {
            selectedDate.setValue(date);
        }
    }

    public void addMood(String mood) {
        List<String> currentMoods = moodList.getValue();
        currentMoods.add(mood);
        moodList.setValue(currentMoods);
    }

    public LiveData<List<String>> getMoodList() {
        return moodList;
    }
}
