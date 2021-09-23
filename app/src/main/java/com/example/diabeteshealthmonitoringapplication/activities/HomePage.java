package com.example.diabeteshealthmonitoringapplication.activities;

import android.annotation.SuppressLint;
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

public class HomePage extends AppCompatActivity {
    private static final String TAG = "HomePage";
    BottomNavigationView bottomNavigationView;
    private String name, mUid, imageUrl, email, phone, deviceToken, type;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        getTypeOfUser(FirebaseAuth.getInstance().getUid());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        new ReadingsFragment())
                .commit();
        bottomNavigationView.setOnItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.reading:
                            inflateContainer(new ReadingsFragment());
                            break;
                        case R.id.message:
                            if (type.equals("Health worker")) {
                                inflateContainer(new PatientsFragment());
                                Objects.requireNonNull(getSupportActionBar()).setTitle("Patients");
                            } else {
                                inflateContainer(new ChatFragment());
                                Objects.requireNonNull(getSupportActionBar()).setTitle("Chats");
                            }
                            break;
                        case R.id.hospital:
                            inflateContainer(new HospitalFragment());
                            Objects.requireNonNull(getSupportActionBar()).setTitle("Hospital");
                            break;
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

    public void getTypeOfUser(String uid) {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User user1 = user.getValue(User.class);
                            if (user1 != null) {
                                if (user1.getUid().equals(uid)) {
                                    name = user1.getUsername();
                                    email = user1.getEmail();
                                    mUid = user1.getUid();
                                    imageUrl = user1.getImageUrl();
                                    phone = user1.getPhone();
                                    type = user1.getRole();
                                    deviceToken = user1.getDeviceToken();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: -> " + error.getMessage());
                    }
                });
    }
}