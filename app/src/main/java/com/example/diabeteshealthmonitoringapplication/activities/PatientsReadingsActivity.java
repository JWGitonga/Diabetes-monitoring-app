package com.example.diabeteshealthmonitoringapplication.activities;

import static androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.diabeteshealthmonitoringapplication.R;

import java.util.Objects;

public class PatientsReadingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_readings);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        intent.getStringArrayListExtra("patientReadings");
        RecyclerView recyclerView = findViewById(R.id.patient_readings_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,VERTICAL));
    }
}