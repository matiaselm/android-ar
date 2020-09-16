package fi.metropolia.kari.arbasic1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.AugmentedImage
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private var testRenderable: ViewRenderable? = null
    private var testRenderable2: ViewRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragment = supportFragmentManager.findFragmentById(R.id.arimage_fragment) as ArFragment;

        val renderableFuture = ViewRenderable.builder()
            .setView(this, R.layout.rendtext)
            .build()
        renderableFuture.thenAccept { it -> testRenderable = it }

        val renderableFuture2 = ViewRenderable.builder()
            .setView(this, R.layout.rendtext2)
            .build()
        renderableFuture2.thenAccept { it -> testRenderable2 = it }

        fragment.arSceneView.scene.addOnUpdateListener { frameTime -> frameUpdate() }
    }

    private fun frameUpdate() {
        val arFrame = fragment.arSceneView.arFrame
        if (arFrame == null || arFrame.camera.trackingState != TrackingState.TRACKING) {
            return
        }

        val updatedAugmentedImages = arFrame.getUpdatedTrackables(AugmentedImage::class.java)
        updatedAugmentedImages.forEach {
            when (it.trackingState) {
                TrackingState.PAUSED -> {
                    val text = "Detected Image: ${it.name} - need more info"
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                }

                TrackingState.STOPPED -> {
                    val text = "Tracking stopped: ${it.name}"
                    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
                }

                TrackingState.TRACKING -> {
                    val anchors = it.anchors
                    if (anchors.isEmpty()) {

                        fit_to_scan_img.visibility = View.GONE
                        val pose = it.centerPose
                        val anchor = it.createAnchor(pose)
                        val anchorNode = AnchorNode(anchor)
                        anchorNode.setParent(fragment.arSceneView.scene)

                        val imgNode = TransformableNode(fragment.transformationSystem)
                        imgNode.setParent(anchorNode)

                        // Set text rotation correctly
                        imgNode.localRotation = Quaternion.axisAngle(Vector3(1f, 0f, 0f), 270f)

                        if (it.name == getString(R.string.img1)) {
                            imgNode.renderable = testRenderable
                        }

                        if (it.name == getString(R.string.img2)) {
                            imgNode.renderable = testRenderable2
                        }
                    }
                }
            }
        }
    }
}
