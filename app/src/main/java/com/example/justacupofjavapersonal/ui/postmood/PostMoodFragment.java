package com.example.justacupofjavapersonal.ui.postmood;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.ui.mood.MoodSelectorDialogFragment;

public class PostMoodFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_mood, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the "Add Emotional State" button
        View addMoodButton = view.findViewById(R.id.addEmoStateButton);

        // Ensure button is not null
        if (addMoodButton != null) {
            // Open Mood Selector Dialog when button is clicked
            addMoodButton.setOnClickListener(v -> {
                MoodSelectorDialogFragment moodDialog = new MoodSelectorDialogFragment();
                moodDialog.show(getParentFragmentManager(), "MoodSelector");
            });
        } else {
            Log.e("PostMoodFragment", "AddEmoStateButton is null!");
        }
    }

}
