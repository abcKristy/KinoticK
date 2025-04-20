package com.example.kinotick.reviewbd;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.kinotick.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.Executors;

@Database(entities = {Review.class}, version = 1)
public abstract class ReviewDatabaseHelper extends RoomDatabase {
    public abstract ReviewDao reviewDao();

    private static volatile ReviewDatabaseHelper instance;

    public static synchronized ReviewDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ReviewDatabaseHelper.class,
                    "reviews_database"
            ).build();
            loadReviewsFromJson(context);  // Загрузка данных при создании БД
        }
        return instance;
    }

    private static void loadReviewsFromJson(Context context) {
        new Thread(() -> {
            try {
                Resources res = context.getResources();
                InputStream is = res.openRawResource(R.raw.review);
                String jsonString = new Scanner(is).useDelimiter("\\A").next();
                JSONArray reviewsArray = new JSONArray(jsonString);

                ReviewDao dao = instance.reviewDao();
                dao.deleteAll();  // Очистка старых данных

                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject reviewJson = reviewsArray.getJSONObject(i);
                    Review review = new Review();
                    review.setMovieTitle(reviewJson.getString("movieTitle"));
                    review.setAuthor(reviewJson.getString("author"));
                    review.setText(reviewJson.getString("text"));
                    review.setRating((float) reviewJson.getDouble("rating"));
                    review.setDate(reviewJson.getString("date"));
                    dao.insert(review);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void addReviewToJson(Context context, Review newReview) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // 1. Проверяем существование файла во внутреннем хранилище
                File internalFile = new File(context.getFilesDir(), "reviews.json");
                JSONArray reviewsArray;

                // 2. Если файл существует - читаем его, иначе читаем из raw ресурсов
                if (internalFile.exists()) {
                    String jsonString = new Scanner(internalFile).useDelimiter("\\A").next();
                    reviewsArray = new JSONArray(jsonString);
                } else {
                    // Читаем из ресурсов при первом запуске
                    InputStream is = context.getResources().openRawResource(R.raw.review);
                    String jsonString = new Scanner(is).useDelimiter("\\A").next();
                    reviewsArray = new JSONArray(jsonString);
                    is.close();
                }

                // 3. Добавляем новый отзыв
                JSONObject reviewJson = new JSONObject();
                reviewJson.put("movieTitle", newReview.getMovieTitle());
                reviewJson.put("author", newReview.getAuthor());
                reviewJson.put("text", newReview.getText());
                reviewJson.put("rating", newReview.getRating());
                reviewJson.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

                reviewsArray.put(reviewJson);

                // 4. Сохраняем ОБНОВЛЕННЫЕ данные во внутреннее хранилище
                FileWriter writer = new FileWriter(internalFile);
                writer.write(reviewsArray.toString());
                writer.close();

                // 5. Добавляем в Room
                reviewDao().insert(newReview);

                Log.d("ReviewDB", "Review added to: " + internalFile.getAbsolutePath());

            } catch (Exception e) {
                Log.e("ReviewDB", "Error saving review", e);
            }
        });
    }
}
