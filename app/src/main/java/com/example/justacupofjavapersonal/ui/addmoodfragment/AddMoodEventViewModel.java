package com.example.justacupofjavapersonal.ui.addmoodfragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddMoodEventViewModel extends ViewModel {
    private final MutableLiveData<String> selectedDate = new MutableLiveData<>();

    public void setSelectedDate(String date) {
        selectedDate.setValue(date);
    }

    public LiveData<String> getSelectedDate() {
        return selectedDate;
    }


    public void initializeSelectedDate(String date) {
        if (selectedDate.getValue() == null) { // dont overwriting existing value
            selectedDate.setValue(date);
        }
    }
}
