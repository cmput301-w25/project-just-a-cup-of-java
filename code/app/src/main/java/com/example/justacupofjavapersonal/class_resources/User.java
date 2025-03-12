package com.example.justacupofjavapersonal.class_resources;

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

    public User(String uid, String email) {
        this.email = email;
        this.uid = uid;
    }

    public User(String name) {
        this.name = name;
    }

    /**
     * Gets the user's name
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gives the user a new name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email
     * @return
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of the user.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the bio of the user.
     *
     * @return the bio
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the bio of the user.
     *
     * @param bio the bio to set
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Gets the profile picture URL of the user.
     *
     * @return the profile picture URL
     */
    public String getProfilePic() {
        return profilePic;
    }

    /**
     * Sets the profile picture URL of the user.
     *
     * @param profilePic the profile picture URL to set
     */
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Gets the username of the user.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the unique identifier (UID) of the user.
     *
     * @return the UID
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the unique identifier (UID) of the user.
     *
     * @param uid the UID to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }


}
