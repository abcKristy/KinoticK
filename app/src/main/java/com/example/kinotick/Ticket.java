package com.example.kinotick;

import java.util.UUID;

public class Ticket {
    private String id;
    private String movieName;
    private String dateTime;
    private String seat;
    private String userFullName;

    public Ticket(String movieName, String dateTime, String seat, String userFullName) {
        this.movieName = movieName;
        this.dateTime = dateTime;
        this.seat = seat;
        this.userFullName = userFullName;
        this.id = UUID.randomUUID().toString();
    }

    // Геттеры
    public String getId() { return id; }
    public String getMovieName() { return movieName; }
    public String getDateTime() { return dateTime; }
    public String getSeat() { return seat; }
    public String getUserFullName() { return userFullName; }
}
