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
    private EditText whyFeelEditText;
    private Button postButton;
    private TextView dateTextView;
    private TextView timeTextView;
    private Button addMoodButton;
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

        optionalTriggerEditText = view.findViewById(R.id.triggerText);
        socialSituationSpinner = view.findViewById(R.id.socialSituationSpinner);
        whyFeelEditText = view.findViewById(R.id.whyFeel);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        addMoodButton = view.findViewById(R.id.addEmoStateButton);
        postButton = view.findViewById(R.id.postmoodbutton);

        Bundle args = getArguments();
        if (args != null) {
            String selectedDate = args.getString("selectedDate", "No date passed");
            dateTextView.setText(selectedDate);
            if (args.containsKey("moodData")) {
                isEditing = true;
                editPosition = args.getInt("editPosition", -1);
                String moodData = args.getString("moodData");
                populateExistingMood(moodData);
            }
        }

        postButton.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            moodPost = new Mood();

            Bundle result = new Bundle();
            result.putString("selectedDate", dateTextView.getText().toString());
            result.putString("selectedMood", selectedMood);
            result.putString("selectedSocialSituation", socialSituationSpinner.getSelectedItem().toString());
            result.putString("optionalTrigger", optionalTriggerEditText.getText().toString());
            if (isEditing) {
                result.putInt("editPosition", editPosition);
            }

            firebaseDB = new FirebaseDB();
            assert currentUser != null;
            firebaseDB.addMoodtoDB(moodPost, currentUser.getUid());

            getParentFragmentManager().setFragmentResult("moodEvent", result);
            NavController navController = Navigation.findNavController(v);
            navController.popBackStack();
        });
    }

    private void populateExistingMood(String moodData) {
        String[] dataParts = moodData.split("\n");
        if (dataParts.length >= 3) {
            selectedMood = dataParts[0].replace("Mood: ", "");
            addMoodButton.setText(selectedMood);
            optionalTriggerEditText.setText(dataParts[2].replace("Trigger: ", ""));
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