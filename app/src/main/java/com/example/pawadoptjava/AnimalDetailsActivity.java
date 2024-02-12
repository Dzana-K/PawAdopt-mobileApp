package com.example.pawadoptjava;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class AnimalDetailsActivity extends AppCompatActivity {

    private ImageView imageView;
    private ApiService apiService;
    private ViewPager viewPager;
    private Context context;
    private TextView animalNameTextView;
    private AnimalPagerAdapter pagerAdapter;
    private TextView firstNameTextView;
    private TextView descriptionTextView;
    public int user_id, animal_id;
    public String animalName;
    private boolean checkIfFavorites;
    private ImageView heartIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_animal_details);
        ;
        viewPager = findViewById(R.id.viewPager);
        heartIcon = findViewById(R.id.heartIcon);

        animalNameTextView = findViewById(R.id.animalNameTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        apiService = new ApiService(this);
        ImageView backArrowImageView = findViewById(R.id.backArrowImageView);
        firstNameTextView=findViewById(R.id.firstNameTextView);

        backArrowImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Navigate back to the previous activity
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("animal_id")) {
            int animalId = intent.getIntExtra("animal_id", -1);
            animal_id=animalId;

            fetchAnimalDetails(animalId);
        }
        imageView = new ImageView(this);
        new CheckFavoritesTask().execute(animal_id);
        if (checkIfFavorites) {
            heartIcon.setImageResource(R.drawable.heart__1_);
        } else {
            heartIcon.setImageResource(R.drawable.heart);
        }
        heartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your logic to add or remove the animal from favorites
                if (checkIfFavorites) {
                    // Remove the animal from favorites
                    removeAnimalFromFavorites(animal_id);
                    // Change icon to outline
                    heartIcon.setImageResource(R.drawable.heart);
                    // Update isAnimalInFavorites flag
                    checkIfFavorites = false;

                } else {
                    // Add the animal to favorites
                    addAnimalToFavorites(animal_id);
                    // Change icon to filled
                    heartIcon.setImageResource(R.drawable.heart__1_);
                    // Update isAnimalInFavorites flag
                    checkIfFavorites = true;

                }
            }
        });

    }

    private void addInfoBox(LinearLayout parentLayout, String infoText, String valueText) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View infoBox = inflater.inflate(R.layout.gray_box_layout, parentLayout, false);
        TextView infoTextView = infoBox.findViewById(R.id.infoTextView);
        TextView valueTextView = infoBox.findViewById(R.id.valueTextView);
        valueTextView.setText(valueText);
        infoTextView.setText(infoText);
        parentLayout.addView(infoBox);
    }
    public void openAdoptionForm(View view) {
        Intent intent = new Intent(this, AdoptionForm.class);
        intent.putExtra("userId", user_id);
        intent.putExtra("animalName", animalName);
        startActivity(intent);
    }
    private void fetchAnimalDetails(int animalId) {
        apiService.fetchAnimalDetails(animalId, new ApiService.AnimalDetailsCallback() {
            @Override
            public void onSuccess(Animal animal) {
                if (animal != null && animal.getPhotos() != null && !animal.getPhotos().isEmpty()) {
                    runOnUiThread(() -> {
                        descriptionTextView.setText((animal.getDescription()));
                        animalNameTextView.setText(animal.getName());
                        pagerAdapter = new AnimalPagerAdapter(animal.getPhotos());
                        viewPager.setAdapter(pagerAdapter);
                        LinearLayout infoLinearLayout = findViewById(R.id.infoLinearLayout);
                        int userId = animal.getUserId();
                        user_id=userId;
                        fetchUserDetails(userId);
                        animalName=animal.getName();
                        // Add age info box
                        addInfoBox(infoLinearLayout, "Age", animal.getAge()  );

                        // Add gender info box
                        addInfoBox(infoLinearLayout, "Gender", animal.getGender() );

                        // Add weight info box
                        addInfoBox(infoLinearLayout, "Weight", animal.getWeight());

                        // Add color info box
                        addInfoBox(infoLinearLayout, "Color", animal.getColor());

                        addInfoBox(infoLinearLayout, "Breed", animal.getBreed());

                    });
                }
            }

            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
                // Handle failure
            }
        });
    }
    private void addAnimalToFavorites(Integer animalId) {
        apiService.addAnimalToFavorites(animalId, new ApiService.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(AnimalDetailsActivity.this, "Animal added to favorites", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(AnimalDetailsActivity.this, "Failed to add animal to favorites: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void removeAnimalFromFavorites(Integer animalId) {
        apiService.removeAnimalFromFavorites(animalId, new ApiService.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(AnimalDetailsActivity.this, "Animal removed from favorites", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(AnimalDetailsActivity.this, "Failed to remove animal from favorites: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }
    private class CheckFavoritesTask extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            int animalId = params[0];
            return isAnimalInFavorites(animalId);
        }

        @Override
        protected void onPostExecute(Boolean isFavorite) {
            if (isFavorite) {
                // Animal is in favorites, update UI accordingly
                heartIcon.setImageResource(R.drawable.heart__1_);
            } else {
                // Animal is not in favorites, update UI accordingly
                heartIcon.setImageResource(R.drawable.heart);
            }
            // Save the value of isFavorite to a variable if needed
            checkIfFavorites = isFavorite;
        }
    }
    private void fetchUserDetails(int userId) {
        Context context = getApplicationContext();
        new FetchUserTask(context).execute(userId, findViewById(R.id.firstNameTextView), findViewById(R.id.lastNameTextView), findViewById(R.id.cityTextView), findViewById(R.id.countryTextView));
    }
    public boolean isAnimalInFavorites(int animalId) {
        try {
            List<Favorite> favoriteAnimals = apiService.getFavorites();

            // Iterate through the list of favorite animals and check if the given animalId matches
            for (Favorite favorite : favoriteAnimals) {
                Animal animal = favorite.getAnimal(); // Get the animal object from the favorite
                if (animal.getId() == animalId) {
                    return true; // Animal is in favorites
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception here, such as logging or displaying an error message
        }

        return false;
    }


    private static class FetchUserTask extends AsyncTask<Object, Void, User> {
        private TextView firstNameTextView;
        private Context context;
        private TextView lastNameTextView, cityTextView, countryTextView;
        public FetchUserTask(Context context) {
            this.context = context;
        }


        @Override
        protected User doInBackground(Object... params) {
            int userId = (int) params[0];

            ApiService apiService = new ApiService(context);
            firstNameTextView = (TextView) params[1];
            lastNameTextView = (TextView) params[2];
            cityTextView = (TextView) params[3];
            countryTextView = (TextView) params[4];

            try {
                return apiService.getUser(userId);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                // Update UI with user details
                firstNameTextView.setText(user.getFirstName());
                lastNameTextView.setText(user.getLastName());
                cityTextView.setText(user.getCity());
                countryTextView.setText(user.getCountry());
            } else {
                // Handle case where user is null
            }
        }
    }


    private class AnimalPagerAdapter extends PagerAdapter {

        private List<String> photos;

        AnimalPagerAdapter(List<String> photos) {
            this.photos = photos;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View itemView = inflater.inflate(R.layout.item_image, container, false);

            ImageView imageView = itemView.findViewById(R.id.imageView);
            Picasso.get().load(photos.get(position)).into(imageView);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}