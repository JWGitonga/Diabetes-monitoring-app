package com.example.diabeteshealthmonitoringapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.fragments.HospitalFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.InteractionFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.ReadingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        BottomNavigationView bottomAppbar = findViewById(R.id.bottom_nav);
        inflateContainer(new ReadingsFragment());
        bottomAppbar.setSelectedItemId(R.id.reading);
        bottomAppbar.setOnNavigationItemSelectedListener((BottomNavigationView.OnNavigationItemSelectedListener) item -> {
            switch (item.getItemId()) {
                case R.id.reading:
                    inflateContainer(new ReadingsFragment());
                    break;
                case R.id.message:
                    inflateContainer(new InteractionFragment());
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
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fm)
                .commit();
    }
}