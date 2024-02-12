package com.example.pawadoptjava;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.io.IOException;
import java.util.List;

import com.example.pawadoptjava.databinding.ActivityMainBinding;
public class DashboardFragment extends Fragment {
    private RecyclerView recyclerView;
    private AnimalAdapter animalAdapter;
    private ApiService apiService;
    private AuthService authService;
    private SearchView searchView;
    private EditText searchEditText;
    private Button searchButton;
    private ImageView dogButton;
    private ImageView catButton;
    private ImageView birdButton;
    private ImageView fishButton;

    private TextView cityTextView, firstNameTextView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        searchView = root.findViewById(R.id.searchView);
        dogButton = root.findViewById(R.id.dogButton);
        catButton = root.findViewById(R.id.catButton);
        birdButton = root.findViewById(R.id.birdButton);
        fishButton = root.findViewById(R.id.fishButton);
        firstNameTextView=root.findViewById(R.id.firstNameTextView);
        cityTextView=root.findViewById(R.id.cityTextView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        authService = new AuthService(getContext());
        apiService = new ApiService(getContext());
        fetchUserInfo();
        fetchAnimals(null);
        Context context = getContext();

        dogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAnimals("dog"); // Pass the view as parameter
            }
        });

        catButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAnimals("cat"); // Pass the view as parameter
            }
        });

        birdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAnimals("bird"); // Pass the view as parameter
            }
        });

        fishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAnimals("fish"); // Pass the view as parameter
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when user submits query
                fetchAnimals(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle text change if needed
                fetchAnimals(newText);
                return false;
            }
        });


        return root;
    }

    private void fetchAnimals(String searchQuery) {
        new Thread(() -> {
            try {
                List<Animal> animals = apiService.getAnimals(searchQuery);
                requireActivity().runOnUiThread(() -> displayAnimals(animals));
            } catch (IOException e) {
                e.printStackTrace();
                // Handle network error
            }
        }).start();
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
                    firstNameTextView.setText("Hello, " + user.getFirstName());


                    cityTextView.setText(user.getCity()+", " +user.getCountry());

                });
            } catch (IOException e) {
                e.printStackTrace();
                // Handle error
            }
        }).start();
    }
    private void displayAnimals(List<Animal> animals) {
        animalAdapter = new AnimalAdapter(getContext(), animals);
        recyclerView.setAdapter(animalAdapter);
    }
    public void filterAnimals(String animalType) {

        fetchAnimals(animalType);
    }


}
