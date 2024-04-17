package com.example.komplainit


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Dashboard : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val cardView = findViewById<CardView>(R.id.cardview2)

        cardView.setOnClickListener {
            val intent = Intent(this, pengaduan::class.java)
            startActivity(intent)
        }


        val cardlist = findViewById<CardView>(R.id.cardview)
        sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        cardlist.setOnClickListener {
            val nik = intent.getStringExtra("nik")

            // Membuat objek Intent untuk memulai aktivitas ListKomplain

            // Menambahkan nilai nik sebagai ekstra ke intent
            intent.putExtra("nik", nik)

            val tokenku = sharedPreferences.getString("token", "")
            Log.d("NIK", "Nilai NIK adalah KU: $nik")

            Log.d("NIK", "Nilai token adalah: $tokenku")

            val intent = Intent(this, ListKomplain::class.java)


            intent.putExtra("nik", nik)

            startActivity(intent)
        }

        val cardViewLogout = findViewById<CardView>(R.id.cardViewLogout)

        cardViewLogout.setOnClickListener {
            // Panggil metode untuk mengarahkan ke layar login
            redirectToLogin()
        }
    }



    private fun redirectToLogin() {
        // Mengarahkan pengguna ke layar login
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        // Menyelesaikan aktivitas saat ini agar pengguna tidak dapat kembali menggunakan tombol back
        finish()
    }


    }
