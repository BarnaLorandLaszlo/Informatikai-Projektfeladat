package com.example.filmajnlalkalmazs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class WatchlistActivity extends AppCompatActivity {

    private RecyclerView recyclerWatchlist;
    private MovieAdapter watchlistAdapter;
    private List<Movie> watchlistMovies = new ArrayList<>();
    private final String API_KEY = "c412675515f5ce72a5bbbd49cec4c943";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        TextView title = findViewById(R.id.textWatchlistTitle);
        recyclerWatchlist = findViewById(R.id.recyclerWatchlist);
        Button btnBack = findViewById(R.id.btnBack);

        // 4 oszlopos grid elrendezés
        recyclerWatchlist.setLayoutManager(new GridLayoutManager(this, 4));
        watchlistAdapter = new MovieAdapter(watchlistMovies);
        recyclerWatchlist.setAdapter(watchlistAdapter);

        btnBack.setOnClickListener(v -> finish());

        int userId = getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getInt("user_id", -1);
        if (userId != -1) {
            UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
            List<Integer> watchlistIds = dbHelper.getWatchlistMovieIds(userId);

            for (int id : watchlistIds) {
                fetchMovieById(id);
            }
        }
    }

    private void fetchMovieById(int movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String title = response.getString("title");
                        String posterPath = response.getString("poster_path");
                        String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;

                        Movie movie = new Movie(movieId, title, fullPosterUrl);
                        watchlistMovies.add(movie);
                        watchlistAdapter.notifyItemInserted(watchlistMovies.size() - 1);

                    } catch (JSONException e) {
                        Log.e("WATCHLIST_JSON", "JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("WATCHLIST_API", "API error: " + error.toString())
        );

        queue.add(request);
    }
}



