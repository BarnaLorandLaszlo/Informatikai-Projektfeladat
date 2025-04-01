package com.example.filmajnlalkalmazs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstNameEditText, lastNameEditText, usernameEditText, emailEditText,
            passwordEditText, birthDateEditText;
    private Button registerButton;
    private TextView alreadyAccountText;

    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbHelper = new UserDatabaseHelper(this);

        firstNameEditText = findViewById(R.id.FirstNameText);
        lastNameEditText = findViewById(R.id.LastNameText);
        usernameEditText = findViewById(R.id.editTextText3);
        emailEditText = findViewById(R.id.EmailText);
        passwordEditText = findViewById(R.id.PasswordText);
        birthDateEditText = findViewById(R.id.DateText);
        registerButton = findViewById(R.id.RegistrationB);
        alreadyAccountText = findViewById(R.id.AlreadyA);
        ImageView backArrow = findViewById(R.id.backArrow);

        // REGISZTRÁCIÓ GOMB
        registerButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String birthDate = birthDateEditText.getText().toString().trim();

            String registrationDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                    email.isEmpty() || password.isEmpty() || birthDate.isEmpty()) {
                Toast.makeText(this, "Kérlek tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = dbHelper.addUser(username, firstName, lastName, password, email, registrationDate, birthDate);

            if (result != -1) {
                Toast.makeText(this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();
                // Átlépés a login képernyőre
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Hiba történt a regisztráció során.", Toast.LENGTH_SHORT).show();
            }
        });
        backArrow.setOnClickListener(v -> {

            startActivity(new Intent(RegistrationActivity.this, NavigationActivity.class));

        });

        // "Already have an account?" szöveg esemény
        alreadyAccountText.setOnClickListener(v -> {
            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
