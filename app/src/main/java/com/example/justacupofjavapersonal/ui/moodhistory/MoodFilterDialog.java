package com.example.justacupofjavapersonal.ui.moodhistory;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.justacupofjavapersonal.R;

public class MoodFilterDialog extends DialogFragment {

    private FilterListener filterListener;
    private Spinner emotionSpinner;
    public interface FilterListener {
        void onFilterApplied(boolean recentWeek, String emotion, String reasonKeyword);
    }

    public MoodFilterDialog(FilterListener listener) {
        this.filterListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.mood_history_filter, null); // Initialize Spinner
        emotionSpinner = view.findViewById(R.id.emotionSpinner);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                requireContext().getResources().getStringArray(R.array.emotion_options)
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Disable "Select an emotion"
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    view.setEnabled(false); // Make first item non-selectable
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emotionSpinner.setAdapter(adapter);
        emotionSpinner.setSelection(0, false); // Ensure "Select an emotion" is shown by default

        CheckBox recentWeekCheckBox = view.findViewById(R.id.recentWeekCheckBox);

        EditText reasonEditText = view.findViewById(R.id.reasonKeywordEditText);
        Button applyButton = view.findViewById(R.id.applyFilterButton);

        applyButton.setOnClickListener(v -> {
            boolean recentWeek = recentWeekCheckBox.isChecked();
            String emotion = emotionSpinner.getSelectedItem().toString();
            // Ignore the default placeholder if it's selected
            if (emotion.equals("Select an emotion")) {
                emotion = null; // No filter applied
            }
            String reasonKeyword = reasonEditText.getText().toString().trim();

            filterListener.onFilterApplied(recentWeek, emotion.equals("Any") ? null : emotion, reasonKeyword);
            dismiss();
        });

        builder.setView(view).setTitle("Filter Mood History");
        return builder.create();
    }




    }

