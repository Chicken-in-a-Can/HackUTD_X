package com.hackutdx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.opengl.EGLConfig;
import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PersistableBundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.*;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.SharedCamera;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.util.EnumSet;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class AR_Activity extends AppCompatActivity implements GLSurfaceView.Renderer{
    private Session session;
    HandlerThread backgroundThread;
    Handler backgroundHandler;
    GLSurfaceView gl_view;
    GestureDetector gestureDetector;
    private boolean installRequired;

    @Override
    protected void onCreate(Bundle arState){
        super.onCreate(arState);

        gl_view = new GLSurfaceView(this);

        setContentView(gl_view);

        gestureDetector = new GestureDetector(
                this,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent event) {
                        onSingleTapUp(event);
                        return true;
                    }

                    @Override
                    public boolean onDown(MotionEvent event) {
                        return true;
                    }
                }
        );
        gl_view.setOnTouchListener(
                new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View view, MotionEvent event){
                        return gestureDetector.onTouchEvent(event);
                    }
                }
        );
        gl_view.setPreserveEGLContextOnPause(true);
        gl_view.setEGLContextClientVersion(2);
        gl_view.setEGLConfigChooser(8, 8, 8, 8 ,16, 0);
        gl_view.setRenderer(this);
        gl_view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequired = false;

    }
    @Override
    protected void onResume(){
        super.onResume();
        if(session == null){
            Exception exception = null;
            String message = null;
            try{
                switch(ArCoreApk.getInstance().requestInstall(this, !installRequired)){
                    case INSTALL_REQUESTED:
                        installRequired = true;
                        return;
                    case INSTALLED:
                        break;
                }
                String CAMERA_PERMISSION = Manifest.permission.CAMERA;
                int CAMERA_PERMISSION_CODE = 0;
                if(!(ContextCompat.checkSelfPermission(this, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED)){
                    ActivityCompat.requestPermissions(
                            this, new String[] {CAMERA_PERMISSION}, CAMERA_PERMISSION_CODE
                    );
                    return;
                }
                session = new Session(this);
            } catch(Exception e){
                message = "Some error with AR";
                exception = e;
            }
            if(message != null){
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                return;
            }
            Config config = new Config(session);
            if(!session.isSupported(config)){
                Toast.makeText(this, "Not supported", Toast.LENGTH_SHORT).show();
            }
            session.configure(config);
            try {
                session.resume();
            } catch (CameraNotAvailableException e) {
                throw new RuntimeException(e);
            }
            gl_view.onResume();
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        if(session != null){
            gl_view.onPause();
            session.pause();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, javax.microedition.khronos.egl.EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }
}