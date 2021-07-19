package com.example.diabeteshealthmonitoringapplication;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Arrays;

public class Registration extends AppCompatActivity {
    private static final String TAG = "Register";
    private EditText nameEt, emailEt, phoneEt, passwordEt, confirmPasswordEt;
    private ImageView imageView;
    private Uri imageUrl;
    private Button register;
    private String name, email, phone, password, cPassword;
    private ProgressDialog progressDialog;
    String imageUri;

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
        register = findViewById(R.id.register_btn);
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
            register(email, password);
            User user = new User(FirebaseAuth.getInstance().getUid(),name,email,phone,imageUri);
            addUserToDb(user);
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults[0] == Activity.RESULT_OK) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
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

    private void register(String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(task -> {
                    if (task.getUser() != null) {
                        addImageToStorage();
                    }
                })
                .addOnFailureListener(error -> {
                    if (error instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
                    } else if (error instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Unknown exception "+error.toString(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "register: error "+error.toString());
                    }
                });
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void addImageToStorage() {
        StorageReference reference = FirebaseStorage.getInstance()
                .getReference("user_images/" + FirebaseAuth.getInstance().getUid());
        reference.putFile(imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.setMessage("Finalizing");
                        imageUri = reference.getDownloadUrl().toString();
                    }
                });
    }
    private void addUserToDb(User user){
        FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().getUid())
                .setValue(user)
                .addOnSuccessListener(task->{
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this,HomePage.class));
                });
    }
}