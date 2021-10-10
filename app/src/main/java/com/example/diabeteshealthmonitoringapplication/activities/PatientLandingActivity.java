package com.example.diabeteshealthmonitoringapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.fragments.ChatFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.DoctorsFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.HospitalFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.ReadingsFragment;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PatientLandingActivity extends AppCompatActivity {
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
                        default:
                            return true;
                    }
                    return true;
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Registration.class));
            return true;
        } else if (item.getItemId() == R.id.register_with_doctor) {
            Toast.makeText(this, "Register clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.readings_patient) {
            startActivity(new Intent(this, PatientsReadingsActivity.class));
            return true;
        } else if (item.getItemId() == R.id.booking_patient) {
            startActivity(new Intent(this, BookingActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void inflateContainer(Fragment fm) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fm)
                .commit();
    }
}