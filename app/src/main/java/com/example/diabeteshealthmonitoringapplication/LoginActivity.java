package com.example.diabeteshealthmonitoringapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diabeteshealthmonitoringapplication.activities.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class LoginActivity extends AppCompatActivity {
    private String email, password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseAuth.getInstance().getUid()!=null){
            startActivity(new Intent(this, HomePage.class));
            finish();
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing in...");
        EditText mEmail = findViewById(R.id.email_login);
        EditText pass = findViewById(R.id.psswd);
        Button registration = findViewById(R.id.Register);
        TextView forgotPassword = findViewById(R.id.forgot_password);

        Button login = findViewById(R.id.Login);
        login.setOnClickListener(v -> {
            progressDialog.show();
            this.email = mEmail.getText().toString().trim();
            this.password = pass.getText().toString().trim();
            if (email.isEmpty()) {
                mEmail.setError("Cannot be empty");
            } else {
                if (password.isEmpty()) {
                    pass.setError("Cannot be empty");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mEmail.setError("Invalid email address");
                    } else {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(LoginActivity.this, HomePage.class));
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
            EditText email = new EditText(this);
            email.setMaxEms(10);
            email.setPadding(50, 20, 50, 20);
            email.setHint("Enter email address");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Password recovery");
            alertDialog.setView(email);
            alertDialog.setPositiveButton("Submit", (dialog, which) -> {
                String strEmail = email.getText().toString().trim();
                if (strEmail.isEmpty()) {
                    email.setError("Cannot be empty");
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
                        Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(strEmail)
                                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Reset link sent to email", Toast.LENGTH_SHORT).show());
                    }
                }
            });
            AlertDialog alert = alertDialog.create();
            alert.show();
        });
        registration.setOnClickListener(v ->{
            startActivity(new Intent(LoginActivity.this, Registration.class));
            finish();
        } );
    }
}