package com.tiham_dipta.saloonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tiham_dipta.saloonapp.models.User;

import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {


    private String mailString;
    private String passwordString;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private SharedPreferences sharedPreferences;


    private EditText mailEditText;
    private EditText passwordEditText;

    private FancyButton signInFancyButton;
    private Button registerNowButton;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("saloon", MODE_PRIVATE);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        initViews();
        viewClickListeners();
    }


    private void initViews() {
        mailEditText = findViewById(R.id.email_entry);
        passwordEditText = findViewById(R.id.password_entry);

        signInFancyButton = findViewById(R.id.sign_in_button);

        registerNowButton = findViewById(R.id.register_button);
    }


    private void viewClickListeners() {

        signInFancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mailString = mailEditText.getText().toString();
                passwordString = passwordEditText.getText().toString();

                if (mailString.isEmpty()) {
                    mailEditText.setError("Email Field is empty");
                } else if (passwordString.length() < 4) {
                    passwordEditText.setError("Password is too short");
                } else {
                    // Login

                    firebaseAuth.signInWithEmailAndPassword(mailString, passwordString)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()) {

                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        if (user != null) {

                                            DatabaseReference dbUser = firebaseDatabase
                                                    .getReference("user").child(user.getUid());

                                            dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    User userNew = dataSnapshot.getValue(User.class);

                                                    if (userNew != null) {
                                                        int type = Integer.parseInt(userNew.getUserType());

                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putInt("client", type);
                                                        editor.apply();

                                                        // Do next task
                                                        Toast.makeText(LoginActivity.this,
                                                                "Welcome " + userNew.getDisplayName(),
                                                                Toast.LENGTH_LONG).show();

                                                        if (type == 0) {
                                                            Intent intent = new Intent(
                                                                    LoginActivity.this, BarberActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            Intent intent = new Intent(
                                                                    LoginActivity.this, CustomerActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        registerNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
