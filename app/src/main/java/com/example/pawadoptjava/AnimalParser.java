package com.example.pawadoptjava;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class AnimalParser {
    private Gson gson = new Gson();

    public List<Animal> parseAnimals(String json) {
        return gson.fromJson(json, new TypeToken<List<Animal>>() {
        }.getType());
    }
}
