package com.hackutdx;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Map_Stuff {
    public double longitude;
    public double latitude;
    public Context context;


    public LocationListener locationListener;
    public Map_Stuff(Context c) {

        context = c;
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Log.d("location_changed", "" + location.getLongitude() + " " + location.getLatitude());
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        };

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, locationListener);
        Location last_location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        longitude = last_location.getLongitude();
        latitude = last_location.getLatitude();
    }

    public String getURL(String destination_address)
    {
        String url_formatted_destination_address = destination_address.replaceAll(" ", "+");
        String url_string = "https://maps.googleapis.com/maps/api/directions/json?destination=" + url_formatted_destination_address + "&&origin=" + latitude + "," + longitude + "&key=AIzaSyADi3dDW9bZQ_LdXJpSjVALSB-FN9WSzc4";
        ((TextView)((Activity)context).findViewById(R.id.textView3)).setText(url_string);
        Log.d("my_url", url_string);
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
        Log.d("my_result", result.toString().replaceAll("\\s+", ""));

        return (JSONObject) new JSONParser().parse(result.toString().replaceAll("\\s+", ""));
    }

    public List<Step_Tuple> get_steps(JSONObject obj)
    {
        Log.d("My_JSON", obj.toString());
        StringBuilder buffer = new StringBuilder();
        List<JSONObject> routes = (List) obj.get("routes");
        JSONObject route = routes.get(0);
        List<JSONObject> legs = (List) route.get("legs");
        JSONObject leg = legs.get(0);
        List<JSONObject> steps = (List) leg.get("steps");


        List<Step_Tuple> step_tuples = new ArrayList<Step_Tuple>();
        for(JSONObject step: steps)
        {
            step_tuples.add(new Step_Tuple(step));
        }
        return step_tuples;
    }

    static class Step_Tuple
    {
        public String str;
        public long distance_in_meters;
        public Step_Tuple(JSONObject step)
        {
            str = (((String)step.get("html_instructions")).replaceAll("<[^>]*>", " ") + " in " + ((String)((JSONObject)step.get("distance")).get("text"))).replaceAll(" +", " ").trim();
            distance_in_meters = (long)((JSONObject)step.get("distance")).get("value");
        }

        public Step_Tuple(String sstr, long ddistance)
        {
            str = sstr;
            distance_in_meters = ddistance;
        }

        public String toString()
        {
            return str + " " + distance_in_meters;
        }
    }


}
