package com.hackutdx

import com.hackutdx.MainActivity

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.ar.core.Config
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import kotlinx.coroutines.launch

private const val kModelFile = "https://drive.google.com/uc?export=download&id=13_IvgSGIDikcLdU-GyW3RjdREkcFhCnR"

class Compose_AR : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            // A surface container using the 'background' color from the theme
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                var isLoading by remember { mutableStateOf(false) }
                var planeRenderer by remember { mutableStateOf(true) }
                val engine = rememberEngine()
                val modelLoader = rememberModelLoader(engine)
                val childNodes = rememberNodes()
                val coroutineScope = rememberCoroutineScope()

                //Use this line to get directions list from current spot
                Log.d("Passed1" , "we ball with " + MainActivity.getDestination() );
                val steps = Get_directions_list.get_steps(this@Compose_AR, MainActivity.getDestination())
                Log.d("Passed2" , "we ball");
                val next_step = steps.get(0).distance_in_meters
                Log.d("Passed3", "" + next_step)
                ARScene(
                    modifier = Modifier.fillMaxSize(),
                    childNodes = childNodes,
                    engine = engine,
                    modelLoader = modelLoader,
                    planeRenderer = planeRenderer,
                    onSessionConfiguration = { session, config ->
                        config.depthMode =
                            when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                                true -> Config.DepthMode.AUTOMATIC
                                else -> Config.DepthMode.DISABLED
                            }
                        config.instantPlacementMode = Config.InstantPlacementMode.DISABLED
                        config.lightEstimationMode =
                            Config.LightEstimationMode.ENVIRONMENTAL_HDR
                    },
                    onSessionUpdated = { _, frame ->
                        if (childNodes.isNotEmpty()) return@ARScene

                        frame.getUpdatedPlanes()
                            .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let { plane ->
                                isLoading = true
                                childNodes += AnchorNode(
                                    engine = engine,
                                    anchor = plane.createAnchor(plane.centerPose)
                                ).apply {
                                    isEditable = true
                                    coroutineScope.launch {
                                        modelLoader.loadModelInstance(kModelFile)?.let {
                                            addChildNode(
                                                ModelNode(
                                                    modelInstance = it,
                                                    // Scale to fit in a 0.5 meters cube
                                                    scaleToUnits = 10f,
                                                    // Bottom origin instead of center so the
                                                    // model base is on floor
                                                    centerOrigin = Position(x = 0f, y = -2f, z = if(next_step < 25){next_step.toFloat()}else{25f}),
                                                ).apply {
                                                    isEditable = true
                                                }
                                            )
                                        }
                                        planeRenderer = false
                                        isLoading = false
                                    }
                                }
                            }
                    }
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                    )
                }
            }
        }
    }
}