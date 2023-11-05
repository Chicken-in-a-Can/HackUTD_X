package com.hackutdx;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class Map_Stuff {
    public double longitude;
    public double latitude;
    public Context context;


    public final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
    };
    public Map_Stuff(Context c) {

        context = c;

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude = l.getLatitude();
        longitude = l.getLongitude();
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 10, locationListener);
    }

    public String getURL(String destination_address)
    {
        ((TextView)((Activity)context).findViewById(R.id.CurrentFunction)).setText("getURL");
        String url_formatted_destination_address = destination_address.replaceAll(" ", "+");
        String url_string = "https://maps.googleapis.com/maps/api/directions/json?destination=" + url_formatted_destination_address + "&&origin=" + latitude + "," + longitude + "&key=AIzaSyADi3dDW9bZQ_LdXJpSjVALSB-FN9WSzc4";
        ((TextView)((Activity)context).findViewById(R.id.CurrentFunction)).setText(url_string);
        return url_string;
    }

    public JSONObject read_url(String url_string) throws Exception
    {
        ((TextView)((Activity)context).findViewById(R.id.CurrentFunction)).setText("read_url");
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
        ((TextView)((Activity)context).findViewById(R.id.textView)).setText(result.toString());
        return (JSONObject) new JSONParser().parse(result.toString());
    }

    public String get_steps(JSONObject obj)
    {
        //((TextView)((Activity)context).findViewById(R.id.CurrentFunction)).setText("get_steps");
        StringBuilder buffer = new StringBuilder();
        List<Object> routes = (List) obj.get("routes");
        Object route = routes.get(0);
        List<Object> legs = (List) ((JSONObject) route).get("legs");
        Object leg = legs.get(0);
        List<Object> steps = (List) ((JSONObject) leg).get("steps");

        for(Object step: steps)
        {
            buffer.append((String)((JSONObject)step).get("html_instructions"));
            buffer.append(' ');
        }
        return buffer.toString();
    }


}
