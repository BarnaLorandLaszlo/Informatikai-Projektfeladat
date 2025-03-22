package com.example.filmajnlalkalmazs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.database.Cursor;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Gombok
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnMain2 = findViewById(R.id.btnMain2);
        Button btnRegistration = findViewById(R.id.btnRegistration);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigationActivity.this, LoginActivity.class));
            }
        });

        btnMain2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigationActivity.this, RegistrationActivity.class));
            }
        });

        btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigationActivity.this, MainActivity2.class));
            }
        });

        // 🔁 Egyszeri manuális felhasználó beszúrás
        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        Cursor cursor = dbHelper.getUserByUsername("barna123");

        if (cursor != null && cursor.getCount() == 0) {
            long result = dbHelper.addUser(
                    "barna123",
                    "Barna",
                    "Lóránd László",
                    "jelszo123",
                    "barna.lorand@example.com",
                    "2025-03-22",
                    "2000-05-01"
            );

            if (result != -1) {
                Toast.makeText(this, "Felhasználó hozzáadva: barna123", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Hiba a beszúráskor", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Felhasználó már létezik", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) cursor.close();
    }
}


