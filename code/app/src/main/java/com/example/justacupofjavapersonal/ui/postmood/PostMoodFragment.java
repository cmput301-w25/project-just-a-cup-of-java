package com.example.justacupofjavapersonal.ui.postmood;

import android.net.Uri;
import android.app.AlertDialog;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostMoodFragment extends Fragment implements MoodSelectorDialogFragment.MoodSelectionListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText optionalTriggerEditText;
    private Spinner socialSituationSpinner;
    private EditText whyFeelEditText;
    private CardView postButton;
    private ImageView addPhotoImageView;

    private String selectedMood = "Add Emotional State";
    private Uri selectedImageUri;
    private TextView dateTextView;
    private TextView timeTextView;
    private CardView addMoodButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseDB firebaseDB;

    private User user;
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
        super.onViewCreated(view, savedInstanceState);

        optionalTriggerEditText = view.findViewById(R.id.triggerText);
        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
        whyFeelEditText = view.findViewById(R.id.whyFeel);
        addPhotoImageView = view.findViewById(R.id.addPhoto);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        postButton = view.findViewById(R.id.postmoodbutton);


        Bundle args = getArguments();
        if (args != null) {
            String selectedDate = args.getString("selectedDate", "No date passed");
            String selectedTime = args.getString("selectedTime", "No time passed");

            Log.d("PostMoodFragment", "Received Date: " + selectedDate);
            Log.d("PostMoodFragment", "Received Time: " + selectedTime);

            dateTextView.setText(selectedDate);
            timeTextView.setText(selectedTime);
        } else {
            Log.e("PostMoodFragment", "No arguments received!");
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.social_situation_options)
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
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

        addMoodButton.setOnClickListener(v -> {
            MoodSelectorDialogFragment moodDialog = new MoodSelectorDialogFragment();
            moodDialog.setMoodSelectionListener(PostMoodFragment.this);
            moodDialog.show(getParentFragmentManager(), "MoodSelector");
        });

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

                TextView postText = postButton.findViewById(R.id.cardTextView);
                if (postText != null) postText.setText("Edit");
            }
        }

        postButton.setOnClickListener(v -> {
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
                    firebaseDB = new FirebaseDB();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (isEditMode && moodToEdit != null) {
                        moodToEdit.setEmotion(selectedMood);
                        moodToEdit.setTrigger(optionalTriggerEditText.getText().toString());
                        moodToEdit.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
                        moodToEdit.setPrivacy(privacySetting);

                        MaterialToolbar toolbar = requireActivity().findViewById(R.id.topAppBar);
                        toolbar.setTitle("Edit Mood");

                        // Set button text
                        TextView buttonText = postButton.findViewById(R.id.cardTextView);
                        if (buttonText != null) {
                            buttonText.setText("Edit");
                        }


                        firebaseDB.updateMoodInDB(moodToEdit);
                        Toast.makeText(requireContext(), "Mood updated!", Toast.LENGTH_SHORT).show();

                        // Navigate back to AddMoodFragment
                        NavController navController = Navigation.findNavController(view);
                        navController.popBackStack();
                        dialog.dismiss();
                    } else {
                        moodPost = new Mood();
                        moodPost.isDeserializing = false;
                        moodPost.setDate(dateTextView.getText().toString());
                        moodPost.setTime(timeTextView.getText().toString());
                        moodPost.setEmotion(selectedMood);
                        moodPost.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
                        moodPost.setTrigger(optionalTriggerEditText.getText().toString());
                        moodPost.setPrivacy(privacySetting);

                        Bundle result = new Bundle();
                        result.putString("selectedDate", moodPost.getDate());
                        result.putString("selectedTime", moodPost.getTime());
                        result.putString("selectedMood", moodPost.getEmotion());
                        result.putString("selectedSocialSituation", moodPost.getSocialSituation());
                        result.putString("optionalTrigger", moodPost.getTrigger());
                        result.putString("privacySetting", privacySetting);

                        if (currentUser != null) {
                            db.collection("users").document(currentUser.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            user = documentSnapshot.toObject(User.class);
                                            firebaseDB.addMoodtoDB(moodPost, currentUser.getUid());
                                            getParentFragmentManager().setFragmentResult("moodEvent", result);
                                            NavController navController = Navigation.findNavController(v);
                                            navController.popBackStack();
                                            dialog.dismiss();
                                        }
                                    });
                        } else {
                            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            });

            dialog.show();
        });
    }

    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;

        if (addMoodButton != null) {
            TextView textViewInsideCard = addMoodButton.findViewById(R.id.cardTextView);
            if (textViewInsideCard != null) {
                textViewInsideCard.setText(mood);
            } else {
                Log.e("PostMoodFragment", "TextView inside CardView not found!");
            }
        } else {
            Log.e("PostMoodFragment", "AddEmoStateButton is null!");
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

    private void updateMoodButtonUI(String mood) {
        if (addMoodButton != null) {
            TextView textViewInsideCard = addMoodButton.findViewById(R.id.cardTextView);
            if (textViewInsideCard != null) {
                textViewInsideCard.setText(mood);
            }
        }
    }
}
