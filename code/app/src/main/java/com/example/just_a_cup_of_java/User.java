package com.example.just_a_cup_of_java;

import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private String email;
    private String password;
    private String bio;
    private String profilePic;
    private String username;

    private String uid;

    public User() {
        //Empty constructor for firebase
    }
    
    public User(String name, String email, String password, String bio, String profilePic, String username, String uid) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.bio = bio;
        this.profilePic = profilePic;
        this.username = username;
    }

    public User(String email, String username, String uid) {
        this.email = email;
        this.username = username;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
  
}
