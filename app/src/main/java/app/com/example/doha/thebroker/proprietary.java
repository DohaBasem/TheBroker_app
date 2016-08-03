package app.com.example.doha.thebroker;

import java.io.Serializable;

/**
 * Created by DOHA on 30/07/2016.
 */
//The class implements Serializable such that it could be passed through an intent using a bundle between two activities
public class proprietary implements Serializable {
    private double latitude;
    private double longitude;
    private String address;
    private String country;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    private double price;
    public proprietary(){}

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String region) {
        this.address = region;
    }



}
