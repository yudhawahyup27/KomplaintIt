package com.example.komplainit.model

import java.util.Date

data class DetailPengadaan(
    val name: String,
    val image: String,
    val status: String,
    val rating : Int,
    val description : String,
    val jenis_pengaduan: String,
    val tanggal : Date,
)
