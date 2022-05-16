package com.jayesh.finalyearproject.data;

public class User {
    public String name, email, age, experience, location, profileImage;

    public User() {

    }

    public User(String name, String email, String age, String experience, String location, String profileImage) {
        this.age = age;
        this.email = email;
        this.experience = experience;
        this.location = location;
        this.name = name;
        this.profileImage = profileImage;
    }
}
