package com.smitcoderx.learn.trippin_business.UI

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.smitcoderx.learn.trippin_business.API.ApiClient
import com.smitcoderx.learn.trippin_business.Util.Constants
import com.smitcoderx.learn.trippin_business.Util.Constants.REQUEST_CAPTURE_IMAGE
import com.smitcoderx.learn.trippin_business.Util.Constants.REQUEST_PICK_IMAGE
import com.smitcoderx.learn.trippin_business.Util.Constants.TAG
import com.smitcoderx.learn.trippin_business.Util.PreferenceManager
import com.smitcoderx.learn.trippin_business.Util.UploadRequestBody
import com.smitcoderx.learn.trippin_business.Util.getFileName
import com.smitcoderx.learn.trippin_business.databinding.ActivityUploadImageBinding
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UploadImageActivity : AppCompatActivity(), UploadRequestBody.UploadCallback {

    private lateinit var binding: ActivityUploadImageBinding
    private lateinit var currentPhotoPath: String
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        binding = ActivityUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.btnCamera.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }

        binding.submit.setOnClickListener {
            uploadImage()
        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAPTURE_IMAGE) {
                Glide.with(this).load(currentPhotoPath).into(binding.ivImage)
            } else if (requestCode == REQUEST_PICK_IMAGE) {
                selectedImageUri = data?.data
                Glide.with(this).load(selectedImageUri).into(binding.ivImage)
            }
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun dispatchTakePictureIntent() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                    null
                }
                photoFile?.also {
                    selectedImageUri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
                    startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? =
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                Constants.REQUEST_PERMISSION
            )
        }
    }

    private fun uploadImage() {

        val prefs = PreferenceManager(this)

        if (selectedImageUri == null) {
            Snackbar.make(binding.rootLayout, "Select An Image First", Snackbar.LENGTH_SHORT).show()
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.progressBar.progress = 0
        val body = UploadRequestBody(file, "file", this)
        lifecycleScope.launchWhenCreated {
            val response = try {
                ApiClient.retrofitService.sendImage(
                    prefs.getToken()!!, MultipartBody.Part.createFormData("file", file.name, body)
                )
            } catch (e: IOException) {
                Log.e(TAG, "uploadImage IO: ${e.message}")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "uploadImage Http: ${e.message}")
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                val uploadImage = response.body()
                Snackbar.make(
                    binding.rootLayout,
                    uploadImage!!.message.uppercase(Locale.getDefault()),
                    Snackbar.LENGTH_SHORT
                ).show()
                onBackPressed()
            } else if (response.code() == 413) {
                Snackbar.make(
                    binding.rootLayout,
                    "FILE SIZE TOO LARGE. UPLOAD IMAGE UPTO 2MB",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.progressBar.progress = percentage
    }

}