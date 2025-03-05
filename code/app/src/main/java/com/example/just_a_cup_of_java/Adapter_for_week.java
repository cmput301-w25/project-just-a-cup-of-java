package com.example.just_a_cup_of_java;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter_for_week extends RecyclerView.Adapter<Adapter_for_week.WeekDisplayViewHolder>{
    //each item in the list is controlled by the view holder
    private List<days_week> daysWeeks;
    private OnItemClickListener listener; // when user clicks on a day

    public interface OnItemClickListener {
        void onItemClick(int position); // the item that was clicked
    }

    public Adapter_for_week(List<days_week> daysWeeks, OnItemClickListener listener) {
        this.daysWeeks = daysWeeks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WeekDisplayViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekdisplay, parent, false);
        return new WeekDisplayViewHolder(view,listener);
    }
    @Override
    public void onBindViewHolder(@NonNull WeekDisplayViewHolder holder, int position) {
        days_week day = daysWeeks.get(position);
        holder.week_dayText.setText(day.getWeek_day());
        holder.monthText.setText(day.getMonth());

        Log.d("Adapter_for_week", "Binding item at position " + position + ": " + day.getWeek_day() + ", " + day.getMonth());

    }

    @Override
    public int getItemCount() {
        int size = daysWeeks.size();
        Log.d("Adapter_for_week", "Item count: " + size);
        return size;

//        return daysWeeks.size();  // Return number of items
    }
    public void updateData(List<days_week> newDaysWeeks) {
        this.daysWeeks.clear();
        this.daysWeeks.addAll(newDaysWeeks);
        notifyDataSetChanged(); // Update RecyclerView
    }

    public static class WeekDisplayViewHolder extends RecyclerView.ViewHolder {
        public TextView week_dayText;
        public TextView monthText;

        public WeekDisplayViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            week_dayText = itemView.findViewById(R.id.weekday); // Connect to weekdisplay.xml
            monthText = itemView.findViewById(R.id.dateofweek);       // Connect to weekdisplay.xml

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}

