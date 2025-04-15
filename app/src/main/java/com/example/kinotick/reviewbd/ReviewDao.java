package com.example.kinotick.reviewbd;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Query("SELECT * FROM reviews WHERE movieTitle = :movieTitle")
    List<Review> getReviewsForMovie(String movieTitle);

    @Query("SELECT * FROM reviews ORDER BY date DESC LIMIT 10")
    List<Review> getAllReviews();

    @Query("DELETE FROM reviews")
    void deleteAll();


}
