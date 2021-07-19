package com.example.diabeteshealthmonitoringapplication.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.fragments.ChatFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.HospitalFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.InteractionFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.ReadingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,
                        new ReadingsFragment())
                .commit();
        bottomNavigationView.setSelectedItemId(R.id.reading);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.reading:
                            inflateContainer(new ReadingsFragment());
                            break;
                        case R.id.message:
                            inflateContainer(new ChatFragment());
                            break;
                        case R.id.hospital:
                            inflateContainer(new HospitalFragment());
                            break;
                        default:
                            return false;
                    }
                    return false;
                });
    }

    private void inflateContainer(Fragment fm) {
        if (fm.equals(new InteractionFragment())){
            bottomNavigationView.setVisibility(View.GONE);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fm)
                .commit();
    }
}