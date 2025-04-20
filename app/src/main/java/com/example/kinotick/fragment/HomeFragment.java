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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kinotick.R;
import com.example.kinotick.reviewbd.Review;
import com.example.kinotick.reviewbd.ReviewAdapter;
import com.example.kinotick.reviewbd.ReviewDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

        // Находим кнопку добавления отзыва
        Button addReviewButton = view.findViewById(R.id.btn_add_review);

        // Реализуем обработчик нажатия
        addReviewButton.setOnClickListener(v -> {
            // Создаем и показываем диалог добавления отзыва
            showAddReviewDialog();
        });
    }

    private void showAddReviewDialog() {
        // 1. Создаем кастомный диалог
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_review, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // 2. Находим элементы диалога
        EditText etMovieTitle = dialogView.findViewById(R.id.etMovieTitle);
        EditText etAuthor = dialogView.findViewById(R.id.etAuthor);
        EditText etReviewText = dialogView.findViewById(R.id.etReviewText);
        RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        // 3. Обработчик кнопки "Добавить отзыв" внутри диалога
        btnSubmit.setOnClickListener(v -> {
            // Получаем данные из полей
            String movieTitle = etMovieTitle.getText().toString().trim();
            String author = etAuthor.getText().toString().trim();
            String reviewText = etReviewText.getText().toString().trim();
            float rating = ratingBar.getRating();

            // Валидация ввода
            if (movieTitle.isEmpty() || author.isEmpty() || reviewText.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаем новый отзыв
            Review newReview = new Review();
            newReview.setMovieTitle(movieTitle);
            newReview.setAuthor(author);
            newReview.setText(reviewText);
            newReview.setRating(rating);
            newReview.setDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

            // Вот этот вызов должен работать:
            ReviewDatabaseHelper.getInstance(requireContext())
                    .addReviewToJson(requireContext(), newReview);
            // Обновляем интерфейс
            requireActivity().runOnUiThread(() -> {
                loadReviews();
                Toast.makeText(requireContext(), "Отзыв добавлен!", Toast.LENGTH_SHORT).show();
            });

            dialog.dismiss();
        });

        // 4. Показываем диалог
        dialog.show();
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
