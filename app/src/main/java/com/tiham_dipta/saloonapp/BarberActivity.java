package com.tiham_dipta.saloonapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tiham_dipta.saloonapp.models.Booking;
import com.tiham_dipta.saloonapp.models.Customer;
import com.tiham_dipta.saloonapp.models.Schedule;
import com.tiham_dipta.saloonapp.models.User;

import java.util.ArrayList;
import java.util.List;

public class BarberActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    private List<Customer> scheduleList;


    private Toolbar toolbar;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber);

        toolbar = findViewById(R.id.barber_toolbar);
        setSupportActionBar(toolbar);


        firebaseDatabase = FirebaseDatabase.getInstance();


        scheduleList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getCustomersFromFirebase();
    }

    private void getCustomersFromFirebase() {
        Query query = firebaseDatabase.getReference().child("booking")
                .orderByChild("saloonId").equalTo("1");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Customer customer = new Customer();
                final Booking booking = dataSnapshot.getValue(Booking.class);

                if (booking != null) {

                    int index = Integer.parseInt(booking.getScheduleId());

                    final DatabaseReference databaseReference = firebaseDatabase.getReference("schedule")
                            .child(String.valueOf(index-1));

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Schedule schedule = dataSnapshot.getValue(Schedule.class);

                            if (schedule != null) {
                                customer.setScheduleTime(schedule.getTime());

                                DatabaseReference databaseReference = firebaseDatabase.getReference("user")
                                        .child(booking.getUserKey());

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user2 = dataSnapshot.getValue(User.class);

                                        if (user2 != null) {
                                            customer.setCustomerName(user2.getDisplayName());

                                            scheduleList.add(customer);

                                            CustomerAdapter customerAdapter = new CustomerAdapter(
                                                    BarberActivity.this, scheduleList
                                            );
                                            recyclerView.setAdapter(customerAdapter);
                                            customerAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("DatabaseError", databaseError.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("DatabaseError", databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DatabaseError", databaseError.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(BarberActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
