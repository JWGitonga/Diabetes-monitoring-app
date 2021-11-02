package com.example.diabeteshealthmonitoringapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diabeteshealthmonitoringapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private String email, password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing in...");
        EditText mEmail = findViewById(R.id.email_login);
        EditText pass = findViewById(R.id.password);
        Button registration = findViewById(R.id.Register);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        Button login = findViewById(R.id.Login);
        login.setOnClickListener(v -> {
            progressDialog.show();
            this.email = mEmail.getText().toString().trim();
            this.password = pass.getText().toString().trim();
            if (email.isEmpty()) {
                mEmail.setError("Cannot be empty");
                progressDialog.dismiss();
            } else {
                if (password.isEmpty()) {
                    pass.setError("Cannot be empty");
                    progressDialog.dismiss();
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mEmail.setError("Invalid email address");
                        progressDialog.dismiss();
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(LoginActivity.this, IntermediateActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(e -> {
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Invalid credential", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
        forgotPassword.setOnClickListener(v -> {
            final AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
            final EditText edittext = new EditText(LoginActivity.this);
            edittext.setPadding(25, 0, 25, 20);
            edittext.setMaxWidth(100);
            edittext.setHint("Enter email address...");
            alert.setTitle("Password Recovery");
            alert.setView(edittext);
            alert.setIcon(R.drawable.ic_launcher_foreground);
            alert.setPositiveButton("Submit", (dialog, whichButton) -> {
                String email = edittext.getText().toString();
                if (!TextUtils.isEmpty(email)) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> Toast.makeText(LoginActivity.this, "Link was sent to your email", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(LoginActivity.this, "Nothing was entered in the field", Toast.LENGTH_SHORT).show();
                }
            });
            alert.setNegativeButton("Cancel", (dialog, whichButton) -> {
            });
            alert.show();
        });
        registration.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, Registration.class));
            finish();
        });

    }
}