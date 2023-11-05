package com.hackutdx

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.TrackingFailureReason
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.getDescription
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.launch

public class AR_Activity : AppCompatActivity(R.layout.ar_activity) {
    lateinit var sceneView: ARSceneView
    lateinit var loadingView: View
    lateinit var instructionText: TextView;

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
                        sceneView.modelLoader.loadModelInstance(
                            "/home/bob/Programming/HackUTD_X/assets/cone.obj"
                        )?.let{
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
}