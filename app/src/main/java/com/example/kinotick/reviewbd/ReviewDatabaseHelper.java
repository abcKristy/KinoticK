package com.example.kinotick.reviewbd;

import android.content.Context;
import android.content.res.Resources;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.kinotick.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

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
}
