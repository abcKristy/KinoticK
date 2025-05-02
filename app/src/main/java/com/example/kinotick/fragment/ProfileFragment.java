package com.example.kinotick.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kinotick.R;

public class ProfileFragment extends Fragment {

    private static final String ARG_USER_NAME = "user_name";
    private SharedPreferences sharedPref;

    public static ProfileFragment newInstance(String userName) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
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
        String userName = getArguments() != null ? getArguments().getString(ARG_USER_NAME) : "";

        TextView nameText = view.findViewById(R.id.name_text);
        Button editButton = view.findViewById(R.id.edit_button);
        Button logoutButton = view.findViewById(R.id.logout_button);

        nameText.setText(userName);

        editButton.setOnClickListener(v -> {
            // Переход в режим редактирования
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .addToBackStack(null)
                    .commit();
        });

        logoutButton.setOnClickListener(v -> {
            // Выход из профиля
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            // Возврат на экран входа
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        });
    }
}