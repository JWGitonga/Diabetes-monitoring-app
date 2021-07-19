package com.example.diabeteshealthmonitoringapplication.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.diabeteshealthmonitoringapplication.R;
import com.example.diabeteshealthmonitoringapplication.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Register";
    private EditText nameEt, emailEt, phoneEt, passwordEt, confirmPasswordEt;
    private ImageView imageView;
    private Uri imageUri;
    private String name, email, phone, password, cPassword;
    private ProgressDialog progressDialog;
    String imageUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        imageView = findViewById(R.id.image_view);
        nameEt = findViewById(R.id.username_register);
        emailEt = findViewById(R.id.email_register);
        phoneEt = findViewById(R.id.phone_no_register);
        passwordEt = findViewById(R.id.password_register);
        confirmPasswordEt = findViewById(R.id.confirm_password);
        Button register = findViewById(R.id.register_btn);
        imageView.setOnClickListener(v -> {
            if (checkPermission()) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
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
            if (name.isEmpty()) {
                nameEt.setError("Cannot be empty");
            } else {
                if (email.isEmpty()) {
                    emailEt.setError("Cannot be empty");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailEt.setError("Invalid email address");
                    } else {
                        if (password.isEmpty()) {
                            passwordEt.setError("Cannot be empty");
                        } else {
                            if (cPassword.isEmpty()) {
                                confirmPasswordEt.setError("Cannot be empty");
                            } else {
                                if (!password.contentEquals(cPassword)) {
                                    passwordEt.setError("Passwords do not match");
                                    confirmPasswordEt.setError("Passwords do not match");
                                } else {
                                    if (imageUri.getEncodedPath().isEmpty()) {
                                        Toast.makeText(this, "Kindly choose a profile picture", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    } else {
                                        register(email, password);
                                        User user = new User(FirebaseAuth.getInstance().getUid(), name, email, phone, imageUrl);
                                        addUserToDb(user);
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
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void register(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    if (task.getUser() != null) {
                        imageUrl = addImageToStorage();
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

    private String addImageToStorage() {
        final String[] downloadUri = new String[1];
        StorageReference reference = FirebaseStorage.getInstance()
                .getReference("user_images/" + FirebaseAuth.getInstance().getUid());
        reference.putFile(imageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.setMessage("Finalizing");
                        downloadUri[0] = reference.getDownloadUrl().toString();
                    }

                });
        return downloadUri[0];
    }

    private void addUserToDb(User user) {
        FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getUid())
                .setValue(user)
                .addOnSuccessListener(task -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomePage.class));
                });
    }
}