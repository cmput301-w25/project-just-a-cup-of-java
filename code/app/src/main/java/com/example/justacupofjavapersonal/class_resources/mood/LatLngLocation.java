package com.example.justacupofjavapersonal.class_resources.mood;

public class LatLngLocation {
    private double latitude;
    private double longitude;

    public LatLngLocation() {
        // Needed for Firebase
    }

    public LatLngLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
