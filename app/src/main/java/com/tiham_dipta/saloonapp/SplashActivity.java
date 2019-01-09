package com.tiham_dipta.saloonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("saloon", MODE_PRIVATE);
        final int clientID = sharedPreferences.getInt("client", 2);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (clientID == 0) {
                        Intent intent = new Intent(
                                SplashActivity.this, BarberActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (clientID == 1){
                        Intent intent = new Intent(
                                SplashActivity.this, CustomerActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(
                                SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 2000);
        } else {
            Intent intent = new Intent(
                    SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
