package com.example.kinotick.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kinotick.R;
import com.example.kinotick.reviewbd.Review;
import com.example.kinotick.reviewbd.ReviewAdapter;
import com.example.kinotick.reviewbd.ReviewDatabaseHelper;

import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private RecyclerView reviewsRecycler;
    private ReviewAdapter reviewAdapter;
    private Button findMovieButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Настройка RecyclerView - теперь вертикальный
        reviewsRecycler = view.findViewById(R.id.reviews_recycler);
        reviewsRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext()) // По умолчанию вертикальный
        );

        // Остальной код без изменений
        setupReviewsRecycler();
        loadReviews();

        findMovieButton = view.findViewById(R.id.findMovieButton);
        findMovieButton.setOnClickListener(v -> showRandomPrediction());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadReviews();
    }

    private void setupReviewsRecycler() {
        // Добавляем разделитель между элементами
        DividerItemDecoration divider = new DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.HORIZONTAL
        );
        divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.divider));
        reviewsRecycler.addItemDecoration(divider);

        // Оптимизация
        reviewsRecycler.setHasFixedSize(true);
        reviewsRecycler.setItemViewCacheSize(20);
    }

    private void loadReviews() {
        ReviewDatabaseHelper.getInstance(requireContext())
                .reviewDao()
                .getAllReviews()
                .observe(getViewLifecycleOwner(), reviews -> {
                    if (reviews != null && !reviews.isEmpty()) {
                        reviewAdapter = new ReviewAdapter(reviews);
                        reviewsRecycler.setAdapter(reviewAdapter);

                        // Прокрутка к началу (опционально)
                        reviewsRecycler.scrollToPosition(0);

                        Log.d("ReviewDebug", "Loaded reviews: " + reviews.size());
                    } else {
                        Log.d("ReviewDebug", "No reviews found in database");
                    }
                });
    }

    private void showRandomPrediction() {
        // Получаем массив предсказаний
        String[] predictions = getResources().getStringArray(R.array.movie_predictions);
        int randomIndex = new Random().nextInt(predictions.length);

        // Создаем кастомный диалог
        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.TransparentDialog)
                .setView(R.layout.dialog_prediction) // Используем кастомный layout
                .setCancelable(true)
                .create();

        // Настройка прозрачного фона
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        // Инициализация элементов диалога
        dialog.setOnShowListener(dialogInterface -> {
            TextView predictionText = dialog.findViewById(R.id.predictionText);
            Button okButton = dialog.findViewById(R.id.okButton);
            Button tryAgainButton = dialog.findViewById(R.id.tryAgainButton);

            predictionText.setText(predictions[randomIndex]);

            okButton.setOnClickListener(v -> dialog.dismiss());
            tryAgainButton.setOnClickListener(v -> {
                dialog.dismiss();
                showRandomPrediction();
            });
        });

        dialog.show();
    }

}
