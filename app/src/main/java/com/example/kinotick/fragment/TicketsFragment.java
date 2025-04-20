package com.example.kinotick.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.example.kinotick.OrderActivity;
import com.example.kinotick.R;
import com.example.kinotick.seats.CinemaDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class TicketsFragment extends Fragment {
    private CinemaDatabaseHelper dbHelper;
    private AutoCompleteTextView moviesDropdown;
    private AutoCompleteTextView datesDropdown;
    private AutoCompleteTextView timesDropdown;
    private TextInputEditText fioEditText;
    private TextInputEditText notesEditText;
    private MaterialButton continueButton;
    private View dateLayout;
    private View timeLayout;
    private String selectedMovie;
    private String selectedDate;
    private String selectedTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);

        // Инициализация базы данных
        dbHelper = new CinemaDatabaseHelper(getContext());

        // Находим элементы UI
        moviesDropdown = view.findViewById(R.id.moviesDropdown);
        datesDropdown = view.findViewById(R.id.datesDropdown);
        timesDropdown = view.findViewById(R.id.timesDropdown);
        fioEditText = view.findViewById(R.id.fioEditText);
        notesEditText = view.findViewById(R.id.notesEditText);
        continueButton = view.findViewById(R.id.continueButton);
        dateLayout = view.findViewById(R.id.dateLayout);
        timeLayout = view.findViewById(R.id.timeLayout);

        // Заполняем выпадающий список фильмами
        populateMoviesDropdown();

        // Обработчик выбора фильма
        moviesDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedMovie = (String) parent.getItemAtPosition(position);
            populateDatesDropdown(selectedMovie);
            dateLayout.setVisibility(View.VISIBLE);
            timeLayout.setVisibility(View.GONE);
            checkFormCompletion();
        });

        // Обработчик выбора даты
        datesDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedDate = (String) parent.getItemAtPosition(position);
            populateTimesDropdown(selectedMovie, selectedDate);
            timeLayout.setVisibility(View.VISIBLE);
            checkFormCompletion();
        });

        // Обработчик выбора времени
        timesDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            selectedTime = (String) parent.getItemAtPosition(position);
            checkFormCompletion();
        });

        // Слушатель изменений текста для ФИО
        fioEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFormCompletion();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Обработчик нажатия кнопки
        continueButton.setOnClickListener(v -> {
            String fio = fioEditText.getText().toString().trim();
            String notes = notesEditText.getText().toString().trim();

            // Создаем Intent для перехода на OrderActivity
            Intent intent = new Intent(getActivity(), OrderActivity.class);

            // Передаем данные
            intent.putExtra("movieName", selectedMovie);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            intent.putExtra("fio", fio);
            intent.putExtra("notes", notes);

            // Запускаем активность
            startActivity(intent);
        });

        return view;
    }

    private void populateMoviesDropdown() {
        List<String> movieNames = dbHelper.getAllMovieNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                movieNames);
        moviesDropdown.setAdapter(adapter);
    }

    private void populateDatesDropdown(String movieName) {
        List<String> dates = dbHelper.getDatesForMovie(movieName);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                dates);
        datesDropdown.setAdapter(adapter);
        datesDropdown.setText("", false);
    }

    private void populateTimesDropdown(String movieName, String date) {
        List<String> times = dbHelper.getTimesForMovieAndDate(movieName, date);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                times);
        timesDropdown.setAdapter(adapter);
        timesDropdown.setText("", false);
    }

    private void checkFormCompletion() {
        boolean isFioFilled = !fioEditText.getText().toString().trim().isEmpty();
        boolean isMovieSelected = selectedMovie != null && !selectedMovie.isEmpty();
        boolean isDateSelected = selectedDate != null && !selectedDate.isEmpty();
        boolean isTimeSelected = selectedTime != null && !selectedTime.isEmpty();

        continueButton.setEnabled(isFioFilled && isMovieSelected && isDateSelected && isTimeSelected);
    }

    @Override
    public void onDestroyView() {
        dbHelper.close();
        super.onDestroyView();
    }
}