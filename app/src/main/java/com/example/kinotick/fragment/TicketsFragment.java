package com.example.kinotick.fragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.kinotick.R;
import com.example.kinotick.seats.CinemaDatabaseHelper;
import com.example.kinotick.seats.MovieTicket;

import java.util.ArrayList;
import java.util.List;

public class TicketsFragment extends Fragment {
    private CinemaDatabaseHelper dbHelper;
    private TextView ticketsTextView;
    private static final String PREFS_NAME = "CinemaPrefs";
    private static final String PREF_DB_INITIALIZED = "db_initialized";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);

        dbHelper = new CinemaDatabaseHelper(getActivity());
        ticketsTextView = view.findViewById(R.id.ticketsTextView);

        // Проверяем, была ли уже инициализирована БД
        boolean dbInitialized = getActivity()
                .getSharedPreferences(PREFS_NAME, 0)
                .getBoolean(PREF_DB_INITIALIZED, false);

        if (!dbInitialized) {
            // Заполняем БД тестовыми данными только при первом запуске
            dbHelper.populateDatabaseWithTestData();
            getActivity()
                    .getSharedPreferences(PREFS_NAME, 0)
                    .edit()
                    .putBoolean(PREF_DB_INITIALIZED, true)
                    .apply();
        }

        Button refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> refreshTicketsList());

        // Первоначальная загрузка данных
        refreshTicketsList();

        return view;
    }

    private void refreshTicketsList() {
        // Получаем все билеты из базы данных
        List<MovieTicket> allTickets = dbHelper.getAllTicketsFromDatabase();

        if (allTickets.isEmpty()) {
            ticketsTextView.setText("В базе данных нет билетов");
            return;
        }

        // Форматируем вывод
        StringBuilder sb = new StringBuilder();
        String currentMovie = "";
        String currentDate = "";
        String currentTime = "";

        for (MovieTicket ticket : allTickets) {
            // Группируем по фильму, дате и времени
            if (!ticket.getMovieName().equals(currentMovie) ||
                    !ticket.getDate().equals(currentDate) ||
                    !ticket.getTime().equals(currentTime)) {

                currentMovie = ticket.getMovieName();
                currentDate = ticket.getDate();
                currentTime = ticket.getTime();

                sb.append("\n\n=== ").append(currentMovie)
                        .append(" (").append(currentDate).append(", ").append(currentTime).append(") ===\n");
            }

            sb.append("Ряд ").append(ticket.getRow())
                    .append(", место ").append(ticket.getSeat())
                    .append(" - ").append(ticket.isSold() ? "ПРОДАНО" : "свободно")
                    .append("\n");
        }

        ticketsTextView.setText(sb.toString());
    }

    @Override
    public void onDestroyView() {
        dbHelper.close();
        super.onDestroyView();
    }
}