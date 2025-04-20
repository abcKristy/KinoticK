package com.example.kinotick.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kinotick.R;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Получаем данные из Intent
        Intent intent = getIntent();
        String movieName = intent.getStringExtra("movieName");
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String fio = intent.getStringExtra("fio");
        String notes = intent.getStringExtra("notes");

        // Находим View элементы
        TextView movieTextView = findViewById(R.id.movieTextView);
        TextView dateTextView = findViewById(R.id.dateTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        TextView fioTextView = findViewById(R.id.fioTextView);
        TextView notesTextView = findViewById(R.id.notesTextView);

        // Устанавливаем значения
        movieTextView.setText(movieName);
        dateTextView.setText(date);
        timeTextView.setText(time);
        fioTextView.setText(fio);

        if (notes != null && !notes.isEmpty()) {
            notesTextView.setText(notes);
        } else {
            notesTextView.setVisibility(View.GONE);
            findViewById(R.id.notesLabel).setVisibility(View.GONE);
        }
    }
}