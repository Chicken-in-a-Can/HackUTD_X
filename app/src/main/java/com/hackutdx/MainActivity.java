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

import java.util.EnumSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    class Get_directions_list extends AsyncTask<Context, Void, Void>
    {
       /* @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Looper.prepare();
        }*/

        @Override
        protected Void doInBackground(Context... contexts) {
             try {
                Map_Stuff m = new Map_Stuff(contexts[0]);
                TextView route = findViewById(R.id.textView);
                //route.setText("" + m.latitude + " " + m.longitude);
                String steps = m.get_steps(m.read_url(m.getURL("2801 Rutford Avenue")));
                route.setText(steps);

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
                startActivity(new Intent(MainActivity.this, AR_Activity.class));
            }
        });

        new Get_directions_list().execute(this);
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
    }
}