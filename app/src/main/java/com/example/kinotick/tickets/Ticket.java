package com.example.kinotick.tickets;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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
                    .put("movie", movieName)
                    .put("date", dateTime)
                    .put("seat", seat)
                    .put("user", userFullName)
                    .put("valid", true) // Флаг валидности билета
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public static Ticket fromJson(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);
        return new Ticket(
                obj.getString("movie"),
                obj.getString("date"),
                obj.getString("seat"),
                obj.getString("user")
        );
    }
    public Bitmap generateQRCode(int size) {
        try {
            String ticketData = toJson(); // Используем наш метод toJson()
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(ticketData, BarcodeFormat.QR_CODE, size, size);
            BarcodeEncoder encoder = new BarcodeEncoder();
            return encoder.createBitmap(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
