package com.example.filmajnlalkalmazs;

public interface MovieCallback {
    void onSuccess(Movie movie);
    void onError(String errorMessage);
}

