package com.example.android.soloradar;

public class LocationUnit {
    double latitue;

    public LocationUnit() {
    }

    public double getLatitue() {
        return latitue;
    }

    public void setLatitue(double latitue) {
        this.latitue = latitue;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocationUnit(double latitue, double longitude) {
        this.latitue = latitue;
        this.longitude = longitude;
    }

    double longitude;
}
