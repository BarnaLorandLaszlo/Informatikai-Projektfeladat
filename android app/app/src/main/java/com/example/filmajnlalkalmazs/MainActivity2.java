package com.example.filmajnlalkalmazs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

import android.content.Intent;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {

    EditText etMovie;
    Button btnSearch;
    TextView tvTitle, tvOverview, tvReleaseDate, tvRating;
    ImageView ivPoster;
    private int currentMovieId = -1;
    private final String API_KEY = "c412675515f5ce72a5bbbd49cec4c943";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        // EdgeToEdge padding
        View rootView = findViewById(R.id.main); // FONTOS: root layout ID legyen "main"
        if (rootView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // UI elemek inicializálása
        etMovie = findViewById(R.id.etMovie);
        btnSearch = findViewById(R.id.btnSearch);
        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvRating = findViewById(R.id.tvRating);
        ivPoster = findViewById(R.id.ivPoster);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);
        ImageView star1 = findViewById(R.id.star1);
        ImageView star2 = findViewById(R.id.star2);
        ImageView star3 = findViewById(R.id.star3);
        ImageView star4 = findViewById(R.id.star4);
        ImageView star5 = findViewById(R.id.star5);
        ImageView heart = findViewById(R.id.heart);

        star1.setOnClickListener(v -> {
            star1.setImageResource(R.drawable.ye_star);
            star2.setImageResource(R.drawable.black_star);
            star3.setImageResource(R.drawable.black_star);
            star4.setImageResource(R.drawable.black_star);
            star5.setImageResource(R.drawable.black_star);

        });
        star2.setOnClickListener(v -> {
            star1.setImageResource(R.drawable.ye_star);
            star2.setImageResource(R.drawable.ye_star);
            star3.setImageResource(R.drawable.black_star);
            star4.setImageResource(R.drawable.black_star);
            star5.setImageResource(R.drawable.black_star);

        });
        star3.setOnClickListener(v -> {
            star1.setImageResource(R.drawable.ye_star);
            star2.setImageResource(R.drawable.ye_star);
            star3.setImageResource(R.drawable.ye_star);
            star4.setImageResource(R.drawable.black_star);
            star5.setImageResource(R.drawable.black_star);

        });
        star4.setOnClickListener(v -> {
            star1.setImageResource(R.drawable.ye_star);
            star2.setImageResource(R.drawable.ye_star);
            star3.setImageResource(R.drawable.ye_star);
            star4.setImageResource(R.drawable.ye_star);
            star5.setImageResource(R.drawable.black_star);

        });
        star5.setOnClickListener(v -> {
            star1.setImageResource(R.drawable.ye_star);
            star2.setImageResource(R.drawable.ye_star);
            star3.setImageResource(R.drawable.ye_star);
            star4.setImageResource(R.drawable.ye_star);
            star5.setImageResource(R.drawable.ye_star);

        });
        int[] counter = {0};
        heart.setOnClickListener(v -> {
         counter[0]++;
            if (counter[0] % 2 == 0) {
                heart.setImageResource(R.drawable.black_heart);
            } else {
                heart.setImageResource(R.drawable.red_heart);
            }

        });
        btnSave.setOnClickListener(v -> {
            if (counter[0] % 2 != 0) {
                SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                int userId = prefs.getInt("user_id", -1);
                long result = dbHelper.addFavorite(userId, currentMovieId);

                if (result != -1) {
                    Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add favorite.", Toast.LENGTH_SHORT).show();
                }
            }

        });


// stb.





        btnSearch.setOnClickListener(v -> {
            String title = etMovie.getText().toString().trim();
            if (!title.isEmpty()) {
                fetchMovieData(title);
            }
        });
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, WelcomeNavigationActivity.class);
            startActivity(intent);
            finish(); // opcionális, ha nem akarod, hogy vissza lehessen jönni a back gombbal
        });
    }

    public void fetchMovieData(String title) {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + title;

        RequestQueue queue = Volley.newRequestQueue(this);

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

                            Glide.with(this).load(fullPosterUrl).into(ivPoster);
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
