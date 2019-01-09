package com.tiham_dipta.saloonapp.models;

import java.io.Serializable;

public class Booking implements Serializable {

    private String bookingId;
    private String scheduleId;
    private String saloonId;
    private String userKey;

    public Booking(String bookingId, String scheduleId, String saloonId, String userKey) {
        this.bookingId = bookingId;
        this.scheduleId = scheduleId;
        this.saloonId = saloonId;
        this.userKey = userKey;
    }

    public Booking() {
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSaloonId() {
        return saloonId;
    }

    public void setSaloonId(String saloonId) {
        this.saloonId = saloonId;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
