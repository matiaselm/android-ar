package fi.metropolia.kari.arbasic1

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var fragment: ArFragment
    private var testRenderable: ModelRenderable? = null

    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<View>(android.R.id.content) // find the root view of the activity
        return android.graphics.Point(vw.width / 2, vw.height / 2)
    }

    private fun hideButton(){
        Toast.makeText(applicationContext, "Touch", Toast.LENGTH_SHORT).show()
        button.visibility = View.INVISIBLE
        val arView = sceneform_fragment.view
    }

    private fun addObject() {
        val frame = fragment.arSceneView.arFrame
        val pt = getScreenCenter()
        val hits: List<HitResult>
        if (frame != null && testRenderable != null) {
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    val anchor = hit!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(fragment.arSceneView.scene)
                    val mNode = TransformableNode(fragment.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = testRenderable
                    mNode.setOnTapListener{hitTestResult, motionEvent ->
                        hideButton()
                    }
                    mNode.select()
                    break
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener { view -> addObject() }

        fragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment;

        val modelUri =
            //Uri.parse("https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/CesiumMan/glTF/CesiumMan.gltf")
            //Uri.parse("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Fox/glTF/Fox.gltf")  // scale(0.005f)
            Uri.parse("rock.gltf")  // scale(0.005f)

        val renderableFuture = ModelRenderable.builder()
            .setSource(
                this, RenderableSource.builder().setSource(
                    this,
                    modelUri,
                    RenderableSource.SourceType.GLTF2
                )
                    .setScale(0.5f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            )
            .setRegistryId("Fox")
            .build()

        renderableFuture.thenAccept { it -> testRenderable = it }

        renderableFuture.exceptionally { throwable ->
            Toast.makeText(this, "Unable to create renderable", Toast.LENGTH_SHORT).show()
            null
        }

        /*
        fragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            if (testRenderable == null) {
                Log.d("AR","testRenderable == null")
                return@setOnTapArPlaneListener
            }
            val anchor = hitResult!!.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(fragment.arSceneView.scene)
            val viewNode = TransformableNode(fragment.transformationSystem)
            viewNode.setParent(anchorNode)
            viewNode.renderable = testRenderable
            viewNode.select()
            viewNode.setOnTapListener { hitTestRes: HitTestResult?, motionEv: MotionEvent? ->

                // Toast.makeText(this, "Ouch!!!!", Toast.LENGTH_SHORT).show()
            }
        }*/

    }
}
