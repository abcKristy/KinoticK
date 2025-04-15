package com.example.kinotick.reviewbd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kinotick.R;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        // Убедитесь, что все поля установлены
        holder.movieTitle.setText(review.getMovieTitle());
        holder.reviewText.setText(review.getText());
        if (review.getAuthor() != null) {
            holder.author.setText(review.getAuthor());
        } else {
            holder.author.setText("Неизвестный автор");
        }
        // Если rating - float:
        holder.rating.setText(String.format("Оценка: %.1f ★", review.getRating()));

        // Анимация
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                R.anim.fade_in);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle, reviewText, author, rating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.review_movie_title);
            reviewText = itemView.findViewById(R.id.review_text);
            author = itemView.findViewById(R.id.review_author);
            rating = itemView.findViewById(R.id.review_rating);
        }
    }
}
