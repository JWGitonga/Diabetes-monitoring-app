package com.example.diabeteshealthmonitoringapplication.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diabeteshealthmonitoringapplication.DoctorLandingViewModel;
import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.activities.PatientsReadingsActivity;
import com.example.diabeteshealthmonitoringapplication.adapters.ReadingListAdapter;
import com.example.diabeteshealthmonitoringapplication.models.Reading;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorReadingFragment extends Fragment {
    private static final String TAG = "DoctorReadingFragment";
    private ReadingListAdapter adapter;
    private DoctorLandingViewModel viewModel;

    public DoctorReadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(DoctorLandingViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_doctor_reading, container, false);
        ListView listView = view.findViewById(R.id.reading_list);
        viewModel.getMyPatientsReadings().observe(getViewLifecycleOwner(),readingNodes -> {
            adapter = new ReadingListAdapter(requireContext(),R.layout.doctor_reading_item,readingNodes);
            listView.setAdapter(adapter);
            adapter.setOnItemClickListener(position ->
                    startActivity(new Intent(requireContext(), PatientsReadingsActivity.class).putExtra("uid",readingNodes.get(position).getUid())));
        });

        return view;
    }
}