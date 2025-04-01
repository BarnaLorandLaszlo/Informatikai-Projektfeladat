package com.example.filmajnlalkalmazs;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.SharedPreferences;


import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // UI elemek
        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.button2);

        // Insets beállítás
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.buttonLogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Adatbázis példány
        dbHelper = new UserDatabaseHelper(this);

        // Gomb esemény
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Kérlek tölts ki minden mezőt", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = dbHelper.getUserByUsername(username);
            if (cursor.moveToFirst()) {
                String storedHash = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String inputHash = hashPassword(password);
                if (storedHash.equals(inputHash)) {

                    // adatbázisból értékek megadása válzotóknak, majd elmetése SharedPreferences-be (kilépés után is megmarad az érték)

                    int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                    String registrationDate = cursor.getString(cursor.getColumnIndexOrThrow("registration_date"));
                    String birthDate = cursor.getString(cursor.getColumnIndexOrThrow("birth_date"));

                    SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("user_id", userId); // például ID
                    editor.putString("username", firstName);
                    editor.putString("lastname", lastName );
                    editor.putString("email", email );
                    editor.putString("registrationDate", registrationDate );
                    editor.putString("birthDate", birthDate );
                    editor.apply();

                    startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                    // startActivity(new Intent(this, HomeActivity.class));
                } else {
                    Toast.makeText(this, "Hibás jelszó!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Felhasználó nem található!", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e("LoginActivity", "Hashelési hiba", e);
            return "";
        }
    }
}
