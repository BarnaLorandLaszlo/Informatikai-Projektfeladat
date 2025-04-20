package com.example.filmajnlalkalmazs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerPopular;
    private RecyclerView recyclerRecommended;
    private ImageView imageUser;
    private TextView textWelcome;

    private final String API_KEY = "c412675515f5ce72a5bbbd49cec4c943";

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageUser = view.findViewById(R.id.imageUser);
        textWelcome = view.findViewById(R.id.textWelcome);
        recyclerPopular = view.findViewById(R.id.recyclerPopularMovies);
        recyclerRecommended = view.findViewById(R.id.recyclerRecommendedMovies);

        // SharedPreferences: felhasználónév és ID lekérése
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String firstName = prefs.getString("firstname", "Felhasználó");

        textWelcome.setText("Üdv, " + firstName + "!");

        recyclerPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());

        // Kedvencek
        List<Movie> favoriteList = new ArrayList<>();
        MovieAdapter favoriteAdapter = new MovieAdapter(favoriteList);
        recyclerPopular.setAdapter(favoriteAdapter);

        List<Integer> favoriteIds = dbHelper.getFavoriteMovieIds(userId);
        for (int id : favoriteIds) {
            fetchMovieById(id, new MovieCallback() {
                @Override
                public void onSuccess(Movie movie) {
                    favoriteList.add(movie);
                    favoriteAdapter.notifyItemInserted(favoriteList.size() - 1);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("API_FAVORITE_ERROR", errorMessage);
                }
            });
        }

        // Értékelt filmek
        List<Movie> reviewedList = new ArrayList<>();
        MovieAdapter reviewedAdapter = new MovieAdapter(reviewedList);
        recyclerRecommended.setAdapter(reviewedAdapter);

        List<Integer> reviewedIds = dbHelper.getReviewedMovieIds(userId);
        for (int id : reviewedIds) {
            fetchMovieById(id, new MovieCallback() {
                @Override
                public void onSuccess(Movie movie) {
                    reviewedList.add(movie);
                    reviewedAdapter.notifyItemInserted(reviewedList.size() - 1);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("API_REVIEW_ERROR", errorMessage);
                }
            });
        }
    }

    public void fetchMovieById(int movieId, MovieCallback callback) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId + "?api_key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String title = response.getString("title");
                        String posterPath = response.getString("poster_path");
                        String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;

                        Movie movie = new Movie(movieId, title, fullPosterUrl);
                        callback.onSuccess(movie);

                    } catch (JSONException e) {
                        callback.onError("JSON error: " + e.getMessage());
                    }
                },
                error -> callback.onError("API error: " + error.toString())
        );

        queue.add(request);
    }
}

