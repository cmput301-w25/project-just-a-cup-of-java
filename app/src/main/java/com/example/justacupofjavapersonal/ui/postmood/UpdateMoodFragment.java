//package com.example.justacupofjavapersonal.ui.postmood;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.navigation.NavController;
//import androidx.navigation.Navigation;
//import com.example.justacupofjavapersonal.R;
//import com.example.justacupofjavapersonal.ui.viewmodel.MoodViewModel;
//
//public class UpdateMoodFragment extends Fragment {
//    private EditText editMoodInput, editTriggerInput, editSocialSituationInput, editWhyFeelInput;
//    private MoodViewModel moodViewModel;
//    private String selectedMood;
//    private int moodPosition;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_update_mood, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        editMoodInput = view.findViewById(R.id.editEmoStateButton);
//        editTriggerInput = view.findViewById(R.id.editTriggerInput);
//        //editSocialSituationInput = view.findViewById(R.id.editSocialSituationInput);
//        //editWhyFeelInput = view.findViewById(R.id.editWhyFeelInput);
//        Button updateButton = view.findViewById(R.id.updateMoodButton);
//
//        // ✅ Initialize ViewModel
//        moodViewModel = new ViewModelProvider(requireActivity()).get(MoodViewModel.class);
//
//        // ✅ Receive mood data from arguments
//        if (getArguments() != null) {
//            selectedMood = getArguments().getString("selectedMood");
//            moodPosition = getArguments().getInt("moodPosition", -1);
//
//            if (selectedMood != null) {
//                editMoodInput.setText(selectedMood);
//            }
//        }
//
//        // ✅ Handle mood update
//        updateButton.setOnClickListener(v -> {
//            String updatedMood = editMoodInput.getText().toString();
//            String updatedTrigger = editTriggerInput.getText().toString();
//            String updatedSocialSituation = editSocialSituationInput.getText().toString();
//            String updatedWhyFeel = editWhyFeelInput.getText().toString();
//
//            if (!updatedMood.isEmpty() && moodPosition != -1) {
//                moodViewModel.updateMood(moodPosition, updatedMood);
//            }
//
//            // TODO: Save optional fields somewhere (Firebase or ViewModel)
//
//            NavController navController = Navigation.findNavController(v);
//            navController.navigateUp(); // Go back to AddMoodEventFragment
//        });
//    }
//}
