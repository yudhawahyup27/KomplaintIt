package com.example.komplainit

import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.komplainit.common.utils.ApiClient
import com.example.komplainit.model.DetailPengadaan
import com.example.komplainit.model.RatingSubmit
import com.example.komplainit.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date

class Detail_laporan : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var ratingBar: RatingBar
    private var pengaduanId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_laporan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        apiService = ApiClient.apiService
        ratingBar = findViewById(R.id.ratingBar)
        pengaduanId = intent.getIntExtra("id", 0)

        Log.d("ID", "Lihat Id: $pengaduanId")

        if (pengaduanId != 0) {
            fetchPengaduanDetail(pengaduanId)
        }
    }

    // Fungsi untuk mengambil detail pengaduan dari server
    private fun fetchPengaduanDetail(id: Int) {
        apiService.getDetailPengaduan(id).enqueue(object : Callback<DetailPengadaan> {
            override fun onResponse(call: Call<DetailPengadaan>, response: Response<DetailPengadaan>) {
                if (response.isSuccessful) {
                    val detailPengadaan: DetailPengadaan? = response.body()
                    // Bind data yang diperoleh dari API ke UI
                    bindDataToUI(detailPengadaan)
                } else {
                    // Tangani respon gagal
                    Toast.makeText(this@Detail_laporan, "Failed to fetch detail", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DetailPengadaan>, t: Throwable) {
                // Tangani kegagalan panggilan
                Toast.makeText(this@Detail_laporan, "Error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Fungsi untuk mengikat data ke UI setelah mendapatkan respons dari server
    private fun bindDataToUI(detailPengadaan: DetailPengadaan?) {
        // Memastikan detailPengadaan tidak null
        detailPengadaan?.let {
            // Bind ImageView (dengan asumsi Anda menggunakan Glide untuk memuat gambar)
            Glide.with(this@Detail_laporan)
                .load("http://192.168.1.19:8000/storage/${it.image}")
                .into(findViewById(R.id.imageView3))

            findViewById<TextView>(R.id.name_value).text = it.name
            findViewById<TextView>(R.id.tglvalue).text = formatDate(it.tanggal)
            findViewById<TextView>(R.id.statusvalue).text = it.status
            findViewById<TextView>(R.id.pengadaanvalue).text = it.jenis_pengaduan
            findViewById<TextView>(R.id.descriptionvalue).text = it.description

            // Bind nilai rating ke RatingBar
            ratingBar.rating = it.rating.toFloat()

            // Mengirim rating yang diatur oleh pengguna
            ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
                Log.d("Detail_laporan", "Rating changed: $rating")
                val ratingSubmit = RatingSubmit(rating.toInt())
                apiService.submitRating(pengaduanId, ratingSubmit).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            // Tangani respon berhasil
                            Toast.makeText(this@Detail_laporan, "Rating submitted successfully", Toast.LENGTH_SHORT).show()
                            refreshActivity()
                        } else {
                            // Tangani respon gagal
                            Toast.makeText(this@Detail_laporan, "Failed to submit rating", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        // Tangani kegagalan panggilan
                        Toast.makeText(this@Detail_laporan, "Error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy - HH:mm:ss")
        return dateFormat.format(date)
    }
    private fun refreshActivity() {
        val intent = intent
        finish() // Menutup activity saat ini
        startActivity(intent) // Me-restart activity dengan intent yang sama
    }
}
