package com.hackutdx;

import static android.Manifest.permission_group.CAMERA;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.google.ar.core.*;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.util.EnumSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableArButton();
        View enable_ar = findViewById(R.id.enable_ar);
        enable_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    createSession();
                    new AR_Activity();
                } catch (UnavailableDeviceNotCompatibleException e) {
                    throw new RuntimeException(e);
                } catch (UnavailableSdkTooOldException e) {
                    throw new RuntimeException(e);
                } catch (UnavailableArcoreNotInstalledException e) {
                    throw new RuntimeException(e);
                } catch (UnavailableApkTooOldException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
    public void createSession() throws UnavailableDeviceNotCompatibleException, UnavailableSdkTooOldException, UnavailableArcoreNotInstalledException, UnavailableApkTooOldException {
        Session session = new Session(this);
        Config config = new Config(session);
        session.configure(config);
        CameraConfigFilter filter = new CameraConfigFilter(session);
        filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30));
        filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE));
        List<CameraConfig> cameraConfigList = session.getSupportedCameraConfigs(filter);
        session.setCameraConfig(cameraConfigList.get(0));
    }
}