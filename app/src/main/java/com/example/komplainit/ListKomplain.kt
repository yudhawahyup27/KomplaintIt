package com.example.komplainit

import PengaduanAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.komplainit.common.utils.ApiClient
import com.example.komplainit.databinding.ActivityListKomplainBinding
import com.example.komplainit.model.ListPengadaan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListKomplain : AppCompatActivity() {

    private lateinit var binding: ActivityListKomplainBinding
    private lateinit var adapter: PengaduanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListKomplainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        adapter = PengaduanAdapter(arrayListOf(),this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Set up Edge-to-Edge
        enableEdgeToEdge(binding.root)




        // Fetch data from API
        val nik = intent.getStringExtra("nik")
        Log.d("NIK", "Nilai NIK adalah: $nik")
        nik?.let {
            getData(it)
        }


    }

    private fun enableEdgeToEdge(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getData(nik: String) {
        val call = ApiClient.apiService.getPengaduanByNik(nik)
        call.enqueue(object : Callback<ArrayList<ListPengadaan>> {
            override fun onResponse(
                call: Call<ArrayList<ListPengadaan>>,
                response: Response<ArrayList<ListPengadaan>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    if (data != null) {
                        setDataAdapter(data)
                    } else {
                        // Handle null data
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<ArrayList<ListPengadaan>>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun setDataAdapter(data: ArrayList<ListPengadaan>) {
        adapter.setData(data)
    }

}