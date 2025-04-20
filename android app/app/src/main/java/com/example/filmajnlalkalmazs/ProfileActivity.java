package com.example.filmajnlalkalmazs;

import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;
import com.example.filmajnlalkalmazs.Profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileActivity extends AppCompatActivity {

    private String selectedProfileImageName = "default_avatar.png"; // alapértelmezett

    private EditText editDisplayName;
    private EditText editDescription;
    private Button btnSaveProfile;
    private RecyclerView recyclerProfileImages;
    private TextView textEditProfile;
    private ProfileImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editDisplayName = findViewById(R.id.editDisplayName);
        editDescription = findViewById(R.id.editDescription);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        recyclerProfileImages = findViewById(R.id.recyclerProfileImages);
        textEditProfile = findViewById(R.id.textEditProfile); // ha szükséges a címhez

        recyclerProfileImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        String[] imageOptions = {
                "avatar_0_0", "avatar_0_1", "avatar_0_2",
                "avatar_1_0", "avatar_1_1", "avatar_1_2",
                "avatar_2_0", "avatar_2_1", "avatar_2_2"
        };

        adapter = new ProfileImageAdapter(this, imageOptions, imageName -> {
            selectedProfileImageName = imageName; // adapteren keresztül beállítjuk
        });

        recyclerProfileImages.setAdapter(adapter);

        UserDatabaseHelper dbHelper = new UserDatabaseHelper(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        if (userId != -1) {
            // lekérjük az aktuális profiladatokat
            Profile profile = dbHelper.getProfileByUserId(userId);
            if (profile != null) {
                editDisplayName.setText(profile.getDisplayName());
                editDescription.setText(profile.getDescription());
                selectedProfileImageName = profile.getProfilePicture(); // kiválasztott képfájl neve

                // Az adapter kiválasztását is frissítjük
                adapter.setSelectedImage(selectedProfileImageName);
            }
        }

        btnSaveProfile.setOnClickListener(v -> {
            String displayName = editDisplayName.getText().toString().trim();
            String description = editDescription.getText().toString().trim();
            String selectedImage = selectedProfileImageName;

            if (displayName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedImage == null || selectedImage.equals("default_avatar.png")) {
                Toast.makeText(this, "Please select a profile image", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences savePrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
            int saveUserId = savePrefs.getInt("user_id", -1);

            if (saveUserId != -1) {
                UserDatabaseHelper saveDbHelper = new UserDatabaseHelper(this);
                saveDbHelper.insertOrUpdateProfile(saveUserId, displayName, description, selectedImage);

                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();
                finish(); // vissza a beállításokhoz vagy más activity-hez
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
