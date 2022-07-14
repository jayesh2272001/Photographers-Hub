package com.jayesh.finalyearproject.data;

public class User {
    public String name, email, age, experience, location, profileImage, uid, mono, wages, about;

    public User() {

    }

    public User(String name, String email, String age, String experience, String location,
                String profileImage, String uid, String mono, String wages, String about) {
        this.age = age;
        this.email = email;
        this.experience = experience;
        this.location = location;
        this.name = name;
        this.profileImage = profileImage;
        this.uid = uid;
        this.mono = mono;
        this.wages = wages;
        this.about = about;
    }
}
