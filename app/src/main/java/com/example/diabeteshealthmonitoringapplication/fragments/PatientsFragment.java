package com.example.diabeteshealthmonitoringapplication.fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.PatientListAdapter;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientsFragment extends Fragment {
    private static final String TAG = "PatientsFragment";
    private final List<User> patients = new ArrayList<>();
    private PatientListAdapter adapter;

    public PatientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PatientListAdapter(requireContext(), R.layout.doctor_patient_item, getPatients(FirebaseAuth.getInstance().getUid()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients, container, false);
        ListView listView = view.findViewById(R.id.patients_list);
        listView.setAdapter(adapter);
        return view;
    }

    private List<User> getPatients(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("patients/" + uid);
        reference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildren().forEach(patient -> {
                    User user = patient.getValue(User.class);
                    if (user != null) {
                        patients.add(user);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "onCancelled: "+error.getMessage());
            }
        });
        return patients;
    }
}