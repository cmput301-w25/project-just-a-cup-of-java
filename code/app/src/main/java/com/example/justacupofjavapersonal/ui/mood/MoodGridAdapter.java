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

/**
 * Adapter class for the GridView in the AddMoodEventFragment.
 * This class is responsible for creating the view holders for the GridView and binding the data to them.
 */
public class MoodGridAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> moods;

    /**
     * Constructor for the MoodGridAdapter.
     *
     * @param context the context of the adapter
     * @param moods the list of moods to display
     */
    public MoodGridAdapter(Context context, List<String> moods) {
        this.context = context;
        this.moods = moods;
    }

    /**
     * Returns the number of items in the adapter.
     *
     * @return the number of items in the adapter
     */
    @Override
    public int getCount() {
        return moods.size();
    }

    /**
     * Returns the item at the specified position.
     *
     * @param position the position of the item
     * @return the item at the specified position
     */
    @Override
    public Object getItem(int position) {
        return moods.get(position);
    }

    /**
     * Returns the ID of the item at the specified position.
     *
     * @param position the position of the item
     * @return the ID of the item at the specified position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Creates a new View object for the item at the specified position.
     *
     * @param position the position of the item
     * @param convertView the old view to reuse, if possible
     * @param parent the parent that this view will eventually be attached to
     * @return a new View object for the item at the specified position
     */
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
