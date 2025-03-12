package com.example.justacupofjavapersonal.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for the HomeFragment.
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructor for the HomeViewModel.
     */
    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    /**
     * Text to be displayed.
     *
     * @return the text to be displayed in the home fragment
     */
    public LiveData<String> getText() {
        return mText;
    }
}