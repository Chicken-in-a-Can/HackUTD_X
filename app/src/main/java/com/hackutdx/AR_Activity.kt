package com.hackutdx

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

private const val arrow = "https://drive.google.com/file/d/13_IvgSGIDikcLdU-GyW3RjdREkcFhCnR/view?usp=sharing"
public class AR_Activity : AppCompatActivity(R.layout.ar_activity) {
    lateinit var sceneView: ARSceneView
    lateinit var loadingView: View

    var isLoading = false
        set(value){
            field = value
            loadingView.isGone = !value
        }

    private var anchorNode: AnchorNode? = null
        set(value){
            if(field != value){
                field = value
            }
        }

    private var trackingFailureReason: TrackingFailureReason? = null
        set(value) {
            if(field != value){
                field = value
            }
        }

    override fun onCreate(savedInstanceState: Bundle?){

        super.onCreate(savedInstanceState)
        setFullScreen(
            findViewById(R.id.rootView),
            fullScreen = true,
            hideSystemBars = true,
            fitsSystemWindows = true,
        )
        loadingView = findViewById(R.id.loadingView)
        sceneView = findViewById<ARSceneView?>(R.id.sceneView).apply {
            planeRenderer.isEnabled = true
            configureSession{
                session, config ->
                    config.depthMode = when(session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }
            onSessionUpdated = {_, frame ->
                if(anchorNode == null){
                    frame.getUpdatedPlanes()
                        .firstOrNull {
                            it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                        ?.let {plane ->
                            addAnchorNode(plane.createAnchor(plane.centerPose))
                        }
                    }
                }
                onTrackingFailureChanged = { reason ->
                    this@AR_Activity.trackingFailureReason = reason
                }
            }
        }
    private fun addAnchorNode(anchor: Anchor){
        sceneView.addChildNode(
            AnchorNode(sceneView.engine, anchor)
                .apply {
                    isEditable = true
                    lifecycleScope.launch {
                        isLoading = true
                        sceneView.modelLoader.loadModelInstance(arrow)?.let{
                            modelInstance ->
                                addChildNode(
                                    ModelNode(
                                        modelInstance = modelInstance,
                                        scaleToUnits = 0.5f,
                                        centerOrigin = Position(y = -0.5f)
                                    ).apply {
                                        isEditable = true
                                    }
                                )
                        }
                        isLoading = false
                    }
                    anchorNode = this
                }
        )
    }
    private fun Activity.setFullScreen(
        rootView: View,
        fullScreen: Boolean = true,
        hideSystemBars: Boolean = true,
        fitsSystemWindows: Boolean = true,
    ) {
        rootView.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
            if(hasFocus){
                WindowCompat.setDecorFitsSystemWindows(window, fitsSystemWindows)
                WindowInsetsControllerCompat(window, rootView).apply {
                    if (hideSystemBars){
                        if(fullScreen){
                            hide(
                                WindowInsetsCompat.Type.statusBars() or
                                    WindowInsetsCompat.Type.navigationBars()
                            )
                        } else{
                            show(
                                WindowInsetsCompat.Type.statusBars() or
                                        WindowInsetsCompat.Type.navigationBars()
                            )
                        }
                        systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                }
            }
        }
    }
}