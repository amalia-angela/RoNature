package com.example.myapplicationandoroid.authentification;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

public class LoginActivity extends AppCompatActivity {

    FirebaseUser currentUser;
    private EditText email, password;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Wellcome");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // initialising the layout items
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        TextView newdnewaccount = findViewById(R.id.needs_new_account);
        TextView reocverpass = findViewById(R.id.forgetp);
        firebaseAuth = FirebaseAuth.getInstance();
        Button mlogin = findViewById(R.id.login_button);
        progressBar = new ProgressBar(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // checking if user is null or not
        if (firebaseAuth != null) {
            currentUser = firebaseAuth.getCurrentUser();
        }

        //Login
        mlogin.setOnClickListener(v -> {
            String emaill = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            // if email format doesn't matches -> return null
            if (!Patterns.EMAIL_ADDRESS.matcher(emaill).matches()) {
                email.setError("Invalid Email");
                email.setFocusable(true);

            } else {
                loginUser(emaill, pass);
            }
        });

        //Registration Activity(If new account )
        newdnewaccount.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));

        // Recover Your Password using email
        reocverpass.setOnClickListener(v -> showRecoverPasswordDialog());
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailregistr = new EditText(this);//write your registered email
        emailregistr.setText(getString(R.string.email));
        emailregistr.setMinEms(16);
        emailregistr.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailregistr);
        linearLayout.setPadding(10, 10, 10, 10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", (dialog, which) -> {
            String emaill = emailregistr.getText().toString().trim();
            beginRecovery(emaill);//send a mail on the mail to recover password
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void beginRecovery(String emaill) {
//        progressBar.setMessage("Sending Email....");
//        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setVisibility(View.VISIBLE);

        // send reset password email
        firebaseAuth.sendPasswordResetEmail(emaill).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Done sent", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Error Failed", Toast.LENGTH_LONG).show();
        });
    }

    private void loginUser(String emaill, String pass) {
//        progressBar.setMessage("Logging In....");
        progressBar.setVisibility(View.VISIBLE);

        // sign in with email and password after authenticating
        firebaseAuth.signInWithEmailAndPassword(emaill, pass).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                progressBar.setVisibility(View.GONE);
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                    String email = user.getEmail();
                    String uid = user.getUid();
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("email", email);
                    hashMap.put("uid", uid);
                    hashMap.put("name", "");
                    hashMap.put("onlineStatus", "online");
                    hashMap.put("typingTo", "noOne");
                    hashMap.put("phone", "");
                    hashMap.put("image", "");
                    hashMap.put("cover", "");
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    // store the value in Database in "Users" Node
                    DatabaseReference reference = database.getReference("Users");

                    // storing the value in Firebase
                    reference.child(uid).setValue(hashMap);
                }
                Toast.makeText(LoginActivity.this, "Registered User " + user.getEmail(), Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Error Occured", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}