package com.example.kinotick.seats;

public class MovieTicket {
    private String movieName;
    private String date;
    private String time;
    private int row;
    private int seat;
    private boolean isSold;

    public MovieTicket(String movieName, String date, String time, int row, int seat, boolean isSold) {
        this.movieName = movieName;
        this.date = date;
        this.time = time;
        this.row = row;
        this.seat = seat;
        this.isSold = isSold;
    }

    // Геттеры и сеттеры
    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }
}
