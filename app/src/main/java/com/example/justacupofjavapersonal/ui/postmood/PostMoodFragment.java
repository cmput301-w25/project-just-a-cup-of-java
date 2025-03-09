package com.example.justacupofjavapersonal.ui.postmood;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.ui.mood.MoodSelectorDialogFragment;

public class PostMoodFragment extends Fragment {

    private Button addMoodButton; // Button to update

    private EditText optionalTriggerEditText;
    private Spinner socialSituationSpinner;
    private EditText whyFeelEditText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_mood, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // spinner implementation
        optionalTriggerEditText = view.findViewById(R.id.triggerText);
        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
        whyFeelEditText = view.findViewById(R.id.whyFeel);

        // Set up the Spinner (dropdown) for Social Situation
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.social_situation_options, // This is the array from strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(adapter);

        // Find the "Add Emotional State" button
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        Button postButton = view.findViewById(R.id.postmoodbutton); // ✅ Add reference to "Post" button

        // Ensure button is not null
        if (addMoodButton != null) {
            // Open Mood Selector Dialog when button is clicked
            addMoodButton.setOnClickListener(v -> {
                MoodSelectorDialogFragment moodDialog = new MoodSelectorDialogFragment();

                // Listen for mood selection and update button text
                moodDialog.setMoodSelectionListener(selectedMood -> {
                    addMoodButton.setText(selectedMood); // ✅ Update button text
                });

                moodDialog.show(getParentFragmentManager(), "MoodSelector");
            });
        } else {
            Log.e("PostMoodFragment", "AddEmoStateButton is null!");
        }


        postButton.setOnClickListener(v -> {
            if (addMoodButton.getText().equals("Add Emotional State")) {
                Log.e("PostMoodFragment", "No mood selected!");
                return;
            }

            String selectedMood = addMoodButton.getText().toString();

            // ✅ Send only `selectedMood`, remove `selectedDate`
            Bundle bundle = new Bundle();
            bundle.putString("selectedMood", selectedMood);

            Navigation.findNavController(v).navigate(R.id.action_postMood_to_addMood, bundle);
        });


    }
}
