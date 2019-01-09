package com.tiham_dipta.saloonapp;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tiham_dipta.saloonapp.models.Booking;
import com.tiham_dipta.saloonapp.models.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Context context;
    private List<Schedule> scheduleList;
    private List<Booking> bookingList;

    private DatabaseReference databaseReference;

    public ScheduleAdapter(Context context, List<Schedule> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;

        bookingList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_single_bookings, viewGroup, false);

        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ScheduleViewHolder scheduleViewHolder, int i) {
        final Schedule schedule = scheduleList.get(i);

        scheduleViewHolder.scheduleTimeTextView.setText(schedule.getTime());

        if (schedule.isBooked()) {

            scheduleViewHolder.bookFancyButton.setText("Booked");
            scheduleViewHolder.bookFancyButton.setTextColor(
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                            context.getResources().getColor(R.color.white,null):
                            context.getResources().getColor(R.color.white))
            );
            scheduleViewHolder.bookFancyButton.setBackgroundColor(
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                            context.getResources().getColor(R.color.colorPrimary,null):
                            context.getResources().getColor(R.color.colorPrimary))
            );
        } else {

            if (schedule.getSlot() >= 3) {
                scheduleViewHolder.bookFancyButton.setText("No Slot!");
                scheduleViewHolder.bookFancyButton.setTextColor(
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                context.getResources().getColor(R.color.white,null):
                                context.getResources().getColor(R.color.white))
                );
                scheduleViewHolder.bookFancyButton.setBackgroundColor(
                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                context.getResources().getColor(R.color.colorRed,null):
                                context.getResources().getColor(R.color.colorRed))
                );
            }

        }

        scheduleViewHolder.bookFancyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (schedule.isBooked()) {
                    Toast.makeText(context, "Already booked", Toast.LENGTH_LONG).show();
                } else if ( schedule.getSlot() >= 3) {
                    Toast.makeText(context, "No slot available", Toast.LENGTH_LONG).show();

                    if (schedule.getSlot() >= 3) {
                        scheduleViewHolder.bookFancyButton.setText("No Slot!");
                        scheduleViewHolder.bookFancyButton.setTextColor(
                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                        context.getResources().getColor(R.color.white,null):
                                        context.getResources().getColor(R.color.white))
                        );
                        scheduleViewHolder.bookFancyButton.setBackgroundColor(
                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                        context.getResources().getColor(R.color.colorRed,null):
                                        context.getResources().getColor(R.color.colorRed))
                        );
                    }
                } else {

                    // Database Query
                    Query query = databaseReference.child("booking")
                            .orderByChild("scheduleId")
                            .equalTo(schedule.getId());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            long slot = dataSnapshot.getChildrenCount();

                            Log.d("slot", String.valueOf(slot));

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (slot < 3 && user != null) {
                                // Now book

                                schedule.setBooked(true);
                                long slot2 = schedule.getSlot();
                                schedule.setSlot(slot2 + 1);

                                DatabaseReference bookingChild = databaseReference.child("booking");

                                Map<String, String> bookingDataMap = new HashMap<>();

                                bookingDataMap.put("scheduleId", schedule.getId());
                                bookingDataMap.put("saloonId", "1");
                                bookingDataMap.put("userKey", user.getUid());

                                bookingChild.push().setValue(bookingDataMap);

                                scheduleViewHolder.bookFancyButton.setText("Booked");
                                scheduleViewHolder.bookFancyButton.setTextColor(
                                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                                context.getResources().getColor(R.color.white,null):
                                                context.getResources().getColor(R.color.white))
                                );
                                scheduleViewHolder.bookFancyButton.setBackgroundColor(
                                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                                context.getResources().getColor(R.color.colorPrimary,null):
                                                context.getResources().getColor(R.color.colorPrimary))
                                );
                            } else {
                                scheduleViewHolder.bookFancyButton.setText("No Slot!");
                                scheduleViewHolder.bookFancyButton.setTextColor(
                                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                                context.getResources().getColor(R.color.white,null):
                                                context.getResources().getColor(R.color.white))
                                );
                                scheduleViewHolder.bookFancyButton.setBackgroundColor(
                                        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                                context.getResources().getColor(R.color.colorRed,null):
                                                context.getResources().getColor(R.color.colorRed))
                                );
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private TextView scheduleTimeTextView;
        private FancyButton bookFancyButton;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);

            scheduleTimeTextView = itemView.findViewById(R.id.schedule_time);
            bookFancyButton = itemView.findViewById(R.id.book_button);
        }
    }
}
