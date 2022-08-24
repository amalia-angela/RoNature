package com.example.myapplicationandoroid.authentification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplicationandoroid.R;
import com.example.myapplicationandoroid.control.DashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, password, name;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Create Account");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        email = findViewById(R.id.register_email);
        name = findViewById(R.id.register_name);
        password = findViewById(R.id.register_password);
        Button mRegister = findViewById(R.id.register_button);
        TextView existaccount = findViewById(R.id.homepage);
        mAuth = FirebaseAuth.getInstance();
        progressBar = new ProgressBar(this);
//        progressBar.setMessage("Register");

        mRegister.setOnClickListener(v -> {
            String emaill = email.getText().toString().trim();
            String uname = name.getText().toString().trim();
            String pass = password.getText().toString().trim();
            if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                email.setError("Invalid Email");
                email.setFocusable(true);
            } else if (pass.length() < 6) {
                password.setError("Must have more than 6 characters");
                password.setFocusable(true);
            } else {
                registerUser(emaill, pass, uname);
            }
        });
        existaccount.setOnClickListener(v -> startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));
    }

    private void registerUser(String emaill, final String pass, final String uname) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(emaill, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressBar.setVisibility(View.GONE);
                FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                String email = user.getEmail();
                String uid = user.getUid();
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("email", email);
                hashMap.put("uid", uid);
                hashMap.put("name", uname);
                hashMap.put("onlineStatus", "online");
                hashMap.put("typingTo", "noOne");
                hashMap.put("image", "");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Users");
                reference.child(uid).setValue(hashMap);
                Toast.makeText(RegistrationActivity.this, "Registered User " + user.getEmail(), Toast.LENGTH_LONG).show();
                Intent mainIntent = new Intent(RegistrationActivity.this, DashboardActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegistrationActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(RegistrationActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}