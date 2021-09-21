package com.example.diabeteshealthmonitoringapplication.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IntermediateActivity extends AppCompatActivity {
    private static final String TAG = "IntermediateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_mediatectivity);
        FirebaseDatabase.getInstance().getReference("uses")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(u -> {
                            User user = u.getValue(User.class);
                            if (user != null) {
                                if (user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                                    if (user.getRole().equals("Health worker")) {
                                        startActivity(new Intent(IntermediateActivity.this, DoctorLandingActivity.class).putExtra("user", user));
                                    } else {
                                        startActivity(new Intent(IntermediateActivity.this, PatientsReadingsActivity.class).putExtra("user", user));
                                    }
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