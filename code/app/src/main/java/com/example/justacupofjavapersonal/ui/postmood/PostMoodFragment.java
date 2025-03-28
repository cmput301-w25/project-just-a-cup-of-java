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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.Timestamp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

/**
 * PostMoodFragment is a fragment that allows the user to post a mood event.
 */
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
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseDB firebaseDB;

    private User user;
    private String whyFeel;
    private Mood moodPost;

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
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_post_mood, container, false);
    }

    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * It is safe to perform operations on views in this method.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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
        } else {
            Log.e("PostMoodFragment", "No arguments received!");
        }


        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.social_situation_options)
        ) {
            /**
             * Returns whether an item is enabled.
             *
             * @param position
             * @return
             */
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item ("Select a social situation") to prevent selection
                return position != 0;
            }

            /**
             * Gets the dropdown view.
             *
             * @param position
             * @param convertView
             * @param parent
             * @return
             */
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
//WORKING
//        postButton.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//
//            // Retrieve selected date and mood
//            String selectedDate = dateTextView.getText().toString();
//            //added
//            String selectedTime = timeTextView.getText().toString();
//            //added
//            String selectedMood = this.selectedMood;
//            String selectedSocialSituation = socialSituationSpinner.getSelectedItem().toString();
//            String optionalTrigger = optionalTriggerEditText.getText().toString();
//
//            bundle.putString("selectedDate", selectedDate);
//            //added
//            bundle.putString("selectedTime", selectedTime);
//            //added
//            bundle.putString("selectedMood", selectedMood);
//            bundle.putString("selectedSocialSituation", selectedSocialSituation);
//            bundle.putString("optionalTrigger", optionalTrigger);
//
//
//            NavController navController = Navigation.findNavController(v);
//            navController.navigate(R.id.navigation_add_mood, bundle);
//        });
        //WORKING

        //NEW TRY
        postButton.setOnClickListener(v -> {
            // Show the privacy selection dialog first
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Privacy Option");

            // Create layout with checkboxes
            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            CheckBox privateCheckBox = new CheckBox(requireContext());
            privateCheckBox.setText("Private");

            CheckBox publicCheckBox = new CheckBox(requireContext());
            publicCheckBox.setText("Public");

            layout.addView(privateCheckBox);
            layout.addView(publicCheckBox);

            builder.setView(layout);

            // Ensure only one option is selected
            privateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) publicCheckBox.setChecked(false);
            });

            publicCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) privateCheckBox.setChecked(false);
            });

            // Set the "Post" button action in the dialog
            builder.setPositiveButton("Post", null); // Override below to prevent auto-dismiss

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();

            // Override the button behavior so the dialog doesn't close on an invalid selection
            dialog.setOnShowListener(d -> {
                Button postButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                postButton.setOnClickListener(buttonView -> {
                    // Ensure one option is selected
                    if (!privateCheckBox.isChecked() && !publicCheckBox.isChecked()) {
                        Toast.makeText(requireContext(), "Please select Private or Public", Toast.LENGTH_SHORT).show();
                        return; // Prevent dialog from closing
                    }

                    String privacySetting = privateCheckBox.isChecked() ? "Private" : "Public";

                    // Initialize moodPost AFTER selecting privacy
                    moodPost = new Mood();
                    moodPost.setDate(dateTextView.getText().toString());
                    moodPost.setTime(timeTextView.getText().toString());
                    moodPost.setEmotion(selectedMood);
                    moodPost.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
                    moodPost.setTrigger(optionalTriggerEditText.getText().toString());
                    moodPost.setWhyFeel(whyFeelEditText.getText().toString());
                    moodPost.setPrivacy(privacySetting); // Save the privacy setting

                    // Prepare data bundle to send to AddMoodEventFragment
                    Bundle result = new Bundle();
                    result.putString("selectedDate", moodPost.getDate());
                    result.putString("selectedTime", moodPost.getTime());
                    result.putString("selectedMood", moodPost.getEmotion());
                    result.putString("selectedSocialSituation", moodPost.getSocialSituation());
                    result.putString("optionalTrigger", moodPost.getTrigger());
                    result.putString("whyFeel", moodPost.getWhyFeel());
                    result.putString("privacySetting", privacySetting);

                    firebaseDB = new FirebaseDB();
                    //YOU HAVE TO BE LOGGED IN FOR THE POST BUTTON TO WORK
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if (currentUser != null) {
                        db.collection("users").document(currentUser.getUid())
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        user = documentSnapshot.toObject(User.class);
                                        moodPost.setPostDate(Timestamp.now());
                                        firebaseDB.addMoodtoDB(moodPost, currentUser.getUid());

                                        // Send data to AddMoodEventFragment **only after Firebase upload**
                                        getParentFragmentManager().setFragmentResult("moodEvent", result);

                                        // Navigate back **only after Firebase upload succeeds**
                                        NavController navController = Navigation.findNavController(v);
                                        navController.popBackStack();

                                        // Dismiss dialog only after everything is done
                                        dialog.dismiss();
                                    }
                                });
                    } else {
                        Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            // Show the dialog
            dialog.show();
        });
    }

    /** Sets the text for a selected mood.
     *
     * @param mood
     */
    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;
        if (addMoodButton != null) {
            addMoodButton.setText(mood);
        }
    }
}
