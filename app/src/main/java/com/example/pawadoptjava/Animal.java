package com.example.pawadoptjava;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Animal {
    private Integer id;
    private Integer user_id;
    private String name;
    private String description;
    private List<String> photos;
    private String gender;
    private String weight;
    private String color;
    private String age;
    private String breed;
    private String city;
    private String type;
    private String country;


    public List<String> getPhotos() {
        List<String> fullUrls = new ArrayList<>();
        String baseUrl = "http://10.0.2.2:5000/"; // Replace this with your actual base URL

        // Iterate through the relative paths and prepend the base URL
        for (String relativePath : photos) {
            String fullUrl = baseUrl + relativePath;
            fullUrls.add(fullUrl);
        }

        return fullUrls;
    }
    public Animal(int id, String name, String gender, String weight, String color, String age, String breed, String description, List<String> photos, String city, String country, String type, int user_id) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.weight = weight;
        this.color = color;
        this.age = age;
        this.breed = breed;
        this.description = description;
        this.photos = photos;
        this.city = city;
        this.country = country;
        this.type=type;
        this.user_id=user_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getBreed() {
        return breed;
    }
    public String getColor() {
        return color;
    }
    public String getAge() {
        return age;
    }
    public String getWeight() {
        return weight;
    }
    public String getDescription() {
        return description;
    }
    public int getUserId() {return user_id;}

}
