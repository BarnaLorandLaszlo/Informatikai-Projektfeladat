package com.example.filmajnlalkalmazs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.filmajnlalkalmazs.Profile;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerPopular;
    private RecyclerView recyclerRecommended;
    private ImageView imageUser;
    private TextView textWelcome;
    private int lastRandomMovieId = -1;

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
        ImageView imageDice = view.findViewById(R.id.imageDice);
        ImageView imageRecommendation = view.findViewById(R.id.imageRecommendation);
        ImageView imageCheck = view.findViewById(R.id.imageCheck);
        Button btnOpenWatchlist = view.findViewById(R.id.btnOpenWatchlist);

        imageDice.setOnClickListener(v -> fetchRandomMovie());

        imageCheck.setOnClickListener(v -> {
            int userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getInt("user_id", -1);
            if (userId != -1 && lastRandomMovieId != -1) {
                UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
                dbHelper.addToWatchlist(userId, lastRandomMovieId);
                Toast.makeText(requireContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
            }
        });

        btnOpenWatchlist.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WatchlistActivity.class);
            startActivity(intent);
        });

        recyclerPopular.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerRecommended.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
        int userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE).getInt("user_id", -1);

        Profile profile = dbHelper.getProfileByUserId(userId);
        if (profile == null) {
            Intent intent = new Intent(requireContext(), ProfileActivity.class);
            startActivity(intent);
            return;
        }

        textWelcome.setText("Üdv, " + profile.getDisplayName() + "!");
        if (profile.getProfilePicture() != null) {
            int resId = getResources().getIdentifier(profile.getProfilePicture(), "drawable", requireContext().getPackageName());
            if (resId != 0) {
                Glide.with(this).load(resId).into(imageUser);
            }
        }

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

    private void fetchRandomMovie() {
        String discoverUrl = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, discoverUrl, null,
                response -> {
                    try {
                        int totalPages = response.getInt("total_pages");
                        int randomPage = (int) (Math.random() * Math.min(totalPages, 500)) + 1;

                        fetchMovieFromPage(randomPage);
                    } catch (JSONException e) {
                        Log.e("RANDOM_FETCH", "JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("RANDOM_FETCH", "API error: " + error.toString())
        );

        queue.add(request);
    }

    private void fetchMovieFromPage(int page) {
        String url = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&page=" + page;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        var results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            int randomIndex = (int) (Math.random() * results.length());
                            var movie = results.getJSONObject(randomIndex);

                            String posterPath = movie.getString("poster_path");
                            String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                            lastRandomMovieId = movie.getInt("id");

                            ImageView imageRecommendation = requireView().findViewById(R.id.imageRecommendation);
                            Glide.with(this).load(fullPosterUrl).into(imageRecommendation);
                        }
                    } catch (JSONException e) {
                        Log.e("RANDOM_PAGE_FETCH", "JSON error: " + e.getMessage());
                    }
                },
                error -> Log.e("RANDOM_PAGE_FETCH", "API error: " + error.toString())
        );

        queue.add(request);
    }
}


