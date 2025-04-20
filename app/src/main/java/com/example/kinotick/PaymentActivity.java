package com.example.kinotick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kinotick.seats.CinemaDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {

    private ArrayList<String> selectedSeats;
    private String selectedMovie;
    private String selectedDate;
    private String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Получаем данные из Intent
        Intent intent = getIntent();
        String fio = intent.getStringExtra("fio");
        String movie = intent.getStringExtra("movie");
        String dateTime = intent.getStringExtra("dateTime");
        List<String> seats = intent.getStringArrayListExtra("seats");

        selectedSeats = intent.getStringArrayListExtra("seats");
        selectedMovie = intent.getStringExtra("movie");
        selectedDate = intent.getStringExtra("date");
        selectedTime = intent.getStringExtra("time");

        // Рассчитываем сумму
        int ticketPrice = 500;
        int total = seats.size() * ticketPrice;

        // Находим View элементы
        TextView orderInfoTextView = findViewById(R.id.orderInfoTextView);
        TextView totalTextView = findViewById(R.id.totalTextView);

        // Формируем информацию о заказе
        StringBuilder orderInfo = new StringBuilder();
        orderInfo.append("ФИО: ").append(fio).append("\n")
                .append("Фильм: ").append(movie).append("\n")
                .append("Дата и время: ").append(dateTime).append("\n")
                .append("Места: ").append(String.join(", ", seats));

        // Устанавливаем значения
        orderInfoTextView.setText(orderInfo.toString());
        totalTextView.setText(String.format("Сумма платежа: %d руб.", total));

        // Находим View элементы
        Button cardPayButton = findViewById(R.id.cardPayButton);
        Button googlePayButton = findViewById(R.id.googlePayButton);
        Button applePayButton = findViewById(R.id.applePayButton);
        Button sbpPayButton = findViewById(R.id.sbpPayButton);
        Button paymentSuccessButton = findViewById(R.id.paymentSuccessButton);

        // Обработчики кнопок с переходами на котиков
        cardPayButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://findtheinvisiblecow.com/"));
            startActivity(browserIntent);
        });

        googlePayButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://endless.horse/?ref=vgtimes.ru&http://endless.horse/?ref=vgtimes.ru"));
            startActivity(browserIntent);
        });

        applePayButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://quickdraw.withgoogle.com/"));
            startActivity(browserIntent);
        });

        sbpPayButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.staggeringbeauty.com/?ref=vgtimes.ru&http://www.staggeringbeauty.com/?ref=vgtimes.ru"));
            startActivity(browserIntent);
        });

        // Кнопка подтверждения оплаты
        paymentSuccessButton.setOnClickListener(v -> {
            // Обновляем статус мест в БД
            updateSeatsStatus();

            // Возвращаемся на главный экран
            returnToMainActivity();
        });
    }

    private void updateSeatsStatus() {
        CinemaDatabaseHelper dbHelper = new CinemaDatabaseHelper(this);

        for (String seat : selectedSeats) {
            // Парсим ряд и место из строки "Ряд X, Место Y"
            String[] parts = seat.split(", ");
            int row = Integer.parseInt(parts[0].replace("Ряд ", ""));
            int seatNum = Integer.parseInt(parts[1].replace("Место ", ""));

            // Обновляем статус места в БД
            dbHelper.buyTicket(selectedMovie, selectedDate, selectedTime, row, seatNum);
        }

        dbHelper.close();
        Toast.makeText(this, "Места успешно забронированы!", Toast.LENGTH_SHORT).show();
    }

    private void returnToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}