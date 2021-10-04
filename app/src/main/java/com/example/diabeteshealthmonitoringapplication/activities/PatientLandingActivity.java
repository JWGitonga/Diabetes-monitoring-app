package com.example.diabeteshealthmonitoringapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.fragments.ChatFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.DoctorsFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.HospitalFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.PatientsFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.ReadingsFragment;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class PatientLandingActivity extends AppCompatActivity {
    private static final String TAG = "HomePage";
    BottomNavigationView bottomNavigationView;
    private User user;
    private String name, mUid, imageUrl, email, phone, deviceToken, type;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        type = user.getRole();
        name = user.getUsername();
        mUid = user.getUid();
        imageUrl = user.getImageUrl();
        email = user.getEmail();
        phone = user.getPhone();
        deviceToken = user.getDeviceToken();
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        new ReadingsFragment())
                .commit();
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.reading:
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Reading");
                            inflateContainer(new ReadingsFragment());
                            break;
                        case R.id.message:
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                            inflateContainer(new ChatFragment());
                            break;
                        case R.id.doctors:
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Doctors");
                            inflateContainer(new DoctorsFragment());
                            break;
                        case R.id.hospital:
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Hospital");
                            inflateContainer(new HospitalFragment());
                            break;
                        case R.id.readings_patient:
                            // This should not be here either make it a fragment or create entry action.
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Readings List");
                            inflateContainer(new ReadingsFragment());
                        default:
                            return true;
                    }
                    return true;
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