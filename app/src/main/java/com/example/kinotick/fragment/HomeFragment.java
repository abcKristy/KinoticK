package com.example.kinotick.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.kinotick.R;
import com.example.kinotick.reviewbd.Review;
import com.example.kinotick.reviewbd.ReviewAdapter;
import com.example.kinotick.reviewbd.ReviewDatabaseHelper;

import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView reviewsRecycler;
    private ReviewAdapter reviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Настройка RecyclerView
        reviewsRecycler = view.findViewById(R.id.reviews_recycler);
        reviewsRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        // Загрузка отзывов из БД
        loadReviews();

        return view;
    }

    private void loadReviews() {
        new Thread(() -> {
            // Получаем все отзывы из БД
            List<Review> reviews = ReviewDatabaseHelper
                    .getInstance(requireContext())
                    .reviewDao()
                    .getAllReviews();

            // Логируем данные для проверки
            for (Review review : reviews) {
                Log.d("ReviewDebug",
                        "Movie: " + review.getMovieTitle() +
                                ", Author: " + review.getAuthor() +
                                ", Rating: " + review.getRating() +
                                ", Text: " + review.getText());
            }

            // Обновляем UI в главном потоке
            requireActivity().runOnUiThread(() -> {
                reviewAdapter = new ReviewAdapter(reviews);
                reviewsRecycler.setAdapter(reviewAdapter);
            });
        }).start();
    }
}