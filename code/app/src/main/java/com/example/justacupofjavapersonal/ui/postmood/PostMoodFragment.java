package com.example.justacupofjavapersonal.ui.postmood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.app.AlertDialog;
import android.provider.MediaStore;
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
import com.example.justacupofjavapersonal.class_resources.mood.LatLngLocation;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.ui.mood.MoodSelectorDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The PostMoodFragment class is responsible for handling the UI and logic for posting or editing a mood entry.
 * It allows users to select an emotional state, add optional details such as a trigger, social situation, 
 * location, and photo, and choose a privacy setting (Private or Public) before posting or editing the mood entry.
 * 
 * This fragment interacts with Firebase for storing and updating mood data and supports both "Post" and "Edit" modes.
 * 
 */
public class PostMoodFragment extends Fragment implements MoodSelectorDialogFragment.MoodSelectionListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText optionalTriggerEditText;
    private Spinner socialSituationSpinner;
    private EditText whyFeelEditText;
    private CardView postButton;
    private ImageView addPhotoImageView;
    private String photoBase64 = null;

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

    private LatLng selectedLatLng = null;


    /**
     * Called to have the fragment instantiate its user interface view.
     * This method initializes Firebase Authentication and Firestore instances
     * and inflates the layout for the fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     *                 any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     *                  UI should be attached to. The fragment should not add the view itself,
     *                  but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        return inflater.inflate(R.layout.fragment_post_mood, container, false);
    }


    /**
     * Called when the fragment's view has been created. This method initializes UI components,
     * sets up event listeners, and handles both "Post Mood" and "Edit Mood" modes.
     *
     * Key functionalities:
     * - Listens for location selection results and updates the UI accordingly.
     * - Initializes UI elements such as spinners, text views, and buttons.
     * - Handles the "Add Mood" button click to open a mood selection dialog.
     * - Handles the "Add Photo" button click to allow the user to pick an image.
     * - Navigates to a location picker fragment when the "Add Location" button is clicked.
     * - Configures the spinner for selecting social situations with a disabled default option.
     * - Handles "Edit Mode" by pre-filling the UI with existing mood data.
     * - Sets up the "Post" or "Edit" button to validate inputs, handle privacy settings,
     *   and save the mood to the database.
     *
     * @param view The fragment's root view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getParentFragmentManager().setFragmentResultListener("locationSelected", getViewLifecycleOwner(), (requestKey, result) -> {
            double lat = result.getDouble("latitude");
            double lng = result.getDouble("longitude");
            selectedLatLng = new LatLng(lat, lng);
            Toast.makeText(getContext(), "Location selected: " + lat + ", " + lng, Toast.LENGTH_SHORT).show();

            CardView addLocationButton = view.findViewById(R.id.addLocationButton);
            TextView buttonText = addLocationButton.findViewById(R.id.locationCardText);
            buttonText.setText("Location Added ✅");


        });


        optionalTriggerEditText = view.findViewById(R.id.triggerText);
        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
        whyFeelEditText = view.findViewById(R.id.whyFeel);
        addPhotoImageView = view.findViewById(R.id.addPhoto);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        postButton = view.findViewById(R.id.postmoodbutton);

        MaterialToolbar toolbar = requireActivity().findViewById(R.id.topAppBar);

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

        addPhotoImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });


        CardView addLocationButton = view.findViewById(R.id.addLocationButton);
        addLocationButton.setOnClickListener(v -> {
            // You can choose either GPS or open a map fragment here
            Navigation.findNavController(v).navigate(R.id.action_postMood_to_locationPickerFragment);
        });


        // --- Handle Edit Mode ---
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

                if (moodToEdit.getLocation() != null) {
                    selectedLatLng = new LatLng(
                            moodToEdit.getLocation().getLatitude(),
                            moodToEdit.getLocation().getLongitude()
                    );
                    TextView buttonText = view.findViewById(R.id.locationCardText);
                    buttonText.setText("Location Added ✅");
                }

                toolbar.setTitle("Edit Mood");
                TextView postText = postButton.findViewById(R.id.cardTextView);
                if (postText != null) postText.setText("Edit");
            }
        } else {
            toolbar.setTitle("Post Mood");
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
                        moodToEdit.setPhoto(photoBase64);//added
                        firebaseDB.updateMoodInDB(moodToEdit);
                        Toast.makeText(requireContext(), "Mood updated!", Toast.LENGTH_SHORT).show();

                        // Navigate back to AddMoodFragment
                        Navigation.findNavController(view).popBackStack();
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

                        if (selectedLatLng != null) {
                            LatLngLocation location = new LatLngLocation(selectedLatLng.latitude, selectedLatLng.longitude);
                            moodPost.setLocation(location);

                        }




                        //add for photo
                        if (photoBase64 != null) {
                            moodPost.setPhoto(photoBase64);
                        }




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
                                            Navigation.findNavController(v).popBackStack();
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

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        optionalTriggerEditText = view.findViewById(R.id.triggerText);
//        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
//        whyFeelEditText = view.findViewById(R.id.whyFeel);
//        addPhotoImageView = view.findViewById(R.id.addPhoto);
//        dateTextView = view.findViewById(R.id.dateTextView);
//        timeTextView = view.findViewById(R.id.timeTextView);
//        addMoodButton = view.findViewById(R.id.addEmoStateButton);
//        postButton = view.findViewById(R.id.postmoodbutton);
//
//
//        Bundle args = getArguments();
//        if (args != null) {
//            String selectedDate = args.getString("selectedDate", "No date passed");
//            String selectedTime = args.getString("selectedTime", "No time passed");
//
//            Log.d("PostMoodFragment", "Received Date: " + selectedDate);
//            Log.d("PostMoodFragment", "Received Time: " + selectedTime);
//
//            dateTextView.setText(selectedDate);
//            timeTextView.setText(selectedTime);
//        } else {
//            Log.e("PostMoodFragment", "No arguments received!");
//        }
//
//        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
//                requireContext(),
//                android.R.layout.simple_spinner_item,
//                getResources().getStringArray(R.array.social_situation_options)
//        ) {
//            @Override
//            public boolean isEnabled(int position) {
//                return position != 0;
//            }
//
//            @Override
//            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView textView = (TextView) view;
//                if (position == 0) {
//                    textView.setTextColor(getResources().getColor(android.R.color.darker_gray));
//                } else {
//                    textView.setTextColor(getResources().getColor(android.R.color.black));
//                }
//                return view;
//            }
//        };
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        socialSituationSpinner.setAdapter(adapter);
//        socialSituationSpinner.setSelection(0);
//
//        addMoodButton.setOnClickListener(v -> {
//            MoodSelectorDialogFragment moodDialog = new MoodSelectorDialogFragment();
//            moodDialog.setMoodSelectionListener(PostMoodFragment.this);
//            moodDialog.show(getParentFragmentManager(), "MoodSelector");
//        });
//
//        if (args != null && args.containsKey("moodToEdit")) {
//            isEditMode = true;
//            moodToEdit = (Mood) args.getSerializable("moodToEdit");
//
//            if (moodToEdit != null) {
//                selectedMood = moodToEdit.getEmotion();
//                updateMoodButtonUI(selectedMood);
//
//                dateTextView.setText(moodToEdit.getDate());
//                timeTextView.setText(moodToEdit.getTime());
//                optionalTriggerEditText.setText(moodToEdit.getTrigger());
//
//                setSpinnerToValue(socialSituationSpinner, moodToEdit.getSocialSituation());
//
//                TextView postText = postButton.findViewById(R.id.cardTextView);
//                if (postText != null) postText.setText("Edit");
//            }
//        }
//
//        postButton.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//            builder.setTitle("Select Privacy Option");
//
//            LinearLayout layout = new LinearLayout(requireContext());
//            layout.setOrientation(LinearLayout.VERTICAL);
//
//            CheckBox privateCheckBox = new CheckBox(requireContext());
//            privateCheckBox.setText("Private");
//
//            CheckBox publicCheckBox = new CheckBox(requireContext());
//            publicCheckBox.setText("Public");
//
//            layout.addView(privateCheckBox);
//            layout.addView(publicCheckBox);
//            builder.setView(layout);
//
//            privateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) publicCheckBox.setChecked(false);
//            });
//
//            publicCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) privateCheckBox.setChecked(false);
//            });
//
//            builder.setPositiveButton(isEditMode ? "Edit" : "Post", null);
//            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
//
//            AlertDialog dialog = builder.create();
//
//            dialog.setOnShowListener(d -> {
//                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                positiveButton.setOnClickListener(buttonView -> {
//                    if (!privateCheckBox.isChecked() && !publicCheckBox.isChecked()) {
//                        Toast.makeText(requireContext(), "Please select Private or Public", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    String privacySetting = privateCheckBox.isChecked() ? "Private" : "Public";
//                    firebaseDB = new FirebaseDB();
//                    FirebaseUser currentUser = mAuth.getCurrentUser();
//
//                    if (isEditMode && moodToEdit != null) {
//                        moodToEdit.setEmotion(selectedMood);
//                        moodToEdit.setTrigger(optionalTriggerEditText.getText().toString());
//                        moodToEdit.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
//                        moodToEdit.setPrivacy(privacySetting);
//
//                        MaterialToolbar toolbar = requireActivity().findViewById(R.id.topAppBar);
//                        toolbar.setTitle("Edit Mood");
//
//                        // Set button text
//                        TextView buttonText = postButton.findViewById(R.id.cardTextView);
//                        if (buttonText != null) {
//                            buttonText.setText("Edit");
//                        }
//
//
//                        firebaseDB.updateMoodInDB(moodToEdit);
//                        Toast.makeText(requireContext(), "Mood updated!", Toast.LENGTH_SHORT).show();
//
//                        // Navigate back to AddMoodFragment
//                        NavController navController = Navigation.findNavController(view);
//                        navController.popBackStack();
//                        dialog.dismiss();
//                    } else {
//                        moodPost = new Mood();
//                        moodPost.isDeserializing = false;
//                        moodPost.setDate(dateTextView.getText().toString());
//                        moodPost.setTime(timeTextView.getText().toString());
//                        moodPost.setEmotion(selectedMood);
//                        moodPost.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
//                        moodPost.setTrigger(optionalTriggerEditText.getText().toString());
//                        moodPost.setPrivacy(privacySetting);
//
//                        Bundle result = new Bundle();
//                        result.putString("selectedDate", moodPost.getDate());
//                        result.putString("selectedTime", moodPost.getTime());
//                        result.putString("selectedMood", moodPost.getEmotion());
//                        result.putString("selectedSocialSituation", moodPost.getSocialSituation());
//                        result.putString("optionalTrigger", moodPost.getTrigger());
//                        result.putString("privacySetting", privacySetting);
//
//                        if (currentUser != null) {
//                            db.collection("users").document(currentUser.getUid())
//                                    .get()
//                                    .addOnSuccessListener(documentSnapshot -> {
//                                        if (documentSnapshot.exists()) {
//                                            user = documentSnapshot.toObject(User.class);
//                                            firebaseDB.addMoodtoDB(moodPost, currentUser.getUid());
//                                            getParentFragmentManager().setFragmentResult("moodEvent", result);
//                                            NavController navController = Navigation.findNavController(v);
//                                            navController.popBackStack();
//                                            dialog.dismiss();
//                                        }
//                                    });
//                        } else {
//                            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            });
//
//            dialog.show();
//        });
//    }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                addPhotoImageView.setImageBitmap(bitmap);  // You can use a separate preview ImageView if needed
                photoBase64 = encodeImageToBase64(bitmap);  // Save encoded string
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
    }











}
