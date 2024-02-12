package com.example.pawadoptjava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private AnimalAdapter animalAdapter;
    private ApiService apiService;

    private AuthService authService;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorites, container, false);
        authService = new AuthService(getContext());
        recyclerView = root.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        apiService = new ApiService(getContext());
        fetchFavorites();



        return root;
    }
    private void fetchFavorites() {
        // Get user ID from SharedPreferences
        int userId = authService.getUserId();

        new Thread(() -> {
            try {
                ApiService apiService = new ApiService(getContext());
                List<Favorite> favorites = apiService.getFavorites();
                requireActivity().runOnUiThread(() -> displayFavorites(favorites));
                // Do something with the list of favorites
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }).start();
    }




    private void displayFavorites(List<Favorite> favorites) {
        List<Animal> animals = new ArrayList<>();
        for (Favorite favorite : favorites) {
            animals.add(favorite.getAnimal());
        }
        animalAdapter = new AnimalAdapter(getContext(), animals);
        recyclerView.setAdapter(animalAdapter);
    }
}
