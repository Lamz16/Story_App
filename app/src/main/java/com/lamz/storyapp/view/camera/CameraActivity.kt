package com.lamz.storyapp.view.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.lamz.storyapp.R
import com.lamz.storyapp.data.ResultState
import com.lamz.storyapp.databinding.ActivityCameraBinding
import com.lamz.storyapp.view.ViewModelFactory
import com.lamz.storyapp.view.main.MainActivity
import com.lamz.storyapp.view.welcome.WelcomeActivity

class CameraActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel by viewModels<CameraViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var _binding: ActivityCameraBinding? = null
    private val binding get() = _binding
    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                EXTRA_TOKEN = user.token
                binding?.uploadButton?.setOnClickListener { uploadStory() }
            }

            binding?.galleryButton?.setOnClickListener { startGallery() }
            binding?.cameraButton?.setOnClickListener { startCamera() }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding?.previewImageView?.setImageURI(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }



    private fun showLoading(isLoading: Boolean) {
        binding?.progressIndicator?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    uploadStory()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    uploadStory()
                }
                else -> {
                    // No location access granted.
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun uploadStory() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {

                    currentImageUri?.let { uri ->

                        val imageFile = uriToFile(uri, this).reduceFileImage()
                        Log.d("Image File", "showImage: ${imageFile.path}")
                        val description = binding?.edtDescription?.text.toString()
                        showLoading(true)
                        val shareLocation = binding?.btnLocation
                        if (shareLocation?.isChecked == true){
                            viewModel.uploadImage(EXTRA_TOKEN, imageFile, description, location.latitude.toString(), location.longitude.toString())
                            Log.d("My koordinat", "getMyLastLocation:${location.latitude}  ${location.longitude} ")
                            viewModel.upload.observe(this) { result ->
                                if (result != null) {
                                    when (result) {
                                        is ResultState.Loading -> {
                                            showLoading(true)
                                        }

                                        is ResultState.Success -> {
                                            result.data.message.let { showToast(it) }
                                            showLoading(false)
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            finish()
                                        }

                                        is ResultState.Error -> {
                                            showToast(result.error)
                                            showLoading(false)
                                        }
                                    }
                                }

                            }
                        }else {
                            viewModel.uploadImage(EXTRA_TOKEN, imageFile, description)
                            viewModel.upload.observe(this) { result ->
                                if (result != null) {
                                    when (result) {
                                        is ResultState.Loading -> {
                                            showLoading(true)
                                        }

                                        is ResultState.Success -> {
                                            result.data.message.let { showToast(it) }
                                            showLoading(false)
                                            val intent = Intent(this, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                            finish()
                                        }

                                        is ResultState.Error -> {
                                            showToast(result.error)
                                            showLoading(false)
                                        }
                                    }
                                }

                            }
                        }


                    } ?: showToast(getString(R.string.empty_image_warning))


                } else {
                    Toast.makeText(
                        this@CameraActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    companion object {
        private var EXTRA_TOKEN : String = "token"
    }

    override fun onMapReady(p0: GoogleMap) {

    }
}