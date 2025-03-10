package com.example.justacupofjavapersonal.ui.postmood;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.ui.mood.MoodSelectorDialogFragment;

public class PostMoodFragment extends Fragment implements MoodSelectorDialogFragment.MoodSelectionListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText optionalTriggerEditText;
    private Spinner socialSituationSpinner;
    private EditText whyFeelEditText;
    private Button postButton;
    private ImageView addPhotoImageView;
    private Uri selectedImageUri;
    private TextView dateTextView;
    private TextView timeTextView;
    private Button addMoodButton;
    private String selectedMood = "Add Emotional State";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_mood, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the views
        optionalTriggerEditText = view.findViewById(R.id.triggerText);
        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
        whyFeelEditText = view.findViewById(R.id.whyFeel);
        addPhotoImageView = view.findViewById(R.id.addPhoto);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        postButton = view.findViewById(R.id.postmoodbutton);

        // Get arguments passed from AddMoodEventFragment
        Bundle args = getArguments();
        if (args != null) {
            String selectedDate = args.getString("selectedDate", "No date passed");
            String selectedTime = args.getString("selectedTime", "No time passed");

            // Debugging: Print values to Logcat
            Log.d("PostMoodFragment", "Received Date: " + selectedDate);
            Log.d("PostMoodFragment", "Received Time: " + selectedTime);

            // Set date and time in UI
            dateTextView.setText(selectedDate);
            timeTextView.setText(selectedTime);
        }
        else {
            Log.e("PostMoodFragment", "No arguments received!");
        }


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.social_situation_options)
        ) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item ("Select a social situation") to prevent selection
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    // Grey out the first item
                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(adapter);
        socialSituationSpinner.setSelection(0);

        // Find the "Add Emotional State" button
        View addMoodButton = view.findViewById(R.id.addEmoStateButton);

        // Ensure button is not null
        if (addMoodButton != null) {
            // Open Mood Selector Dialog when button is clicked
            addMoodButton.setOnClickListener(v -> {
                MoodSelectorDialogFragment moodDialog = new MoodSelectorDialogFragment(this);
                moodDialog.show(getParentFragmentManager(), "MoodSelector");
            });
        } else {
            Log.e("PostMoodFragment", "AddEmoStateButton is null!");
        }

        postButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();

            // Retrieve selected date and mood
            String selectedDate = dateTextView.getText().toString();
            String selectedMood = this.selectedMood;
            String selectedSocialSituation = socialSituationSpinner.getSelectedItem().toString();
            String optionalTrigger = optionalTriggerEditText.getText().toString();

            bundle.putString("selectedDate", selectedDate);
            bundle.putString("selectedMood", selectedMood);
            bundle.putString("selectedSocialSituation", selectedSocialSituation);
            bundle.putString("optionalTrigger", optionalTrigger);


            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_add_mood, bundle);
        });

    }

    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;
        if (addMoodButton != null) {
            addMoodButton.setText(mood);
        }
    }
}
