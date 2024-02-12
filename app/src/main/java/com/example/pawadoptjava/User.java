package com.example.pawadoptjava;
import com.google.gson.annotations.SerializedName;

public class User {

    private int id;




    private String first_name;


    private String last_name;


    private String email;


    private String address;


    private String city;


    private String country;

    public String getFirstName() {
        return first_name;
    }
    public String getLastName() {
        return last_name;
    }
    public String getEmail() {
        return email;
    }
    public String getAddress() {return address;}
    public String getCity() {
         return city;
    }
    public String getCountry(){
        return country;
    }
}