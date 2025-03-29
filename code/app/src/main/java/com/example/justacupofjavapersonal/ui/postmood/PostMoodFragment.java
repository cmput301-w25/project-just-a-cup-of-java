package com.example.justacupofjavapersonal.ui.postmood;

import android.app.AlertDialog;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PostMoodFragment extends Fragment implements MoodSelectorDialogFragment.MoodSelectionListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText optionalTriggerEditText;
    private Spinner socialSituationSpinner;
    private EditText whyFeelEditText;
    private CardView postButton;
    private ImageView addPhotoImageView;
    private ImageView photoPreview; // Added for photo preview
    private String selectedMood = "Add Emotional State";
    private Uri selectedImageUri;
    private String photoBase64; // To store the Base64-encoded photo
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
        photoPreview = view.findViewById(R.id.photoPreview); // Initialize photo preview
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

        // Add photo button click listener
        addPhotoImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        if (args != null && args.containsKey("editDate")) {
            isEditMode = true;

            // Rebuild moodToEdit manually (optional – just reuse fields directly if you want)
            moodToEdit = new Mood();
            moodToEdit.setDate(args.getString("editDate"));
            moodToEdit.setTime(args.getString("editTime"));
            moodToEdit.setEmotion(args.getString("editMood"));
            moodToEdit.setSocialSituation(args.getString("editSocialSituation"));
            moodToEdit.setTrigger(args.getString("editTrigger"));
            moodToEdit.setPrivacy(args.getString("editPrivacy"));
            moodToEdit.setPhoto(args.getString("editPhoto"));
            moodToEdit.setMoodID(args.getString("editMoodID")); // Optional if you need it

            moodToEdit.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

            // Use values to populate UI
            selectedMood = moodToEdit.getEmotion();
            updateMoodButtonUI(selectedMood);

            dateTextView.setText(moodToEdit.getDate());
            timeTextView.setText(moodToEdit.getTime());
            optionalTriggerEditText.setText(moodToEdit.getTrigger());

            setSpinnerToValue(socialSituationSpinner, moodToEdit.getSocialSituation());

            TextView postText = postButton.findViewById(R.id.cardTextView);
            if (postText != null) postText.setText("Edit");

            if (moodToEdit.getPhoto() != null) {
                try {
                    byte[] decodedString = Base64.decode(moodToEdit.getPhoto(), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    photoPreview.setImageBitmap(decodedByte);
                    photoPreview.setVisibility(View.VISIBLE);
                    photoBase64 = moodToEdit.getPhoto(); // Keep current photo
                } catch (Exception e) {
                    Log.e("PostMoodFragment", "Error decoding photo: " + e.getMessage());
                }
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
                        moodToEdit.setPhoto(photoBase64); // Update the photo

                        firebaseDB.updateMoodInDB(moodToEdit);
                        Toast.makeText(requireContext(), "Mood updated!", Toast.LENGTH_SHORT).show();
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
                        moodPost.setPhoto(photoBase64); // Set the photo

                        Bundle result = new Bundle();
                        result.putString("selectedDate", moodPost.getDate());
                        result.putString("selectedTime", moodPost.getTime());
                        result.putString("selectedMood", moodPost.getEmotion());
                        result.putString("selectedSocialSituation", moodPost.getSocialSituation());
                        result.putString("optionalTrigger", moodPost.getTrigger());
                        result.putString("privacySetting", privacySetting);
                        result.putString("photoBase64", photoBase64); // Pass the photo

                        if (currentUser != null) {
                            db.collection("users").document(currentUser.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            user = documentSnapshot.toObject(User.class);
                                            firebaseDB.addMoodtoDB(moodPost, currentUser.getUid());
                                            getParentFragmentManager().setFragmentResult("moodEvent", result);
                                            NavController navController = Navigation.findNavController(view);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                photoPreview.setImageBitmap(bitmap);
                photoPreview.setVisibility(View.VISIBLE);

                // Convert the bitmap to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                photoBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } catch (IOException e) {
                Log.e("PostMoodFragment", "Error loading image: " + e.getMessage());
                Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;
        updateMoodButtonUI(selectedMood);
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
            } else {
                Log.e("PostMoodFragment", "TextView inside CardView not found!");
            }
        } else {
            Log.e("PostMoodFragment", "AddEmoStateButton is null!");
        }
    }
}