package com.tiham_dipta.saloonapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.tiham_dipta.saloonapp.models.Booking;
import com.tiham_dipta.saloonapp.models.Schedule;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;

    private List<Schedule> scheduleList;



    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        firebaseDatabase = FirebaseDatabase.getInstance();


        scheduleList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getSchedulesFromFirebase();
    }


    private void getSchedulesFromFirebase() {

        DatabaseReference dbSchedules = firebaseDatabase.getReference("schedule");

        dbSchedules.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()) {
                    final Schedule schedule = dataSnapshot.getValue(Schedule.class);

                    if (schedule != null) {
                        // Database Query
                        Query query = firebaseDatabase.getReference().child("booking")
                                .orderByChild("scheduleId")
                                .equalTo(schedule.getId());

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                long slot = dataSnapshot.getChildrenCount();

                                Log.d("slot", String.valueOf(slot));

                                schedule.setSlot(slot);
                                schedule.setBooked(false);

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if (user != null) {

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                        Booking booking = snapshot.getValue(Booking.class);

                                        if (booking != null && booking.getUserKey().equals(user.getUid())) {
                                            schedule.setBooked(true);
                                            break;
                                        }
                                    }

                                    scheduleList.add(schedule);

                                    ScheduleAdapter scheduleAdapter = new ScheduleAdapter(
                                            CustomerActivity.this, scheduleList);
                                    recyclerView.setAdapter(scheduleAdapter);
                                    scheduleAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("DatabaseError", databaseError.getMessage());
                            }
                        });
                    }
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

            Intent intent = new Intent(CustomerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
