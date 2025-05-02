package com.example.kinotick.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kinotick.R;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {

    private EditText firstNameInput, lastNameInput, middleNameInput, otherGenreInput;
    private CheckBox actionCheck, comedyCheck, dramaCheck, fantasyCheck,
            horrorCheck, romanceCheck, scifiCheck, thrillerCheck, otherCheck;
    private LinearLayout otherGenreContainer;
    private SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPref = requireActivity().getSharedPreferences("user_prefs", 0);

        // Инициализация полей ввода
        firstNameInput = view.findViewById(R.id.first_name_input);
        lastNameInput = view.findViewById(R.id.last_name_input);
        middleNameInput = view.findViewById(R.id.middle_name_input);
        otherGenreInput = view.findViewById(R.id.other_genre_input);
        otherGenreContainer = view.findViewById(R.id.other_genre_container);

        // Инициализация чекбоксов жанров
        actionCheck = view.findViewById(R.id.genre_action);
        comedyCheck = view.findViewById(R.id.genre_comedy);
        dramaCheck = view.findViewById(R.id.genre_drama);
        fantasyCheck = view.findViewById(R.id.genre_fantasy);
        horrorCheck = view.findViewById(R.id.genre_horror);
        romanceCheck = view.findViewById(R.id.genre_romance);
        scifiCheck = view.findViewById(R.id.genre_scifi);
        thrillerCheck = view.findViewById(R.id.genre_thriller);
        otherCheck = view.findViewById(R.id.genre_other);

        // Обработчик для чекбокса "Другое"
        otherCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            otherGenreContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) otherGenreInput.setText("");
        });

        Button saveButton = view.findViewById(R.id.save_button);

        // Загрузка сохраненных данных
        loadProfileData();

        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void loadProfileData() {
        if (sharedPref.getBoolean("is_logged_in", false)) {
            firstNameInput.setText(sharedPref.getString("first_name", ""));
            lastNameInput.setText(sharedPref.getString("last_name", ""));
            middleNameInput.setText(sharedPref.getString("middle_name", ""));

            try {
                JSONArray genresArray = new JSONArray(sharedPref.getString("fav_genres", "[]"));
                for (int i = 0; i < genresArray.length(); i++) {
                    String genre = genresArray.getString(i);

                    if (genre.startsWith("other:")) {
                        otherCheck.setChecked(true);
                        otherGenreInput.setText(genre.substring(6));
                        continue;
                    }

                    switch (genre) {
                        case "action": actionCheck.setChecked(true); break;
                        case "comedy": comedyCheck.setChecked(true); break;
                        case "drama": dramaCheck.setChecked(true); break;
                        case "fantasy": fantasyCheck.setChecked(true); break;
                        case "horror": horrorCheck.setChecked(true); break;
                        case "romance": romanceCheck.setChecked(true); break;
                        case "scifi": scifiCheck.setChecked(true); break;
                        case "thriller": thrillerCheck.setChecked(true); break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveProfile() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String middleName = middleNameInput.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            // Показываем ошибку, если обязательные поля не заполнены
            return;
        }

        // Собираем выбранные жанры
        List<String> selectedGenres = new ArrayList<>();
        if (actionCheck.isChecked()) selectedGenres.add("action");
        if (comedyCheck.isChecked()) selectedGenres.add("comedy");
        if (dramaCheck.isChecked()) selectedGenres.add("drama");
        if (fantasyCheck.isChecked()) selectedGenres.add("fantasy");
        if (horrorCheck.isChecked()) selectedGenres.add("horror");
        if (romanceCheck.isChecked()) selectedGenres.add("romance");
        if (scifiCheck.isChecked()) selectedGenres.add("scifi");
        if (thrillerCheck.isChecked()) selectedGenres.add("thriller");

        // Обработка "Другого" жанра
        if (otherCheck.isChecked()) {
            String otherGenre = otherGenreInput.getText().toString().trim();
            if (!otherGenre.isEmpty()) {
                selectedGenres.add("other:" + otherGenre);
            }
        }

        // Сохраняем данные
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("is_logged_in", true);
        editor.putString("first_name", firstName);
        editor.putString("last_name", lastName);
        if (!middleName.isEmpty()) editor.putString("middle_name", middleName);
        editor.putString("fav_genres", new JSONArray(selectedGenres).toString());
        editor.apply();

        // Возвращаемся назад
        getParentFragmentManager().popBackStack();
    }
}