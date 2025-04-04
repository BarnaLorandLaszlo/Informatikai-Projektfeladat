package com.example.filmajnlalkalmazs.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    // Adatbázis konstansok
    private static final String DATABASE_NAME = "user_database.db";
    //private static final int DATABASE_VERSION = 1;
    private static final int DATABASE_VERSION = 4;

    // JELSZÓ HASH FUNKCIÓ (SHA-256)

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    //hash password
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
            throw new RuntimeException("SHA-256 algoritmus nem érhető el", e);
        }
    }
    // Kedvencek tábla
    private static final String TABLE_FAVORITES = "favorites";
    private static final String CREATE_TABLE_FAVORITES =
            "CREATE TABLE " + TABLE_FAVORITES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "movie_id INTEGER, " +
                    "added_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)";
    // Kedvencek tábla létrehozása
    private static final String TABLE_REVIEWS = "reviews";
    private static final String CREATE_TABLE_REVIEWS =
            "CREATE TABLE " + TABLE_REVIEWS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER, " +
                    "movie_id INTEGER, " +
                    "rating REAL, " +
                    "review_text TEXT, " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    // "FOREIGN KEY(movie_id) REFERENCES movies(id) ON DELETE CASCADE" +
                    ");";

// annak az ellenőrzése, hogy már van-e olyan rakordunk a táblában, amit most hozzá szeretnénk adni
public int updateReview(int userId, int movieId, float rating, String reviewText) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put("rating", rating);
    values.put("review_text", reviewText);
    values.put("created_at", "datetime('now')"); // opcionális

    return db.update("reviews", values, "user_id = ? AND movie_id = ?",
            new String[]{String.valueOf(userId), String.valueOf(movieId)});
}

// review update metódus
public boolean reviewExists(int userId, int movieId) {
    SQLiteDatabase db = this.getReadableDatabase();
    Cursor cursor = db.rawQuery(
            "SELECT 1 FROM reviews WHERE user_id = ? AND movie_id = ?",
            new String[]{String.valueOf(userId), String.valueOf(movieId)}
    );
    boolean exists = cursor.moveToFirst();
    cursor.close();
    return exists;
}



    // Felhasználók táblac

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_REGISTRATION_DATE = "registration_date";
    private static final String COLUMN_BIRTH_DATE = "birth_date";

    // SQL tábla létrehozása
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                    COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_REGISTRATION_DATE + " TEXT NOT NULL, " +
                    COLUMN_BIRTH_DATE + " TEXT NOT NULL" +
                    ");";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_FAVORITES);
        db.execSQL(CREATE_TABLE_REVIEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Próbáljuk újra létrehozni a reviews táblát a javított verzióval
            db.execSQL("DROP TABLE IF EXISTS reviews");
            db.execSQL(CREATE_TABLE_REVIEWS);
        }
    }
    //Értékelés hozzáadás
    public long addReview(int userId, int movieId, float rating, String reviewText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("movie_id", movieId);
        values.put("rating", rating);
        values.put("review_text", reviewText);
        return db.insert(TABLE_REVIEWS, null, values);
    }


    // Felhasználó hozzáadása
    public long addUser(String username, String firstName, String lastName, String password, String email, String registrationDate, String birthDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_PASSWORD, hashPassword(password));
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_REGISTRATION_DATE, registrationDate);
        values.put(COLUMN_BIRTH_DATE, birthDate);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    // Felhasználó keresése név alapján
    public Cursor getUserByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});
    }

    // Összes felhasználó lekérdezése
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }
    public long addFavorite(int userId, int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("movie_id", movieId);
        // added_at automatikusan a jelenlegi idő lesz
        return db.insert(TABLE_FAVORITES, null, values);
    }
    // annak az ellenőrzése, hogy a favorite táblában van-e már olyan adat, amit mi beszúrni szerettünk volna
    public boolean checkFavoriteExists(int userId, int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM Favorites WHERE user_id = ? AND movie_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(movieId)});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // favorites táblából való rekord törlés user_id és movie_id alapján
    public boolean removeFavorite(int userId, int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete("Favorites", "user_id = ? AND movie_id = ?",
                new String[]{String.valueOf(userId), String.valueOf(movieId)});
        return rowsAffected > 0;
    }


}



