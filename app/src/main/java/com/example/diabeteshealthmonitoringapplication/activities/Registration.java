package com.example.diabeteshealthmonitoringapplication.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.example.diabeteshealthmonitoringapplication.models.AssociatedHospital;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Register";
    private EditText nameEt, emailEt, phoneEt, passwordEt, confirmPasswordEt;
    private RadioGroup radioGroup;
    private ImageView imageView;
    private Uri imageUrl;
    private String name, email, phone, password, cPassword, role;
    private ProgressDialog progressDialog;
    private String imageUri;
    private String hospital;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Registration");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        imageView = findViewById(R.id.image_view);
        nameEt = findViewById(R.id.username_register);
        emailEt = findViewById(R.id.email_register);
        phoneEt = findViewById(R.id.phone_no_register);
        passwordEt = findViewById(R.id.password_register);
        confirmPasswordEt = findViewById(R.id.confirm_password);
        radioGroup = findViewById(R.id.role_radio_group);
        Button register = findViewById(R.id.register_btn);
        imageView.setOnClickListener(v -> {
            if (checkPermission()) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityIfNeeded(intent, 100);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            }
        });
        register.setOnClickListener(v -> {
            progressDialog.show();
            name = nameEt.getText().toString().trim();
            email = emailEt.getText().toString().trim();
            phone = phoneEt.getText().toString().trim();
            password = passwordEt.getText().toString().trim();
            cPassword = confirmPasswordEt.getText().toString().trim();
            int checkId = radioGroup.getCheckedRadioButtonId();
            if (checkId == R.id.patient_radio_button) role = "Patient";
            else if (checkId == R.id.doctor_radio_button) role = "Health worker";
            else return;
            if (name.isEmpty()) {
                nameEt.setError("Cannot be empty");
                progressDialog.dismiss();
            } else {
                if (email.isEmpty()) {
                    emailEt.setError("Cannot be empty");
                    progressDialog.dismiss();
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailEt.setError("Invalid email address");
                        progressDialog.dismiss();
                    } else {
                        if (phone.isEmpty()) {
                            phoneEt.setError("Cannot be empty");
                            progressDialog.dismiss();
                        } else {
                            if (password.isEmpty()) {
                                passwordEt.setError("Cannot be empty");
                                progressDialog.dismiss();
                            } else {
                                if (cPassword.isEmpty()) {
                                    confirmPasswordEt.setError("Cannot be empty");
                                    progressDialog.dismiss();
                                } else {
                                    if (!password.equals(cPassword)) {
                                        passwordEt.setError("Passwords does not match");
                                        confirmPasswordEt.setError("Passwords does not match");
                                        progressDialog.dismiss();
                                    } else {
                                        if (imageUrl == null) {
                                            Toast.makeText(getApplicationContext(), "Please select a profile picture", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            if (role.isEmpty()) {
                                                Toast.makeText(getApplicationContext(), "Please select a role", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            } else {
                                                register(name, email, phone, password, role);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && Arrays.equals(permissions, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityIfNeeded(intent, 200);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imageUrl = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUrl);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void register(String name, String email, String phone, String password, String role) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    if (task.getUser() != null) {
                        addImageToStorage(name, email, phone, role);
                        Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(error -> {
                    if (error instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Unknown exception " + error.toString(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "register: error " + error.toString());
                    }
                });
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addImageToStorage(String name, String email, String phone, String role) {
        StorageReference reference = FirebaseStorage.getInstance()
                .getReference("user_images/" + FirebaseAuth.getInstance().getUid());
        reference.putFile(imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.setMessage("Finalizing");
                        reference.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    imageUri = uri.toString();
                                    Log.i(TAG, "addImageToStorage: " + imageUri);
                                    String token = FirebaseInstanceId.getInstance().getToken();
                                    User user = new User(FirebaseAuth.getInstance().getUid(), name, email, phone, imageUri, role, token);
                                    addUserToDb(user);
                                });
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addUserToDb(User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid());
        ref.setValue(user)
                .addOnSuccessListener(task -> {
                    if (user.getRole().equals("Health worker")) {
                        View view = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null, false);
                        Spinner spinner = view.findViewById(R.id.hospital_spinner);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.hospitals, android.R.layout.simple_dropdown_item_1line);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                hospital = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                Log.i(TAG, "onNothingSelected: Something is always selected");
                            }
                        });
                        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                                .setTitle("Please select hospital")
                                .setCancelable(false)
                                .setView(view)
                                .setPositiveButton("Submit", (dialog, which) -> {
                                    if (hospital.isEmpty()) {
                                        Toast.makeText(getApplicationContext(), "Please select a hospital", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String uid = FirebaseAuth.getInstance().getUid();
                                        AssociatedHospital associatedHospital = new AssociatedHospital(uid, name, hospital);
                                        FirebaseDatabase.getInstance().getReference("doc_hospital/" + uid)
                                                .setValue(associatedHospital)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful() && task1.isComplete()) {
                                                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(this, LoginActivity.class));
                                                        finish();
                                                    }
                                                });
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                });
    }
}