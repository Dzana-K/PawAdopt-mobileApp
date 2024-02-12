package com.example.pawadoptjava;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import static androidx.core.content.ContextCompat.startActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AuthService {
    private static final String BASE_URL = "http://10.0.2.2:5000/";
    private final OkHttpClient client = new OkHttpClient();
    private Context context;

    public AuthService(Context context) {
        this.context = context;
    }
    public void login(String email, String password, final LoginCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL + "login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Failed to login. Please try again.");
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    String accessToken = jsonResponse.getString("access_token");
                    String refreshToken = jsonResponse.getString("refresh_token");
                    int userId = jsonResponse.getInt("user_id");

                    // Save tokens and user ID to SharedPreferences
                    saveAccessToken(accessToken);
                    saveRefreshToken(refreshToken);
                    saveUserId(userId);

                    callback.onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure("Error parsing server response.");
                }
            }
        });
    }
    public void register(String email, String password, String firstName, String lastName,String address, String city,String country, final LoginCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
            jsonBody.put("first_name", firstName);
            jsonBody.put("last_name", lastName);
            jsonBody.put("city", city);
            jsonBody.put("country", country);
            jsonBody.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL + "register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("Failed to login. Please try again.");
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response.body().string());
                    String accessToken = jsonResponse.getString("access_token");
                    String refreshToken = jsonResponse.getString("refresh_token");
                    int userId = jsonResponse.getInt("user_id");

                    // Save tokens and user ID to SharedPreferences
                    saveAccessToken(accessToken);
                    saveRefreshToken(refreshToken);
                    saveUserId(userId);

                    callback.onSuccess();
                } catch (JSONException e) {
                    e.printStackTrace();
                    callback.onFailure("Error parsing server response.");
                }
            }
        });
    }

    private void saveAccessToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("accessToken", token);
        editor.apply();
    }
    private void saveRefreshToken(String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("refreshToken", token);
        editor.apply();
    }

    private void saveUserId(int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mySharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userId", userId);
        editor.apply();
    }
    public String refreshAccessToken() throws IOException {
        String refreshToken = getRefreshToken();
        if (refreshToken == null) {
            throw new IOException("No refresh token available.");
        }

        RequestBody formBody = new FormBody.Builder()
                .add("refresh_token", refreshToken)

                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "refresh")
                .header("Authorization", "Bearer " + refreshToken)
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String json = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
                String newAccessToken = jsonObject.get("access_token").getAsString();
                saveAccessToken(newAccessToken); // Save the new access token
                return newAccessToken;
            } else {
                throw new IOException("Failed to refresh access token: " + response);
            }
        }
    }
    public void logout(final LogoutCallback callback) {
        String accessToken = getAccessToken();
        Request request = new Request.Builder()
                .url(BASE_URL + "logout")
                .header("Authorization", "Bearer " + accessToken)
                .post(RequestBody.create(new byte[0]))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    clearTokens();
                    callback.onSuccess();

                } else {
                    callback.onFailure("Failed to logout. Please try again.");
                }
            }
        });
    }

    public interface LogoutCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }
    // Method to include access token in the HTTP header
   public Request.Builder addAccessTokenToHeader(Request.Builder requestBuilder) throws IOException {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            throw new IOException("No access token available.");
        }
        return requestBuilder.header("Authorization", "Bearer " + accessToken);
    }
    public void clearTokens() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("accessToken");
        editor.remove("refreshToken");
        editor.remove("userId");
        editor.apply();
    }
    public String getAccessToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mySharedPreferences", MODE_PRIVATE);

        return sharedPreferences.getString("accessToken", null);
    }
    public String getRefreshToken() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mySharedPreferences", MODE_PRIVATE);

        return sharedPreferences.getString("refreshToken", null);
    }
    public Integer getUserId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("mySharedPreferences", MODE_PRIVATE);

        return sharedPreferences.getInt("userId", -1);
    }



    public interface LoginCallback {
        void onSuccess();

        void onFailure(String errorMessage);
    }
}
