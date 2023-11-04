package com.hackutdx;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.util.EnumSet;
import java.util.List;

public class AR_Activity extends AppCompatActivity {
    protected void OnCreate(Bundle arState) throws UnavailableDeviceNotCompatibleException, UnavailableSdkTooOldException, UnavailableArcoreNotInstalledException, UnavailableApkTooOldException {
        super.onCreate(arState);
        setContentView(R.layout.ar_activity);

        View ar_view = findViewById(R.id.ar_view);

        Session session = new Session(this);
        Config config = new Config(session);
        session.configure(config);

        CameraConfigFilter filter = new CameraConfigFilter(session);
        filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30));
        filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE));

        List<CameraConfig> cameraConfigList = session.getSupportedCameraConfigs(filter);
        session.setCameraConfig(cameraConfigList.get(0));

        while (true){
        }
    }
}
