package com.example.justacupofjavapersonal.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for the DashboardFragment.
 */
public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructor for the DashboardViewModel.
     */
    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    /**
     * Text to be displayed.
     *
     * @return the text to be displayed in the dashboard fragment
     */
    public LiveData<String> getText() {
        return mText;
    }
}