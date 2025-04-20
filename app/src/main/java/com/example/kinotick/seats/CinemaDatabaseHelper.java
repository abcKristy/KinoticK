package com.example.kinotick.seats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CinemaDatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "cinema.db";
    public static final int DATABASE_VERSION = 1;

    // Название таблицы и столбцов
    public static final String TABLE_TICKETS = "tickets";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MOVIE_NAME = "movie_name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_ROW = "row_number";
    public static final String COLUMN_SEAT = "seat_number";
    public static final String COLUMN_IS_SOLD = "is_sold";

    // SQL запрос для создания таблицы
    private static final String CREATE_TABLE_TICKETS = "CREATE TABLE " + TABLE_TICKETS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MOVIE_NAME + " TEXT,"
            + COLUMN_DATE + " TEXT,"
            + COLUMN_TIME + " TEXT,"
            + COLUMN_ROW + " INTEGER,"
            + COLUMN_SEAT + " INTEGER,"
            + COLUMN_IS_SOLD + " INTEGER"
            + ")";

    public CinemaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TICKETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        onCreate(db);
    }

    // Добавление нового билета
    public long addTicket(MovieTicket ticket) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_MOVIE_NAME, ticket.getMovieName());
        values.put(COLUMN_DATE, ticket.getDate());
        values.put(COLUMN_TIME, ticket.getTime());
        values.put(COLUMN_ROW, ticket.getRow());
        values.put(COLUMN_SEAT, ticket.getSeat());
        values.put(COLUMN_IS_SOLD, ticket.isSold() ? 1 : 0);

        long id = db.insert(TABLE_TICKETS, null, values);
        db.close();
        return id;
    }

    // Покупка билета (изменение статуса места на "продано")
    public boolean buyTicket(String movieName, String date, String time, int row, int seat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_SOLD, 1);

        int rowsAffected = db.update(TABLE_TICKETS, values,
                COLUMN_MOVIE_NAME + " = ? AND " +
                        COLUMN_DATE + " = ? AND " +
                        COLUMN_TIME + " = ? AND " +
                        COLUMN_ROW + " = ? AND " +
                        COLUMN_SEAT + " = ?",
                new String[]{movieName, date, time, String.valueOf(row), String.valueOf(seat)});

        db.close();
        return rowsAffected > 0;
    }

    // Получение всех билетов для конкретного сеанса
    public List<MovieTicket> getTicketsForSession(String movieName, String date, String time) {
        List<MovieTicket> tickets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TICKETS,
                new String[]{COLUMN_MOVIE_NAME, COLUMN_DATE, COLUMN_TIME, COLUMN_ROW, COLUMN_SEAT, COLUMN_IS_SOLD},
                COLUMN_MOVIE_NAME + " = ? AND " + COLUMN_DATE + " = ? AND " + COLUMN_TIME + " = ?",
                new String[]{movieName, date, time},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                MovieTicket ticket = new MovieTicket(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5) == 1
                );
                tickets.add(ticket);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tickets;
    }

    // Проверка, свободно ли место
    public boolean isSeatAvailable(String movieName, String date, String time, int row, int seat) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TICKETS,
                new String[]{COLUMN_IS_SOLD},
                COLUMN_MOVIE_NAME + " = ? AND " + COLUMN_DATE + " = ? AND " + COLUMN_TIME + " = ? AND " +
                        COLUMN_ROW + " = ? AND " + COLUMN_SEAT + " = ?",
                new String[]{movieName, date, time, String.valueOf(row), String.valueOf(seat)},
                null, null, null);

        boolean isAvailable = false;
        if (cursor.moveToFirst()) {
            isAvailable = cursor.getInt(0) == 0;
        }
        cursor.close();
        db.close();
        return isAvailable;
    }

    // Наполнение базы данных тестовыми данными
    public void populateDatabaseWithTestData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Очищаем таблицу перед добавлением новых данных
            db.delete(TABLE_TICKETS, null, null);

            // Добавляем тестовые данные для нескольких фильмов и сеансов
            String[] movieNames = {"Интерстеллар", "Начало", "Матрица", "Крестный отец"};
            String[] dates = {"2023-12-01", "2023-12-02", "2023-12-03"};
            String[] times = {"10:00", "14:00", "18:00", "22:00"};

            // Для каждого фильма, даты и времени создаем зал (10 рядов по 20 мест)
            for (String movie : movieNames) {
                for (String date : dates) {
                    for (String time : times) {
                        for (int row = 1; row <= 10; row++) {
                            for (int seat = 1; seat <= 17; seat++) {
                                // Случайным образом помечаем некоторые места как проданные
                                boolean isSold = Math.random() > 0.7;

                                ContentValues values = new ContentValues();
                                values.put(COLUMN_MOVIE_NAME, movie);
                                values.put(COLUMN_DATE, date);
                                values.put(COLUMN_TIME, time);
                                values.put(COLUMN_ROW, row);
                                values.put(COLUMN_SEAT, seat);
                                values.put(COLUMN_IS_SOLD, isSold ? 1 : 0);

                                db.insert(TABLE_TICKETS, null, values);
                            }
                        }
                    }
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<MovieTicket> getAllTicketsFromDatabase() {
        List<MovieTicket> tickets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TICKETS,
                new String[]{COLUMN_MOVIE_NAME, COLUMN_DATE, COLUMN_TIME, COLUMN_ROW, COLUMN_SEAT, COLUMN_IS_SOLD},
                null, null, null, null,
                COLUMN_MOVIE_NAME + ", " + COLUMN_DATE + ", " + COLUMN_TIME + ", " + COLUMN_ROW + ", " + COLUMN_SEAT);

        if (cursor.moveToFirst()) {
            do {
                MovieTicket ticket = new MovieTicket(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4),
                        cursor.getInt(5) == 1
                );
                tickets.add(ticket);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tickets;
    }
    // Новый метод для получения всех уникальных названий фильмов
    public List<String> getAllMovieNames() {
        Set<String> movieNamesSet = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_TICKETS,
                new String[]{COLUMN_MOVIE_NAME},
                null, null,
                COLUMN_MOVIE_NAME, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                movieNamesSet.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return new ArrayList<>(movieNamesSet);
    }
    public List<String> getDatesForMovie(String movieName) {
        Set<String> datesSet = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_TICKETS,
                new String[]{COLUMN_DATE},
                COLUMN_MOVIE_NAME + " = ?",
                new String[]{movieName},
                COLUMN_DATE, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                datesSet.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return new ArrayList<>(datesSet);
    }
    // Метод для получения времени сеансов для конкретного фильма и даты
    public List<String> getTimesForMovieAndDate(String movieName, String date) {
        Set<String> timesSet = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(true, TABLE_TICKETS,
                new String[]{COLUMN_TIME},
                COLUMN_MOVIE_NAME + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{movieName, date},
                COLUMN_TIME, null, COLUMN_TIME + " ASC", null);

        if (cursor.moveToFirst()) {
            do {
                timesSet.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return new ArrayList<>(timesSet);
    }

}