package com.example.filmajnlalkalmazs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.Context;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeNavigationActivity extends AppCompatActivity {
    TextView welcomeText; // csak deklaráljuk itt

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_navigation);
        Button btnSearch = findViewById(R.id.btnSearch);
        welcomeText = findViewById(R.id.tvWelcome); // csak most hívjuk meg, miután betöltött a layout

        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String last_name = prefs.getString("lastname", "user"); // fallback ha null
        welcomeText.setText("Welcome back, " + last_name + "!");

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeNavigationActivity.this, MainActivity2.class));
            }
        });
    }
}


