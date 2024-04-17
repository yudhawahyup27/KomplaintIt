package com.example.komplainit.services
import com.example.komplainit.model.CreateKomplaint
import com.example.komplainit.model.DetailPengadaan
import com.example.komplainit.model.ListPengadaan
import com.example.komplainit.model.LoginRequest
import com.example.komplainit.model.LoginResponse
import com.example.komplainit.model.RatingSubmit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

public interface ApiService {
    @POST("login")
    fun login(@Body LoginRequest: LoginRequest): Call<LoginResponse>

//  Get Data All Pengaduan

    @GET("get-all-pengaduan")
     fun getAllPengaduan(): Call<ArrayList<ListPengadaan>>

//     Get All By Nick

    @GET("/api/pengaduan/{nik}")
    fun getPengaduanByNik(@Path("nik") nik: String): Call<ArrayList<ListPengadaan>>

//    Get by id
    @GET("/api/detail-pengaduan/{id}")
fun getDetailPengaduan(@Path("id") id: Int): Call<DetailPengadaan>

   @PUT("/api/pengaduan/{id}/rating")
    fun submitRating(@Path("id") id: Int, @Body ratingSubmit: RatingSubmit): Call<Void>



    @POST("/create-pengaduan")
    @Multipart
    fun submitComplaint(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("jenis_pengaduan") jenisPengaduan: RequestBody
    ): Call<CreateKomplaint>
}