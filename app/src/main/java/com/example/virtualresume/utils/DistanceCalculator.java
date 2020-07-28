package com.example.virtualresume.utils;

import com.parse.ParseGeoPoint;

public abstract class DistanceCalculator {

    protected ParseGeoPoint userHome;
    protected ParseGeoPoint contactHome;
    final private double radiusEarth = 6371.8;

    public DistanceCalculator(ParseGeoPoint userHome, ParseGeoPoint contactHome) {
        this.userHome = userHome;
        this.contactHome = contactHome;
    }

    protected double calculateDistanceKilometer(ParseGeoPoint userHome, ParseGeoPoint contactHome){

        double userHomeLatitudeRad = Math.toRadians(userHome.getLatitude());
        double userHomeLongitudeRad = Math.toRadians(userHome.getLongitude());
        double contactHomeLatitudeRad = Math.toRadians(userHome.getLatitude());
        double contactHomeLongitudeRad = Math.toRadians(userHome.getLongitude());

        double latitudeDistance = contactHomeLatitudeRad - userHomeLatitudeRad;
        double longitudeDistance = contactHomeLongitudeRad - userHomeLongitudeRad;

        //Haversine formula
        double distance = Math.pow(Math.sin(latitudeDistance / 2), 2)
                + Math.cos(userHomeLatitudeRad)
                * Math.cos(contactHomeLatitudeRad)
                * Math.pow(Math.sin(longitudeDistance / 2), 2);
        distance = 2 * Math.asin(Math.sqrt(distance)) * radiusEarth;

        return distance;
    }


}
