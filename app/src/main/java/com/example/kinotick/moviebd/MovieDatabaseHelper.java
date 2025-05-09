package com.example.kinotick.moviebd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kinotick.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MovieDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movie_database.db";
    private static final int DATABASE_VERSION = 2;

    // Название таблицы и столбцов
    public static final String TABLE_MOVIES = "movies";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_RATING = "rating";
    public static final String COLUMN_POSTER_URL = "poster_url";
    public static final String COLUMN_RELEASE_DATE = "release_date";

    // SQL для создания таблицы
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_MOVIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT NOT NULL, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_GENRE + " TEXT, " +
                    COLUMN_DURATION + " INTEGER, " +
                    COLUMN_RATING + " REAL, " +
                    COLUMN_POSTER_URL + " TEXT, " +
                    COLUMN_RELEASE_DATE + " INTEGER, " +
                    "status TEXT, " +
                    "show_dates TEXT);"; // Даты храним как JSON-массив
    public MovieDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }


    // Добавление нового фильма
    public long addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_DESCRIPTION, movie.getDescription());
        values.put(COLUMN_GENRE, movie.getGenre());
        values.put(COLUMN_DURATION, movie.getDuration());
        values.put(COLUMN_RATING, movie.getRating());
        values.put(COLUMN_POSTER_URL, movie.getPosterUrl());
        values.put(COLUMN_RELEASE_DATE, movie.getReleaseDate().getTime());

        // Добавляем статус
        values.put("status", movie.getStatus());

        // Сериализуем даты показа в JSON
        if (movie.getShowDates() != null && !movie.getShowDates().isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            JSONArray datesArray = new JSONArray();
            for (Date date : movie.getShowDates()) {
                datesArray.put(dateFormat.format(date));
            }
            values.put("show_dates", datesArray.toString());
        } else {
            values.put("show_dates", "[]"); // Пустой массив JSON
        }

        long id = db.insert(TABLE_MOVIES, null, values);
        db.close();
        return id;
    }

    // Получение всех фильмов
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_MOVIES, null, null, null, null, null, COLUMN_TITLE + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie();
                    movie.setId(getIntFromCursor(cursor, COLUMN_ID));
                    movie.setTitle(getStringFromCursor(cursor, COLUMN_TITLE));
                    movie.setDescription(getStringFromCursor(cursor, COLUMN_DESCRIPTION));
                    movie.setGenre(getStringFromCursor(cursor, COLUMN_GENRE));
                    movie.setDuration(getIntFromCursor(cursor, COLUMN_DURATION));
                    movie.setRating(getDoubleFromCursor(cursor, COLUMN_RATING));
                    movie.setPosterUrl(getStringFromCursor(cursor, COLUMN_POSTER_URL));

                    // Чтение release_date
                    long dateMillis = getLongFromCursor(cursor, COLUMN_RELEASE_DATE);
                    if (dateMillis != -1) {
                        movie.setReleaseDate(new Date(dateMillis));
                    }

                    // Чтение status
                    movie.setStatus(getStringFromCursor(cursor, "status"));

                    // Чтение show_dates
                    String showDatesJson = getStringFromCursor(cursor, "show_dates");
                    if (showDatesJson != null) {
                        try {
                            JSONArray datesArray = new JSONArray(showDatesJson);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            List<Date> showDates = new ArrayList<>();
                            for (int i = 0; i < datesArray.length(); i++) {
                                showDates.add(dateFormat.parse(datesArray.getString(i)));
                            }
                            movie.setShowDates(showDates);
                        } catch (Exception e) {
                            Log.e("MovieDB", "Error parsing show_dates", e);
                        }
                    }
                    Log.d("MovieDB", "Loaded movie: " + movie.getTitle()
                            + ", status=" + movie.getStatus()
                            + ", dates=" + movie.getShowDates().size());

                    movies.add(movie);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return movies;
    }

    // Вспомогательные методы для безопасного чтения данных
    private String getStringFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index != -1 ? cursor.getString(index) : null;
    }

    private int getIntFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index != -1 ? cursor.getInt(index) : -1;
    }

    private double getDoubleFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index != -1 ? cursor.getDouble(index) : -1;
    }

    private long getLongFromCursor(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        return index != -1 ? cursor.getLong(index) : -1;
    }

    public void initializeFromJson(Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MOVIES); // Очищаем таблицу

        try {
            InputStream is = context.getResources().openRawResource(R.raw.movies);
            String json = new Scanner(is).useDelimiter("\\A").next();
            JSONArray moviesArray = new JSONArray(json);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            for (int i = 0; i < moviesArray.length(); i++) {
                JSONObject movieObj = moviesArray.getJSONObject(i);

                Movie movie = new Movie();
                movie.setTitle(movieObj.getString("title"));
                movie.setDescription(movieObj.getString("description"));
                movie.setGenre(movieObj.getString("genre"));
                movie.setDuration(movieObj.getInt("duration"));
                movie.setRating(movieObj.getDouble("rating"));
                movie.setPosterUrl(movieObj.getString("poster_url"));
                if (movieObj.has("status")) {
                    movie.setStatus(movieObj.getString("status"));
                }
                // Парсим release_date
                Date releaseDate = dateFormat.parse(movieObj.getString("release_date"));
                movie.setReleaseDate(releaseDate);

                // Парсим status и show_dates (если есть)
                if (movieObj.has("show_dates")) {
                    JSONArray datesArray = movieObj.getJSONArray("show_dates");
                    List<Date> showDates = new ArrayList<>();
                    for (int j = 0; j < datesArray.length(); j++) {
                        showDates.add(dateFormat.parse(datesArray.getString(j)));
                    }
                    movie.setShowDates(showDates);
                }

                addMovie(movie);
            }
        } catch (Exception e) {
            Log.e("MovieDB", "Error initializing from JSON", e);
        } finally {
            db.close();
        }
    }
}
