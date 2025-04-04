package com.example.justacupofjavapersonal.ui.addmoodfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.justacupofjavapersonal.class_resources.mood.Mood;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel class for the AddMoodEventFragment.
 * This class holds the selected date and the list of moods that the user has selected.
 */
public class AddMoodEventViewModel extends ViewModel {
    private final MutableLiveData<String> selectedDate = new MutableLiveData<>();
    private final MutableLiveData<List<Mood>> moodList = new MutableLiveData<>(new ArrayList<>());

    /**
     * Sets the selected date to the given date.
     *
     * @param date the date to set as the selected date
     */
    public void setSelectedDate(String date) {
        selectedDate.setValue(date);
    }

    /**
     * Returns the selected date.
     *
     * @return the selected date
     */
    public LiveData<String> getSelectedDate() {
        return selectedDate;
    }

    /**
     * Initializes the selected date to the given date if it has not been set yet.
     *
     * @param date the date to initialize the selected date to
     */
    public void initializeSelectedDate(String date) {
        if (selectedDate.getValue() == null) {
            selectedDate.setValue(date);
        }
    }

    /**
     * Adds the given mood to the list of moods.
     *
     * @param mood the mood to add to the list
     */
    public void addMood(Mood mood) {
        List<Mood> currentMoods = moodList.getValue();
        currentMoods.add(mood);
        moodList.setValue(currentMoods);
    }

    /**
     * Removes the given mood from the list of moods.
     *
     */
    public LiveData<List<Mood>> getMoodList() {
        return moodList;
    }
}
