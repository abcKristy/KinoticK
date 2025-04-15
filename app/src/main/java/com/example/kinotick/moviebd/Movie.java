package com.example.kinotick.moviebd;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Movie {
    private int id;
    private String title;
    private String description;
    private String genre;
    private int duration;
    private double rating;
    private String posterUrl;
    private Date releaseDate;

    public Movie() {}

    public Movie(String title, String description, String genre, int duration,
                 double rating, String posterUrl, Date releaseDate) {
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getFormattedDate() {
        if (releaseDate == null) return "";
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return format.format(releaseDate);
    }
}