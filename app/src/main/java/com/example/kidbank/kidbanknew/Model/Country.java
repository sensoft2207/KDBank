package com.example.kidbank.kidbanknew.Model;

/**
 * Created by vishal on 27/2/18.
 */

public class Country {

    String country_name;
    String id;
    String country_code;

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Country() {

    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public Country(String country_name) {
        this.country_name = country_name;
    }
}
