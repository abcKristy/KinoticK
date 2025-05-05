package com.example.kinotick.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kinotick.R;
import com.example.kinotick.tickets.Ticket;
import com.example.kinotick.tickets.TicketsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private static RecyclerView ticketsRecyclerView;
    private static TicketsAdapter ticketsAdapter;
    private static List<Ticket> ticketList = new ArrayList<>();

    private static final String TICKETS_KEY = "user_tickets";
    private static SharedPreferences sharedPref;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = requireActivity().getSharedPreferences("user_prefs", 0);
        loadTicketsFromStorage();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = requireActivity().getSharedPreferences("user_prefs", 0);

        TextView nameText = view.findViewById(R.id.name_text);
        TextView genresText = view.findViewById(R.id.genres_text);
        Button editButton = view.findViewById(R.id.edit_button);
        Button logoutButton = view.findViewById(R.id.logout_button);

        // Формируем полное имя
        String fullName;
        if (sharedPref.getString("middle_name", "").isEmpty())
            fullName = sharedPref.getString("last_name", "") + " " +
                    sharedPref.getString("first_name", "") +
                    "";
        else fullName = sharedPref.getString("last_name", "") + " " +
                sharedPref.getString("first_name", "") +
                " " +
                sharedPref.getString("middle_name", "");
        nameText.setText(fullName.trim());

        // Формируем список жанров
        List<String> genresList = new ArrayList<>();
        try {
            JSONArray genresArray = new JSONArray(sharedPref.getString("fav_genres", "[]"));
            for (int i = 0; i < genresArray.length(); i++) {
                String genre = genresArray.getString(i);
                genresList.add(getGenreDisplayName(genre));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        genresText.setText("Любимые жанры: " + String.join(", ", genresList));

        editButton.setOnClickListener(v -> openEditProfile());
        logoutButton.setOnClickListener(v -> logout());
        TextView birthDateText = view.findViewById(R.id.birth_date_text);
        String birthDate = sharedPref.getString("birth_date", "");
        if (!birthDate.isEmpty()) {
            birthDateText.setText("Дата рождения: " + birthDate);
            birthDateText.setVisibility(View.VISIBLE);
        } else {
            birthDateText.setVisibility(View.GONE);
        }
        ImageView avatarImage = view.findViewById(R.id.avatar_image);
        int avatarId = sharedPref.getInt("avatar_id", 1);
        int resId = getResources().getIdentifier(
                "ava" + avatarId,
                "drawable",
                requireContext().getPackageName()
        );
        avatarImage.setImageResource(resId);

        // Инициализация RecyclerView
        ticketsRecyclerView = view.findViewById(R.id.tickets_recycler);
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ticketsAdapter = new TicketsAdapter(ticketList);
        ticketsRecyclerView.setAdapter(ticketsAdapter);
    }

    public static void saveTicketsToStorage() {
        JSONArray ticketsArray = new JSONArray();
        for (Ticket ticket : ticketList) {
            ticketsArray.put(ticket.toJson());
        }

        sharedPref.edit()
                .putString(TICKETS_KEY, ticketsArray.toString())
                .apply();
    }

    private void loadTicketsFromStorage() {
        String ticketsJson = sharedPref.getString(TICKETS_KEY, "[]");
        try {
            JSONArray ticketsArray = new JSONArray(ticketsJson);
            ticketList.clear();

            for (int i = 0; i < ticketsArray.length(); i++) {
                Ticket ticket = Ticket.fromJson(ticketsArray.getString(i));
                ticketList.add(ticket);
            }

            if (ticketsAdapter != null) {
                ticketsAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Функция для добавления нового билета
    public static void pushToProfile(Ticket ticket) {
        ticketList.add(0, ticket);
        ticketsAdapter.notifyItemInserted(0);
        ticketsRecyclerView.smoothScrollToPosition(0);
        saveTicketsToStorage(); // Сохраняем после добавления
    }
    private String getGenreDisplayName(String genreKey) {
        if (genreKey.startsWith("other:")) {
            return genreKey.substring(6);
        }

        switch (genreKey) {
            case "action": return "Боевик";
            case "comedy": return "Комедия";
            case "drama": return "Драма";
            case "fantasy": return "Фэнтези";
            case "horror": return "Ужасы";
            case "romance": return "Мелодрама";
            case "scifi": return "Фантастика";
            case "thriller": return "Триллер";
            default: return genreKey;
        }
    }

    private void openEditProfile() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        getParentFragmentManager().popBackStack();
    }
    public static ProfileFragment newInstance(String userName) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("user_name", userName);
        fragment.setArguments(args);
        return fragment;
    }
}