package com.tiham_dipta.saloonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class RegisterActivity extends AppCompatActivity {

    private String displayName;
    private String email;
    private String password;
    private String confirmPassword;

    private int clientID;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private SharedPreferences sharedPreferences;


    private Toolbar toolbar;

    private FancyButton uploadImageFancyButton;

    private NiceSpinner userTypeNiceSpinner;

    private EditText displayNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;

    private FancyButton registerFancyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);


        initViews();
        initObject();
        viewClickListeners();
    }

    private void initObject() {

        sharedPreferences = getSharedPreferences("saloon", MODE_PRIVATE);

        // init FirebaseAuth

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }


    private void initViews() {

        uploadImageFancyButton = findViewById(R.id.avatar_upload_button);

        userTypeNiceSpinner = findViewById(R.id.user_type_nice_spinner);
        List<String> stringLinkedList = new LinkedList<>(Arrays.asList("Barber", "Customer"));
        userTypeNiceSpinner.attachDataSource(stringLinkedList);

        displayNameEditText = findViewById(R.id.display_name_entry);
        emailEditText = findViewById(R.id.email_entry);
        passwordEditText = findViewById(R.id.pass_entry);
        confirmPasswordEditText = findViewById(R.id.confirm_pass_entry);

        registerFancyButton = findViewById(R.id.register_in_button);
    }

    private void viewClickListeners() {

        uploadImageFancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        userTypeNiceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clientID = position;
            }
        });

        registerFancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayName = displayNameEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirmPassword = confirmPasswordEditText.getText().toString();

                if (displayName.isEmpty()) {
                    displayNameEditText.setError("Name Field is empty");
                } else if (email.isEmpty()) {
                    emailEditText.setError("Email Field is empty");
                } else if (password.length() < 4) {
                    passwordEditText.setError("Password is too short");
                } else if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Password didn't matched");
                } else {
                    // Now Register

                    // Create an user with Email and Password
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        if (user != null) {
                                            // User is Created
                                            // Now you need to update the user data to the Realtime Database.

                                            // init database reference
                                            DatabaseReference databaseReference = firebaseDatabase.getReference();

                                            // If user is not null then update the data to the database
                                            DatabaseReference userChild =
                                                    databaseReference.child("user").child(user.getUid());

                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(displayName)
                                                    .build();

                                            Map<String, String> userDataMap = new HashMap<>();

                                            userDataMap.put("displayName", displayName);
                                            userDataMap.put("email", email);
                                            userDataMap.put("userType", String.valueOf(clientID));

                                            userChild.setValue(userDataMap);

                                            user.updateProfile(profileUpdates);

                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt("client", clientID);
                                            editor.apply();

                                            // Do next task
                                            if (clientID == 0) {
                                                Intent intent = new Intent(
                                                        RegisterActivity.this, BarberActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Intent intent = new Intent(
                                                        RegisterActivity.this, CustomerActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    } else if (task.isCanceled()){
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Authentication cancelled",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Authentication cancelled",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
