package fi.metropolia.kari.arbasic1

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.ar.core.AugmentedImageDatabase
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment

class AlmgFrag: ArFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        // Disable plane renderer and planeDiscoveryController
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        return view
    }

    override fun getSessionConfiguration(session: Session?): Config {

        // Create image database and add it to of session configuration
        val config = super.getSessionConfiguration(session)
        setupAugmentedImageDatabase(config,session)
        return config
    }

    private fun setupAugmentedImageDatabase(config: Config?, session: Session?) {
        val assetManager = context!!.assets
        val augmentedImageDb: AugmentedImageDatabase = AugmentedImageDatabase(session)

        val inputStream1 = assetManager.open("sofa.jpg")
        val augmentedImageBitmap1 = BitmapFactory.decodeStream(inputStream1)

        val inputStream2 = assetManager.open("corals.jpg")
        val augmentedImageBitmap2 = BitmapFactory.decodeStream(inputStream2)

        augmentedImageDb.addImage("sofa", augmentedImageBitmap1)
        augmentedImageDb.addImage("corals",augmentedImageBitmap2)

        if (config != null) {
            config.augmentedImageDatabase = augmentedImageDb
        }
    }
}