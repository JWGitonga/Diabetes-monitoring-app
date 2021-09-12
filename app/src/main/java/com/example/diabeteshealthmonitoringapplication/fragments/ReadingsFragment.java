package com.example.diabeteshealthmonitoringapplication.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.activities.Registration;
import com.example.diabeteshealthmonitoringapplication.models.AssociatedHospital;
import com.example.diabeteshealthmonitoringapplication.models.Reading;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.example.diabeteshealthmonitoringapplication.notification.APIService;
import com.example.diabeteshealthmonitoringapplication.notification.Client;
import com.example.diabeteshealthmonitoringapplication.notification.Data;
import com.example.diabeteshealthmonitoringapplication.notification.MyResponse;
import com.example.diabeteshealthmonitoringapplication.notification.Sender;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReadingsFragment extends Fragment {
    private static final String TAG = "ReadingsFragment";
    private TextView reading, date, suggestion;
    private String strReading, strDate, strSuggestion;
    private List<AssociatedHospital> doctors;
    private View view;
    private User mUser;
    private APIService apiService;

    public ReadingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        doctors = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_readings, container, false);
        reading = view.findViewById(R.id.reading_et);
        date = view.findViewById(R.id.date_et);
        FloatingActionButton upload = view.findViewById(R.id.fab_upload);
        suggestion = view.findViewById(R.id.suggestion);

        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (v.getId() == R.id.date_et && hasFocus) {
                DatePickerDialog datePicker = new DatePickerDialog(requireContext());
                datePicker.setOnDateSetListener((view1, year, month, dayOfMonth) -> {
                    strDate = dayOfMonth + "/" + month + "/" + year;
                    date.setText(strDate);
                });
                datePicker.create();
                datePicker.show();
            }
        });

        upload.setOnClickListener(v -> {
            strReading = reading.getText().toString().trim();
            strSuggestion = suggestion.getText().toString().trim();
            if (strReading.isEmpty()) {
                reading.setError("Cannot be empty");
            } else {
                if (strDate.isEmpty()) {
                    reading.setError("Cannot be empty");
                } else {
                    String uid = FirebaseAuth.getInstance().getUid();
                    Reading r = new Reading(uid, strReading, strDate, strSuggestion);
                    String[] fom = strDate.split("/");
                    FirebaseDatabase.getInstance().getReference("readings/" + uid + "/records/" + fom[0] + "-" + fom[1] + "-" + fom[2])
                            .setValue(r)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(requireContext(), "Successfully uploaded reading...", Toast.LENGTH_SHORT).show();
                                    reading.setText("");
                                    date.setText("");
                                    suggestion.setText("");
                                }
                            }).addOnFailureListener(e -> Toast.makeText(requireContext(), "An error occurred try again", Toast.LENGTH_SHORT).show());
                }
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            FirebaseAuth.getInstance().signOut();
            requireContext().startActivity(new Intent(requireContext(), Registration.class));
            requireActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.register_with_doctor) {
            List<AssociatedHospital> docs = getDoctors();
            PopupMenu popupMenu = new PopupMenu(requireContext(), reading);
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
                                Toast.makeText(requireContext(), "Sent notification to doctor", Toast.LENGTH_SHORT).show();
                                Data data = new Data(from, "Patient Request", "Health Living", uid, R.drawable.ic_launcher_foreground);
                                Sender sender = new Sender(data, mUser.getDeviceToken());
                                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {
                                        if (response.isSuccessful()) {
                                            assert response.body() != null;
                                            if (response.body().success != 1) {
                                                Toast.makeText(requireContext(), "Failed to send notification check internet and try again", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                                        Toast.makeText(requireContext(), "Something went wrong....", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                return true;
            });
            popupMenu.show();
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

}