package com.example.kinotick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Locale;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final List<Movie> movies;
    private final Context context;

    public MovieAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.rating.setText(String.format(Locale.getDefault(), "%.1f", movie.getRating()));
        holder.date.setText(movie.getFormattedDate());
        holder.description.setText(movie.getDescription());

        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        public ImageView poster;
        public TextView title, genre, rating, date, description;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.movie_poster);
            title = itemView.findViewById(R.id.movie_title);
            genre = itemView.findViewById(R.id.movie_genre);
            rating = itemView.findViewById(R.id.movie_rating);
            date = itemView.findViewById(R.id.movie_date);
            description = (TextView) itemView.findViewById(R.id.movie_description);
        }
    }
}