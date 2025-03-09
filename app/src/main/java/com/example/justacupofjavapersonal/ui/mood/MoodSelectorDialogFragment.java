package com.example.justacupofjavapersonal.ui.mood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.justacupofjavapersonal.R;
import java.util.ArrayList;
import java.util.Arrays;

public class MoodSelectorDialogFragment extends DialogFragment {

    private GridView moodGridView;

    // 🔥 Interface to send selected mood back to PostMoodFragment
    public interface MoodSelectionListener {
        void onMoodSelected(String mood);
    }

    private MoodSelectionListener moodSelectionListener;

    public void setMoodSelectionListener(MoodSelectionListener listener) {
        this.moodSelectionListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mood_selector, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Set width to 85% of screen width
            getDialog().getWindow().setLayout((int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moodGridView = view.findViewById(R.id.moodGridView);

        // List of moods with emojis
        ArrayList<String> moods = new ArrayList<>(Arrays.asList(
                "Happiness 😀", "Fear 😱", "Anger 😡", "Disgust 🤢",
                "Confused 🤔", "Shame 🤫", "Sadness 😔", "Surprise 😮"
        ));

        // Set up grid adapter
        MoodGridAdapter adapter = new MoodGridAdapter(requireContext(), moods);
        moodGridView.setAdapter(adapter);

        // Handle mood selection
        moodGridView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedMood = moods.get(position);

            // 🔥 Send selected mood back to PostMoodFragment
            if (moodSelectionListener != null) {
                moodSelectionListener.onMoodSelected(selectedMood);
            }

            dismiss(); // Close the popup
        });
    }
}
