package com.example.justacupofjavapersonal.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class MoodViewModel extends ViewModel {
    private final MutableLiveData<List<String>> moodList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<String>> getMoodList() {
        return moodList;
    }

    public void addMood(String mood) {
        List<String> currentList = moodList.getValue();
        if (currentList != null) {
            currentList.add(mood);
            moodList.setValue(currentList);
        }
    }
}
