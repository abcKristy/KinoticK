package com.example.kinotick.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kinotick.R;

public class LoginFragment extends Fragment {

    private EditText nameInput;
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
        nameInput = view.findViewById(R.id.name_input);
        Button loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> {
            String userName = nameInput.getText().toString().trim();
            if (!userName.isEmpty()) {
                // Сохраняем данные пользователя
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("is_logged_in", true);
                editor.putString("user_name", userName);
                editor.apply();

                // Переходим на экран профиля
                showProfileFragment(userName);
            }
        });
    }

    private void showProfileFragment(String userName) {
        ProfileFragment profileFragment = ProfileFragment.newInstance(userName);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, profileFragment)
                .commit();
    }
}