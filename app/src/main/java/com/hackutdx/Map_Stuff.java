package com.hackutdx;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Map_Stuff {
    public double longitude;
    public double latitude;

    public Map_Stuff(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        };
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    public String getURL(String destination_address)
    {
        String url_formatted_destination_address = destination_address.replaceAll(" ", "+");
        String url_string = "https://maps.googleapis.com/maps/api/directions/json?destination=" + url_formatted_destination_address + "&&origin=" + latitude + "," + longitude + "&key=AIzaSyADi3dDW9bZQ_LdXJpSjVALSB-FN9WSzc4";
        return url_string;
    }

    public JSONObject read_url(String url_string) throws Exception
    {
        StringBuilder result = new StringBuilder();
        URL url = new URL(url_string);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        return (JSONObject) new JSONParser().parse(result.toString());
    }
}
