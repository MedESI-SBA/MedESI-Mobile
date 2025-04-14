package com.sahtech.medesi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sahtech.medesi.databinding.ActivityRestPasswordBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class RestPassword : AppCompatActivity() {
    private lateinit var binding: ActivityRestPasswordBinding
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRestPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            resetPassword(email)
        }

    }


    private fun resetPassword(email: String) {
        val url = "https://medesi.loca.lt/api/forgot-password"
        val jsonBody = JSONObject().apply {
            put("email", email)
            put("user_type", "student")
        }

        val requestBody =
            jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RestPassword, "Reset failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
                    if (response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@RestPassword, "Password Reset Link sent successfully", Toast.LENGTH_SHORT).show()

                            Handler().postDelayed({
                                startActivity(Intent(this@RestPassword, Login::class.java))
                                finish()
                            },2000)

                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@RestPassword, "Reset failed: ${jsonResponse.getString("message")}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }
}