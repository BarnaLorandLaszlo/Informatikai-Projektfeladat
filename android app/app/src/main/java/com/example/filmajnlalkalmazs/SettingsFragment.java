package com.example.filmajnlalkalmazs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.filmajnlalkalmazs.database.UserDatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Gombok inicializálása
        Button btnChangeName = view.findViewById(R.id.btnChangeName);
        Button btnChangeProfilePicture = view.findViewById(R.id.btnChangeProfilePicture);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        Button btnClearAll = view.findViewById(R.id.btnClearAll);
        Button btnClearFavorites = view.findViewById(R.id.btnClearFavorites);
        Button btnClearReviews = view.findViewById(R.id.btnClearReviews);
        Button btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        // Kattintások kezelése → átdob a ProfileActivity-re
        View.OnClickListener openProfileEditor = v -> {
            Intent intent = new Intent(requireContext(), ProfileActivity.class);
            startActivity(intent);
        };

        btnChangeName.setOnClickListener(openProfileEditor);
        btnChangeProfilePicture.setOnClickListener(openProfileEditor);

        // Kilépés kezelése
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Log out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .clear()
                                .apply();

                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Összes adat törlése (user rekord megtartása)
        btnClearAll.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete All Data")
                    .setMessage("Are you sure you want to delete all your data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .getInt("user_id", -1);
                        if (userId != -1) {
                            UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
                            dbHelper.deleteUserAssociatedData(userId);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Csak kedvencek törlése
        btnClearFavorites.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Favorites")
                    .setMessage("Are you sure you want to delete all your favorites?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .getInt("user_id", -1);
                        if (userId != -1) {
                            UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
                            dbHelper.deleteFavorites(userId);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Csak értékelések törlése
        btnClearReviews.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Reviews")
                    .setMessage("Are you sure you want to delete all your reviews?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .getInt("user_id", -1);
                        if (userId != -1) {
                            UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
                            dbHelper.deleteReviews(userId);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Fiók törlése
        btnDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account and all associated data?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                .getInt("user_id", -1);
                        if (userId != -1) {
                            UserDatabaseHelper dbHelper = new UserDatabaseHelper(requireContext());
                            dbHelper.deleteUserCompletely(userId);

                            requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                    .edit()
                                    .clear()
                                    .apply();

                            Intent intent = new Intent(requireContext(), NavigationActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return view;
    }
}



