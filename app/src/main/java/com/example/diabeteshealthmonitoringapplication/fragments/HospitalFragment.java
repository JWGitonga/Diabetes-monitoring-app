package com.example.diabeteshealthmonitoringapplication.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.Registration;
import com.example.diabeteshealthmonitoringapplication.models.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class HospitalFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private EditText date, time;
    private String strDate, strTime, strHospital;

//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

//    private String mParam1;
//    private String mParam2;

    public HospitalFragment() {
        // Required empty public constructor
    }


//    public static HospitalFragment newInstance(String param1, String param2) {
//        HospitalFragment fragment = new HospitalFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital, container, false);
        date = view.findViewById(R.id.date_et_hospital);
        time = view.findViewById(R.id.time_et_hospital);
        Button uploadAppointment = view.findViewById(R.id.book_apmnt_btn);
        Spinner hospitalSpinner = view.findViewById(R.id.hospital_spinner);
        date.setOnFocusChangeListener((v, hasFocus) -> {
            if (v.getId() == R.id.date_et_hospital && hasFocus) {
                DatePickerDialog datePicker = new DatePickerDialog(requireContext());
                datePicker.setOnDateSetListener((view12, year, month, dayOfMonth) -> {
                    strDate = dayOfMonth + "/" + month + "/" + year;
                    date.setText(strDate);
                });
                datePicker.create();
                datePicker.show();
            }
        });
        time.setOnFocusChangeListener((v, hasFocus) -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int mMinute = calendar.get(Calendar.MINUTE);
            if (v.getId() == R.id.time_et_hospital && hasFocus) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                        (view1, hourOfDay, minute) -> {
                            strTime = hourOfDay + ":" + minute;
                            time.setText(strTime);
                        }, hour, mMinute, true);
                timePickerDialog.create();
                timePickerDialog.show();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.hospitals,
                android.R.layout.simple_dropdown_item_1line);
        hospitalSpinner.setAdapter(adapter);
        hospitalSpinner.setOnItemSelectedListener(this);
        uploadAppointment.setOnClickListener(v -> {
            if (strDate.isEmpty()) {
                date.setError("Cannot be empty");
            } else {
                if (strTime.isEmpty()) {
                    time.setError("Cannot be empty");
                } else {
                    if (strHospital.isEmpty()) {
                        Toast.makeText(requireContext(), "Please choose a hospital", Toast.LENGTH_SHORT).show();
                    } else {
                        String uid = FirebaseAuth.getInstance().getUid();
                        Appointment appointment = new Appointment(uid, strDate, strTime, strHospital);
                        String[] fom = strDate.split("/");
                        FirebaseDatabase.getInstance().getReference("appointments/" + uid + ":" + fom[0]+"-"+fom[1]+"-"+fom[2])
                                .setValue(appointment)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(), "Appointment booked", Toast.LENGTH_SHORT).show();
                                        date.setText("");
                                        time.setText("");
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(requireContext(), "Fatal error", Toast.LENGTH_SHORT).show());
                    }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.exit) {
            FirebaseAuth.getInstance().signOut();
            requireContext().startActivity(new Intent(requireContext(), Registration.class));
            requireActivity().finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        strHospital = parent.getAdapter().getItem(position).toString();
        Toast.makeText(requireContext(), strHospital + " was selected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Something is always selected
    }
}