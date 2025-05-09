package com.example.kinotick.moviebd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private String status; // "now_showing" или "coming_soon"
    private List<Date> showDates = new ArrayList<>(); // Инициализация по умолчанию
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
    public String getStatus() {
        return status != null ? status : "now_showing"; // Значение по умолчанию только при чтении
    }
    public void setStatus(String status) { this.status = status; }

    public List<Date> getShowDates() { return showDates; }
    public void setShowDates(List<Date> showDates) { this.showDates = showDates; }
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
    // Форматирование дат показа
    public String getFormattedShowDates() {
        if (showDates == null || showDates.isEmpty()) return "";
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        StringBuilder dates = new StringBuilder();
        for (Date date : showDates) {
            dates.append(format.format(date)).append(", ");
        }
        return dates.length() > 0 ? dates.substring(0, dates.length() - 2) : "";
    }

}