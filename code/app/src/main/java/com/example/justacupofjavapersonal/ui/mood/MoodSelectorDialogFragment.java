package com.example.justacupofjavapersonal.ui.mood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.justacupofjavapersonal.R;
import java.util.ArrayList;
import java.util.Arrays;

public class MoodSelectorDialogFragment extends DialogFragment {
    public MoodSelectorDialogFragment() {

    }

    public interface MoodSelectionListener {
        void onMoodSelected(String mood);
    }
    private GridView moodGridView;
    private MoodSelectionListener moodSelectionListener;

    public MoodSelectorDialogFragment(MoodSelectionListener listener) {
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
            getDialog().getWindow().setLayout((int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        moodGridView = view.findViewById(R.id.moodGridView);

        ArrayList<String> moods = new ArrayList<>(Arrays.asList(
                "Happiness 😀", "Fear 😱", "Anger 😡", "Disgust 🤢",
                "Confused 🤔", "Shame 🤫", "Sadness 😔", "Surprise 😮"
        ));

        MoodGridAdapter adapter = new MoodGridAdapter(requireContext(), moods);
        moodGridView.setAdapter(adapter);

        moodGridView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedMood = moods.get(position);
            Toast.makeText(requireContext(), "Selected: " + selectedMood, Toast.LENGTH_SHORT).show();

            if (moodSelectionListener != null) {
                moodSelectionListener.onMoodSelected(selectedMood);
            }
            dismiss();
        });
    }
}
