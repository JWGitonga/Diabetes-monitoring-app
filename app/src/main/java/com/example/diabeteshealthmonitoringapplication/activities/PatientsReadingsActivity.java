package com.example.diabeteshealthmonitoringapplication.activities;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.PatientReadingsAdapter;
import com.example.diabeteshealthmonitoringapplication.viewmodels.DoctorLandingViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class PatientsReadingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_readings);
        DoctorLandingViewModel viewModel = new ViewModelProvider(this).get(DoctorLandingViewModel.class);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Readings");
        Intent intent = getIntent();
        String id = intent.getStringExtra("uid");
        ImageView noReadingsIv = findViewById(R.id.no_readings_iv);
        TextView noReadingTv = findViewById(R.id.no_readings_tv);
        RecyclerView recyclerView = findViewById(R.id.patient_readings_recycler);
        recyclerView.setHasFixedSize(true);
        viewModel.getPatientReadings(id).observe(this, readings -> {
            if (readings.isEmpty()) {
                noReadingsIv.setVisibility(View.VISIBLE);
                noReadingTv.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                noReadingsIv.setVisibility(View.GONE);
                noReadingTv.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                PatientReadingsAdapter adapter = new PatientReadingsAdapter(readings, this);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, VERTICAL));
                adapter.setOnItemClickListener(position -> Snackbar.make(recyclerView, "Item position " + position + " clicked...", Snackbar.LENGTH_LONG)
                        .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show());
            }

        });

    }

}