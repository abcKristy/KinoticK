package com.example.kinotick.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kinotick.moviebd.Movie;
import com.example.kinotick.moviebd.MovieAdapter;
import com.example.kinotick.moviebd.MovieDatabaseHelper;
import com.example.kinotick.R;

import java.util.List;

public class MoviesFragment extends Fragment {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private MovieDatabaseHelper dbHelper;
    private static final int PICK_IMAGE_REQUEST = 1;
    private int currentMoviePosition = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MovieDatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        recyclerView = view.findViewById(R.id.movies_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Для визуальной отладки
        recyclerView.setBackgroundColor(Color.LTGRAY);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализируем БД из JSON
        dbHelper.initializeFromJson(requireContext());

        // Загружаем фильмы
        List<Movie> movies = dbHelper.getAllMovies();
        adapter = new MovieAdapter(movies, getContext());
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}