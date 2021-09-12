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
    private List<Reading> readings;

    public DoctorReadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readings = new ArrayList<>();
        adapter = new ReadingListAdapter(requireContext(),R.layout.doctor_reading_item,getPatients());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_doctor_reading, container, false);
        ListView listView = view.findViewById(R.id.reading_list);
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(position ->
                startActivity(new Intent(requireContext(), PatientsReadingsActivity.class).putExtra("patientReadings", (Parcelable) readings)));
        return view;
    }
    List<Reading> getPatients(){
        FirebaseDatabase.getInstance().getReference("patients/"+ FirebaseAuth.getInstance().getUid()+"/")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(patient->{
                            User user = patient.getValue(User.class);
                            if (user!=null){
                                FirebaseDatabase.getInstance().getReference("readings/"+user.getUid()+"/records/")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                snapshot.getChildren().forEach(record->{
                                                    Reading reading = record.getValue(Reading.class);
                                                    readings.add(reading);
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.i(TAG, "onCancelled: "+error.getMessage());
                                            }
                                        });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return readings;
    }
}