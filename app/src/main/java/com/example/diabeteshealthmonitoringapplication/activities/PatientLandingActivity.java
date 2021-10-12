package com.example.diabeteshealthmonitoringapplication.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.fragments.ChatFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.DoctorsFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.HospitalFragment;
import com.example.diabeteshealthmonitoringapplication.fragments.ReadingsFragment;
import com.example.diabeteshealthmonitoringapplication.models.AssociatedHospital;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.example.diabeteshealthmonitoringapplication.notification.APIService;
import com.example.diabeteshealthmonitoringapplication.notification.Client;
import com.example.diabeteshealthmonitoringapplication.notification.Data;
import com.example.diabeteshealthmonitoringapplication.notification.MyResponse;
import com.example.diabeteshealthmonitoringapplication.notification.Sender;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientLandingActivity extends AppCompatActivity {
    private static final String TAG = "PatientLandingActivity";
    BottomNavigationView bottomNavigationView;
    private User user;
    private String name, mUid, imageUrl, email, phone, deviceToken, type;
    private List<AssociatedHospital> doctors;
    private User mUser;
    private APIService apiService;
    List<AssociatedHospital> docs;

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
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        doctors = new ArrayList<>();
        docs = getDoctors();
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, Registration.class));
            return true;
        } else if (item.getItemId() == R.id.register_with_doctor) {
            Toast.makeText(this, "Register clicked", Toast.LENGTH_SHORT).show();
            PopupMenu popupMenu = new PopupMenu(this, bottomNavigationView);
            docs.forEach(doctor -> popupMenu.getMenu().add(doctor.getName() + " - " + doctor.getHospital() + " - " + doctor.getUid()));
            popupMenu.setOnMenuItemClickListener(item1 -> {
                String s = item1.toString();
                String[] chars = s.split(" - ");
                String uid = chars[2];
                String from = FirebaseAuth.getInstance().getUid();
                getUser(uid);
                Map<String, String> request = new HashMap<>();
                request.put("from", from);
                request.put("response", "request");
                FirebaseDatabase.getInstance().getReference("patient_requests/" + uid + "/" + from + "/")
                        .setValue(request)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.isComplete()) {
                                Toast.makeText(this, "Sent notification to doctor", Toast.LENGTH_SHORT).show();
                                Data data = new Data(from, "Patient Request", "Health Living", uid, R.drawable.ic_launcher_foreground);
                                Sender sender = new Sender(data, mUser.getDeviceToken());
                                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            if (response.body().success != 1) {
                                                Toast.makeText(PatientLandingActivity.this, "Failed to send notification check internet and try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                        Toast.makeText(PatientLandingActivity.this, "Something went wrong....", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                return true;
            });
            popupMenu.show();
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

    private void getUser(String uid) {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getChildren().forEach(user -> {
                            User u = user.getValue(User.class);
                            if (u != null) {
                                if (u.getUid().equals(uid)) {
                                    mUser = u;
                                }
                            } else {
                                Log.i(TAG, "onDataChange: Null user");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: error -> " + error.getMessage());
                    }
                });
    }

    List<AssociatedHospital> getDoctors() {
        FirebaseDatabase.getInstance().getReference("doc_hospital")
                .addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        doctors.clear();
                        snapshot.getChildren().forEach(associatedHospDoc -> {
                            AssociatedHospital doc = associatedHospDoc.getValue(AssociatedHospital.class);
                            doctors.add(doc);
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.i(TAG, "onCancelled: " + error.getMessage());
                    }
                });
        return doctors;
    }

    private void inflateContainer(Fragment fm) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        fm)
                .commit();
    }
}