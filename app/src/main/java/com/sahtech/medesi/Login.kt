package com.sahtech.medesi

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.sahtech.medesi.databinding.ActivityLoginBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val client = OkHttpClient()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val textView = binding.question
        val text = getString(R.string.don_t_have_an_account_yet_contact_us)

        val startIndex = text.indexOf("Contact Us")
        val endIndex = startIndex + "Contact Us".length

        val spannable = SpannableString(text).apply {
            setSpan(ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Toast.makeText(widget.context, "Contact Us Clicked", Toast.LENGTH_SHORT).show()
                }
            }, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textView.text = spannable

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginUser(email, password)
        }

        binding.forgotPasswordContainer.setOnClickListener{
            startActivity(Intent(this,RestPassword::class.java))
        }
    }

    private fun loginUser(email: String, password: String) {
        val url = "https://medesi.loca.lt/api/login"
        val jsonBody = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("user_type", "student")
        }

        val requestBody = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), jsonBody.toString())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@Login, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val token = jsonResponse.getString("token")
                            saveToken(token)
                            runOnUiThread {
                                Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            runOnUiThread {
                                Toast.makeText(this@Login, "Error parsing response: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@Login, "Login failed: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    private fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }
}
