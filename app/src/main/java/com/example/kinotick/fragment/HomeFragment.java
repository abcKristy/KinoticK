package com.example.kinotick.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.kinotick.R;

public class HomeFragment extends Fragment {
    private ProgressBar reviewsProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        reviewsProgressBar = view.findViewById(R.id.reviews_progress_bar);

        // Заглушка для будущей загрузки отзывов
        loadMockReviews();

        return view;
    }

    private void loadMockReviews() {
        // Позже заменим на реальные данные из ReviewDatabaseHelper
        new Handler().postDelayed(() -> {
            reviewsProgressBar.setVisibility(View.GONE);
            // Здесь будет RecyclerView с отзывами
        }, 1500);
    }
}