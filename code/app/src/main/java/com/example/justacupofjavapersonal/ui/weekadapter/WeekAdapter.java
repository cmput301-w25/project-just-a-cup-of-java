
package com.example.justacupofjavapersonal.ui.weekadapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.audiofx.Visualizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;

import java.util.List;

/**
 * WeekAdapter is a RecyclerView.Adapter that displays a list of weekdays with their corresponding dates.
 * It allows for item selection and highlights the selected item.
 */
public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {

    private List<String> weekDays;
    private int selectedPosition = -1;

    private OnItemClickListener onItemClickListener; // Interface for item clicks

    public WeekAdapter(List<String> weekDays, int selectedPosition, OnItemClickListener onItemClickListener) {
        this.weekDays = weekDays;
        this.onItemClickListener = onItemClickListener;
        this.selectedPosition = selectedPosition;

    }

    /**
     * Called when RecyclerView needs a new {@link WeekViewHolder} of the given type to represent an item.
     * This method will inflate the item layout and create the ViewHolder.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new WeekViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_each_day, parent, false);
        return new WeekViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents
     * of the {@link WeekViewHolder#itemView} to reflect the item at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Split the formatted date (e.g., "Mon 12") into weekday and date
        String[] parts = weekDays.get(position).split(" ");
        holder.weekDayText.setText(parts[0]); // "Mon"
        holder.dateText.setText(parts[1]);    // "12"
        // Highlight selected item
        if (selectedPosition == position) {
            holder.circularLayout.setBackgroundColor(Color.parseColor("#6525E0CF")); // Highlight color

        } else {
            holder.circularLayout.setBackgroundColor(Color.TRANSPARENT); // Default background
            holder.weekDayText.setTextColor(Color.BLACK);
            holder.dateText.setTextColor(Color.BLACK);
        }
        holder.circularLayout.setSelected(selectedPosition == position);  // Add this line


        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged(); // Refresh RecyclerView to update colors
            onItemClickListener.onItemClick(position, weekDays.get(position));
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return weekDays.size();
    }
    /**
     * Interface definition for a callback to be invoked when an item in the week view is clicked.
     */
    public interface OnItemClickListener {
        void onItemClick(int position, String date);
    }
    /**
     * ViewHolder class for the WeekAdapter RecyclerView.
     * This class holds the views for each item in the RecyclerView.
     */
    static class WeekViewHolder extends RecyclerView.ViewHolder {
        TextView weekDayText, dateText;
        LinearLayout circularLayout;

        public WeekViewHolder(@NonNull View itemView) {
            super(itemView);
            weekDayText = itemView.findViewById(R.id.weekDayText);
            dateText = itemView.findViewById(R.id.dateText);
            circularLayout=itemView.findViewById(R.id.circularLayout);
        }
    }
}