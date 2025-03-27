package com.example.justacupofjavapersonal.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel class for the NotificationsFragment.
 */
public class NotificationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Constructor for the NotificationsViewModel.
     */
    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    /**
     * Text to be displayed.
     *
     * @return the text to be displayed in the notifications fragment
     */
    public LiveData<String> getText() {
        return mText;
    }
}