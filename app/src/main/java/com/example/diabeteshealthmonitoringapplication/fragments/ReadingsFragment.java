package com.example.diabeteshealthmonitoringapplication.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.activities.PatientsReadingsActivity;
import com.example.diabeteshealthmonitoringapplication.models.Reading;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class ReadingsFragment extends Fragment {
    private EditText reading, time, date, suggestion;
    private String strReading, strDate, strSuggestion, strTime;

    public ReadingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_readings, container, false);
        reading = view.findViewById(R.id.reading_et);
        date = view.findViewById(R.id.date_et);
        time = view.findViewById(R.id.time_et);
        Button upload = view.findViewById(R.id.fab_upload);
        suggestion = view.findViewById(R.id.suggestion);
        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (v.getId() == R.id.date_et && hasFocus) {
                DatePickerDialog datePicker = new DatePickerDialog(requireContext());
                datePicker.setOnDateSetListener((view1, year, month, dayOfMonth) -> {
                    strDate = dayOfMonth + "/" + month + "/" + year;
                    date.setText(strDate);
                });
                datePicker.create();
                datePicker.show();
            }
        });
        time.setOnFocusChangeListener((v, hasFocus) -> {
            int hour = Calendar.HOUR_OF_DAY;
            int minute = Calendar.MINUTE;
            if (v.getId() == R.id.time_et && hasFocus) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute1) -> {
                    strTime = hourOfDay + " : " + minute1;
                    time.setText(strTime);
                }, hour, minute, true);
                timePickerDialog.create();
                timePickerDialog.show();
            }
        });

        upload.setOnClickListener(v -> {
            strReading = reading.getText().toString().trim();
            strSuggestion = suggestion.getText().toString().trim();
            if (strReading.isEmpty()) {
                reading.setError("Cannot be empty");
            } else {
                if (strDate.isEmpty()) {
                    reading.setError("Cannot be empty");
                } else {
                    if (strReading.length() > 3) {
                        reading.setError("Invalid reading");
                    } else {
                        String uid = FirebaseAuth.getInstance().getUid();
                        Reading r = new Reading(uid, strReading+"mg/dL", strDate, strTime, strSuggestion);
                        String[] fom = strDate.split("/");
                        FirebaseDatabase.getInstance().getReference("readings/" + uid + "/" + fom[0] + "-" + fom[1] + "-" + fom[2] + " -> " + strTime)
                                .setValue(r)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Successfully uploaded reading...", Toast.LENGTH_SHORT).show();
                                        reading.setText("");
                                        date.setText("");
                                        time.setText("");
                                        suggestion.setText("");
                                        startActivity(new Intent(requireContext(), PatientsReadingsActivity.class).putExtra("uid", FirebaseAuth.getInstance().getUid()));
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "An error occurred try again", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
        return view;
    }
}