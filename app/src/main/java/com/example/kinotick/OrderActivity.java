package com.example.kinotick;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kinotick.seats.CinemaDatabaseHelper;
import com.example.kinotick.seats.MovieTicket;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private CinemaDatabaseHelper dbHelper;
    private String selectedMovie;
    private String selectedDate;
    private String selectedTime;
    private TextView selectedSeatsInfo;
    private TextView errorMessage;
    private List<String> selectedSeats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Инициализация базы данных
        dbHelper = new CinemaDatabaseHelper(this);

        // Получаем данные из Intent
        Intent intent = getIntent();
        selectedMovie = intent.getStringExtra("movieName");
        selectedDate = intent.getStringExtra("date");
        selectedTime = intent.getStringExtra("time");
        String fio = intent.getStringExtra("fio");
        String notes = intent.getStringExtra("notes");

        // Находим View элементы
        TextView movieTextView = findViewById(R.id.movieTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        TextView fioTextView = findViewById(R.id.fioTextView);
        TextView notesTextView = findViewById(R.id.notesTextView);
        View notesContainer = findViewById(R.id.notesContainer);
        LinearLayout cinemaHallLayout = findViewById(R.id.cinemaHallLayout);
        Button confirmButton = findViewById(R.id.confirmButton);
        selectedSeatsInfo = findViewById(R.id.selectedSeatsInfo);
        errorMessage = findViewById(R.id.errorMessage);

        // Устанавливаем значения
        movieTextView.setText(selectedMovie);
        dateTextView.setText(selectedDate);
        timeTextView.setText(selectedTime);
        fioTextView.setText(fio);

        // Показываем пожелания, если они есть
        if (notes != null && !notes.isEmpty()) {
            notesTextView.setText(notes);
            notesContainer.setVisibility(View.VISIBLE);
        }

        // Создаем кинозал
        createCinemaHall(cinemaHallLayout, confirmButton);

        // Обработчик кнопки подтверждения
        confirmButton.setOnClickListener(v -> {
            // Получаем данные из полей
            String fioText = fioTextView.getText().toString();
            String dateTime = dateTextView.getText().toString() + " " + timeTextView.getText().toString();

            // Создаем Intent для перехода на PaymentActivity
            Intent paymentIntent = new Intent(OrderActivity.this, PaymentActivity.class);

            // Передаем данные
            paymentIntent.putExtra("fio", fioText);
            paymentIntent.putExtra("movie", selectedMovie);
            paymentIntent.putExtra("dateTime", dateTime);
            paymentIntent.putStringArrayListExtra("seats", new ArrayList<>(selectedSeats));

            // Запускаем активность
            startActivity(paymentIntent);
        });
    }
    private void createCinemaHall(LinearLayout cinemaHallLayout, Button confirmButton) {
        // Получаем актуальную информацию о занятых местах из БД
        List<MovieTicket> tickets = dbHelper.getTicketsForSession(selectedMovie, selectedDate, selectedTime);

        // Рассчитываем размер места
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int seatSize = (screenWidth - 100) / 20;

        // Создаем 10 рядов
        for (int row = 1; row <= 10; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            // Добавляем номер ряда
            TextView rowNumber = new TextView(this);
            rowNumber.setText(String.valueOf(row));
            rowNumber.setWidth(40);
            rowNumber.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            rowLayout.addView(rowNumber);

            // Создаем 20 мест в ряду
            for (int seat = 1; seat <= 17; seat++) {
                View seatView = new View(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(seatSize, seatSize);
                params.setMargins(2, 2, 2, 2);
                seatView.setLayoutParams(params);

                // Проверяем, занято ли место
                boolean isSeatTaken = isSeatTaken(tickets, row, seat);

                // Создаем форму квадрата
                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(4f);

                if (isSeatTaken) {
                    // Занятое место - темно-красное
                    shape.setColor(Color.parseColor("#B71C1C"));
                    seatView.setClickable(false);
                } else {
                    // Свободное место - светлое
                    shape.setColor(Color.parseColor("#EEEEEE"));
                    shape.setStroke(2, Color.parseColor("#9E9E9E"));

                    // Обработчик выбора места
                    int finalRow = row;
                    int finalSeat = seat;
                    seatView.setOnClickListener(v -> {
                        String seatInfo = "Ряд " + finalRow + ", Место " + finalSeat;

                        if (v.getTag() == null || !(boolean)v.getTag()) {
                            // Место выбрано
                            shape.setColor(Color.parseColor("#4CAF50"));
                            v.setTag(true);
                            selectedSeats.add(seatInfo);
                        } else {
                            // Место отменено
                            shape.setColor(Color.parseColor("#EEEEEE"));
                            shape.setStroke(2, Color.parseColor("#9E9E9E"));
                            v.setTag(false);
                            selectedSeats.remove(seatInfo);
                        }

                        // Обновляем информацию о выбранных местах
                        updateSelectedSeatsInfo();

                        // Проверяем, есть ли выбранные места
                        checkSelectedSeats(cinemaHallLayout, confirmButton);
                    });
                }

                seatView.setBackground(shape);
                rowLayout.addView(seatView);
            }

            cinemaHallLayout.addView(rowLayout);
        }
    }

    private void updateSelectedSeatsInfo() {
        if (selectedSeats.isEmpty()) {
            selectedSeatsInfo.setText("Выбранные места: нет");
        } else {
            StringBuilder sb = new StringBuilder("Выбранные места:\n");
            for (String seat : selectedSeats) {
                sb.append("• ").append(seat).append("\n");
            }
            selectedSeatsInfo.setText(sb.toString().trim());
        }
    }

    private boolean isSeatTaken(List<MovieTicket> tickets, int row, int seat) {
        for (MovieTicket ticket : tickets) {
            if (ticket.getRow() == row && ticket.getSeat() == seat && ticket.isSold()) {
                return true;
            }
        }
        return false;
    }

    private void checkSelectedSeats(LinearLayout cinemaHallLayout, Button confirmButton) {
        confirmButton.setEnabled(!selectedSeats.isEmpty());
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}