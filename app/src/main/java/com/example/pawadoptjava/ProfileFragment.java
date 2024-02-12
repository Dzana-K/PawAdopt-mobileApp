package com.example.pawadoptjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private AnimalAdapter animalAdapter;
    private ApiService apiService;

    private AuthService authService;

    private TextView textViewFirstName;
    private TextView textViewLastName;
    private TextView textViewEmail, textViewLogout, textViewAddress, textViewCity, textViewCountry;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        authService = new AuthService(getContext());
        recyclerView = root.findViewById(R.id.recyclerView);

        textViewFirstName = root.findViewById(R.id.textViewFirstName);
        textViewLastName = root.findViewById(R.id.textViewLastName);
        textViewEmail = root.findViewById(R.id.textViewEmailValue);
        textViewAddress = root.findViewById(R.id.textViewAddressValue);
        textViewCity = root.findViewById(R.id.textViewCityValue);
        textViewCountry = root.findViewById(R.id.textViewCountryValue);
        textViewLogout = root.findViewById(R.id.textViewLogout);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        apiService = new ApiService(getContext());
        fetchUsersAnimals();
        fetchUserInfo();
        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call logout method in AuthService
                authService.logout(new AuthService.LogoutCallback() {
                    @Override
                    public void onSuccess() {
                        // Navigate to login or home page
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        }
                });
            }
        });

        return root;
    }
    private void fetchUserInfo() {
        // Get user ID from SharedPreferences
        int userId = authService.getUserId();

        new Thread(() -> {
            try {
                // Call API service to fetch user info
                User user = apiService.getUser(userId);

                requireActivity().runOnUiThread(() -> {
                    // Update UI with user info
                    textViewFirstName.setText(user.getFirstName());
                    textViewLastName.setText(user.getLastName());
                    textViewEmail.setText(user.getEmail());
                    textViewAddress.setText(user.getAddress());
                    textViewCity.setText(user.getCity());
                    textViewCountry.setText(user.getCountry());
                });
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }).start();
    }
    private void fetchUsersAnimals() {
        new Thread(() -> {
            try {
                List<Animal> animals = apiService.getUsersAnimals();
                requireActivity().runOnUiThread(() -> displayAnimals(animals));
            } catch (IOException e) {
                e.printStackTrace();

            }
        }).start();
    }



    private void displayAnimals(List<Animal> animals) {
        animalAdapter = new AnimalAdapter(getContext(), animals);
        recyclerView.setAdapter(animalAdapter);
    }

}

