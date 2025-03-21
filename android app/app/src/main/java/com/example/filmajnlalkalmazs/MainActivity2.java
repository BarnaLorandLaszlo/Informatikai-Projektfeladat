package com.example.filmajnlalkalmazs;

import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {

    EditText etMovie;
    Button btnSearch;
    TextView tvTitle, tvOverview, tvReleaseDate, tvRating;
    ImageView ivPoster;

    private final String API_KEY = "c412675515f5ce72a5bbbd49cec4c943";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

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

        btnSearch.setOnClickListener(v -> {
            String title = etMovie.getText().toString().trim();
            if (!title.isEmpty()) {
                fetchMovieData(title);
            }
        });
    }

    private void fetchMovieData(String title) {
        String url = "https://api.themoviedb.org/3/search/movie?api_key=" + API_KEY + "&query=" + title;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        if (results.length() > 0) {
                            JSONObject movie = results.getJSONObject(0);

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
