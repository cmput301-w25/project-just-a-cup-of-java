package com.example.justacupofjavapersonal.ui.postmood;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
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
    private Mood moodPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

        // Get passed-in date/time values
        Bundle args = getArguments();
        if (args != null) {
            dateTextView.setText(args.getString("selectedDate", "No date passed"));
            timeTextView.setText(args.getString("selectedTime", "No time passed"));
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.social_situation_options)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(adapter);

        addMoodButton.setOnClickListener(v -> {
            MoodSelectorDialogFragment moodDialog = new MoodSelectorDialogFragment(this);
            moodDialog.show(getParentFragmentManager(), "MoodSelector");
        });

        addPhotoImageView.setOnClickListener(v -> openImagePicker());

        postButton.setOnClickListener(v -> {
            moodPost = new Mood();
            moodPost.setDate(dateTextView.getText().toString());
            moodPost.setTime(timeTextView.getText().toString());
            moodPost.setEmotion(selectedMood);
            moodPost.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
            moodPost.setTrigger(optionalTriggerEditText.getText().toString());
            moodPost.setWhyFeel(whyFeelEditText.getText().toString());
            moodPost.setPrivacy("Private"); // Temporary default

            Bundle result = new Bundle();
            result.putString("selectedDate", moodPost.getDate());
            result.putString("selectedTime", moodPost.getTime());
            result.putString("selectedMood", moodPost.getEmotion());
            result.putString("selectedSocialSituation", moodPost.getSocialSituation());
            result.putString("optionalTrigger", moodPost.getTrigger());
            result.putString("whyFeel", moodPost.getWhyFeel());
            result.putString("privacySetting", moodPost.getPrivacy());

            // Image data
            if (moodPost.getPhoto() != null) {
                result.putString("photoBase64", moodPost.getPhoto());
            }
            if (selectedImageUri != null) {
                result.putString("photoUri", selectedImageUri.toString());
            }

            getParentFragmentManager().setFragmentResult("moodEvent", result);
            Navigation.findNavController(v).popBackStack();
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] imageBytes = baos.toByteArray();
                String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                moodPost = moodPost == null ? new Mood() : moodPost;
                moodPost.setPhoto(base64Image);
                addPhotoImageView.setImageURI(selectedImageUri);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;
        if (addMoodButton != null) {
            addMoodButton.setText(mood);
        }
    }
}
