package com.example.pawadoptjava;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final AuthService authService;


    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }


    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();

        // Add access token to the header
        try {
            requestBuilder = authService.addAccessTokenToHeader(requestBuilder);
        } catch (IOException e) {
            // Handle token retrieval error
            e.printStackTrace();
        }

        Response response = chain.proceed(requestBuilder.build());

        // Check if token expired
        if (response.code() == 401) {
            // Token expired, refresh it
            try {
                String newAccessToken = authService.refreshAccessToken();
                response.close();
                // Retry the original request with the new access token
                requestBuilder = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken);
                response = chain.proceed(requestBuilder.build());
            } catch (IOException e) {
                // Handle token refresh error
                e.printStackTrace();
            }
        }

        return response;
    }
}

