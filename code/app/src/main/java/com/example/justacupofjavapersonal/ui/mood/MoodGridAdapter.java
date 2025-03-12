package com.example.justacupofjavapersonal.ui.mood;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.BaseAdapter;
import com.example.justacupofjavapersonal.R;
import java.util.List;

public class MoodGridAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> moods;

    public MoodGridAdapter(Context context, List<String> moods) {
        this.context = context;
        this.moods = moods;
    }

    @Override
    public int getCount() {
        return moods.size();
    }

    @Override
    public Object getItem(int position) {
        return moods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_mood, parent, false);
        }

        TextView moodText = convertView.findViewById(R.id.moodText);
        moodText.setText(moods.get(position));
        moodText.setGravity(Gravity.CENTER);

        return convertView;
    }
}
