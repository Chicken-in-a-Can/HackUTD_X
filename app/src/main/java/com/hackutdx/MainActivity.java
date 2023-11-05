package com.hackutdx;

import static android.Manifest.permission_group.CAMERA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.*;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public List<Map_Stuff.Step_Tuple> generated_steps_list;
    public List<Map_Stuff.Step_Tuple> get_steps()
    {
        generated_steps_list = new ArrayList<>();
        Get_directions_list gdl = new Get_directions_list();
        gdl.execute(this);
        while(!gdl.finished);
        return generated_steps_list;
    }
    class Get_directions_list extends AsyncTask<Context, Void, Void>
    {
        public boolean finished;
        public List<Map_Stuff.Step_Tuple> steps;
        @Override
        protected Void doInBackground(Context... contexts) {
             try {
                 finished = false;
                Looper.prepare();
                Map_Stuff m = new Map_Stuff(contexts[0]);
                steps = m.get_steps(m.read_url(m.getURL("2801 Rutford Avenue")));
                int i = 0;
                for(Map_Stuff.Step_Tuple step : steps)
                 {
                     Log.d("final_step_" + i++, step.str + " " + step.distance_in_meters);
                     ((MainActivity)(contexts[0])).generated_steps_list.add(new Map_Stuff.Step_Tuple(step.str, step.distance_in_meters));
                 }
                finished = true;

            }catch(Exception e){
                 Log.d("StackTrace",e.getMessage());
                StackTraceElement[] ste_arr = e.getStackTrace();
                for(int i = 0; i < ste_arr.length; i++)
                {
                    Log.d("StackTrace_" + i, ste_arr[i].toString());
                }

            }
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableArButton();
        View enable_ar = findViewById(R.id.enable_ar);
        enable_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Compose_AR.class));
            }
        });

        //Use this line to get directions list from current spot
        List<Map_Stuff.Step_Tuple> steps = get_steps();
        //steps.get(0).distance_in_meters
        //            .str


        try{
            for(Map_Stuff.Step_Tuple st: steps)
            {
            Log.d("Step_out" , st.toString());
            }
        }catch(Exception e){
            Log.d("steps_StackTrace",e.getMessage());
            StackTraceElement[] ste_arr = e.getStackTrace();
            for(int i = 0; i < ste_arr.length; i++)
            {
            Log.d("steps_StackTrace_" + i, ste_arr[i].toString());
            }

        }
    }
    void enableArButton(){
        View enable_ar = findViewById(R.id.enable_ar);
        ArCoreApk.getInstance().checkAvailabilityAsync(this, availability -> {
            if(availability.isSupported()){
                enable_ar.setVisibility(View.VISIBLE);
                enable_ar.setEnabled(true);
            } else{
                enable_ar.setVisibility(View.INVISIBLE);
                enable_ar.setEnabled(false);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        Activity activity = this;
        String CAMERA_PERMISSION = Manifest.permission.CAMERA;
        int CAMERA_PERMISSION_CODE = 0;
        if(!(ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(
                    activity, new String[] {CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE
            );
        }
        String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
        int LOCATION_PERMISSION_CODE = 0;
        if(!(ContextCompat.checkSelfPermission(activity, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(
                    activity, new String[] {LOCATION_PERMISSION}, LOCATION_PERMISSION_CODE
            );
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results){
        super.onRequestPermissionsResult(requestCode, permissions, results);
        String CAMERA_PERMISSION = Manifest.permission.CAMERA;
        int CAMERA_PERMISSION_CODE = 0;
        if(!(ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED)){
            Toast.makeText(this, "Camera permission is needed to run AR", Toast.LENGTH_LONG).show();
            if(!(ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION))){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", this.getPackageName(), null));
                this.startActivity(intent);
            }
            finish();
        }

        String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
        int LOCATION_PERMISSION_CODE = 0;
        if(!(ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED)){
            Toast.makeText(this, "Fine Location permission is needed to run maps API", Toast.LENGTH_LONG).show();
            if(!(ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION))){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", this.getPackageName(), null));
                this.startActivity(intent);
            }
            finish();
        }
    }
}