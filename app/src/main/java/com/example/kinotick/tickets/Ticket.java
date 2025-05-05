package com.example.kinotick.tickets;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;
import java.util.UUID;

public class Ticket implements Serializable {
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
    // Добавляем методы для преобразования в JSON и обратно
    public String toJson() {
        try {
            return new JSONObject()
                    .put("id", id)
                    .put("movieName", movieName)
                    .put("dateTime", dateTime)
                    .put("seat", seat)
                    .put("userFullName", userFullName)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}"; // Возвращаем пустой JSON в случае ошибки
        }
    }

    public static Ticket fromJson(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        return new Ticket(
                obj.getString("movieName"),
                obj.getString("dateTime"),
                obj.getString("seat"),
                obj.getString("userFullName")
        );
    }
}
