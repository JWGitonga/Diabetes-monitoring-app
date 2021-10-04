package com.example.diabeteshealthmonitoringapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diabeteshealthmonitoringapplication.viewmodels.DoctorLandingViewModel;
import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.PatientListAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PatientsFragment extends Fragment {
    private PatientListAdapter adapter;
    private DoctorLandingViewModel doctorLandingViewModel;

    public PatientsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorLandingViewModel = new ViewModelProvider(this).get(DoctorLandingViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patients, container, false);
        ListView listView = view.findViewById(R.id.patients_list);
        doctorLandingViewModel.getPatientRequests(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .observe(getViewLifecycleOwner(), requests -> {
                    adapter = new PatientListAdapter(requireContext(), R.layout.doctor_patient_item, requests);
                    listView.setAdapter(adapter);

                });
        return view;
    }
}