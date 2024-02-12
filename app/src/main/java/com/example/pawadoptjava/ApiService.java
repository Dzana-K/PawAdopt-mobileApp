package com.example.pawadoptjava;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

// ApiService.java
public class ApiService {
    private static final String BASE_URL = "http://10.0.2.2:5000/";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private AuthService authService;
    private SharedPreferences sharedPreferences;
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    private Context context;

    public ApiService(Context context) {
        this.authService = new AuthService(context);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        // Add the authentication interceptor
        clientBuilder.addInterceptor(new AuthInterceptor(authService));

        // Build the OkHttpClient
        client = clientBuilder.build();
        this.context = context;
        sharedPreferences = context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
    }
    public List<Animal> getAnimals(String searchQuery) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "animals").newBuilder();
        if (searchQuery != null && !searchQuery.isEmpty()) {
            urlBuilder.addQueryParameter("searchQuery", searchQuery);
        }
        String url = urlBuilder.build().toString();

        String accessToken = getToken();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                return gson.fromJson(json, new TypeToken<List<Animal>>(){}.getType());
            } else {
                throw new IOException("Unexpected response code " + response);
            }
        }
    }
    public List<Animal> getUsersAnimals() throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "user/posts").newBuilder();

        String url = urlBuilder.build().toString();


        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                return gson.fromJson(json, new TypeToken<List<Animal>>(){}.getType());
            } else {
                throw new IOException("Unexpected response code " + response);
            }
        }
    }
    public List<Favorite> getFavorites() throws IOException {
        List<Favorite> favorites = new ArrayList<>();

        Request request = new Request.Builder()
                .url(BASE_URL + "/favorites")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code " + response);
            }

            String responseData = response.body().string();
            JSONArray jsonArray = new JSONArray(responseData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                // Parse favorite object
                int id = jsonObject.getInt("id");
                JSONObject animalObject = jsonObject.getJSONObject("animal");
                Animal animal = parseAnimal(animalObject);

                Favorite favorite = new Favorite(id, animal);
                favorites.add(favorite);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return favorites;
    }

    private Animal parseAnimal(JSONObject jsonObject) throws JSONException {
        // Parse animal object
        int id = jsonObject.getInt("id");
        String name = jsonObject.getString("name");
        String gender = jsonObject.getString("gender");
        String weight = jsonObject.getString("weight");
        String color = jsonObject.getString("color");
        String age = jsonObject.getString("age");
        String breed = jsonObject.getString("breed");
        int user_id = jsonObject.getInt("user_id");
        String type = jsonObject.getString("type");
        String description = jsonObject.getString("description");
        List<String> photos = new ArrayList<>();
        JSONArray photosArray = jsonObject.getJSONArray("photos");
        for (int i = 0; i < photosArray.length(); i++) {
            photos.add(photosArray.getString(i));
        }
        String city = jsonObject.getString("city");
        String country = jsonObject.getString("country");

        return new Animal(id, name, gender, weight, color, age, breed, description, photos, city, country, type, user_id);
    }
    public void fetchAnimalDetails(int animalId, final AnimalDetailsCallback callback) {
        String accessToken = getToken();
        Request request = new Request.Builder()
                .url("http://10.0.2.2:5000/animal/" + animalId)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ApiService", "Failed to fetch animal details", e);
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    Animal animal = gson.fromJson(json, Animal.class);
                    callback.onSuccess(animal);
                } else {
                    Log.e("ApiService", "Failed to fetch animal details: " + response.code());
                    callback.onFailure(new IOException("Failed to fetch animal details: " + response.code()));
                }
            }
        });
    }
    public User getUser(int userId) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "user/" + userId).newBuilder();
        String url = urlBuilder.build().toString();
        String accessToken = getToken();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                return gson.fromJson(json, User.class);
            } else {
                throw new IOException("Unexpected response code " + response);
            }
        }
    }

    public interface AnimalDetailsCallback {
        void onSuccess(Animal animal);
        void onFailure(IOException e);
    }
    public void sendEmail(String recipientEmail, String subject, String body)throws IOException {
        // Create JSON request body
        String json = "{\"recipient\": \"" + recipientEmail + "\", \"subject\": \"" + subject + "\", \"body\": \"" + body + "\"}";

        // Create request object
        RequestBody requestBody = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(BASE_URL+"send_email")
                .post(requestBody)
                .build();

        // Execute the request asynchronously
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to send email: " + response);
            }
        }
    }
    public void addAnimalToFavorites(int animalId, final EmailCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("animal_id", animalId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

        Request request = new Request.Builder()
                .url(BASE_URL + "/favorites/add")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Failed to add animal to favorites", e);
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to add animal to favorites: " + response.body().string());
                    callback.onFailure("Failed to add animal to favorites");
                } else {
                    Log.d(TAG, "Animal added to favorites successfully");
                    callback.onSuccess();
                }
            }
        });
    }

    public void removeAnimalFromFavorites(int animalId, final EmailCallback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "/favorites/delete/" + animalId)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Failed to remove animal from favorites", e);
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e(TAG, "Failed to remove animal from favorites: " + response.body().string());
                    callback.onFailure("Failed to remove animal from favorites");
                } else {
                    Log.d(TAG, "Animal removed from favorites successfully");
                    callback.onSuccess();
                }
            }
        });
    }

    // Callback interface for email sending result
    public interface EmailCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
    public String getToken() {
        return sharedPreferences.getString("accessToken", null);
    }
}

