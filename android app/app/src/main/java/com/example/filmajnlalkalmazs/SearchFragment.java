package com.example.filmajnlalkalmazs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchFragment extends Fragment {

    int userId;
    int[] counter = {0};
    int reviews = 0;
    EditText etMovie;
    Button btnSearch, btnSave, btnBack;
    TextView tvTitle, tvOverview, tvReleaseDate, tvRating;
    ImageView ivPoster, star1, star2, star3, star4, star5, heart;
    String reviewText = "";
    private int currentMovieId = -1;
    private final String API_KEY = "c412675515f5ce72a5bbbd49cec4c943";
    UserDatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new UserDatabaseHelper(requireContext());

        // Inicializálás
        etMovie = view.findViewById(R.id.etMovie);
        btnSearch = view.findViewById(R.id.btnSearch);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvOverview = view.findViewById(R.id.tvOverview);
        tvReleaseDate = view.findViewById(R.id.tvReleaseDate);
        tvRating = view.findViewById(R.id.tvRating);
        ivPoster = view.findViewById(R.id.ivPoster);
        btnSave = view.findViewById(R.id.btnSave);
        btnBack = view.findViewById(R.id.btnBack);
        star1 = view.findViewById(R.id.star1);
        star2 = view.findViewById(R.id.star2);
        star3 = view.findViewById(R.id.star3);
        star4 = view.findViewById(R.id.star4);
        star5 = view.findViewById(R.id.star5);
        heart = view.findViewById(R.id.heart);

        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        tvTitle.setText("Nincs találat.");

        setStarListeners();

        heart.setOnClickListener(v -> {
            counter[0]++;
            heart.setImageResource((counter[0] % 2 == 0) ? R.drawable.black_heart : R.drawable.red_heart);
        });

        btnSave.setOnClickListener(v -> {
            if (counter[0] % 2 != 0) {
                if (!dbHelper.checkFavoriteExists(userId, currentMovieId) && !tvTitle.getText().toString().equals("Nincs találat.")) {
                    long result = dbHelper.addFavorite(userId, currentMovieId);
                    Toast.makeText(getContext(), (result != -1) ? "Added to favorites!" : "Failed to add favorite.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "This movie is already in your favorites.", Toast.LENGTH_SHORT).show();
                }
            } else if (dbHelper.checkFavoriteExists(userId, currentMovieId)) {
                boolean removed = dbHelper.removeFavorite(userId, currentMovieId);
                Toast.makeText(getContext(), (removed) ? "Removed from favorites!" : "Failed to remove/add favorite.", Toast.LENGTH_SHORT).show();
            } else if (tvTitle.getText().toString().equals("Nincs találat.")) {
                Toast.makeText(getContext(), "Failed to remove favorite.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSearch.setOnClickListener(v -> {
            String title = etMovie.getText().toString().trim();
            if (!title.isEmpty()) {
                fetchMovieData(title);
            }
        });

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setStarListeners() {
        ImageView[] stars = {star1, star2, star3, star4, star5};

        for (int i = 0; i < stars.length; i++) {
            final int rating = i + 1;
            stars[i].setOnClickListener(v -> {
                reviews = rating;
                for (int j = 0; j < stars.length; j++) {
                    stars[j].setImageResource(j < rating ? R.drawable.ye_star : R.drawable.black_star);
                }

                if (dbHelper.reviewExists(userId, currentMovieId)) {
                    int rows = dbHelper.updateReview(userId, currentMovieId, reviews, reviewText);
                    Toast.makeText(getContext(), rows > 0 ? "Értékelés frissítve!" : "Nem sikerült frissíteni az értékelést.", Toast.LENGTH_SHORT).show();
                } else if (userId != -1) {
                    long result = dbHelper.addReview(userId, currentMovieId, reviews, reviewText);
                    Toast.makeText(getContext(), result != -1 ? "Vélemény elmentve!" : "Hiba történt a mentés során.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Nem vagy bejelentkezve!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void fetchMovieData(String title) {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + title;

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject movie = results.getJSONObject(0);
                            currentMovieId = movie.getInt("id");

                            String movieTitle = movie.getString("title");
                            String overview = movie.getString("overview");
                            String releaseDate = movie.getString("release_date");
                            double rating = movie.getDouble("vote_average");
                            String posterPath = movie.getString("poster_path");
                            String fullPosterUrl = "https://image.tmdb.org/t/p/w500" + posterPath;

                            tvTitle.setText("Cím: " + movieTitle);
                            tvOverview.setText("Leírás:\n" + overview);
                            tvReleaseDate.setText("Megjelenés: " + releaseDate);
                            tvRating.setText("Értékelés: " + rating + "/10");

                            if (dbHelper.checkFavoriteExists(userId, currentMovieId)) {
                                heart.setImageResource(R.drawable.red_heart);
                                counter[0]++;
                            }

                            Glide.with(requireContext()).load(fullPosterUrl).into(ivPoster);
                        } else {
                            tvTitle.setText("Nincs találat.");
                            tvOverview.setText("");
                            tvReleaseDate.setText("");
                            tvRating.setText("");
                            ivPoster.setImageDrawable(null);
                        }
                    } catch (JSONException e) {
                        tvTitle.setText("Hiba az adatok feldolgozásakor.");
                    }
                },
                error -> tvTitle.setText("Hálózati hiba: " + error.toString())
        );

        queue.add(request);
    }
}
