package com.example.justacupofjavapersonal.ui.moodhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import com.example.justacupofjavapersonal.R;
import com.example.justacupofjavapersonal.class_resources.mood.Mood;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MoodAdapter extends ArrayAdapter<Mood> {
    private Context context;
    private List<Mood> moodList;

    public MoodAdapter(Context context, List<Mood> moodList) {
        super(context, 0, moodList);
        this.context = context;
        this.moodList = moodList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.mood_list_item, parent, false);
        }

        Mood currentMood = moodList.get(position);

        // Find the TextViews in mood_list_item.xml
        TextView moodTextView = convertView.findViewById(R.id.moodTextView);
        TextView socialSituationTextView = convertView.findViewById(R.id.socialSituationTextView);
        TextView triggerTextView = convertView.findViewById(R.id.triggerTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        // Set text for each field
        moodTextView.setText("Mood: " + currentMood.getState());
        socialSituationTextView.setText("Social Situation: " +
                (currentMood.getSocialSituation() != null ? currentMood.getSocialSituation().toString() : "None"));
        triggerTextView.setText("Trigger: " + (currentMood.getTrigger() != null ? currentMood.getTrigger() : "None"));

        // Format and display date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        dateTextView.setText("Date: " + sdf.format(currentMood.getPostDate()));

        return convertView;
    }
}
