package com.tiham_dipta.saloonapp.models;

import java.io.Serializable;

public class Schedule implements Serializable {

    private String id;
    private String date;
    private String time;
    private String saloonId;

    private long slot;
    private boolean booked;

    public Schedule(String id, String date, String time, String saloonId) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.saloonId = saloonId;
    }

    public Schedule() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSaloonId() {
        return saloonId;
    }

    public void setSaloonId(String saloonId) {
        this.saloonId = saloonId;
    }

    public long getSlot() {
        return slot;
    }

    public void setSlot(long slot) {
        this.slot = slot;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
