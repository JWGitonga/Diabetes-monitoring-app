package com.example.diabeteshealthmonitoringapplication.activities;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.adapters.PatientReadingsAdapter;
import com.example.diabeteshealthmonitoringapplication.models.Reading;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;

public class PatientsReadingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_readings);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        List<Reading> readingList=intent.getParcelableArrayListExtra("patientReadings");
        RecyclerView recyclerView = findViewById(R.id.patient_readings_recycler);
        recyclerView.setHasFixedSize(true);
        PatientReadingsAdapter adapter = new PatientReadingsAdapter(readingList,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,VERTICAL));
        adapter.setOnItemClickListener(position -> Snackbar.make(recyclerView,"Item position "+position+" clicked...",Snackbar.LENGTH_LONG)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE).show());
    }

}