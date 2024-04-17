package com.example.komplainit

import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast


import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.komplainit.common.utils.ApiClient
import com.example.komplainit.common.utils.FileUtil
import com.example.komplainit.model.CreateKomplaint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class pengaduan : AppCompatActivity() {
    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pengaduan)
        setupUI()
    }

    private fun setupUI() {
        // Initialize views and adapters
        val coursesspinner = findViewById<Spinner>(R.id.coursesspinner)
        val btnChooseImage = findViewById<Button>(R.id.btnChooseImage)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val descriptiontext: EditText = findViewById(R.id.description)

        val jenisPengaduanAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.Jenis_Pengadaan_values,
            android.R.layout.simple_spinner_item
        )
        jenisPengaduanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        coursesspinner.adapter = jenisPengaduanAdapter

        // Button to choose image
        btnChooseImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGallery()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    REQUEST_IMAGE_PICK_PERMISSION
                )
            }
        }

        // Button to submit
        buttonSubmit.setOnClickListener {
            val description = descriptiontext.text.toString()
            val jenisPengaduan = coursesspinner.selectedItem.toString()

            if (description.isNotEmpty() && jenisPengaduan.isNotEmpty() ) {
                getToken()?.let { token ->
                    submitComplaint(description, jenisPengaduan, token)
                } ?: run {
                    Toast.makeText(this@pengaduan, "Token is empty!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@pengaduan, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE)
        return sharedPreferences.getString("token", "") ?: ""
    }

    private fun submitComplaint(description: String, jenisPengaduan: String, token: String) {
        val descriptionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
        val imageBody = RequestBody.create("image/*".toMediaTypeOrNull(), selectedImageFile!!)
        val imagePart = MultipartBody.Part.createFormData("image", selectedImageFile!!.name, imageBody)
        val jenisPengaduanRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), jenisPengaduan)

        val apiService = ApiClient.apiService
        val call = apiService.submitComplaint(token, descriptionBody, imagePart, jenisPengaduanRequestBody)

        call.enqueue(object : Callback<CreateKomplaint> {
            override fun onResponse(call: Call<CreateKomplaint>, response: Response<CreateKomplaint>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@pengaduan, "Complaint submitted successfully", Toast.LENGTH_SHORT).show()
                    clearFields()
                } else {
                    Toast.makeText(this@pengaduan, "Failed to submit Jontok", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateKomplaint>, t: Throwable) {
                Toast.makeText(this@pengaduan, "Failed to submit complaint: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun clearFields() {
        val descriptiontext: EditText = findViewById(R.id.description)
        val imageView = findViewById<ImageView>(R.id.imageView)
        descriptiontext.text.clear()
        imageView.setImageResource(R.drawable.upload)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_IMAGE_PICK_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            val imageView = findViewById<ImageView>(R.id.imageView)

            selectedImageFile = FileUtil.getFileFromUri(this, selectedImageUri!!)
            imageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val REQUEST_IMAGE_PICK_PERMISSION = 101
    }
}