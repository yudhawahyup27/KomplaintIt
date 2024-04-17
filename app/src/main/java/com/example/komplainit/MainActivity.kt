package com.example.komplainit
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.komplainit.Dashboard
import com.example.komplainit.ListKomplain
import com.example.komplainit.R
import com.example.komplainit.common.utils.ApiClient
import com.example.komplainit.model.LoginRequest
import com.example.komplainit.model.LoginResponse
import com.example.komplainit.services.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var edEmail: EditText
    private lateinit var edPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edEmail = findViewById(R.id.edEmail)
        edPassword = findViewById(R.id.edPassword)
        val btnSignIn: Button = findViewById(R.id.btnSignIn)

        btnSignIn.setOnClickListener {
            val email = edEmail.text.toString()
            val password = edPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter both email and password.", Toast.LENGTH_SHORT).show()
            } else {
                val loginRequest = LoginRequest(email, password)
                login(loginRequest)
            }
        }
    }

    private fun login(loginRequest: LoginRequest) {


        val call = ApiClient.apiService.login(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    val nik = loginResponse?.nik

                    if (token != null && token.isNotEmpty()) {
                        val sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE)
                        sharedPreferences.edit().putString("token", token).apply()
                        Toast.makeText(this@MainActivity, "Login Berhasil Yipi.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@MainActivity, Dashboard::class.java)
                        intent.putExtra("nik", nik)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Login failed, please check your credentials.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle failure
                t.printStackTrace()
            }
        })
    }
}
