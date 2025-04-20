package com.example.filmajnlalkalmazs;

public class Profile {
    private String displayName;
    private String description;
    private String profilePicture;

    public Profile(String displayName, String description, String profilePicture) {
        this.displayName = displayName;
        this.description = description;
        this.profilePicture = profilePicture;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}

