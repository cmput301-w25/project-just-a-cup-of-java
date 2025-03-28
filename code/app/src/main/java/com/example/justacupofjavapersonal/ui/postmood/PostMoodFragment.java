package com.example.justacupofjavapersonal.ui.postmood;

import android.net.Uri;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.FirebaseDB;
import com.example.justacupofjavapersonal.class_resources.User;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.ui.mood.MoodSelectorDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostMoodFragment extends Fragment implements MoodSelectorDialogFragment.MoodSelectionListener {

    private EditText optionalTriggerEditText;
    private Spinner socialSituationSpinner;
    private TextView dateTextView, timeTextView;
    private CardView addMoodButton, postButton;
    private ImageView addPhotoImageView;

    private String selectedMood = "Add Emotional State";
    private Uri selectedImageUri;

    private FirebaseDB firebaseDB;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Mood moodPost;
    private Mood moodToEdit;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_post_mood, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        optionalTriggerEditText = view.findViewById(R.id.triggerText);
        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        addPhotoImageView = view.findViewById(R.id.addPhoto);
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        postButton = view.findViewById(R.id.postmoodbutton);

        firebaseDB = new FirebaseDB();

        // Setup spinner
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.social_situation_options)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(adapter);

        // Mood picker
        addMoodButton.setOnClickListener(v -> {
            MoodSelectorDialogFragment moodDialog = new MoodSelectorDialogFragment();
            moodDialog.setMoodSelectionListener(PostMoodFragment.this);
            moodDialog.show(getParentFragmentManager(), "MoodSelector");
        });

        // Check if we're in edit mode
        Bundle args = getArguments();
        if (args != null && args.containsKey("moodToEdit")) {
            isEditMode = true;
            moodToEdit = (Mood) args.getSerializable("moodToEdit");

            if (moodToEdit != null) {
                selectedMood = moodToEdit.getEmotion();
                updateMoodButtonUI(selectedMood);

                dateTextView.setText(moodToEdit.getDate());
                timeTextView.setText(moodToEdit.getTime());
                optionalTriggerEditText.setText(moodToEdit.getTrigger());

                setSpinnerToValue(socialSituationSpinner, moodToEdit.getSocialSituation());

                // Change "Post" to "Edit"
                TextView postText = postButton.findViewById(R.id.cardTextView);
                if (postText != null) postText.setText("Edit");
            }
        } else if (args != null) {
            // Normal mode
            dateTextView.setText(args.getString("selectedDate", ""));
            timeTextView.setText(args.getString("selectedTime", ""));
        }

        postButton.setOnClickListener(v -> showPrivacyDialog(view));
    }

    private void showPrivacyDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Privacy Option");

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        CheckBox privateCheckBox = new CheckBox(requireContext());
        privateCheckBox.setText("Private");

        CheckBox publicCheckBox = new CheckBox(requireContext());
        publicCheckBox.setText("Public");

        layout.addView(privateCheckBox);
        layout.addView(publicCheckBox);

        builder.setView(layout);

        privateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) publicCheckBox.setChecked(false);
        });

        publicCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) privateCheckBox.setChecked(false);
        });

        builder.setPositiveButton(isEditMode ? "Edit" : "Post", null);
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(d -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(buttonView -> {
                if (!privateCheckBox.isChecked() && !publicCheckBox.isChecked()) {
                    Toast.makeText(requireContext(), "Please select Private or Public", Toast.LENGTH_SHORT).show();
                    return;
                }

                String privacySetting = privateCheckBox.isChecked() ? "Private" : "Public";

                if (isEditMode && moodToEdit != null) {
                    moodToEdit.setEmotion(selectedMood);
                    moodToEdit.setTrigger(optionalTriggerEditText.getText().toString());
                    moodToEdit.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
                    moodToEdit.setPrivacy(privacySetting);

                    firebaseDB.updateMoodInDB(moodToEdit);
                    Toast.makeText(requireContext(), "Mood updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Mood newMood = new Mood();
                    newMood.setDate(dateTextView.getText().toString());
                    newMood.setTime(timeTextView.getText().toString());
                    newMood.setEmotion(selectedMood);
                    newMood.setTrigger(optionalTriggerEditText.getText().toString());
                    newMood.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
                    newMood.setPrivacy(privacySetting);

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        firebaseDB.addMoodtoDB(newMood, currentUser.getUid());
                    }
                }

                NavController navController = Navigation.findNavController(getView());
                navController.popBackStack();
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void updateMoodButtonUI(String mood) {
        TextView textViewInsideCard = addMoodButton.findViewById(R.id.cardTextView);
        if (textViewInsideCard != null) {
            textViewInsideCard.setText(mood);
        }
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;
        updateMoodButtonUI(mood);
    }
}
