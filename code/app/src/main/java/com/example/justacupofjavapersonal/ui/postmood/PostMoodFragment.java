package com.example.justacupofjavapersonal.ui.postmood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import com.example.justacupofjavapersonal.ui.mood.MoodSelectorDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;

/**
 * PostMoodFragment is a fragment that allows the user to post a mood event.
 */
public class PostMoodFragment extends Fragment implements MoodSelectorDialogFragment.MoodSelectionListener {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText whyFeelEditText;
    private Spinner socialSituationSpinner;
    private CardView postButton; // Changed from Button to CardView
    private ImageView addPhotoImageView;
    private Uri selectedImageUri;
    private TextView dateTextView;
    private TextView timeTextView;
    private CardView addMoodButton; // Changed to CardView to match XML
    private TextView cardTextView; // Added to reference the TextView inside addEmoStateButton
    private String selectedMood = "Add Emotional State";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseDB firebaseDB;
    private Mood moodPost;
    private int editPosition = -1;
    private boolean isEditing = false;

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

        whyFeelEditText = view.findViewById(R.id.whyFeel);
        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
        addPhotoImageView = view.findViewById(R.id.addPhoto);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        cardTextView = view.findViewById(R.id.cardTextView); // Initialize the TextView inside addEmoStateButton
        postButton = view.findViewById(R.id.postmoodbutton);

        // Initialize moodPost
        moodPost = new Mood();

        // Set up addPhotoImageView click listener
        addPhotoImageView.setOnClickListener(v -> {
            Log.d("PostMoodFragment", "Add Photo button clicked - Verifying click event");
            Toast.makeText(getContext(), "Photo button clicked", Toast.LENGTH_SHORT).show();
            openImagePicker();
        });

        Bundle args = getArguments();
        if (args != null) {
            String selectedDate = args.getString("selectedDate", "No date passed");
            String selectedTime = args.getString("selectedTime", "No time selected");
            dateTextView.setText(selectedDate);
            timeTextView.setText(selectedTime);
            if (args.containsKey("moodData")) {
                isEditing = true;
                editPosition = args.getInt("editPosition", -1);
                String moodData = args.getString("moodData");
                populateExistingMood(moodData);
            }
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

        // Ensure addMoodButton is not null
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
            FirebaseUser currentUser = mAuth.getCurrentUser();

            // Do not reinitialize moodPost here; use the existing instance
            // moodPost = new Mood(); // Removed this line

            Bundle result = new Bundle();
            result.putString("selectedDate", dateTextView.getText().toString());
            result.putString("selectedTime", timeTextView.getText().toString());
            result.putString("selectedMood", selectedMood);
            result.putString("selectedSocialSituation", socialSituationSpinner.getSelectedItem().toString());
            result.putString("whyFeel", whyFeelEditText.getText().toString());
            result.putString("privacySetting", "Private"); // Default privacy setting
            if (isEditing) {
                result.putInt("editPosition", editPosition);
            }
            // Pass the photo data
            result.putString("photoBase64", moodPost.getPhoto());
            if (selectedImageUri != null) {
                result.putString("photoUri", selectedImageUri.toString());
            }

            // Debug the photo field
            Log.d("PostMoodFragment", "Photo after posting: " + (moodPost.getPhoto() != null ? "Present" : "Null"));
            Toast.makeText(getContext(), "Mood posted, check the image", Toast.LENGTH_SHORT).show();

            // Save to Firestore
            firebaseDB = new FirebaseDB();
            assert currentUser != null;
            moodPost.setUid(currentUser.getUid());
            firebaseDB.addMoodtoDB(moodPost, currentUser.getUid());

            getParentFragmentManager().setFragmentResult("moodEvent", result);
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
    }

    private void populateExistingMood(String moodData) {
        String[] dataParts = moodData.split("\n");
        if (dataParts.length >= 2) {
            selectedMood = dataParts[0].replace("Mood: ", "");
            if (cardTextView != null) {
                cardTextView.setText(selectedMood);
            }
        }
    }

    // New method to open the image picker
    private void openImagePicker() {
        Log.d("PostMoodFragment", "Launching image picker intent");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        try {
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
                Log.d("PostMoodFragment", "Image picker intent launched successfully");
            } else {
                Log.e("PostMoodFragment", "No app available to handle image picker intent");
                Toast.makeText(getContext(), "No gallery app available", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("PostMoodFragment", "Error launching image picker: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to open image picker", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of the image picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("PostMoodFragment", "onActivityResult called, requestCode: " + requestCode + ", resultCode: " + resultCode);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                // Convert the image to Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                // Compress and convert to base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // 50% quality to reduce size
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                if (moodPost == null) {
                    moodPost = new Mood(); // Initialize if null to avoid NullPointerException
                }
                moodPost.setPhoto(base64Image); // Set the base64 string in the Mood object
                addPhotoImageView.setImageURI(selectedImageUri); // Preview the image
                Log.d("PostMoodFragment", "Image successfully processed and set");
            } catch (Exception e) {
                Log.e("PostMoodFragment", "Error converting image to base64: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("PostMoodFragment", "Image picker failed or canceled");
        }
    }

    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;
        if (cardTextView != null) {
            cardTextView.setText(mood);
        }
    }
}