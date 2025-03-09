package com.example.justacupofjavapersonal.ui.weekadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justacupofjavapersonal.R;

import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.WeekViewHolder> {

    private List<String> weekDays;

    public WeekAdapter(List<String> weekDays) {
        this.weekDays = weekDays;
    }

    @NonNull
    @Override
    public WeekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_each_day, parent, false);
        return new WeekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeekViewHolder holder, int position) {
        // Split the formatted date (e.g., "Mon 12") into weekday and date
        String[] parts = weekDays.get(position).split(" ");
        holder.weekDayText.setText(parts[0]); // "Mon"
        holder.dateText.setText(parts[1]);    // "12"
    }

    @Override
    public int getItemCount() {
        return weekDays.size();
    }

    static class WeekViewHolder extends RecyclerView.ViewHolder {
        TextView weekDayText, dateText;

        public WeekViewHolder(@NonNull View itemView) {
            super(itemView);
            weekDayText = itemView.findViewById(R.id.weekDayText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}