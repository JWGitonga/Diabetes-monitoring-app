package com.example.diabeteshealthmonitoringapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.Reading;

import java.util.List;

public class PatientReadingsAdapter extends RecyclerView.Adapter<PatientReadingsAdapter.PatientReadingViewHolder> {
    private List<Reading> readings;
    private Context context;
    private OnItemClick listener;

    public PatientReadingsAdapter(List<Reading> readings, Context context) {
        this.readings = readings;
        this.context = context;
    }

    public interface OnItemClick {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientReadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PatientReadingViewHolder(
                LayoutInflater.from(context)
                        .inflate(R.layout.patient_reading_item,
                                parent,
                                false),
                listener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull PatientReadingViewHolder holder, int position) {
        holder.bind(readings.get(position));
    }

    @Override
    public int getItemCount() {
        return readings.size();
    }

    public static class PatientReadingViewHolder extends RecyclerView.ViewHolder {
        private final TextView reading;
        private final TextView date;
        private final TextView description;

        public PatientReadingViewHolder(View view, OnItemClick listener) {
            super(view);
            reading = view.findViewById(R.id.patient_reading_title);
            date = view.findViewById(R.id.patient_reading_date);
            description = view.findViewById(R.id.patient_reading_description);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }

        public void bind(Reading read) {
            reading.setText(read.getReading());
            date.setText(read.getDate());
            description.setText(read.getSuggestion());
        }
    }
}
