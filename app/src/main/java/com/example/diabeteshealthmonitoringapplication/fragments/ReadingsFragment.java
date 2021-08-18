package com.example.diabeteshealthmonitoringapplication.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.Registration;
import com.example.diabeteshealthmonitoringapplication.models.Reading;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class ReadingsFragment extends Fragment {
    private TextView reading, date, suggestion;
    private String strReading, strDate, strSuggestion;

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

    public ReadingsFragment() {
        // Required empty public constructor
    }


//    public static ReadingsFragment newInstance(String param1, String param2) {
//        ReadingsFragment fragment = new ReadingsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_readings, container, false);
        reading = view.findViewById(R.id.reading_et);
        date = view.findViewById(R.id.date_et);
        FloatingActionButton upload = view.findViewById(R.id.fab_upload);
        suggestion = view.findViewById(R.id.suggestion);

        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (v.getId() == R.id.date_et && hasFocus) {
                DatePickerDialog datePicker = new DatePickerDialog(requireContext());
                datePicker.updateDate(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
                datePicker.setOnDateSetListener((view1, year, month, dayOfMonth) -> {
                    strDate = dayOfMonth + "/" + month + "/" + year;
                    date.setText(strDate);
                });
                datePicker.create();
                datePicker.show();
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
                    String uid = FirebaseAuth.getInstance().getUid();
                    Reading r = new Reading(uid, strReading, strDate, strSuggestion);
                    FirebaseDatabase.getInstance().getReference("readings/" + uid)
                            .setValue(r)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Successfully uploaded reading...", Toast.LENGTH_SHORT).show();
                                    reading.setText("");
                                    date.setText("");
                                    suggestion.setText("");
                                }
                            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "An error occurred try again", Toast.LENGTH_SHORT).show());
                    }
                }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            FirebaseAuth.getInstance().signOut();
            requireContext().startActivity(new Intent(requireContext(), Registration.class));
            requireActivity().finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}