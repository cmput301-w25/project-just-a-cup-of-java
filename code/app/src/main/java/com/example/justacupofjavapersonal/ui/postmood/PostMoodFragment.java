package com.example.justacupofjavapersonal.ui.postmood;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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
    private CardView postButton;
    private ImageView addPhotoImageView;
    private ImageView photoPreview;
    private TextView dateTextView, timeTextView;
    private CardView addMoodButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseDB firebaseDB;

    private Mood moodToEdit;
    private boolean isEditMode = false;
    private String selectedMood = "Add Emotional State";
    private String photoBase64 = null;

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
        addPhotoImageView = view.findViewById(R.id.addPhoto);
        photoPreview = view.findViewById(R.id.photoPreview);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        postButton = view.findViewById(R.id.postmoodbutton);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.social_situation_options)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(adapter);

        addMoodButton.setOnClickListener(v -> {
            MoodSelectorDialogFragment dialog = new MoodSelectorDialogFragment();
            dialog.setMoodSelectionListener(this);
            dialog.show(getParentFragmentManager(), "MoodSelector");
        });

        addPhotoImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        Bundle args = getArguments();
        if (args != null) {
            String selectedDate = args.getString("selectedDate", "");
            String selectedTime = args.getString("selectedTime", "");
            dateTextView.setText(selectedDate);
            timeTextView.setText(selectedTime);

            if (args.containsKey("editDate")) {
                isEditMode = true;
                moodToEdit = new Mood();
                moodToEdit.setDate(args.getString("editDate"));
                moodToEdit.setTime(args.getString("editTime"));
                moodToEdit.setEmotion(args.getString("editMood"));
                moodToEdit.setSocialSituation(args.getString("editSocialSituation"));
                moodToEdit.setTrigger(args.getString("editTrigger"));
                moodToEdit.setPrivacy(args.getString("editPrivacy"));
                moodToEdit.setPhoto(args.getString("editPhoto"));
                moodToEdit.setMoodID(args.getString("editMoodID"));
                moodToEdit.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                selectedMood = moodToEdit.getEmotion();
                TextView moodText = addMoodButton.findViewById(R.id.cardTextView);
                if (moodText != null) {
                    moodText.setText(selectedMood);
                }
                optionalTriggerEditText.setText(moodToEdit.getTrigger());
                setSpinnerToValue(socialSituationSpinner, moodToEdit.getSocialSituation());

                if (moodToEdit.getPhoto() != null) {
                    try {
                        byte[] decoded = Base64.decode(moodToEdit.getPhoto(), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                        photoPreview.setImageBitmap(bitmap);
                        photoPreview.setVisibility(View.VISIBLE);
                        photoBase64 = moodToEdit.getPhoto();
                    } catch (Exception e) {
                        Log.e("PostMoodFragment", "Error decoding photo: " + e.getMessage());
                    }
                }

                TextView postText = postButton.findViewById(R.id.cardTextView);
                if (postText != null) postText.setText("Edit");
            }
        }

        postButton.setOnClickListener(v -> showPrivacyDialog());
    }

    private void showPrivacyDialog() {
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

        privateCheckBox.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) publicCheckBox.setChecked(false);
        });

        publicCheckBox.setOnCheckedChangeListener((button, isChecked) -> {
            if (isChecked) privateCheckBox.setChecked(false);
        });

        builder.setView(layout);
        builder.setPositiveButton(isEditMode ? "Edit" : "Post", null);
        builder.setNegativeButton("Cancel", (d, which) -> d.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            confirmButton.setOnClickListener(btn -> {
                if (!privateCheckBox.isChecked() && !publicCheckBox.isChecked()) {
                    Toast.makeText(requireContext(), "Please select Private or Public", Toast.LENGTH_SHORT).show();
                    return;
                }

                String privacy = privateCheckBox.isChecked() ? "Private" : "Public";
                firebaseDB = new FirebaseDB();
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (isEditMode && moodToEdit != null) {
                    moodToEdit.setEmotion(selectedMood);
                    moodToEdit.setTrigger(optionalTriggerEditText.getText().toString());
                    moodToEdit.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
                    moodToEdit.setPrivacy(privacy);
                    moodToEdit.setPhoto(photoBase64);

                    firebaseDB.updateMoodInDB(moodToEdit);
                    Toast.makeText(requireContext(), "Mood updated!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Mood mood = new Mood();
                    mood.setDate(dateTextView.getText().toString());
                    mood.setTime(timeTextView.getText().toString());
                    mood.setEmotion(selectedMood);
                    mood.setSocialSituation(socialSituationSpinner.getSelectedItem().toString());
                    mood.setTrigger(optionalTriggerEditText.getText().toString());
                    mood.setPrivacy(privacy);
                    mood.setPhoto(photoBase64);
                    mood.setUid(currentUser.getUid());

                    firebaseDB.addMoodtoDB(mood, currentUser.getUid());

                    Bundle result = new Bundle();
                    result.putString("selectedDate", mood.getDate());
                    result.putString("selectedTime", mood.getTime());
                    result.putString("selectedMood", mood.getEmotion());
                    result.putString("selectedSocialSituation", mood.getSocialSituation());
                    result.putString("optionalTrigger", mood.getTrigger());
                    result.putString("privacySetting", mood.getPrivacy());
                    result.putString("photoBase64", mood.getPhoto());

                    getParentFragmentManager().setFragmentResult("moodEvent", result);

                    NavController navController = Navigation.findNavController(requireView());
                    navController.popBackStack();
                    dialog.dismiss();
                }
            });
        });

        dialog.show();
    }

    private void setSpinnerToValue(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onMoodSelected(String mood) {
        selectedMood = mood;
        if (addMoodButton != null) {
            TextView moodText = addMoodButton.findViewById(R.id.cardTextView);
            if (moodText != null) {
                moodText.setText(mood);
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
                photoPreview.setImageBitmap(bitmap);
                photoPreview.setVisibility(View.VISIBLE);
                photoBase64 = encodeImageToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}
