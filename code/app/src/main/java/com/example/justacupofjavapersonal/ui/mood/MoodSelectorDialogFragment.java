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

/**
 * MoodSelectorDialogFragment is a dialog fragment that allows the user to select a mood from a list of moods.
 */
public class MoodSelectorDialogFragment extends DialogFragment {
    public MoodSelectorDialogFragment() {

    }

    /**
     * MoodSelectionListener is an interface that listens for when a mood is selected.
     */
    public interface MoodSelectionListener {
        void onMoodSelected(String mood);
    }
    private GridView moodGridView;
    private MoodSelectionListener moodSelectionListener;

    /**
     * Constructor for the MoodSelectorDialogFragment.
     *
     * @param listener the listener for when a mood is selected
     */
    public MoodSelectorDialogFragment(MoodSelectionListener listener) {
        this.moodSelectionListener = listener;
    }

    /**
     * Called to do initial creation of a fragment.
     * This is called after onAttach(Activity) and before onCreateView(LayoutInflater, ViewGroup, Bundle).
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mood_selector, container, false);
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally tied to Activity.onStart of the containing Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout((int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.85), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * It is called after onCreateView(LayoutInflater, ViewGroup, Bundle) and before onViewStateRestored(Bundle).
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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
